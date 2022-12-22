package com.romankryvolapov.loramessenger.helpers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import com.romankryvolapov.loramessenger.models.LoraSettings
import com.romankryvolapov.loramessenger.models.LoraSettingsConst
import kotlinx.coroutines.delay
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class BluetoothHelper(
  private val preferences: SharedPreferences,
) {

  companion object {
    private const val LAST_BLUETOOTH_DEVICE_ADDRESS = "LAST_BLUETOOTH_DEVICE_ADDRESS"
    private const val TIMEOUT_DELAY = 100L
    private const val TIMEOUT_SET_SETTINGS_DELAY = 3000L
    private const val TIMEOUT_GET_DATA_DELAY = 1000L
    private const val TIMEOUT_ERROR_DELAY = 1000L
    private const val TIMEOUT_EMPTY_DELAY = 1000L
    private const val MAX_MESSAGE_SIZE = 256
  }

  private var socket: BluetoothSocket? = null
  private var devices: List<BluetoothDevice> = emptyList()
  private var selectedBluetoothDevice: BluetoothDevice? = null
  private var currentBluetoothConnectionStatus = false
  private val currentLoraSettings = LoraSettings()
  private var currentLoraOutputMessages: LinkedList<String> = LinkedList<String>()

  private var showMessage: ((String) -> Unit)? = null
  private var serialPortMessage: ((String) -> Unit)? = null
  private var bluetoothDevices: ((List<String>) -> Unit)? = null
  private var bluetoothConnectionStatus: ((Boolean) -> Unit)? = null
  private var bluetoothDevicePosition: ((Int) -> Unit)? = null
  private var loraSettings: ((LoraSettings) -> Unit)? = null

  suspend fun setup() {
    while (true) {
      try {
        if (socket == null || socket?.isConnected != true) {
          delay(TIMEOUT_EMPTY_DELAY)
        } else {
          val buffer = ByteArray(MAX_MESSAGE_SIZE)
          val inputStream = socket?.inputStream
          val dataInputStream = DataInputStream(inputStream)
          val bytes = dataInputStream.read(buffer)
          val currentMessage = String(buffer, 0, bytes)
            .alphaNumericOnly()
          if (currentMessage.isNotEmpty()) {
            serialPortMessage?.invoke(currentMessage)
            parseSerialPortMessageCommands(currentMessage)
          }
          if (currentLoraOutputMessages.isNotEmpty()) {
            currentLoraOutputMessages.last.let { message ->
              val outputStream = socket?.outputStream
              val dataOutputStream = DataOutputStream(outputStream)
              dataOutputStream.writeBytes(message)
              serialPortMessage?.invoke(message)
            }
            currentLoraOutputMessages.removeLast()
            delay(TIMEOUT_GET_DATA_DELAY)
          } else {
            delay(TIMEOUT_DELAY)
          }
        }
      } catch (e: Exception) {
        showMessage?.invoke("Exception: $e")
        delay(TIMEOUT_ERROR_DELAY)
      }
    }
  }

  private fun String.alphaNumericOnly(): String {
    val regex = Regex("[^A-Za-z0-9:| ]")
    return regex.replace(this, "")
      .replace("|", "\n")
  }

  fun getLoraSettings() {
    currentLoraOutputMessages.add(LoraSettingsConst.ALL_SETTINGS.getCommand)
  }

  suspend fun setLoraSettings(const: LoraSettingsConst, value: String) {
    const.setCommand?.let {
      currentLoraOutputMessages.add("${const.setCommand}$value")
      delay(TIMEOUT_SET_SETTINGS_DELAY)
      currentLoraOutputMessages.add(const.getCommand)
    }
  }

  fun getLoraSetting(setting: String) {
    currentLoraOutputMessages.add(setting)
  }

  private fun parseSerialPortMessageCommands(message: String) {
    when {
      message.startsWith("GetChannel:") -> {
        message.replace("GetChannel:", "").let {
          try {
            currentLoraSettings.channel = Integer.parseInt(it)
            loraSettings?.invoke(currentLoraSettings)
          } catch (e: Exception) {
            showMessage?.invoke("Exception: $e")
          }
        }
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun connectToSavedBluetoothDevice(context: Context) {
    try {
      val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
      val bluetoothAdapter = bluetoothManager.adapter
      if (bluetoothAdapter == null) {
        showMessage?.invoke("Devise not have bluetooth")
        return
      }
      if (!bluetoothAdapter.isEnabled) {
        showMessage?.invoke("Bluetooth not enabled")
        return
      }
      devices = bluetoothAdapter.bondedDevices.toList()
      bluetoothAdapter.cancelDiscovery()
      val lastDevice = preferences.getString(LAST_BLUETOOTH_DEVICE_ADDRESS, "")
      devices.firstOrNull { device ->
        device.address == lastDevice
      }?.let {
        bluetoothDevicePosition?.invoke(devices.indexOf(it))
        selectedBluetoothDevice = it
        connectBluetoothDevice()
      }
    } catch (e: Exception) {
      showMessage?.invoke("Exception: $e")
    }
  }

  @SuppressLint("MissingPermission")
  fun getPairedDevices(context: Context) {
    try {
      val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
      val bluetoothAdapter = bluetoothManager.adapter
      if (bluetoothAdapter == null) {
        showMessage?.invoke("Devise not have bluetooth")
        return
      }
      if (!bluetoothAdapter.isEnabled) {
        showMessage?.invoke("Bluetooth not enabled")
        return
      }
      devices = bluetoothAdapter.bondedDevices.toList()
      bluetoothAdapter.cancelDiscovery()
      bluetoothDevices?.invoke(
        devices.map { device ->
          device.name + " (" + device.address + ")"
        }
      )
      val lastSelected = preferences.getString(LAST_BLUETOOTH_DEVICE_ADDRESS, "")
      devices.firstOrNull { device ->
        device.address == lastSelected
      }?.let { device ->
        bluetoothDevicePosition?.invoke(devices.indexOf(device))
        selectedBluetoothDevice = device
      }
    } catch (e: Exception) {
      showMessage?.invoke("Exception: $e")
    }
  }

  fun connectOrDisconnectBluetoothDevice() {
    if (socket?.isConnected != true) {
      connectBluetoothDevice()
    } else {
      disconnectBluetoothDevice()
    }
  }

  @SuppressLint("MissingPermission")
  private fun connectBluetoothDevice() {
    try {
      selectedBluetoothDevice?.let { device ->
        showMessage?.invoke("Start connection to ${selectedBluetoothDevice?.name}")
        val uuid = device.uuids[0].uuid
        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket?.connect()
        currentBluetoothConnectionStatus = true
        bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
        showMessage?.invoke("Connected")
        preferences
          .edit()
          .putString(LAST_BLUETOOTH_DEVICE_ADDRESS, device.address)
          .commit()
      } ?: run {
        currentBluetoothConnectionStatus = false
        bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
        showMessage?.invoke("Device not selected")
      }
    } catch (e: Exception) {
      currentBluetoothConnectionStatus = false
      bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
      showMessage?.invoke("Exception: $e")
    }
  }

  private fun disconnectBluetoothDevice() {
    try {
      socket?.close()
      socket = null
      currentBluetoothConnectionStatus = false
      bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
      showMessage?.invoke("Disconnected")
    } catch (e: Exception) {
      currentBluetoothConnectionStatus = false
      bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
      showMessage?.invoke("Exception: $e")
    }
  }

  fun subscribeToMessages(
    showMessage: (String) -> Unit,
  ) {
    this.showMessage = showMessage
  }

  fun subscribeToData(
    serialPortMessage: (String) -> Unit,
    bluetoothDevices: (List<String>) -> Unit,
    bluetoothConnectionStatus: (Boolean) -> Unit,
    bluetoothDevicePosition: (Int) -> Unit,
    loraSettings: ((LoraSettings) -> Unit),
  ) {
    this.serialPortMessage = serialPortMessage
    this.bluetoothDevices = bluetoothDevices
    this.bluetoothConnectionStatus = bluetoothConnectionStatus
    this.bluetoothDevicePosition = bluetoothDevicePosition
    this.loraSettings = loraSettings
    this.bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
  }

  fun selectBluetoothDevice(position: Int) {
    if (devices.size > position) {
      selectedBluetoothDevice = devices[position]
    }
  }

}
package com.romankryvolapov.loramessenger.helpers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.delay
import java.io.DataInputStream

class BluetoothHelper(
  private val preferences: SharedPreferences,
) {

  companion object {
    private const val LAST_BLUETOOTH_DEVICE_ADDRESS = "LAST_BLUETOOTH_DEVICE_ADDRESS"
  }

  private var socket: BluetoothSocket? = null
  private var devices: List<BluetoothDevice> = emptyList()
  private var selectedBluetoothDevice: BluetoothDevice? = null
  private var currentBluetoothConnectionStatus = false
  private var currentSerialPortMessage = ""

  private var showMessage: ((String) -> Unit)? = null
  private var serialPortMessage: ((String) -> Unit)? = null
  private var bluetoothDevices: ((List<String>) -> Unit)? = null
  private var bluetoothConnectionStatus: ((Boolean) -> Unit)? = null
  private var bluetoothDevicePosition: ((Int) -> Unit)? = null

  suspend fun setup() {
    while (true) {
      try {
        if (socket == null || socket?.isConnected != true) {
          delay(500)
        } else {
          val buffer = ByteArray(256)
          var bytes: Int
          val inputStream = socket?.inputStream
//          val outputStream = socket?.outputStream
          val dataInputStream = DataInputStream(inputStream)
//          val dataOutputStream = DataOutputStream(outputStream)
          bytes = dataInputStream.read(buffer)
          currentSerialPortMessage = String(buffer, 0, bytes).replace("\n", "").trim()
          serialPortMessage?.invoke(currentSerialPortMessage)
          delay(100)
        }
      } catch (e: Exception) {
        showMessage?.invoke("Exception: $e")
        delay(500)
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
  ) {
    this.serialPortMessage = serialPortMessage
    this.bluetoothDevices = bluetoothDevices
    this.bluetoothConnectionStatus = bluetoothConnectionStatus
    this.bluetoothDevicePosition = bluetoothDevicePosition
    this.bluetoothConnectionStatus?.invoke(currentBluetoothConnectionStatus)
    this.serialPortMessage?.invoke(currentSerialPortMessage)
  }

  fun selectBluetoothDevice(position: Int) {
    if (devices.size > position) {
      selectedBluetoothDevice = devices[position]
    }
  }

}
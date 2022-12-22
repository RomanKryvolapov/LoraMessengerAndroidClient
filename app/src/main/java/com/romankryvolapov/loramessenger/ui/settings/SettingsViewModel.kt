package com.romankryvolapov.loramessenger.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.loramessenger.helpers.BluetoothHelper
import com.romankryvolapov.loramessenger.models.LoraSettings
import com.romankryvolapov.loramessenger.models.LoraSettingsConst
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel(
  private val bluetoothHelper: BluetoothHelper,
  private val dispatcherIO: CoroutineDispatcher,
) : ViewModel() {

  private var connectOrDisconnectBluetoothDevice: Job? = null
  private var getPairedDevicesJob: Job? = null
  private var subscribeToBluetoothSerialJob: Job? = null
  private var currentLog = StringBuffer()

  private val circularArray: LinkedList<String> = LinkedList<String>()

  private var _loraSettingsFlow = MutableStateFlow(LoraSettings())
  val loraSettingsFlow: StateFlow<LoraSettings> = _loraSettingsFlow

  private var _bluetoothDevicesFlow = MutableStateFlow<List<String>>(emptyList())
  val bluetoothDevicesFlow: StateFlow<List<String>> = _bluetoothDevicesFlow

  private var _bluetoothDevicePositionFlow = MutableStateFlow(0)
  val bluetoothDevicePositionFlow: StateFlow<Int> = _bluetoothDevicePositionFlow

  private var _bluetoothConnectionStatusFlow = MutableStateFlow(false)
  val bluetoothConnectionStatusFlow: StateFlow<Boolean> = _bluetoothConnectionStatusFlow

  private var _connectionLogFlow = MutableStateFlow("")
  val connectionLogFlow: StateFlow<String> = _connectionLogFlow

  @SuppressLint("MissingPermission")
  fun getBluetoothDevices(context: Context) {
    getPairedDevicesJob?.cancel()
    getPairedDevicesJob = viewModelScope.launch(dispatcherIO) {
      bluetoothHelper.getPairedDevices(context)
    }
  }

  fun selectBluetoothDevice(position: Int) {
    viewModelScope.launch(dispatcherIO) {
      bluetoothHelper.selectBluetoothDevice(position)
    }
  }

  fun connectOrDisconnectBluetoothDevice() {
    connectOrDisconnectBluetoothDevice?.cancel()
    connectOrDisconnectBluetoothDevice = viewModelScope.launch(dispatcherIO) {
      bluetoothHelper.connectOrDisconnectBluetoothDevice(

      )
    }
  }

  fun subscribeToData() {
    subscribeToBluetoothSerialJob?.cancel()
    subscribeToBluetoothSerialJob = viewModelScope.launch(dispatcherIO) {
      bluetoothHelper.subscribeToData(
        serialPortMessage = { message ->
          circularArray.add(message)
          circularArray.forEach {
            currentLog.append("$it\n")
          }
          _connectionLogFlow.value = currentLog.toString()
          if (circularArray.size >= 5) {
            circularArray.removeFirst()
          }
          currentLog = StringBuffer()
        },
        bluetoothConnectionStatus = { _bluetoothConnectionStatusFlow.value = it },
        bluetoothDevices = { _bluetoothDevicesFlow.value = it },
        bluetoothDevicePosition = { _bluetoothDevicePositionFlow.value = it },
        loraSettings = { _loraSettingsFlow.value = it },
      )
    }
  }

  fun getLoraSettings() {
    bluetoothHelper.getLoraSettings()
  }

  fun setLoraSettings(const: LoraSettingsConst, value: String) {
    viewModelScope.launch(dispatcherIO) {
      bluetoothHelper.setLoraSettings(const, value)
    }
  }

  fun setDefaultLoraSettings() {

  }


}
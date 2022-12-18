package com.romankryvolapov.loramessenger.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.collection.CircularArray
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.loramessenger.helpers.BluetoothHelper
import com.romankryvolapov.loramessenger.models.LoraSettings
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

  private var loraSettings: LoraSettings? = null

  private val circularArray: CircularArray<String> = CircularArray<String>(3).apply {
    addFirst("")
    addFirst("")
    addFirst("")
  }

  private var _loraSettingsFlow = MutableStateFlow<LoraSettings?>(null)
  val loraSettingsFlow: StateFlow<LoraSettings?> = _loraSettingsFlow

  private var _bluetoothDevicesFlow = MutableStateFlow<List<String>>(emptyList())
  val bluetoothDevicesFlow: StateFlow<List<String>> = _bluetoothDevicesFlow

  private var _bluetoothDevicePositionFlow = MutableStateFlow(0)
  val bluetoothDevicePositionFlow: StateFlow<Int> = _bluetoothDevicePositionFlow

  private var _bluetoothConnectionStatusFlow = MutableStateFlow(false)
  val bluetoothConnectionStatusFlow: StateFlow<Boolean> = _bluetoothConnectionStatusFlow

  private var _connectionLogFlow = MutableStateFlow(listOf("", "", ""))
  val connectionLogFlow: StateFlow<List<String>> = _connectionLogFlow

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
          if (message.isNotEmpty() && message != "\n") {
            circularArray.addFirst(message)
            _connectionLogFlow.value = listOf<String>(
              circularArray.get(0),
              circularArray.get(1),
              circularArray.get(2),
            )
          }
        },
        bluetoothConnectionStatus = { _bluetoothConnectionStatusFlow.value = it },
        bluetoothDevices = { _bluetoothDevicesFlow.value = it },
        bluetoothDevicePosition = { _bluetoothDevicePositionFlow.value = it },
      )
    }
  }

  fun getLoraSettings() {

  }

  fun setDefaultLoraSettings() {

  }


}
package com.romankryvolapov.loramessenger.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.loramessenger.helpers.BluetoothHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
  private val bluetoothHelper: BluetoothHelper,
  private val dispatcherIO: CoroutineDispatcher,
) : ViewModel() {

  private var _showMessageFlow = MutableStateFlow<String?>(null)
  val showMessageFlow: StateFlow<String?> = _showMessageFlow

  fun setupBluetoothHelper(context: Context) {
    bluetoothHelper.setup()
    bluetoothHelper.subscribeToMessages(
      showMessage = { _showMessageFlow.value = it },
    )
    bluetoothHelper.connectToSavedBluetoothDevice(context)
  }

  fun clearMessage() {
    viewModelScope.launch(dispatcherIO) {
      _showMessageFlow.value = null
    }
  }
}
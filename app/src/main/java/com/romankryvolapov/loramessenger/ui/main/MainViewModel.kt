package com.romankryvolapov.loramessenger.ui.main

import androidx.lifecycle.ViewModel
import com.romankryvolapov.loramessenger.helpers.BluetoothHelper

class MainViewModel(
  private val bluetoothHelper: BluetoothHelper,
) : ViewModel() {

  fun setupBluetoothHelper() {
    bluetoothHelper.setup()
  }
}
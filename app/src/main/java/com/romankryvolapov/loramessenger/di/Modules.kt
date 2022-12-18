package com.romankryvolapov.loramessenger.di

import android.content.Context
import android.content.SharedPreferences
import com.romankryvolapov.loramessenger.helpers.BluetoothHelper
import com.romankryvolapov.loramessenger.ui.main.MainViewModel
import com.romankryvolapov.loramessenger.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DISPATCHER_IO = "DISPATCHER_IO"
private const val DISPATCHER_MAIN = "DISPATCHER_MAIN"
const val PREFERENCES_FILE = "com.romankryvolapov.loramessenger.preferences"
val applicationModule = module {
  viewModel<MainViewModel> {
    MainViewModel(
      bluetoothHelper = get() as BluetoothHelper,
      dispatcherIO = get(named(DISPATCHER_IO)) as CoroutineDispatcher,
    )
  }
  viewModel<SettingsViewModel> {
    SettingsViewModel(
      bluetoothHelper = get() as BluetoothHelper,
      dispatcherIO = get(named(DISPATCHER_IO)) as CoroutineDispatcher,
    )
  }
  single<BluetoothHelper>() {
    BluetoothHelper(
      preferences = get() as SharedPreferences,
    )
  }
  single<CoroutineDispatcher>(named(DISPATCHER_IO)) {
    Dispatchers.IO
  }
  single<CoroutineDispatcher>(named(DISPATCHER_MAIN)) {
    Dispatchers.Main
  }
  single<SharedPreferences>() {
    androidApplication().getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
  }
}

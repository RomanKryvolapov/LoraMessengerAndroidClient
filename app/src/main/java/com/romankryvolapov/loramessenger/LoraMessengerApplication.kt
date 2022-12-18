package com.romankryvolapov.loramessenger

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.romankryvolapov.loramessenger.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class LoraMessengerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    setupKoin()
  }

  private fun setupKoin() {
    startKoin {
      androidContext(this@LoraMessengerApplication)
      if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
      modules(listOf(applicationModule))
    }
  }

}
package com.romankryvolapov.loramessenger.helpers

import android.util.Log
import com.romankryvolapov.loramessenger.BuildConfig

fun Any.logDebug(message: String, tag: String) {
  if (BuildConfig.DEBUG) {
    Log.d(tag, message)
  }
}

fun Any.logError(message: String, tag: String) {
  if (BuildConfig.DEBUG) {
    Log.e(tag,message)
  }
}

fun logDebug(message: String, tag: String) {
  if (BuildConfig.DEBUG) {
    Log.d(tag, message)
  }
}

fun logError(message: String, tag: String) {
  if (BuildConfig.DEBUG) {
    Log.e(tag, message)
  }
}
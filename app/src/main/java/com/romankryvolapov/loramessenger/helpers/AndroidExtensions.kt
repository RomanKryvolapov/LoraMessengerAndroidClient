package com.romankryvolapov.loramessenger.helpers

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View?.showMessageSnackBar(message: String?) {
  when {
    this == null -> return
    message == null -> return
    else -> {
      Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .apply {
          view.translationY = -(this.context.resources.dpToPx(60f))
        }.show()
    }
  }
}

fun Resources.dpToPx(dp: Float): Float {
  return dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
package com.romankryvolapov.loramessenger.helpers

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
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

inline fun TextView.setDrawableRightTouch(
  crossinline clickListener: () -> Unit
) {
  val drawableRight = 2
  setOnTouchListener(View.OnTouchListener { _, event ->
    if (event.action == MotionEvent.ACTION_DOWN && event.rawX >= (right - compoundDrawables[drawableRight].bounds.width())) {
      clickListener.invoke()
      return@OnTouchListener true
    }
    false
  }
  )
}
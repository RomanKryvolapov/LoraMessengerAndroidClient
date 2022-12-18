package com.romankryvolapov.loramessenger.helpers

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

fun <T> Flow<T>.launch(scope: LifecycleCoroutineScope) {
  scope.launchWhenResumed { this@launch.flowOn(Dispatchers.Main).collect() }
}

fun <T> Flow<T>.launch(scope: CoroutineScope) {
  scope.launch { this@launch.flowOn(Dispatchers.IO).collect() }
}
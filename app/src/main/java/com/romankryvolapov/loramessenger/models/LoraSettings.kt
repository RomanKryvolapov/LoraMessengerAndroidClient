package com.romankryvolapov.loramessenger.models

data class LoraSettings(
  var channel: Int? = null,
  var airSpeed: Int? = null,
  var packetSize: Int? = null,
  var power: Int? = null,
  var encryptionKey: Int? = null,
  var address: Int? = null,
  var netID: Int? = null,
)

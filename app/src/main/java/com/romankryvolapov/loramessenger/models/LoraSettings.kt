package com.romankryvolapov.loramessenger.models

data class LoraSettings(
  val channel: Int? = null,
  val airDataRate: Int? = null,
  val packetSize: Int? = null,
  val power: Int? = null,
  val encryptionKey: Int? = null,
  val address: Int? = null,
  val netID: Int? = null,
)

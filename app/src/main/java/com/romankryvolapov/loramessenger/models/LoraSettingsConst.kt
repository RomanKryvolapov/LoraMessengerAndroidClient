package com.romankryvolapov.loramessenger.models

enum class LoraSettingsConst(val getCommand: String, val setCommand: String? = null) {
  ALL_SETTINGS("GetSettings"),
  CHANNEL("GetChannel", "SetChannel:"),
  AIR_SPEED("GetAirSpeed"),
  SERIAL_SPEED("GetSerialSpeed"),
  BOARD_SERIAL_SPEED("GetBoardSerialSpeed"),
  PACKET_SIZE("GetPacketSize"),
  POWER("GetPower"),
  KEY("GetKey"),
  NET_ID("GetNetID"),
  ADDRESS("GetAddress"),
  MODE("GetMode"),
  PARITY_BIT("GetParityBit"),
  TRANSMISSION_MODE("GetTransmissionMode"),
  REPEATER_MODE("GetRepeaterMode"),
  MONITORING_BEFORE_DATA_TRANSMISSION_MODE("GetMonitoringBeforeDataTransmissionMode"),
  WOR_MODE("GetWORMode"),
  WOR_CYCLE("GetWORCycle"),
  RSSI_TO_THE_END_OF_RECEIVED_DATA_MODE("GetRSSItoTheEndOfReceivedDataMode"),
  LISTENING_TO_AIR_AMBIENT_MODE("GetListeningToAirAmbientMode"),
  SAVING_SETTINGS_MEMORY("GetSavingSettingsMemory"),
  FREE_RAM_MEMORY("GetFreeRamMemory"),
}




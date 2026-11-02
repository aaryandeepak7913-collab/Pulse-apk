package com.aaryan.pulse.domain.entity

import android.bluetooth.BluetoothHidDevice

data class DeviceHidConnectionState(
    val state: Int = BluetoothHidDevice.STATE_DISCONNECTED,
    val deviceName: String = ""
)
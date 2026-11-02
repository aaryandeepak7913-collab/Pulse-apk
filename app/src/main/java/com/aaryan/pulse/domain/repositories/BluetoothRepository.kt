package com.aaryan.pulse.domain.repositories

import com.aaryan.pulse.domain.entity.DeviceEntity

interface BluetoothRepository {
    fun isBluetoothSupported(): Boolean
    fun isBluetoothEnabled(): Boolean
    fun getLocalDeviceName(): String
    fun getBluetoothPermissions(): Array<String>
    fun areBluetoothPermissionsGranted(): Boolean
    fun getBluetoothScanningPermissions(): Array<String>
    fun areBluetoothScanningPermissionsGranted(): Boolean
    fun getBondedDevices(): List<DeviceEntity>
    fun startDiscovery(): Boolean
    fun cancelDiscovery(): Boolean
    fun unpairDevice(address: String): Boolean
}
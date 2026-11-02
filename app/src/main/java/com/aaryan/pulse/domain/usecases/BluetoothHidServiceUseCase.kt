package com.aaryan.pulse.domain.usecases

import com.aaryan.pulse.common.utils.REMOTE_REPORT_ID
import com.aaryan.pulse.domain.entity.DeviceHidConnectionState
import com.aaryan.pulse.domain.repositories.BluetoothHidProfileRepository
import kotlinx.coroutines.flow.StateFlow

class BluetoothHidServiceUseCase(private val repository: BluetoothHidProfileRepository) {

    fun startHidProfile() {
        repository.startHidProfile()
    }

    fun stopHidProfile() {
        repository.stopHidProfile()
    }

    fun disconnectDevice(): Boolean {
        return repository.disconnectDevice()
    }

    fun getDeviceHidConnectionState(): StateFlow<DeviceHidConnectionState> {
        return repository.getDeviceHidConnectionState()
    }

    fun sendRemoteReport(bytes: ByteArray): Boolean {
        return repository.sendReport(REMOTE_REPORT_ID, bytes)
    }
}
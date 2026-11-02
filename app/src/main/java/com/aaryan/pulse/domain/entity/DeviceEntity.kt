package com.aaryan.pulse.domain.entity

import android.bluetooth.BluetoothClass.Device
import androidx.compose.ui.graphics.vector.ImageVector
import com.aaryan.pulse.common.utils.AppIcons

// Audio / camera minor classes that are never a TV
private val NON_TV_MINOR_CLASSES = setOf(
    Device.AUDIO_VIDEO_WEARABLE_HEADSET,
    Device.AUDIO_VIDEO_HANDSFREE,
    Device.AUDIO_VIDEO_MICROPHONE,
    Device.AUDIO_VIDEO_LOUDSPEAKER,
    Device.AUDIO_VIDEO_HEADPHONES,
    Device.AUDIO_VIDEO_PORTABLE_AUDIO,
    Device.AUDIO_VIDEO_CAR_AUDIO,
    Device.AUDIO_VIDEO_HIFI_AUDIO,
    Device.AUDIO_VIDEO_VIDEO_CAMERA,
    Device.AUDIO_VIDEO_CAMCORDER
)

// Names that clearly are not a TV (headphones, speakers, watches...)
private val NON_TV_NAME = Regex(
    "buds|airpod|headphone|headset|earbud|earphone|speaker|soundbar|sound bar|watch|boombox|\\bband\\b",
    RegexOption.IGNORE_CASE
)

// Many TVs do not report a proper Bluetooth class, so their name is checked too
private val TV_NAME = Regex(
    "\\btv\\b|\\[tv\\]|androidtv|googletv|firetv|mitv|bravia|webos|tizen|roku|chromecast|fire stick|firestick|" +
        "shield|hisense|tcl|vizio|projector|mi box|mibox|streaming|set.?top",
    RegexOption.IGNORE_CASE
)

data class DeviceEntity(
    val name: String,
    val macAddress: String,
    private val category: Int,
    private val deviceClass: Int = 0
) {
    /** True when the device looks like a TV / TV box / projector. */
    val isTv: Boolean = when {
        category == Device.Major.PHONE ||
            category == Device.Major.WEARABLE ||
            category == Device.Major.PERIPHERAL ||
            category == Device.Major.HEALTH ||
            category == Device.Major.TOY -> false
        NON_TV_NAME.containsMatchIn(name) -> false
        category == Device.Major.AUDIO_VIDEO -> deviceClass !in NON_TV_MINOR_CLASSES
        else -> TV_NAME.containsMatchIn(name)
    }

    val imageVector: ImageVector = if(isTv) AppIcons.Tv else when(category) {
        Device.Major.COMPUTER -> AppIcons.Computer
        Device.Major.PHONE -> AppIcons.Phone
        Device.Major.NETWORKING -> AppIcons.Networking
        Device.Major.AUDIO_VIDEO -> AppIcons.AudioVideo
        Device.Major.PERIPHERAL -> AppIcons.Peripheral
        Device.Major.IMAGING -> AppIcons.Imaging
        Device.Major.WEARABLE -> AppIcons.Wearable
        Device.Major.TOY -> AppIcons.Toy
        Device.Major.HEALTH -> AppIcons.Health
        Device.Major.UNCATEGORIZED -> AppIcons.Uncategorized
        else -> AppIcons.Bluetooth
    }
}

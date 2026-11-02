package com.aaryan.pulse.ui.screens

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aaryan.pulse.R
import com.aaryan.pulse.common.extensions.getActivity
import com.aaryan.pulse.common.utils.AppIcons
import com.aaryan.pulse.common.utils.checkBluetoothConnectPermission
import com.aaryan.pulse.ui.views.ActivationView

@Composable
fun BluetoothActivationScreen(
    isBluetoothEnabled: Boolean,
    openBluetoothDeviceSelectionScreen: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    DisposableEffect(isBluetoothEnabled) {
        if(isBluetoothEnabled) {
            openBluetoothDeviceSelectionScreen()
        }
        onDispose {}
    }

    ActivationView(
        topBarTitle = stringResource(id = R.string.activation),
        image = AppIcons.BluetoothDisabled,
        title = stringResource(id = R.string.bluetooth_disabled_info),
        message = stringResource(id = R.string.bluetooth_disabled_message),
        buttonIcon = AppIcons.Bluetooth,
        buttonText = stringResource(id = R.string.bluetooth_enabled_button),
        buttonOnClick = {
            if (checkBluetoothConnectPermission(context)) {
                (context.getActivity())?.let { activity ->
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    activity.startActivity(enableBtIntent)
                }
            }
        },
        openSettings = openSettings,
        modifier = modifier
    )
}
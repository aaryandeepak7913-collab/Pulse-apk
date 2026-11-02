package com.aaryan.pulse.ui.screens

import android.bluetooth.BluetoothHidDevice
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.itemsIndexed
import com.aaryan.pulse.ui.components.StaggeredEntrance
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.aaryan.pulse.ui.components.MaterialFilledTonalButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaryan.pulse.R
import com.aaryan.pulse.common.utils.AppIcons
import com.aaryan.pulse.common.utils.isMacAddress
import com.aaryan.pulse.domain.entity.DeviceEntity
import com.aaryan.pulse.domain.entity.DeviceHidConnectionState
import com.aaryan.pulse.ui.components.AppScaffold
import com.aaryan.pulse.ui.components.BluetoothPairingFromARemoteDeviceDropdownMenuItem
import com.aaryan.pulse.ui.components.BluetoothPairingFromAScannedDeviceDropdownMenuItem
import com.aaryan.pulse.ui.components.BluetoothPairingOverflowMenu
import com.aaryan.pulse.ui.components.DefaultElevatedCard
import com.aaryan.pulse.ui.components.EnterBluetoothAddressManuallyDropdownMenuItem
import com.aaryan.pulse.ui.components.HelpAction
import com.aaryan.pulse.ui.components.LoadingDialog
import com.aaryan.pulse.ui.components.SettingsAction
import com.aaryan.pulse.ui.components.SimpleDialog
import com.aaryan.pulse.ui.components.TemplateDialog
import com.aaryan.pulse.ui.components.TextLarge
import com.aaryan.pulse.ui.components.TextMedium
import com.aaryan.pulse.ui.components.TextNormal
import com.aaryan.pulse.ui.components.TextNormalError
import com.aaryan.pulse.ui.components.TextNormalSecondary
import com.aaryan.pulse.ui.views.DeviceItemView
import com.aaryan.pulse.ui.views.DevicesSelectionScreenHelpModalBottomSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private data class InternalDevice(val name: String = "", val macAddress: String = "")

@Composable
fun DevicesSelectionScreen(
    isBluetoothEnabled: Boolean,
    isBluetoothServiceStarted: Boolean,
    isBluetoothHidProfileRegistered: Boolean,
    bluetoothDeviceHidConnectionState: DeviceHidConnectionState,
    closeApp: () -> Unit,
    navigateUp: () -> Unit,
    startHidService: () -> Unit,
    stopHidService: () -> Unit,
    devicesFlow: StateFlow<List<DeviceEntity>>,
    findBondedDevices: () -> Unit,
    connectDevice: (String) -> Unit,
    disconnectDevice: () -> Unit,
    unpairDevice: (address: String) -> Boolean,
    autoConnectionDeviceAddressFlow: Flow<String>,
    saveAutoConnectionDeviceAddress: (String) -> Unit,
    openRemoteScreen: (deviceName: String) -> Unit,
    openPairingFromAScannedDeviceScreen: () -> Unit,
    openPairingFromARemoteDeviceScreen: () -> Unit,
    openSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val devices by devicesFlow.collectAsStateWithLifecycle()
    val autoConnectionDeviceAddress: String by autoConnectionDeviceAddressFlow.collectAsStateWithLifecycle("")

    var showBluetoothAddressDialog: Boolean by remember { mutableStateOf(false) }
    var showHelpBottomSheet: Boolean by remember { mutableStateOf(false) }
    var deviceToAutoConnect: InternalDevice by remember { mutableStateOf(InternalDevice()) }
    var deviceToUnpair: InternalDevice by remember { mutableStateOf(InternalDevice()) }

    DisposableEffect(isBluetoothEnabled) {
        if(!isBluetoothEnabled) {
            stopHidService()
            navigateUp()
        }
        onDispose {}
    }

    DisposableEffect(isBluetoothServiceStarted) {
        if(!isBluetoothServiceStarted && isBluetoothEnabled) {
            startHidService()
        }
        onDispose {}
    }

    DisposableEffect(bluetoothDeviceHidConnectionState.state) {
        if(bluetoothDeviceHidConnectionState.state == BluetoothHidDevice.STATE_CONNECTED) {
            openRemoteScreen(bluetoothDeviceHidConnectionState.deviceName)
        }
        onDispose {}
    }

    BackHandler(enabled = true, onBack = closeApp)

    LaunchedEffect(Unit) {
        findBondedDevices()
    }

    StatelessDevicesSelectionScreen(
        snackbarHostState = snackbarHostState,
        isBluetoothServiceStarted = isBluetoothServiceStarted,
        isBluetoothHidProfileRegistered = isBluetoothHidProfileRegistered,
        bluetoothDeviceHidConnectionState = bluetoothDeviceHidConnectionState,

        devices = devices,
        connectDevice = connectDevice,
        autoConnectDeviceAddress = autoConnectionDeviceAddress,
        openPairingFromAScannedDeviceScreen = openPairingFromAScannedDeviceScreen,
        openPairingFromARemoteDeviceScreen = openPairingFromARemoteDeviceScreen,
        openSettings = openSettings,

        failureHidConnectionDialog = {
            SimpleDialog(
                confirmButtonText = stringResource(id = R.string.retry),
                dismissButtonText = stringResource(id = R.string.close),
                onConfirmation = {
                    stopHidService()
                    startHidService()
                },
                onDismissRequest = closeApp,
                dialogTitle = stringResource(id = R.string.error),
                dialogText = stringResource(id = R.string.bluetooth_failed_to_register_app_message)
            )
        },
        connectingDialog = {
            LoadingDialog(
                title = stringResource(id = R.string.connection),
                message = stringResource(id = R.string.bluetooth_device_connecting_message, bluetoothDeviceHidConnectionState.deviceName),
                buttonText = stringResource(id = android.R.string.cancel),
                onButtonClick = disconnectDevice
            )
        },
        bluetoothAddressDialog = {
            BluetoothAddressDialog(
                connectDevice = {
                    showBluetoothAddressDialog = false
                    connectDevice(it)
                },
                close = {
                    showBluetoothAddressDialog = false
                }
            )
        },
        helpBottomSheet = {
            DevicesSelectionScreenHelpModalBottomSheet(
                onDismissRequest = { showHelpBottomSheet = false },
                //modifier = modifier
            )
        },
        unpairDeviceDialog = {
            SimpleDialog(
                confirmButtonText = stringResource(id = R.string.unpair),
                dismissButtonText = stringResource(id = android.R.string.cancel),
                onConfirmation = {
                    val msg = if(unpairDevice(deviceToUnpair.macAddress)) {
                        context.getString(R.string.unpair_device_successful)
                    } else {
                        context.getString(R.string.unpair_device_failure)
                    }
                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                    deviceToUnpair = InternalDevice()
                },
                onDismissRequest = {
                    deviceToUnpair = InternalDevice()
                },
                dialogTitle = deviceToUnpair.name,
                dialogText = stringResource(id = R.string.unpair_device_warning_message)
            )
        },
        autoConnectDeviceDialog = {
            if(autoConnectionDeviceAddress == deviceToAutoConnect.macAddress) {
                // Disabled auto connect
                SimpleDialog(
                    confirmButtonText = stringResource(id = android.R.string.ok),
                    dismissButtonText = stringResource(id = android.R.string.cancel),
                    onConfirmation = {
                        saveAutoConnectionDeviceAddress("")
                        deviceToAutoConnect = InternalDevice()
                    },
                    onDismissRequest = {
                        deviceToAutoConnect = InternalDevice()
                    },
                    dialogTitle = deviceToAutoConnect.name,
                    dialogText = stringResource(id = R.string.disabled_automatic_connection_message)
                )
            } else {
                // Enabled auto connect
                SimpleDialog(
                    confirmButtonText = stringResource(id = android.R.string.ok),
                    dismissButtonText = stringResource(id = android.R.string.cancel),
                    onConfirmation = {
                        saveAutoConnectionDeviceAddress(deviceToAutoConnect.macAddress)
                        deviceToAutoConnect = InternalDevice()
                    },
                    onDismissRequest = {
                        deviceToAutoConnect = InternalDevice()
                    },
                    dialogTitle = deviceToAutoConnect.name,
                    dialogText = stringResource(id = R.string.enabled_automatic_connection_message)
                )
            }
        },

        showBluetoothAddressDialog = showBluetoothAddressDialog,
        onShowBluetoothAddressDialogChanged = { showBluetoothAddressDialog = it },
        showHelpBottomSheet = showHelpBottomSheet,
        onShowHelpBottomSheetChanged = { showHelpBottomSheet = it },
        deviceToUnpair = deviceToUnpair,
        onDeviceToUnpairChanged = { deviceToUnpair = it },
        deviceToAutoConnect = deviceToAutoConnect,
        onDeviceToAutoConnectChanged = { deviceToAutoConnect = it },
        modifier = modifier
    )
}

@Composable
private fun StatelessDevicesSelectionScreen(
    snackbarHostState: SnackbarHostState,
    isBluetoothServiceStarted: Boolean,
    isBluetoothHidProfileRegistered: Boolean,
    bluetoothDeviceHidConnectionState: DeviceHidConnectionState,

    devices: List<DeviceEntity>,
    connectDevice: (String) -> Unit,
    autoConnectDeviceAddress: String,
    openPairingFromAScannedDeviceScreen: () -> Unit,
    openPairingFromARemoteDeviceScreen: () -> Unit,
    openSettings: () -> Unit,

    failureHidConnectionDialog: @Composable () -> Unit,
    connectingDialog: @Composable () -> Unit,
    helpBottomSheet: @Composable () -> Unit,
    bluetoothAddressDialog: @Composable () -> Unit,
    unpairDeviceDialog: @Composable () -> Unit,
    autoConnectDeviceDialog: @Composable () -> Unit,

    showBluetoothAddressDialog: Boolean,
    onShowBluetoothAddressDialogChanged: (Boolean) -> Unit,
    showHelpBottomSheet: Boolean,
    onShowHelpBottomSheetChanged: (Boolean) -> Unit,
    deviceToUnpair: InternalDevice,
    onDeviceToUnpairChanged: (InternalDevice) -> Unit,
    deviceToAutoConnect: InternalDevice,
    onDeviceToAutoConnectChanged: (InternalDevice) -> Unit,

    modifier: Modifier = Modifier
) {
    AppScaffold(
        title = stringResource(id = R.string.devices),
        modifier = modifier,
        topBarActions = {
            BluetoothPairingOverflowMenu { closeDropdownMenu: () -> Unit ->
                BluetoothPairingFromAScannedDeviceDropdownMenuItem(
                    openBluetoothPairingScreen = {
                        openPairingFromAScannedDeviceScreen()
                        closeDropdownMenu()
                    }
                )
                BluetoothPairingFromARemoteDeviceDropdownMenuItem(
                    openBluetoothPairingScreen = {
                        openPairingFromARemoteDeviceScreen()
                        closeDropdownMenu()
                    }
                )
                EnterBluetoothAddressManuallyDropdownMenuItem(
                    onClick = {
                        onShowBluetoothAddressDialogChanged(true)
                        closeDropdownMenu()
                    }
                )
            }

            HelpAction(showHelp = { onShowHelpBottomSheetChanged(!showHelpBottomSheet) })
            SettingsAction(openSettings)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        DevicesListView(
            devices = devices,
            connectDevice = connectDevice,
            autoConnectDeviceAddress = autoConnectDeviceAddress,
            autoConnect = onDeviceToAutoConnectChanged,
            unpairDevice = onDeviceToUnpairChanged,
            pairNewDevice = openPairingFromAScannedDeviceScreen,
            modifier = Modifier,
            contentPadding = innerPadding
        )

        // Dialog / ModalBottomSheet
        when {
            isBluetoothServiceStarted && !isBluetoothHidProfileRegistered -> failureHidConnectionDialog()
            bluetoothDeviceHidConnectionState.state == BluetoothHidDevice.STATE_CONNECTING -> connectingDialog()
            showHelpBottomSheet -> helpBottomSheet()
            showBluetoothAddressDialog -> bluetoothAddressDialog()
            deviceToUnpair.macAddress != "" -> unpairDeviceDialog()
            deviceToAutoConnect.macAddress != "" -> autoConnectDeviceDialog()
        }
    }
}

@Composable
private fun DevicesListView(
    devices: List<DeviceEntity>,
    connectDevice: (String) -> Unit,
    autoConnectDeviceAddress: String,
    autoConnect: (InternalDevice) -> Unit,
    unpairDevice: (InternalDevice) -> Unit,
    pairNewDevice: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var showAllDevices: Boolean by rememberSaveable { mutableStateOf(false) }

    val visibleDevices = if(showAllDevices) devices else devices.filter { it.isTv }
    val cardShape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius))
    val horizontalPadding = dimensionResource(id = R.dimen.padding_max)

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        item {
            StaggeredEntrance(index = 0) {
            HomeHeader(
                modifier = Modifier.padding(
                    start = horizontalPadding + dimensionResource(id = R.dimen.padding_small),
                    end = horizontalPadding,
                    top = dimensionResource(id = R.dimen.padding_large)
                )
            )
            }
        }

        item {
            StaggeredEntrance(index = 1) {
            Row(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                FilterPill(
                    text = stringResource(id = R.string.filter_tvs),
                    selected = !showAllDevices,
                    onClick = { showAllDevices = false }
                )
                FilterPill(
                    text = stringResource(id = R.string.filter_all_devices),
                    selected = showAllDevices,
                    onClick = { showAllDevices = true }
                )
            }
            }
        }

        if(visibleDevices.isEmpty()) {
            item {
                EmptyTvsView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                )
            }
        }

        itemsIndexed(visibleDevices) { index, device ->
            StaggeredEntrance(index = index + 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .clip(cardShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { connectDevice(device.macAddress) }
            ) {
                DeviceItemView(
                    name = device.name,
                    macAddress = device.macAddress,
                    icon = device.imageVector,
                    isAutoConnectDeviceAddress = autoConnectDeviceAddress == device.macAddress,
                    autoConnect = { autoConnect(InternalDevice(device.name, device.macAddress)) },
                    unpair = { unpairDevice(InternalDevice(device.name, device.macAddress)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(id = R.dimen.padding_large),
                            top = dimensionResource(id = R.dimen.padding_large),
                            bottom = dimensionResource(id = R.dimen.padding_large),
                            end = dimensionResource(id = R.dimen.padding_small)
                        )
                )
            }
            }
        }

        item {
            StaggeredEntrance(index = visibleDevices.size + 2) {
            MaterialFilledTonalButton(
                onClick = pairNewDevice,
                text = stringResource(id = R.string.pair_new_tv),
                icon = AppIcons.TVChannelIncrease,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            )
            }
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.select_your_tv),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextNormalSecondary(
            text = stringResource(id = R.string.select_tv_subtitle),
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if(selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(250),
        label = "pillBackground"
    )
    val content by animateColorAsState(
        targetValue = if(selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "pillContent"
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_max),
                vertical = dimensionResource(id = R.dimen.padding_medium)
            ),
        contentAlignment = Alignment.Center
    ) {
        TextNormal(text = text, color = content)
    }
}

@Composable
private fun EmptyTvsView(modifier: Modifier = Modifier) {
    DefaultElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_max) * 2),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = AppIcons.Tv,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextMedium(text = stringResource(id = R.string.no_tvs_found))
            TextNormalSecondary(
                text = stringResource(id = R.string.no_tvs_found_message),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BluetoothAddressDialog(
    connectDevice: (String) -> Unit,
    close: () -> Unit,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }
    val textState = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TemplateDialog(
        title = {
            TextLarge(text = stringResource(id = R.string.bluetooth_address))
        },
        content = {
            Column {
                TextNormal(text = stringResource(id = R.string.bluetooth_address_prompt))
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(isMacAddress(textState.value)) {
                                connectDevice(textState.value)
                            } else {
                                showError.value = true
                            }
                        }
                    ),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.keyboard_key_corner_radius)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent
                    )
                )
                if(showError.value) {
                    TextNormalError(text = stringResource(id = R.string.bluetooth_address_wrong_format))
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.connection),
        onConfirmation = {
            if(isMacAddress(textState.value)) {
                connectDevice(textState.value)
            } else {
                showError.value = true
            }
        },
        dismissButtonText = stringResource(id = R.string.close),
        onDismissRequest = close,
        modifier = modifier
    )
}
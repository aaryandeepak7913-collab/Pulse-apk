package com.aaryan.pulse.ui.screens

import android.bluetooth.BluetoothHidDevice
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaryan.pulse.R
import com.aaryan.pulse.common.utils.MOUSE_SPEED_DEFAULT_VALUE
import com.aaryan.pulse.common.utils.REMOTE_INPUT_NONE
import com.aaryan.pulse.common.utils.getKeyboardLayout
import com.aaryan.pulse.domain.entity.DeviceHidConnectionState
import com.aaryan.pulse.domain.entity.RemoteNavigationEntity
import com.aaryan.pulse.domain.entity.remoteInput.MouseAction
import com.aaryan.pulse.domain.entity.remoteInput.RemoteInput
import com.aaryan.pulse.domain.entity.remoteInput.keyboard.KeyboardLanguage
import com.aaryan.pulse.domain.entity.remoteInput.keyboard.virtualKeyboard.VirtualKeyboardLayout
import com.aaryan.pulse.presentation.viewmodel.SettingsViewModel
import com.aaryan.pulse.ui.components.AppScaffold
import com.aaryan.pulse.ui.components.BrightnessDecDropdownMenuItem
import com.aaryan.pulse.ui.components.BrightnessIncDropdownMenuItem
import com.aaryan.pulse.ui.components.DirectionButtonsAction
import com.aaryan.pulse.ui.components.DisconnectDropdownMenuItem
import com.aaryan.pulse.ui.components.FadeAnimatedContent
import com.aaryan.pulse.ui.components.HelpDropdownMenuItem
import com.aaryan.pulse.ui.components.KeyboardAction
import com.aaryan.pulse.ui.components.LoadingDialog
import com.aaryan.pulse.ui.components.MoreOverflowMenu
import com.aaryan.pulse.ui.components.MouseAction
import com.aaryan.pulse.ui.components.SettingsDropdownMenuItem
import com.aaryan.pulse.ui.views.RemoteScreenHelpModalBottomSheet
import com.aaryan.pulse.ui.views.keyboard.AdvancedKeyboardLayoutView
import com.aaryan.pulse.ui.views.keyboard.VirtualKeyboardView
import com.aaryan.pulse.ui.views.mouse.MousePadLayout
import com.aaryan.pulse.ui.views.remote.MinimalistRemoteView
import com.aaryan.pulse.ui.views.remote.RemoteView
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelDialog
import com.aaryan.pulse.ui.views.remoteNavigation.RemoteDirectionalPadNavigation
import com.aaryan.pulse.ui.views.remoteNavigation.RemoteSwipeNavigation

private enum class NavigationToggle {
    DIRECTION,
    MOUSE
}

@Composable
fun RemoteScreen(
    deviceName: String,
    isBluetoothServiceStarted: Boolean,
    bluetoothDeviceHidConnectionState: DeviceHidConnectionState,
    navigateUp: () -> Unit,
    closeApp: () -> Unit,
    openSettings: () -> Unit,
    settingsViewModel: SettingsViewModel,
    disconnectDevice: () -> Unit,
    forceDisconnectDevice: () -> Unit,
    sendRemoteKeyReport: (ByteArray) -> Unit,
    sendMouseKeyReport: (MouseAction, Float, Float, Float) -> Unit,
    sendKeyboardKeyReport: (ByteArray) -> Unit,
    sendTextReport: (String, VirtualKeyboardLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current

    var navigationToggle by rememberSaveable { mutableStateOf(NavigationToggle.DIRECTION) }

    var showKeyboard: Boolean by remember { mutableStateOf(false) }

    var showHelpBottomSheet: Boolean by remember { mutableStateOf(false) }

    val useMinimalistRemote: Boolean by settingsViewModel.useMinimalistRemote
        .collectAsStateWithLifecycle(initialValue = false)

    val remoteNavigationMode: RemoteNavigationEntity by settingsViewModel.remoteNavigation
        .collectAsStateWithLifecycle(initialValue = RemoteNavigationEntity.D_PAD)

    BackHandler(enabled = true, onBack = closeApp)

    DisposableEffect(isBluetoothServiceStarted) {
        if(!isBluetoothServiceStarted) {
            navigateUp()
        }
        onDispose {}
    }

    DisposableEffect(bluetoothDeviceHidConnectionState.state) {
        if(bluetoothDeviceHidConnectionState.state == BluetoothHidDevice.STATE_DISCONNECTED) {
            navigateUp()
        }
        onDispose {}
    }

    StatelessRemoteScreen(
        deviceName = deviceName,
        bluetoothDeviceHidConnectionState = bluetoothDeviceHidConnectionState,
        forceDisconnectDevice = forceDisconnectDevice,
        showHelpBottomSheet = showHelpBottomSheet,
        onShowHelpBottomSheetChanged = { showHelpBottomSheet = it },
        isLandscapeMode = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        topBarActions = {
            TopBarActions(
                openSettings = openSettings,
                disconnectDevice = disconnectDevice,
                navigationToggle = navigationToggle,
                onNavigationToggleChanged = { navigationToggle = it },
                showKeyboard = showKeyboard,
                onShowKeyboardChanged = { showKeyboard = it },
                showHelpBottomSheet = showHelpBottomSheet,
                onShowHelpBottomSheetChanged = { showHelpBottomSheet = it },
                sendRemoteKeyReport = sendRemoteKeyReport,
                showBrightnessButtons = !useMinimalistRemote
            )
        },
        remoteLayout = {
            if(useMinimalistRemote) {
                var showTVChannelButtons: Boolean by remember { mutableStateOf(false) }

                MinimalistRemoteView(
                    sendRemoteKeyReport = sendRemoteKeyReport,
                    showTVChannelButtons = {
                        showTVChannelButtons = !showTVChannelButtons
                    },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_normal))
                )

                if(showTVChannelButtons) {
                    TVChannelDialog(
                        sendRemoteKeyReport = sendRemoteKeyReport,
                        sendNumberKeyReport = sendKeyboardKeyReport,
                        onDismissRequest = {
                            showTVChannelButtons = false
                        }
                    )
                }
            } else {
                RemoteView(
                    sendRemoteKeyReport = sendRemoteKeyReport,
                    sendNumberKeyReport = sendKeyboardKeyReport,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_normal))
                )
            }
        },
        navigationLayout = {
            NavigationLayout(
                settingsViewModel = settingsViewModel,
                remoteNavigationMode = remoteNavigationMode,
                sendRemoteKeyReport = sendRemoteKeyReport,
                sendMouseKeyReport = sendMouseKeyReport,
                navigationToggle = navigationToggle
            )
        },
        keyboardView = {
            KeyboardModalSheet(
                settingsViewModel = settingsViewModel,
                sendKeyboardKeyReport = sendKeyboardKeyReport,
                sendTextReport = sendTextReport,
                showKeyboard = showKeyboard,
                onShowKeyboardChanged = { showKeyboard = it }
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatelessRemoteScreen(
    deviceName: String,
    bluetoothDeviceHidConnectionState: DeviceHidConnectionState,
    forceDisconnectDevice: () -> Unit,
    showHelpBottomSheet: Boolean,
    onShowHelpBottomSheetChanged: (Boolean) -> Unit,
    isLandscapeMode: Boolean,
    topBarActions: @Composable (RowScope.() -> Unit),
    remoteLayout: @Composable () -> Unit,
    navigationLayout: @Composable () -> Unit,
    keyboardView: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        title = deviceName,
        modifier = modifier,
        scrollBehavior = null,
        topBarActions = topBarActions
    ) { innerPadding ->

        if(isLandscapeMode) {
            RemoteLandscapeView(
                remoteLayout = remoteLayout,
                navigationLayout = navigationLayout,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            RemotePortraitView(
                remoteLayout = remoteLayout,
                navigationLayout = navigationLayout,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }

        // Dialog / ModalBottomSheet
        when {
            bluetoothDeviceHidConnectionState.state == BluetoothHidDevice.STATE_CONNECTING -> {
                LoadingDialog(
                    title = stringResource(id = R.string.connection),
                    message = stringResource(id = R.string.bluetooth_device_disconnecting_message, bluetoothDeviceHidConnectionState.deviceName),
                    buttonText = stringResource(id = R.string.disconnect),
                    onButtonClick = forceDisconnectDevice
                )
            }
            showHelpBottomSheet -> {
                RemoteScreenHelpModalBottomSheet(
                    onDismissRequest = { onShowHelpBottomSheetChanged(false) },
                    modifier = modifier
                )
            }
            else -> {
                keyboardView()
            }
        }
    }
}

@Composable
private fun RemoteLandscapeView(
    remoteLayout: @Composable () -> Unit,
    navigationLayout: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var rowSize by remember { mutableStateOf(Size.Zero) }

    Row(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates -> rowSize = layoutCoordinates.size.toSize() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = with(LocalDensity.current) { (0.5f * rowSize.width).toDp() })
                .align(Alignment.CenterVertically)
                .padding(
                    start = dimensionResource(id = R.dimen.padding_large),
                    top = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource(id = R.dimen.padding_large)
                ),
        ) {
            navigationLayout()
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center
        ) {
            remoteLayout()
        }
    }
}

@Composable
private fun RemotePortraitView(
    remoteLayout: @Composable () -> Unit,
    navigationLayout: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .heightIn(
                    max = with(LocalConfiguration.current) {
                        if (screenHeightDp >= screenWidthDp * 1.9f) // Si la hauteur de l'appareil est suffisamment haute par rapport à sa largeur (ratio ~ 1/2)
                            screenWidthDp.dp // On peut se permettre de prendre pour hauteur la largeur de l'écran
                        else // Sinon
                            (screenHeightDp * 0.50).dp // On prend 50% de la hauteur de l'écran
                    }
                )
                .align(Alignment.CenterHorizontally),
        ) {
            remoteLayout()
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = dimensionResource(id = R.dimen.padding_large),
                    end = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource(id = R.dimen.padding_large)
                ),
            contentAlignment = Alignment.Center
        ) {
            navigationLayout()
        }
    }
}

@Composable
private fun NavigationLayout(
    settingsViewModel: SettingsViewModel,
    remoteNavigationMode: RemoteNavigationEntity,
    sendRemoteKeyReport: (bytes: ByteArray) -> Unit,
    sendMouseKeyReport: (input: MouseAction, x: Float, y: Float, wheel: Float) -> Unit,
    navigationToggle: NavigationToggle
) {
    FadeAnimatedContent(targetState = navigationToggle) {
        when(it) {
            NavigationToggle.DIRECTION -> {
                if(remoteNavigationMode == RemoteNavigationEntity.D_PAD) {
                    RemoteDirectionalPadNavigation(
                        sendRemoteKeyReport = sendRemoteKeyReport,
                        modifier = Modifier.aspectRatio(1f)
                    )
                } else {
                    RemoteSwipeNavigation(
                        sendRemoteKeyReport = sendRemoteKeyReport,
                        modifier = Modifier
                    )
                }
            }

            NavigationToggle.MOUSE -> {
                val mouseSpeed: Float by settingsViewModel.mouseSpeed
                    .collectAsStateWithLifecycle(initialValue = MOUSE_SPEED_DEFAULT_VALUE)

                val shouldInvertMouseScrollingDirection: Boolean by settingsViewModel.shouldInvertMouseScrollingDirection
                    .collectAsStateWithLifecycle(initialValue = false)

                val useGyroscope: Boolean by settingsViewModel.useGyroscope.collectAsStateWithLifecycle(initialValue = false)

                MousePadLayout(
                    mouseSpeed = mouseSpeed,
                    shouldInvertMouseScrollingDirection = shouldInvertMouseScrollingDirection,
                    useGyroscope = useGyroscope,
                    sendMouseInput = sendMouseKeyReport,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun KeyboardModalSheet(
    settingsViewModel: SettingsViewModel,
    sendKeyboardKeyReport: (ByteArray) -> Unit,
    sendTextReport: (String, VirtualKeyboardLayout) -> Unit,
    showKeyboard: Boolean,
    onShowKeyboardChanged: (Boolean) -> Unit,
) {
    val useAdvancedKeyboard: Boolean by settingsViewModel.useAdvancedKeyboard
        .collectAsStateWithLifecycle(initialValue = false)

    val keyboardLanguage: KeyboardLanguage by settingsViewModel.keyboardLanguage
        .collectAsStateWithLifecycle(initialValue = KeyboardLanguage.ENGLISH_US)

    if (useAdvancedKeyboard) {
        AdvancedKeyboardLayoutView(
            keyboardLanguage = keyboardLanguage,
            sendKeyboardKeyReport = sendKeyboardKeyReport,
            showKeyboardBottomSheet = showKeyboard,
            onShowKeyboardBottomSheetChanged = onShowKeyboardChanged
        )
    } else {
        val mustClearInputField: Boolean by settingsViewModel.mustClearInputField
            .collectAsStateWithLifecycle(initialValue = false)

        var virtualKeyboardLayout: VirtualKeyboardLayout by remember {
            mutableStateOf(getKeyboardLayout(keyboardLanguage))
        }

        LaunchedEffect(keyboardLanguage) {
            virtualKeyboardLayout = getKeyboardLayout(keyboardLanguage)
        }

        VirtualKeyboardView(
            mustClearInputField = mustClearInputField,
            sendKeyboardKeyReport = sendKeyboardKeyReport,
            sendTextReport = { sendTextReport(it, virtualKeyboardLayout) },
            showKeyboardBottomSheet = showKeyboard,
            onShowKeyboardBottomSheetChanged = onShowKeyboardChanged
        )
    }
}

@Composable
private fun TopBarActions(
    openSettings: () -> Unit,
    disconnectDevice: () -> Unit,
    navigationToggle: NavigationToggle,
    onNavigationToggleChanged: (NavigationToggle) -> Unit,
    showKeyboard: Boolean,
    onShowKeyboardChanged: (Boolean) -> Unit,
    showHelpBottomSheet: Boolean,
    onShowHelpBottomSheetChanged: (Boolean) -> Unit,
    sendRemoteKeyReport: (ByteArray) -> Unit,
    showBrightnessButtons: Boolean
) {

    FadeAnimatedContent(targetState = navigationToggle) {
        when (it) {
            NavigationToggle.MOUSE -> {
                DirectionButtonsAction(
                    showDirectionButtons = {
                        onNavigationToggleChanged(NavigationToggle.DIRECTION)
                    }
                )
            }

            NavigationToggle.DIRECTION -> {
                MouseAction(
                    showMousePad = {
                        onNavigationToggleChanged(NavigationToggle.MOUSE)
                    }
                )
            }
        }
    }

    KeyboardAction(
        showKeyboard = {
            onShowKeyboardChanged(!showKeyboard)
        }
    )

    MoreOverflowMenu { closeDropdownMenu: () -> Unit ->
        if(showBrightnessButtons) {
            BrightnessIncDropdownMenuItem(
                touchDown = {
                    sendRemoteKeyReport(RemoteInput.REMOTE_INPUT_BRIGHTNESS_INC)
                },
                touchUp = {
                    sendRemoteKeyReport(REMOTE_INPUT_NONE)
                }
            )
            BrightnessDecDropdownMenuItem(
                touchDown = {
                    sendRemoteKeyReport(RemoteInput.REMOTE_INPUT_BRIGHTNESS_DEC)
                },
                touchUp = {
                    sendRemoteKeyReport(REMOTE_INPUT_NONE)
                }
            )
        }
        DisconnectDropdownMenuItem(
            disconnect = {
                closeDropdownMenu()
                disconnectDevice()
            }
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
        HelpDropdownMenuItem(
            showHelp = {
                closeDropdownMenu()
                onShowHelpBottomSheetChanged(!showHelpBottomSheet)
            }
        )
        SettingsDropdownMenuItem(
            showSettingsScreen = {
                closeDropdownMenu()
                openSettings()
            }
        )
    }
}
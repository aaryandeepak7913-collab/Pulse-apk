package com.aaryan.pulse.ui.views.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.aaryan.pulse.R
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.BackButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.BrightnessVerticalButtons
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.HomeButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.MenuButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.PowerButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.VolumeMuteButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.VolumeVerticalButtons

@Composable
fun RemoteView(
    sendRemoteKeyReport: (bytes: ByteArray) -> Unit,
    sendNumberKeyReport: (bytes: ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CircleShape
    val padding: Dp = dimensionResource(id = R.dimen.padding_medium)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            VolumeVerticalButtons(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(2f).padding(padding), shape = shape)
            VolumeMuteButton(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(1f).padding(padding), shape = shape)
            BackButton(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(1f).padding(padding), shape = shape)
        }
        Column(modifier = Modifier.weight(1f)) {
            HomeButton(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(1f).padding(padding), shape = shape)
            MenuButton(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(1f).padding(padding), shape = shape)
        }
        Column(modifier = Modifier.weight(1f)) {
            BrightnessVerticalButtons(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(2f).padding(padding), shape = shape)
            PowerButton(sendReport = sendRemoteKeyReport, modifier = Modifier.weight(1f).padding(padding), shape = shape)
        }
    }
}

@Composable
fun MinimalistRemoteView(
    sendRemoteKeyReport: (bytes: ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CircleShape
    val padding: Dp = dimensionResource(id = R.dimen.padding_medium)

    Column(modifier = modifier) {
            sendReport = sendRemoteKeyReport,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(padding),
            shape = shape
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(padding)) {
                VolumeVerticalButtons(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier.align(Alignment.Start),
                    shape = shape
                )
            }

            Column(
                modifier = Modifier.weight(2f)
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    VolumeMuteButton(
                        sendReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .weight(1f)
                            .padding(padding),
                        shape = shape
                    )

                        sendReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .weight(1f)
                            .padding(padding),
                        shape = shape
                    )
                }
                    touchDown = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )
            }

            Column(modifier = Modifier.weight(1f).padding(padding)) {
                BrightnessVerticalButtons(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier.align(Alignment.End),
                    shape = shape
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            BackButton(
                sendReport = sendRemoteKeyReport,
                modifier = Modifier
                    .weight(1f)
                    .padding(padding),
                shape = shape
            )
            HomeButton(
                sendReport = sendRemoteKeyReport,
                modifier = Modifier
                    .weight(1f)
                    .padding(padding),
                shape = shape
            )
            MenuButton(
                sendReport = sendRemoteKeyReport,
                modifier = Modifier
                    .weight(1f)
                    .padding(padding),
                shape = shape
            )
            PowerButton(
                sendReport = sendRemoteKeyReport,
                modifier = Modifier
                    .weight(1f)
                    .padding(padding),
                shape = shape
            )
        }
    }
}
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
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.ClosedCaptionsButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.HomeButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.MenuButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.MultimediaLayout
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.PowerButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton0
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton1
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton2
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton3
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton4
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton5
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton6
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton7
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton8
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelButton9
import com.aaryan.pulse.ui.views.remote.buttonsLayouts.TVChannelVerticalButtons
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

    Column(modifier = modifier) {
        MultimediaLayout(
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
                .weight(4f),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                VolumeVerticalButtons(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .weight(2f)
                        .padding(padding)
                        .align(Alignment.Start),
                    shape = shape
                )

                VolumeMuteButton(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                BackButton(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TVChannelButton1(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton4(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton7(
                    sendReport = sendNumberKeyReport,
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
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TVChannelButton2(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton5(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton8(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton0(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TVChannelButton3(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton6(
                    sendReport = sendNumberKeyReport,
                    modifier = Modifier
                        .weight(1f)
                        .padding(padding),
                    shape = shape
                )

                TVChannelButton9(
                    sendReport = sendNumberKeyReport,
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
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                TVChannelVerticalButtons(
                    sendReport = sendRemoteKeyReport,
                    modifier = Modifier
                        .weight(2f)
                        .padding(padding)
                        .align(Alignment.End),
                    shape = shape
                )

                ClosedCaptionsButton(
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
}

@Composable
fun MinimalistRemoteView(
    sendRemoteKeyReport: (bytes: ByteArray) -> Unit,
    showTVChannelButtons: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CircleShape
    val padding: Dp = dimensionResource(id = R.dimen.padding_medium)

    Column(modifier = modifier) {
        MultimediaLayout(
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

                    ClosedCaptionsButton(
                        sendReport = sendRemoteKeyReport,
                        modifier = Modifier
                            .weight(1f)
                            .padding(padding),
                        shape = shape
                    )
                }
                TVChannelButton(
                    touchDown = {},
                    touchUp = showTVChannelButtons,
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
package com.aaryan.pulse.ui.views.remoteNavigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aaryan.pulse.R
import com.aaryan.pulse.common.utils.AppIcons
import com.aaryan.pulse.common.utils.ArcShape
import com.aaryan.pulse.common.utils.REMOTE_INPUT_NONE
import com.aaryan.pulse.domain.entity.remoteInput.RemoteInput
import com.aaryan.pulse.ui.components.RemoteButtonSurface
import com.aaryan.pulse.ui.components.StatefulRemoteButton
import com.aaryan.pulse.ui.components.pressGlow

private val TopArcShape = ArcShape(-45f, -90f)
private val BottomArcShape = ArcShape(45f, 90f)
private val LeftArcShape = ArcShape(135f, 90f)
private val RightArcShape = ArcShape(-45f, 90f)

@Composable
fun RemoteDirectionalPadNavigation(
    sendRemoteKeyReport: (bytes: ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = dimensionResource(id = R.dimen.elevation_1)
) {
    val gap = BorderStroke(2.dp, MaterialTheme.colorScheme.background)

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            // ---- Top ----
            RemoteButtonSurface(
                modifier = Modifier.fillMaxSize(),
                shape = TopArcShape,
                elevation = elevation,
                border = gap
            ) {
                DPadButton(sendReport = sendRemoteKeyReport, bytes = RemoteInput.REMOTE_INPUT_MENU_UP)
            }

            // ---- Bottom ----
            RemoteButtonSurface(
                modifier = Modifier.fillMaxSize(),
                shape = BottomArcShape,
                elevation = elevation,
                border = gap
            ) {
                DPadButton(sendReport = sendRemoteKeyReport, bytes = RemoteInput.REMOTE_INPUT_MENU_DOWN)
            }

            // ---- Left ----
            RemoteButtonSurface(
                modifier = Modifier.fillMaxSize(),
                shape = LeftArcShape,
                elevation = elevation,
                border = gap
            ) {
                DPadButton(sendReport = sendRemoteKeyReport, bytes = RemoteInput.REMOTE_INPUT_MENU_LEFT)
            }

            // ---- Right ----
            RemoteButtonSurface(
                modifier = Modifier.fillMaxSize(),
                shape = RightArcShape,
                elevation = elevation,
                border = gap
            ) {
                DPadButton(sendReport = sendRemoteKeyReport, bytes = RemoteInput.REMOTE_INPUT_MENU_RIGHT)
            }

            // ---- Center ----
            RemoteButtonSurface(
                modifier = Modifier.fillMaxSize(0.3333f),
                shape = CircleShape,
                elevation = elevation,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                border = gap
            ) {
                DPadButton(sendReport = sendRemoteKeyReport, bytes = RemoteInput.REMOTE_INPUT_MENU_PICK)
            }

        }

        // ---- Icons ----
        Column(modifier = Modifier.wrapContentSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Spacer(Modifier.weight(1f).padding(8.dp))
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    imageVector = AppIcons.Up,
                    contentDescription = stringResource(id = R.string.up),
                )
                Spacer(Modifier.weight(1f).padding(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    imageVector = AppIcons.Left,
                    contentDescription = stringResource(id = R.string.left)
                )
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    imageVector = AppIcons.Pick,
                    contentDescription = stringResource(id = R.string.pick),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    imageVector = AppIcons.Right,
                    contentDescription = stringResource(id = R.string.right)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(0.dp).weight(1f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Spacer(Modifier.weight(1f).padding(8.dp))
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    imageVector = AppIcons.Down,
                    contentDescription = stringResource(id = R.string.down)
                )
                Spacer(Modifier.weight(1f).padding(8.dp))
            }
        }
    }
}

@Composable
private fun DPadButton(
    bytes: ByteArray,
    sendReport: (bytes: ByteArray) -> Unit
) {
    StatefulRemoteButton(
        touchDown = { sendReport(bytes) },
        touchUp = { sendReport(REMOTE_INPUT_NONE) }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pressGlow(it, maxAlpha = 0.22f)
                .clickable(
                    interactionSource = it,
                    indication = LocalIndication.current,
                    onClick = {}
                )
        )
    }
}
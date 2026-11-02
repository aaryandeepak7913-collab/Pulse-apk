package com.aaryan.pulse.ui.views.mouse

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.aaryan.pulse.ui.components.ButtonContentTemplate
import com.aaryan.pulse.ui.components.RemoteButtonSurface
import com.aaryan.pulse.ui.components.StatefulRemoteButton
import com.aaryan.pulse.ui.components.pressGlow

@Composable
fun MouseButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    RemoteButtonSurface(
        modifier = modifier,
        shape = shape
    ) {
        StatefulRemoteButton(
            touchDown = touchDown,
            touchUp = touchUp
        ) { interactionSource ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .pressGlow(interactionSource)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = {}
                    )
            )
        }
    }
}

@Composable
fun ScrollMouseButton(
    touchDown: () -> Unit,
    touchUp: () -> Unit,
    image: ImageVector,
    contentDescription: String,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    RemoteButtonSurface(
        modifier = modifier,
        shape = shape
    ) {
        ButtonContentTemplate(
            touchDown = touchDown,
            touchUp = touchUp,
            shape = shape
        ) {
            Icon(
                imageVector = image,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(0.50f)
            )
        }
    }
}
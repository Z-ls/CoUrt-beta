package it.polito.mad.court.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun FloatingButton(
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End),
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}
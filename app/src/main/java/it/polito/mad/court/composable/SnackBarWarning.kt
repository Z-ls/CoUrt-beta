package it.polito.mad.court.composable

import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SnackBarWarning(
    showSnackBar: Boolean,
    message: String,
    actionLabel: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showSnackBar)
        Snackbar(
            modifier = modifier,
            action = {
                TextButton(onClick = action) {
                    Text(text = actionLabel)
                }
            }
        ) {
            Text(text = message)
        }
}
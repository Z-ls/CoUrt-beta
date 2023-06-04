package it.polito.mad.court.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogComment(
    user: User,
    res: Reservation,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit
) {

    val comment = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rating",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Rate your experience with ${res.court.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                OutlinedTextField(
                    value = comment.value,
                    onValueChange = {
                        comment.value = it
                    },
                    label = {
                        Text(text = "Comment")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (comment.value.isNotEmpty())
                        DbCourt().addOrUpdateComment(
                            user = user,
                            reservation = res,
                            comment = comment.value
                        )
                    onConfirmClick()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    comment.value = ""
                    onDismiss()
                }
            ) {
                Text(text = "Dismiss")
            }
        }
    )
}
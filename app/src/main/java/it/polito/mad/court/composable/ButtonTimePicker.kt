package it.polito.mad.court.composable

import android.app.TimePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ButtonTimePicker(selectedTime: LocalTime, onTimeChange: (LocalTime) -> Unit) {

    val context = LocalContext.current

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourSel, minuteSel ->
            onTimeChange(parseTimeSelected(hourSel, minuteSel))
        },
        selectedTime.hour,
        selectedTime.minute,
        true
    )

    OutlinedButton(
        content = {
            Text(
                color = Color.Black,
                text = "${selectedTime.hour} : ${selectedTime.minute}"
            )
        },
        onClick = { timePickerDialog.show() }
    )
}

fun parseTimeSelected(hourSel: Int, minuteSel: Int): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return if (hourSel < 10 && minuteSel < 10) {
        LocalTime.parse(("0${hourSel}:0${minuteSel}"), formatter)
    } else if (hourSel < 10) {
        LocalTime.parse(("0${hourSel}:${minuteSel}"), formatter)
    } else if (minuteSel < 10) {
        LocalTime.parse(("${hourSel}:0${minuteSel}"), formatter)
    } else {
        LocalTime.parse(("${hourSel}:${minuteSel}"), formatter)
    }
}
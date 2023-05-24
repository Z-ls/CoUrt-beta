package it.polito.mad.court.composable

import android.app.DatePickerDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ButtonDatePicker(selectedDate: LocalDate, onTimeChange: (LocalDate) -> Unit) {

    val context = LocalContext.current

    val datePickerDialog = DatePickerDialog(
        context,
        { _, yearSel, monthSel, daySel ->
            onTimeChange(
                parseDateSelected(yearSel, monthSel, daySel)
            )
        },
        selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth
    )

    OutlinedButton(
        content = {
            Text(
                color = Color.Black,
                text = "${selectedDate.dayOfMonth} / ${selectedDate.monthValue} / ${selectedDate.year}"
            )
        },
        onClick = { datePickerDialog.show() }
    )
}

fun parseDateSelected(yearSel: Int, monthSel: Int, daySel: Int): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return if (daySel < 10 && monthSel < 10) {
        LocalDate.parse(("0${daySel}-0${monthSel + 1}-${yearSel}"), formatter)
    } else if (daySel < 10) {
        LocalDate.parse(("0${daySel}-${monthSel + 1}-${yearSel}"), formatter)
    } else if (monthSel < 10) {
        LocalDate.parse(("${daySel}-0${monthSel + 1}-${yearSel}"), formatter)
    } else {
        LocalDate.parse(("${daySel}-${monthSel + 1}-${yearSel}"), formatter)
    }
}
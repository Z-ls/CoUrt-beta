package it.polito.mad.court.composable

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@Composable
fun BirthdayDatePicker(
    selectedDate: LocalDate? = null,
    onSelectDate: (LocalDate) -> Unit = {}
) {
    val context = LocalContext.current
    var date by remember {
        mutableStateOf(LocalDate.now())
    }

    val datePicker = DatePickerDialog(
        context,
        fun(_, t1, t2, t3) {
            date = LocalDate.of(t1, t2 + 1, t3)
            onSelectDate(date)
        },
        date.year, date.monthValue, date.dayOfMonth
    )

    // Show the dialog when the user clicks the button
    OutlinedButton(
        modifier= Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = buttonColors(
        containerColor = Color.White,
        contentColor = Color.Black
    ),
        onClick = { datePicker.show() }) {
        Text(
            textAlign = TextAlign.Start,
            text = when (selectedDate) {
                LocalDate.now() -> "Select your birthday"
                else -> "$date"
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DatePickerPreview() {
    var date = LocalDate.now()
    BirthdayDatePicker(date, onSelectDate = {
        date = it
    })
}
package it.polito.mad.court

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.mad.court.composable.ButtonDatePicker
import it.polito.mad.court.composable.CardReservation
import it.polito.mad.court.composable.DialogReservationForm
import it.polito.mad.court.composable.FloatingButton
import it.polito.mad.court.composable.searchByCourtName
import it.polito.mad.court.dataclass.DateString
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.User
import it.polito.mad.court.ui.theme.CoUrtTheme
import it.polito.mad.court.ui.theme.Orange80
import java.time.LocalDate


class ViewReservations : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoUrtTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PageViewReservations()
                }
            }
        }
    }
}

@Composable
fun PageViewReservations(user: User = User()) {

    val listReservations = remember { mutableStateOf<List<Reservation>>(listOf()) }
    val selectedReservation = remember { mutableStateOf(Reservation()) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val showDialog = remember { mutableStateOf(false) }
    val showRatingDialog = remember { mutableStateOf(false) }

    fun getListReservations() {
        DbCourt().getReservations { reservations ->
            listReservations.value = reservations.filter { res ->
                res.date.date == DateString(selectedDate.value).date
            }
        }
    }

    LaunchedEffect(selectedDate.value) {
        getListReservations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Title()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            ButtonDatePicker(selectedDate = selectedDate.value) {
                selectedDate.value = it
            }
        }
        RowCardReservations(
            listReservations = listReservations.value,
            selectedReservation = selectedReservation,
            showDialog = showDialog,
            getListReservations = ::getListReservations,
            showRatingDialog = showRatingDialog
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            if (showDialog.value) {
                DialogReservationForm(
                    res = selectedReservation.value,
                    user = user,
                    onDismiss = {
                        showDialog.value = false
                        getListReservations()
                    }
                )
            }
            if (showRatingDialog.value) {
                RatingDialog(
                    res = selectedReservation.value,
                    onDismiss = {
                        showRatingDialog.value = false
                        getListReservations()
                    },
                    onRatingSelected = {
                        DbCourt().addOrUpdateRating(selectedReservation.value.court, it)
                        showRatingDialog.value = false
                    }
                )
            }
            FloatingButton(onClick = {
                selectedReservation.value = Reservation(user = user)
                showDialog.value = true
            }, containerColor = Orange80) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                    Text(
                        fontSize = 16.sp,
                        text = "Add Reservation"
                    )
                }
            }
        }
    }
}

@Composable
fun RowCardReservations(
    listReservations: List<Reservation>,
    selectedReservation: MutableState<Reservation>,
    showDialog: MutableState<Boolean>,
    showRatingDialog: MutableState<Boolean>,
    getListReservations: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(500.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        itemsIndexed(listReservations) { _, res ->
            CardReservation(
                res = res,
                onModifyClick = {
                    selectedReservation.value = res
                    showDialog.value = true
                    getListReservations()
                },
                onRemoveClick = {
                    DbCourt().deleteReservation(res)
                    getListReservations()
                },
                onRatingClick = {
                    selectedReservation.value = res
                    showRatingDialog.value = true
                    getListReservations()
                })
        }
    }
}

@Composable
fun RatingDialog(
    onRatingSelected: (Int) -> Unit, onDismiss: () -> Unit, res: Reservation
) {

    val ratingRange = (0..5)
    val selectedRating = remember { mutableStateOf(0) }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Rate this",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                ratingRange.forEach { rating ->
                    IconButton(
                        onClick = { onRatingSelected(rating) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = if (rating < selectedRating.value) Color.Yellow else Color.Gray
                        )
                    }
                }
            }
            Button(
                onClick = { onRatingSelected(selectedRating.value) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Submit")
            }
        }
    }
}


@Composable
fun Title() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleLarge,
        fontSize = 30.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black,
        textAlign = TextAlign.Start,
        text = "Reservations"
    )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    PageViewReservations()
}
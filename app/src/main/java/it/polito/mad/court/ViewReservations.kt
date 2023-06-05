package it.polito.mad.court

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.court.composable.ButtonDatePicker
import it.polito.mad.court.composable.CardReservation
import it.polito.mad.court.composable.DialogComment
import it.polito.mad.court.composable.DialogInvitation
import it.polito.mad.court.composable.DialogReservationForm
import it.polito.mad.court.composable.PageTitle
import it.polito.mad.court.dataclass.DateString
import it.polito.mad.court.dataclass.Invitation
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.TimeString
import it.polito.mad.court.dataclass.User
import it.polito.mad.court.ui.theme.CoUrtTheme
import it.polito.mad.court.ui.theme.Orange80
import java.time.LocalDate
import java.time.LocalTime


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageViewReservations(user: User = User()) {

    val isUsingLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val listReservations = remember { mutableStateOf<List<Reservation>>(listOf()) }
    val selectedReservation = remember { mutableStateOf(Reservation()) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedUsers = remember { mutableStateOf(listOf(User())) }
    val showDialog = remember { mutableStateOf(false) }
    val showRatingDialog = remember { mutableStateOf(false) }
    val showInviteDialog = remember { mutableStateOf(false) }
    var toggleRefresh by remember { mutableStateOf(false) }
    val context = LocalContext.current
//    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    fun toggleRefresh() {
        toggleRefresh = !toggleRefresh
    }

    LaunchedEffect(toggleRefresh, selectedDate.value) {
        DbCourt().getReservations { reservations ->
            listReservations.value = reservations.filter { res ->
                res.date.date == DateString(selectedDate.value).date && res.players.map { it.email }
                    .contains(user.email)
            }
        }
    }

    LaunchedEffect(showDialog.value, showRatingDialog.value, showInviteDialog.value) {
        if (showDialog.value || showRatingDialog.value || showInviteDialog.value) {
            (context as? Activity)?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            (context as? Activity)?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = {
                        selectedReservation.value = Reservation(user = user)
                        showDialog.value = true
                    }, containerColor = Orange80
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(horizontal = 54.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            fontSize = 16.sp, text = "Add Reservation"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = {
                        toggleRefresh()
                    }, modifier = Modifier.wrapContentWidth(), containerColor = Color.White
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    top = 16.dp, bottom = it.calculateBottomPadding(), start = 16.dp, end = 16.dp
                )
        ) {
            if (!isUsingLandscape)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PageTitle("Reservations")
                }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                ButtonDatePicker(selectedDate = selectedDate.value) { date ->
                    selectedDate.value = date
                }
            }
            RowCardReservations(
                listReservations = listReservations.value,
                selectedReservation = selectedReservation,
                showDialog = showDialog,
                toggleRefresh = ::toggleRefresh,
                showRatingDialog = showRatingDialog,
                showInviteDialog = showInviteDialog,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                if (showDialog.value) {
                    DialogReservationForm(res = selectedReservation.value,
                        user = user,
                        onDismiss = {
                            showDialog.value = false
                            toggleRefresh()
                        })
                }
                if (showRatingDialog.value) {
                    DialogComment(user = user, res = selectedReservation.value, onConfirmClick = {
                        showRatingDialog.value = false
                        toggleRefresh()
                    }, onDismiss = {
                        showRatingDialog.value = false
                        toggleRefresh()
                    })
                }
                if (showInviteDialog.value) {
                    DialogInvitation(
                        emailList = selectedUsers,
                        onClickInvite = { showWarning, warning ->
                            selectedUsers.value.forEach { receiver ->
                                if (selectedReservation.value.players.map { p -> p.email }
                                        .contains(receiver.email)) {
                                    warning.value =
                                        "User ${receiver.email} is already in the reservation"
                                    showWarning.value = true
                                    return@DialogInvitation
                                }
                                val inv = Invitation(
                                    sender = user,
                                    receiver = receiver,
                                    reservation = selectedReservation.value,
                                    dateSent = DateString(LocalDate.now()),
                                    timeSent = TimeString(LocalTime.now())
                                )
                                DbCourt().addInvitation(
                                    invitation = inv
                                ) {
                                    showInviteDialog.value = false
                                }
                            }
                        },
                        onClickCancel = {
                            selectedUsers.value = mutableListOf(User())
                            showInviteDialog.value = false
                        })
                }
            }
        }
    }
}

@Composable
fun CardReservations(
    res: Reservation,
    selectedReservation: MutableState<Reservation>,
    showDialog: MutableState<Boolean>,
    showRatingDialog: MutableState<Boolean>,
    showInviteDialog: MutableState<Boolean>,
    toggleRefresh: () -> Unit
) {
    CardReservation(res = res, onInviteClick = {
        selectedReservation.value = res
        showInviteDialog.value = true
        toggleRefresh()
    }, onModifyClick = {
        selectedReservation.value = res
        showDialog.value = true
        toggleRefresh()
    }, onRemoveClick = {
        DbCourt().deleteReservation(res)
        toggleRefresh()
    }, onRatingClick = {
        selectedReservation.value = res
        showRatingDialog.value = true
        toggleRefresh()
    })
}


@Composable
fun RowCardReservations(
    listReservations: List<Reservation>,
    selectedReservation: MutableState<Reservation>,
    showDialog: MutableState<Boolean>,
    showRatingDialog: MutableState<Boolean>,
    showInviteDialog: MutableState<Boolean>,
    toggleRefresh: () -> Unit
) {
    val modifier = Modifier
        .fillMaxSize()

    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LazyRow(
            modifier = modifier
        ) {
            itemsIndexed(listReservations) { _, res ->
                CardReservations(
                    res = res,
                    selectedReservation = selectedReservation,
                    showDialog = showDialog,
                    showRatingDialog = showRatingDialog,
                    showInviteDialog = showInviteDialog,
                    toggleRefresh = toggleRefresh
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            itemsIndexed(listReservations) { _, res ->
                CardReservations(
                    res = res,
                    selectedReservation = selectedReservation,
                    showDialog = showDialog,
                    showRatingDialog = showRatingDialog,
                    showInviteDialog = showInviteDialog,
                    toggleRefresh = toggleRefresh
                )
            }
        }
    }
}

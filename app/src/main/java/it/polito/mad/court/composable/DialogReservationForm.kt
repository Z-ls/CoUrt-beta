package it.polito.mad.court.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.Court
import it.polito.mad.court.dataclass.DateString
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.TimeString
import it.polito.mad.court.dataclass.User
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogReservationForm(
    user: User = User(), res: Reservation = Reservation(user = user), onDismiss: () -> Unit
) {
    val isUpdating by remember { mutableStateOf(res.id != "") }
    var newReservation by remember { mutableStateOf(res) }
    var court by remember { mutableStateOf(res.court) }
    var showWarning by remember { mutableStateOf(false) }
    var msgWarnings by remember { mutableStateOf(listOf<String>()) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 2 }
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                modifier = Modifier,
                state = pagerState,
                pageSpacing = 0.dp,
                userScrollEnabled = true,
                reverseLayout = false,
                contentPadding = PaddingValues(0.dp),
                beyondBoundsPageCount = 0,
                pageSize = PageSize.Fill,
                key = null,
                pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                    Orientation.Horizontal
                ),
                pageContent = { it ->
                    when (it) {
                        0 -> {
                            DialogCourtForm(
                                court = court,
                                res = newReservation,
                                onSelect = { selectedCourt ->
                                    court = selectedCourt
                                    newReservation.court = selectedCourt
                                }
                            )
                        }

                        1 -> {
                            DialogUserForm(
                                res = newReservation,
                                onMinPlayersChanged = { newReservation.minPlayers = it },
                                onMaxPlayersChanged = { newReservation.maxPlayers = it },
                                onSkillLevelValueChange = { newReservation.skillLevel = it })
                        }
                    }
                }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showWarning) msgWarnings.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(it)
                    }
                }
                Button(
                    onClick = {
                        if (pagerState.canScrollBackward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth(), enabled = pagerState.canScrollBackward
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = {
                        if (pagerState.canScrollForward) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            coroutineScope.launch {
                                if (checkValidReservation(newReservation).first) {
                                    if (isUpdating) {
                                        DbCourt().updateReservation(newReservation)
                                    } else {
                                        DbCourt().addReservation(user, newReservation)
                                    }
                                    onDismiss()
                                } else {
                                    msgWarnings =
                                        checkValidReservation(newReservation).second
                                    showWarning = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (pagerState.canScrollForward) Text("Next") else Text("Save")
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            newReservation = Reservation(user = user)
                            court = Court()
                            onDismiss()
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

suspend fun checkValidReservation(res: Reservation): Pair<Boolean, List<String>> {
    var valid = true
    val warnings = mutableListOf<String>()

    if (res.court.name.isEmpty()) {
        valid = false
        warnings.add("Court name cannot be empty")
    }
    if (LocalDateTime.of(res.date.date, res.time.time).isBefore(LocalDateTime.now())) {
        valid = false
        warnings.add("Date and time cannot be in the past")
    }
    if (res.minPlayers < 1) {
        valid = false
        warnings.add("Minimum number of players cannot be less than 1")
    }
    if (res.maxPlayers < res.minPlayers) {
        valid = false
        warnings.add("Maximum number of players cannot be less than minimum number of players")
    }
    if (res.duration == 0) {
        valid = false
        warnings.add("Please choose a duration")
    }
    if (res.time.time.plusMinutes(res.duration.toLong()) > LocalTime.of(23, 59)) {
        valid = false
        warnings.add("The reservation cannot end after midnight")
    }
    if (res.time.time.isBefore(res.court.openingTime)) {
        valid = false
        warnings.add("The court is not opened at the selected time")
    }
    if (res.time.time.plusHours(res.duration.toLong()).isAfter(res.court.closingTime)) {
        valid = false
        warnings.add("The court is closed at the selected time")
    }
    if (checkCourtAvailability(
            res.id,
            res.court,
            res.date.date,
            res.time.time,
            res.duration
        ).not()
    ) {
        valid = false
        warnings.add("The court is not available at the selected time")
    }
    return Pair(valid, warnings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogUserForm(
    res: Reservation = Reservation(),
    onSkillLevelValueChange: (Int) -> Unit,
    onMinPlayersChanged: (Int) -> Unit,
    onMaxPlayersChanged: (Int) -> Unit,
) {

    var minPlayers by remember { mutableIntStateOf(res.minPlayers) }
    var maxPlayers by remember { mutableIntStateOf(res.maxPlayers) }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth(0.5f),
                value = minPlayers.toString(),
                onValueChange = {
                    if (it.isNotEmpty()) {
                        minPlayers = it.toInt()
                        onMinPlayersChanged(it.toInt())
                    } else {
                        minPlayers = 0
                        onMinPlayersChanged(0)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Min") },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.padding(start = 8.dp),
                value = maxPlayers.toString(),
                onValueChange = {
                    if (it.isNotEmpty()) {
                        maxPlayers = it.toInt()
                        onMaxPlayersChanged(it.toInt())
                    } else {
                        maxPlayers = 0
                        onMaxPlayersChanged(0)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Max") },
                singleLine = true
            )
        }
        Row(
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkillLevelSlider(
                res = res, onSkillLevelChanged = onSkillLevelValueChange
            )
        }
    }
}

@Composable
fun SkillLevelSlider(
    res: Reservation, onSkillLevelChanged: (Int) -> Unit
) {

    var skillLevelIndex by remember { mutableIntStateOf(res.skillLevel) }

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex == 0, onClick = {
                skillLevelIndex = 0
                onSkillLevelChanged(0)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Beginner")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex == 1, onClick = {
                skillLevelIndex = 1
                onSkillLevelChanged(1)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Intermediate")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex == 2, onClick = {
                skillLevelIndex = 2
                onSkillLevelChanged(2)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Advanced")
        }
    }
}


@Composable
fun DialogCourtForm(
    court: Court,
    res: Reservation,
    onSelect: (Court) -> Unit
) {

    var selectedDate by remember { mutableStateOf(res.date.date) }
    var selectedTime by remember { mutableStateOf(res.time.time) }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DialogCourtSelection(
            courtName = res.court.name,
            onSelect = onSelect,
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ButtonDatePicker(selectedDate) {
                selectedDate = it
                res.date = DateString(it)
            }
            Spacer(modifier = Modifier.width(16.dp))
            ButtonTimePicker(selectedTime) {
                selectedTime = it
                res.time = TimeString(it)
            }
        }
        court.let {
            DurationDropdown(price = it.price,
                duration = res.duration,
                onDurationSelected = { duration ->
                    res.duration = duration
                    res.price = (duration * it.price).roundToInt()
                }
            )
        }
    }
}

@Composable
fun DurationDropdown(
    price: Double, duration: Int, onDurationSelected: (Int) -> Unit
) {
    var selectedDuration by remember { mutableIntStateOf(duration) }
    var expanded by remember { mutableStateOf(false) }
    val durationItems = (1..10).toList()

    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (selectedDuration > 0) {
                    val dur = if (selectedDuration > 1) "$selectedDuration hours"
                    else "$selectedDuration hour"
                    dur + " * $price = ${(selectedDuration * price).roundToInt()} Euro"
                } else "Select duration",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true })
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .background(Color.White)
            ) {
                durationItems.forEach { duration ->
                    DropdownMenuItem(onClick = {
                        selectedDuration = duration
                        onDurationSelected(duration)
                        expanded = false
                    }, text = { Text(text = duration.toString()) })
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogCourtSelection(courtName: String, onSelect: (Court) -> Unit) {

    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf(listOf<Court>()) }
    var isSearchOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .clickable { isSearchOpen = true }
        .width(300.dp)) {
        OutlinedTextField(
            placeholder = {
                if (courtName.isNotBlank()) Text(courtName)
                else Text("By Court Name...")
            },
            value = searchText,
            onValueChange = {
                searchText = it
                DbCourt().searchCourtByName(
                    it
                ) { list -> searchResult = list }
                isSearchOpen = true
            },
            label = { if (courtName.isNotBlank()) Text(courtName) else Text("Enter court name...") },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = 300.dp),
            expanded = isSearchOpen,
            onDismissRequest = { isSearchOpen = false },
            properties = PopupProperties(focusable = false)
        ) {
            searchResult.forEach { result ->
                DropdownMenuItem(onClick = {
                    onSelect(result)
                    searchText = result.name
                    isSearchOpen = false
                }, text = { Text(result.name) })
            }
        }
    }
}

suspend fun checkCourtAvailability(
    id: String,
    court: Court,
    date: LocalDate,
    time: LocalTime,
    duration: Int
): Boolean = suspendCoroutine { continuation ->
    DbCourt().checkCourtAvailability(id, court, date, time, duration) {
        continuation.resume(it)
    }
}



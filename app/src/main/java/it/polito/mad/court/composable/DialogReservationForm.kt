package it.polito.mad.court.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.github.javafaker.Faker
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.Court
import it.polito.mad.court.dataclass.DateString
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.TimeString
import it.polito.mad.court.dataclass.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogReservationForm(
    user: User = User(), res: Reservation = Reservation(user = user), onDismiss: () -> Unit
) {
    val isUpdating = remember { mutableStateOf(res.id != "") }
    val newReservation = remember { mutableStateOf(res) }
    val court = remember { mutableStateOf(res.court) }
    val showWarning = remember { mutableStateOf(false) }
    val msgWarnings = remember { mutableStateOf(listOf<String>()) }
    val pagerState = rememberPagerState(0)
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
                state = pagerState, pageCount = 2
            ) { page ->
                when (page) {
                    0 -> {
                        DialogCourtForm(
                            court = court, res = newReservation.value
                        )
                    }

                    1 -> {
                        DialogUserForm(
                            res = newReservation.value,
                            onMinPlayersChanged = { newReservation.value.minPlayers = it },
                            onMaxPlayersChanged = { newReservation.value.maxPlayers = it },
                            onSkillLevelValueChange = { newReservation.value.skillLevel = it })
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showWarning.value) msgWarnings.value.forEach {
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
                                if (checkValidReservation(newReservation.value).first) {
                                    if (isUpdating.value) {
                                        DbCourt().updateReservation(newReservation.value)
                                    } else {
                                        DbCourt().addReservation(newReservation.value)
                                    }
                                    onDismiss()
                                } else {
                                    msgWarnings.value =
                                        checkValidReservation(newReservation.value).second
                                    showWarning.value = true
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
                            newReservation.value = Reservation(user = user)
                            court.value = Court()
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


fun checkValidReservation(res: Reservation): Pair<Boolean, List<String>> {
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
    runBlocking { }
    DbCourt().checkCourtAvailability(
        res.court.name,
        res.date.date,
        res.time.time,
        res.duration
    ) { available ->
        if (!available) {
            valid = false
            warnings.add("Court is not available at the selected time")
        }
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

    val minPlayers = remember { mutableStateOf(res.minPlayers) }
    val maxPlayers = remember { mutableStateOf(res.maxPlayers) }

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
                value = minPlayers.value.toString(),
                onValueChange = {
                    if (it.isNotEmpty()) {
                        minPlayers.value = it.toInt()
                        onMinPlayersChanged(it.toInt())
                    } else {
                        minPlayers.value = 0
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
                value = maxPlayers.value.toString(),
                onValueChange = {
                    if (it.isNotEmpty()) {
                        maxPlayers.value = it.toInt()
                        onMaxPlayersChanged(it.toInt())
                    } else {
                        maxPlayers.value = 0
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

    val skillLevelIndex = remember { mutableStateOf(res.skillLevel) }

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex.value == 0, onClick = {
                skillLevelIndex.value = 0
                onSkillLevelChanged(0)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Beginner")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex.value == 1, onClick = {
                skillLevelIndex.value = 1
                onSkillLevelChanged(1)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Intermediate")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(selected = skillLevelIndex.value == 2, onClick = {
                skillLevelIndex.value = 2
                onSkillLevelChanged(2)
            })
            Text(modifier = Modifier.padding(end = 16.dp), text = "Advanced")
        }
    }
}


@Composable
fun DialogCourtForm(
    court: MutableState<Court>, res: Reservation
) {

    val selectedDate = remember { mutableStateOf(res.date.date) }
    val selectedTime = remember { mutableStateOf(res.time.time) }

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
            onSelect = {
                court.value = it
                res.court = it
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ButtonDatePicker(selectedDate.value) {
                selectedDate.value = it
                res.date = DateString(it)
            }
            Spacer(modifier = Modifier.width(16.dp))
            ButtonTimePicker(selectedTime.value) {
                selectedTime.value = it
                res.time = TimeString(it)
            }
        }
        court.value.let {
            DurationDropdown(price = it.price,
                duration = res.duration,
                onDurationSelected = { duration ->
                    res.duration = duration
                    res.price = (duration * it.price).roundToInt()
                })
        }
    }
}

@Composable
fun DurationDropdown(
    price: Double, duration: Int, onDurationSelected: (Int) -> Unit
) {
    var selectedDuration by remember { mutableStateOf(duration) }
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


@SuppressLint("MutableCollectionMutableState")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogCourtSelection(courtName: String, onSelect: (Court) -> Unit) {

    var searchText by remember { mutableStateOf("") }
    val searchResult = remember { mutableStateOf(mutableListOf<Court>()) }
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
                searchByCourtName(
                    it,
                    searchResult,
                )
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
            searchResult.value.forEach { result ->
                DropdownMenuItem(onClick = {
                    onSelect(result)
                    searchText = result.name
                    isSearchOpen = false
                }, text = { Text(result.name) })
            }
        }
    }
}

@Preview
@Composable
fun DialogReservationFormPreview() {
//    val court = Court(
//        name = "Basketball court",
//        address = "Via Giuseppe Verdi, 5, 10124 Torino TO",
//        city = "Torino",
//        country = "Italy",
//        sport = "Basketball",
//        price = 10.0,
//        rating = 4.5
//    )
//    val user = User(
//        email = "johndoe@gmail.com",
//        firstname = "John",
//        lastname = "doe",
//        nickname = "J-Doe",
//        gender = "male",
//        birthdate = LocalDate.of(1993, 5, 5),
//        height = 1.83,
//        weight = 75.0,
//        city = "Torino",
//        country = "Italy",
//        bio = "I like playing basketball",
//        phone = "110-120-12315"
//    )
//    val res = Reservation(
//        court = court,
//        user = user,
//        date = DateString(LocalDate.now()),
//        time = TimeString(LocalTime.now()),
//        duration = 1,
//        price = 10,
//        numPlayers = 8,
//        skillLevel = 0
//
//    )
//    DialogReservationForm(user, res) {}
    val faker = Faker()
    for (i in 1..20) {
        DbCourt().addCourt(
            Court(
                rating = faker.number().randomDouble(1, 0, 5),
                name = faker.company().name(),
                address = faker.address().streetAddress(),
                city = faker.address().city(),
                country = faker.address().country(),
                phone = faker.phoneNumber().phoneNumber(),
                email = faker.internet().emailAddress(),
                website = faker.internet().url(),
                openingTime = "08:00",
                closingTime = "19:00",
                price = faker.number().randomDouble(1, 0, 100),
                image = faker.internet().image(),
                sport = faker.team().sport(),
                description = faker.lorem().paragraph(),
                isOutdoor = faker.bool().bool()
            )
        )
    }
}

fun searchByCourtName(
    name: String, results: MutableState<MutableList<Court>>
) {
    val gson = Gson()

    FirebaseDatabase.getInstance().getReference("courts").get()
        .addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                results.value = dataSnapshot.children.map {
                    gson.fromJson(
                        it.value.toString(), Court::class.java
                    )
                }.filter { it.name.contains(name, ignoreCase = true) }.toMutableList()
            }
        }
}


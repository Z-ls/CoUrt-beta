package it.polito.mad.court.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lab4.R
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.Court
import it.polito.mad.court.dataclass.User
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CardCourt(user: User, court: Court) {
    var rating by remember { mutableFloatStateOf(court.rating) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var valid by remember { mutableStateOf<Boolean?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    var isRatingExpanded by remember { mutableStateOf(false) }
    var isCheckInExpanded by remember { mutableStateOf(false) }
    val onClick = {
        if (isExpanded) {
            isRatingExpanded = false
            isCheckInExpanded = false
        }
        isExpanded = !isExpanded
    }
    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutLinearInEasing
        )
    )

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = R.drawable.basketball_indoor),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = court.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${rating}/5",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = court.address,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = court.sport,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = court.price.toString() + " euro/hour",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = court.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alpha(alpha)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            textAlign = TextAlign.End,
                            text = court.comment,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CourtActionButtonRow(
                            onCheckClick = {
                                isCheckInExpanded = !isCheckInExpanded
                            },
                            onRatingClick = {
                                isRatingExpanded = !isRatingExpanded
                            })
                    }
                    AnimatedVisibility(visible = isCheckInExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            ButtonDatePicker(selectedDate = selectedDate) {
                                selectedDate = it
                            }
                            ButtonTimePicker(selectedTime = selectedTime) {
                                selectedTime = it
                            }
                            IconButton(
                                onClick = {
                                    DbCourt().checkCourtAvailability(
                                        id = "ThisIsNotAnId",
                                        court = court,
                                        date = selectedDate,
                                        time = selectedTime,
                                        duration = 0
                                    ) { valid = it }
                                }) {
                                when (valid) {
                                    null -> Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )

                                    true -> Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )

                                    false -> Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = isRatingExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RatingStars(
                                currRating = rating,
                                onRatingClick = { index ->
                                    DbCourt().addOrUpdateRating(
                                        user = user,
                                        court = court,
                                        rating = index
                                    )
                                    DbCourt().getRating(court = court) { rating = index.toFloat() }
                                })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CourtActionButtonRow(
    onCheckClick: () -> Unit,
    onRatingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onCheckClick,
            modifier = Modifier.padding(end = 8.dp),
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Modify",
                tint = Color.Black
            )
        }

        IconButton(
            onClick = onRatingClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun RatingStars(
    currRating: Float,
    onRatingClick: (Int) -> Unit
) {
    var ratings by remember { mutableFloatStateOf(currRating) }

    Row {
        repeat(5) { index ->
            val filled = index < ratings
            val starIcon = if (filled) Icons.Filled.Star else Icons.Outlined.Star
            val starColor = if (filled) Color.Yellow else Color.Gray
            IconToggleButton(
                checked = filled,
                onCheckedChange = {
                    onRatingClick(index + 1)
                    ratings = index + 1F
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = starIcon,
                    contentDescription = null,
                    tint = starColor
                )
            }
        }
    }
}
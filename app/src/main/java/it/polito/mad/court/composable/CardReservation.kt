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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab4.R
import it.polito.mad.court.dataclass.Reservation

@Composable
fun CardReservation(
    res: Reservation, onModifyClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onRatingClick: () -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    val onClick = { isExpanded = !isExpanded }
    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutLinearInEasing
        )
    )

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = cardColors(
            containerColor = Color(0xEDF2F2F2),
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.basketball_indoor),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
                    text = res.date.toString() + " " + res.time.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = res.court.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = res.court.address,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                ReservationDetailRow("Durations", "${res.duration} hours")
                ReservationDetailRow("Players", "${res.numPlayers} / ${res.maxPlayers}")
                AnimatedVisibility(
                    visible = isExpanded,
                    Modifier.alpha(alpha)
                ) {
                    CardReservationAdditionalInfo(res, onModifyClick, onRemoveClick, onRatingClick)
                }
            }
        }
    }
}

@Composable
fun CardReservationAdditionalInfo(
    res: Reservation,
    onModifyClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onRatingClick: () -> Unit
) {
    Column {
        ReservationDetailRow("Price", "${res.price} Euro")
        ReservationDetailRow(
            "Level",
            listOf("Beginner", "Intermediate", "Advanced")[res.skillLevel]
        )
        ActionButtonRow(
            onModifyClick = onModifyClick,
            onRemoveClick = onRemoveClick,
            onRatingClick = onRatingClick,
        )
    }
}

@Composable
fun ReservationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ActionButtonRow(
    onModifyClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onRatingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = { onModifyClick() },
            modifier = Modifier.padding(end = 8.dp),
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Modify",
                tint = Color.Black
            )
        }

        IconButton(
            onClick = { onRemoveClick() },
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.Red
            )
        }

        IconButton(
            onClick = { onRatingClick() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color.Gray
            )
        }
    }
}
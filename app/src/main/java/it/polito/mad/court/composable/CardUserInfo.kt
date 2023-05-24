package it.polito.mad.court.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.polito.mad.court.dataclass.User
import java.time.LocalDate

@Composable
fun CardUserInfo(user: User) {

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
            .clickable(onClick = onClick)
    ) {
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
                    text = user.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                UserDetailRow("Bio:", user.bio)
                UserDetailRow("Gender:", user.gender)
                UserDetailRow("Location:", "${user.city}, ${user.country}")
                AnimatedVisibility(
                    visible = isExpanded,
                    Modifier.alpha(alpha)
                ) {
                    Column {
                        UserDetailRow("Email:", user.email)
                        UserDetailRow("Birthdate:", user.birthdate.toString())
                        UserDetailRow("Height:", "${user.height} cm")
                        UserDetailRow("Weight:", "${user.weight} kg")
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            when (label) {
                "Bio:" -> ""
                else -> label
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontWeight = when (label) {
                "Bio:" -> FontWeight.Bold
                else -> FontWeight.Normal
            },
        )
    }
}

@Preview
@Composable
fun CardUserInfoPreview() {
    val user = User(
        email = "johndoe@gmail.com",
        firstname = "John",
        lastname = "doe",
        nickname = "J-Doe",
        gender = "male",
        birthdate = LocalDate.of(1993, 5, 5),
        height = 1.83,
        weight = 75.0,
        city = "Torino",
        country = "Italy",
        bio = "I like playing basketball",
        phone = "110-120-12315"
    )
    CardUserInfo(user = user)
}
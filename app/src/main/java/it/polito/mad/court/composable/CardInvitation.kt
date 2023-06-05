package it.polito.mad.court.composable

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.Invitation
import it.polito.mad.court.dataclass.InvitationStatus
import it.polito.mad.court.dataclass.User
import it.polito.mad.court.ui.theme.Crimson80
import it.polito.mad.court.ui.theme.LightGreen80
import it.polito.mad.court.ui.theme.Yellow80

@Composable
fun CardInvitation(
    user: User,
    invitation: Invitation
) {
    var status by remember { mutableStateOf(invitation.status) }
    var isExpanded by remember { mutableStateOf(false) }

    fun isPending(): Boolean {
        return status == InvitationStatus.PENDING
    }

    val cardModifier =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .width(300.dp)
                .clickable(
                    enabled = isPending(),
                    onClick = {
                        isExpanded = !isExpanded
                    })
        else Modifier
            .padding(8.dp)
            .clickable(
                enabled = isPending(),
                onClick = {
                    isExpanded = !isExpanded
                })

    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                InvitationStatus.ACCEPTED -> LightGreen80
                InvitationStatus.PENDING -> Yellow80
                InvitationStatus.DECLINED -> Crimson80
                else -> {
                    LightGreen80
                }
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = invitation.reservation?.court?.name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = invitation.reservation?.court?.address ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = invitation.reservation?.court?.sport ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = invitation.reservation?.court?.price?.toString() + " euro/hour",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = invitation.sender?.nickname ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    RowInvitationActions(
                        onClickAccept = {
                            if (DbCourt().acceptInvitation(
                                    user = user,
                                    invitation = invitation
                                ).first
                            ) {
                                status = InvitationStatus.ACCEPTED
                                isExpanded = false
                            }

                        },
                        onClickDecline = {
                            DbCourt().declineInvitation(
                                invitation = invitation
                            )
                            status = InvitationStatus.DECLINED
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RowInvitationActions(
    onClickAccept: () -> Unit,
    onClickDecline: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = onClickAccept,
        ) {
            Text(text = "Accept")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = onClickDecline
        ) {
            Text(text = "Decline")
        }
    }
}
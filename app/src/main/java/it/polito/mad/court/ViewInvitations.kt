package it.polito.mad.court

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.mad.court.composable.CardInvitation
import it.polito.mad.court.composable.PageTitle
import it.polito.mad.court.dataclass.Invitation
import it.polito.mad.court.dataclass.User
import it.polito.mad.court.ui.theme.CoUrtTheme
import it.polito.mad.court.ui.theme.Orange80

class ViewInvitations : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoUrtTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavHost()
                }
            }
        }
    }
}

@Composable
fun NavHost(user: User = User()) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "listReceived",
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = 1)
            )
        }
    ) {
        composable("listReceived") { PageViewInvitations(user, navController, false) }
        composable("listSent") { PageViewInvitations(user, navController, true) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageViewInvitations(
    user: User = User(),
    navController: NavController,
    bySender: Boolean = false
) {

    var listInvitations by remember { mutableStateOf<List<Invitation>>(listOf()) }
    var toggleRefresh by remember { mutableStateOf(false) }

    fun toggleRefresh() {
        toggleRefresh = !toggleRefresh
    }

    LaunchedEffect(toggleRefresh) {
        DbCourt().getInvitationsByRole(user.email, bySender) { Invitations ->
            listInvitations = Invitations
        }
    }

    Scaffold(
        floatingActionButton = {
            RowFloatingButtons(
                bySender = bySender,
                navController = navController,
                toggleRefresh = ::toggleRefresh
            )
        }
    )
    {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    top = 16.dp,
                    bottom = it.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (bySender) PageTitle("Invitations Sent") else PageTitle("Invitations Received")
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                RowCardInvitations(
                    user = user,
                    listInvitations = listInvitations,
                )
            }
        }
    }
}

@Composable
fun RowFloatingButtons(
    bySender: Boolean,
    navController: NavController,
    toggleRefresh: () -> Unit
) {
    Row {
        FloatingActionButton(
            onClick = {
                if (bySender)
                    navController.navigate("listReceived")
                else
                    navController.navigate("listSent")
            },
            modifier = Modifier
                .wrapContentWidth(),
            containerColor = Orange80
        ) {
            if (bySender)
                Icon(Icons.Filled.ArrowBack, contentDescription = "To Received Invitations")
            else
                Icon(Icons.Filled.ArrowForward, contentDescription = "To Sent Invitations")
        }
        Spacer(modifier = Modifier.width(16.dp))
        FloatingActionButton(
            onClick = {
                toggleRefresh()
            },
            modifier = Modifier
                .wrapContentWidth(),
            containerColor = Color.White
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
        }
    }
}

@Composable
fun RowCardInvitations(
    user: User,
    listInvitations: List<Invitation>,
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        itemsIndexed(listInvitations) { _, invitation ->
            CardInvitation(
                user = user,
                invitation = invitation
            )
        }
    }
}

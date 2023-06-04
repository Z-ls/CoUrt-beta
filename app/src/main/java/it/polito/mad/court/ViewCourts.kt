package it.polito.mad.court

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import it.polito.mad.court.composable.CardCourt
import it.polito.mad.court.composable.PageTitle
import it.polito.mad.court.dataclass.Court
import it.polito.mad.court.dataclass.User
import it.polito.mad.court.ui.theme.CoUrtTheme


class ViewCourts : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoUrtTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PageViewCourts()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageViewCourts(user: User = User()) {

    val listCourts = remember { mutableStateOf<List<Court>>(listOf()) }
    var toggleRefresh by remember { mutableStateOf(false) }

    fun getListCourts() {
        DbCourt().getCourts(user) { courts ->
            listCourts.value = courts
        }
    }

    LaunchedEffect(toggleRefresh) {
        listCourts.value = listOf()
        getListCourts()
    }

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
            ) {
                FloatingActionButton(
                    onClick = {
                        toggleRefresh = !toggleRefresh
                    },
                    modifier = Modifier.wrapContentWidth(),
                    containerColor = Color.White
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        })
    {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = it.calculateBottomPadding()
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PageTitle("Courts")
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                RowCardCourts(
                    user = user,
                    listCourts = listCourts.value,
                )
            }
        }
    }
}

@Composable
fun RowCardCourts(
    user: User,
    listCourts: List<Court>,
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        itemsIndexed(listCourts) { _, court ->
            CardCourt(
                user = user,
                court = court
            )
        }
    }
}
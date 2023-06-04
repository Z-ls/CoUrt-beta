package it.polito.mad.court.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.User

@Composable
fun DialogInvitation(
    emailList: MutableState<List<User>>,
    onClickInvite: () -> Unit,
    onClickCancel: () -> Unit
) {

    Dialog(
        onDismissRequest = onClickCancel,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxSize(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                emailList.value.forEachIndexed { index, user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DialogUserSelection(user.email) { user ->
                            val list = emailList.value.toMutableList()
                            list[index] = user
                            emailList.value = list
                        }
                        Column(modifier = Modifier.wrapContentWidth())
                        {
                            IconButton(
                                onClick = {
                                    val list = emailList.value.toMutableList()
                                    list.add(User())
                                    emailList.value = list
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                        if (index > 0) {
                            IconButton(
                                onClick = {
                                    val list = emailList.value.toMutableList()
                                    list.removeAt(index)
                                    emailList.value = list
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onClickInvite,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text("Invite")
                    }
                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )
                    Button(
                        onClick = onClickCancel,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogUserSelection(userEmail: String, onSelect: (User) -> Unit) {

    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf(listOf<User>()) }
    var isSearchOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .clickable { isSearchOpen = true }
        .fillMaxWidth(0.9f)
    ) {
        OutlinedTextField(
            placeholder = {
                if (userEmail.isNotBlank()) Text(userEmail)
                else Text("By User Email...")
            },
            value = searchText,
            onValueChange = {
                searchText = it
                DbCourt().searchUserByEmail(
                    it
                ) { list -> searchResult = list }
                isSearchOpen = true
            },
            label = { if (userEmail.isNotBlank()) Text(userEmail) else Text("Enter email...") },
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
                    searchText = result.email
                    isSearchOpen = false
                }, text = { Text(result.email) })
            }
        }
    }
}

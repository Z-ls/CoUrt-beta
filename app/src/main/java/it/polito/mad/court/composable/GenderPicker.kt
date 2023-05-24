package it.polito.mad.court.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GenderPicker(
    onGenderSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = when (selectedGender) {
                    "" -> "Select Your Gender"
                    else -> selectedGender
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (selectedGender != "") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
//                    alpha = ContentAlpha.medium
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = if (selectedGender != "") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
//                    alpha = ContentAlpha.medium
                )
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                GenderOption("Male", selectedGender == "Male") {
                    selectedGender = "Male"
                    onGenderSelected(selectedGender)
                    isExpanded = false
                }
                GenderOption("Female", selectedGender == "Female") {
                    selectedGender = "Female"
                    onGenderSelected(selectedGender)
                    isExpanded = false
                }
                GenderOption("Non-Binary", selectedGender == "Non-Binary") {
                    selectedGender = "Non-Binary"
                    onGenderSelected(selectedGender)
                    isExpanded = false
                }
            }
        }
    }
}

@Composable
fun GenderOption(
    gender: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable { onClick() }
            .padding(start = 16.dp, end = 16.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = false,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(text = gender)
    }
}

@Composable
@Preview
fun GenderPickerPreview() {
    GenderPicker {}
}
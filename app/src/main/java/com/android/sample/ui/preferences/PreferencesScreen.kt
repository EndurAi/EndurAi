package com.android.sample.ui.preferences

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.model.preferences.Preferences
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.preferences.UnitySystem
import com.android.sample.model.preferences.WeightUnit
import com.android.sample.ui.composables.SaveButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(preferencesViewModel: PreferencesViewModel) {
    val preferences = preferencesViewModel.preferences.collectAsState().value
        ?: return Text(text = "No Preferences registred. Should not happen", color = Color.Red)

    var distanceSystem by remember { mutableStateOf(preferences.unity) }
    var weightUnit by remember { mutableStateOf(preferences.weight) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modify preferences", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White),
                modifier = Modifier.testTag("preferencesTopBar")
            )
        },
        bottomBar = {
            SaveButton(onSaveClick = {
                val newPreferences = Preferences(distanceSystem,weightUnit)
                preferencesViewModel.updatePreferences(newPreferences)
            }, testTag = "preferencesSaveButton")
        }
    ) { paddingValues ->
        PreferencesContent(
            modifier = Modifier.padding(paddingValues),
            distanceSystem = distanceSystem,
            onDistanceChange = { distanceSystem = it },
            weightUnit = weightUnit,
            onWeightChange = { weightUnit = it }
        )
    }
}

@Composable
fun PreferencesContent(
    modifier: Modifier = Modifier,
    distanceSystem: UnitySystem,
    onDistanceChange: (UnitySystem) -> Unit,
    weightUnit: WeightUnit,
    onWeightChange: (WeightUnit) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreferenceItem(
            title = "Distance system",
            currentValue = distanceSystem,
            onValueChange = { onDistanceChange(it) },
            options = UnitySystem.values().toList(),
            testTag = "distanceSystem"
        )

        PreferenceItem(
            title = "Weight unit",
            currentValue = weightUnit,
            onValueChange = { onWeightChange(it) },
            options = WeightUnit.values().toList(),
            testTag = "weightUnit"
        )
    }
}

@Composable
fun <T> PreferenceItem(
    title: String,
    currentValue: T,
    onValueChange: (T) -> Unit,
    options: List<T>,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag(testTag + "Menu"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.testTag(testTag + "MenuText"))

            DropdownMenuItem(currentValue = currentValue, onValueChange = onValueChange, options = options, testTag = testTag)
        }
    }
}

@Composable
fun <T> DropdownMenuItem(
    currentValue: T,
    onValueChange: (T) -> Unit,
    options: List<T>,
    testTag: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag(testTag + "Button")
        ) {
            Text(text = currentValue.toString(), color = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    modifier = Modifier.testTag(testTag + option.toString())
                )
            }
        }
    }
}


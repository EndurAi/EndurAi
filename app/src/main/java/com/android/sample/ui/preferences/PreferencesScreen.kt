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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Check
import com.android.sample.model.preferences.Preferences
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.preferences.UnitySystem
import com.android.sample.model.preferences.WeightUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(preferencesViewModel: PreferencesViewModel) {
    val preferences = preferencesViewModel.preferences.collectAsState().value
        ?: return Text(text = "No ToDo selected. Should not happen", color = Color.Red)

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
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            SaveButton {

                val newPreferences = Preferences(distanceSystem,weightUnit)
                preferencesViewModel.updatePreferences(newPreferences)



            }
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
            options = UnitySystem.values().toList()
        )

        PreferenceItem(
            title = "Weight unit",
            currentValue = weightUnit,
            onValueChange = { onWeightChange(it) },
            options = WeightUnit.values().toList()
        )
    }
}

@Composable
fun <T> PreferenceItem(
    title: String,
    currentValue: T,
    onValueChange: (T) -> Unit,
    options: List<T>
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            DropdownMenuItem(currentValue = currentValue, onValueChange = onValueChange, options = options)
        }
    }
}

@Composable
fun <T> DropdownMenuItem(
    currentValue: T,
    onValueChange: (T) -> Unit,
    options: List<T>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            shape = RoundedCornerShape(8.dp)
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
                    }
                )
            }
        }
    }
}

@Composable
fun SaveButton(onSaveClick: () -> Unit) {
    Button(
        onClick = onSaveClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Save",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save", color = Color.White)
    }
}
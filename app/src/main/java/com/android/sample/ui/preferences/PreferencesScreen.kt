package com.android.sample.ui.preferences

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.sample.R
import com.android.sample.model.preferences.Preferences
import com.android.sample.model.preferences.PreferencesViewModel
import com.android.sample.model.preferences.UnitsSystem
import com.android.sample.model.preferences.WeightUnit
import com.android.sample.ui.composables.ArrowBack
import com.android.sample.ui.composables.SaveButton
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Blue
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.BlueWorkoutCard
import com.android.sample.ui.theme.DarkBlue
import com.android.sample.ui.theme.FontSizes
import com.android.sample.ui.theme.FontSizes.ButtonFontSize
import com.android.sample.ui.theme.FontSizes.MediumTitleFontSize
import com.android.sample.ui.theme.FontSizes.TitleFontSize
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Shape.buttonShape
import com.android.sample.ui.theme.Shape.smallButtonShape
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.VeryLightBlue
import com.android.sample.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    navigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel
) {
  val context = LocalContext.current

  val preferences =
      requireNotNull(preferencesViewModel.preferences.collectAsState().value) {
        "Preferences should not be null."
      }

  var unitsSystem by remember { mutableStateOf(preferences.unitsSystem) }
  var weightUnit by remember { mutableStateOf(preferences.weight) }

  Scaffold(
      topBar = { TopBar(navigationActions, R.string.Preferences) })

  { paddingValues ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally
      ) {
          PreferencesContent(
              modifier = Modifier.padding(paddingValues),
              distanceSystem = unitsSystem,
              onDistanceChange = { unitsSystem = it },
              weightUnit = weightUnit,
              onWeightChange = { weightUnit = it })
          Spacer(modifier = Modifier.fillMaxHeight(0.7f))
          SubmitButton(
              onClick = {
                  val newPreferences = Preferences(unitsSystem, weightUnit)
                  preferencesViewModel.updatePreferences(newPreferences)
                  Toast.makeText(context, "Changes successful", Toast.LENGTH_SHORT).show()
                  navigationActions.goBack()
              })}
      }
}

@Composable
fun PreferencesContent(
    modifier: Modifier = Modifier,
    distanceSystem: UnitsSystem,
    onDistanceChange: (UnitsSystem) -> Unit,
    weightUnit: WeightUnit,
    onWeightChange: (WeightUnit) -> Unit
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        PreferenceItem(
            title = "System of units",
            currentValue = distanceSystem,
            onValueChange = { onDistanceChange(it) },
            options = UnitsSystem.values().toList(),
            testTag = "unitsSystem")

        PreferenceItem(
            title = "Weight unit",
            currentValue = weightUnit,
            onValueChange = { onWeightChange(it) },
            options = WeightUnit.values().toList(),
            testTag = "weightUnit")
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
      shape = buttonShape,
      modifier = Modifier.padding(20.dp).fillMaxWidth(0.8f).shadow(4.dp, shape = buttonShape),
      color = VeryLightBlue) {
        Row(
            modifier = Modifier.fillMaxWidth().testTag(testTag + "Menu"),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = title,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = OpenSans,
                  fontSize = TitleFontSize,
                  modifier = Modifier.padding(10.dp).fillMaxWidth(0.6f).testTag(testTag + "MenuText"))

               Spacer(modifier = Modifier.fillMaxWidth(0.07f))
              DropdownMenuItem(
                  currentValue = currentValue,
                  onValueChange = onValueChange,
                  options = options,
                  testTag = testTag)
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

  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
  )
    {
        Image(
            painter = rememberImagePainter(data = R.drawable.rectangle_inner_shadow),
            contentDescription = "Rectangle",
            modifier = Modifier.size(150.dp).clickable { expanded = true }.testTag(testTag + "Button"))
        Row{
            Image(
                painter = rememberImagePainter(data = R.drawable.arrow_white),
                contentDescription = "Rectangle",
                modifier = Modifier.size(15.dp))
            Text(text = currentValue.toString(), color = Color.White, fontFamily = OpenSans, fontSize = ButtonFontSize)
        }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
            text = { Text(option.toString()) },
            onClick = {
              onValueChange(option)
              expanded = false
            },
            modifier = Modifier.testTag(testTag + option.toString()))
      }
    }
  }
}

@Composable
fun SubmitButton(onClick: () -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Button(
            onClick = {onClick()},
            shape = smallButtonShape,
            modifier = Modifier.testTag("preferencesSaveButton")
                .fillMaxWidth(0.4f)
                .shadow(4.dp, smallButtonShape)
                .background(brush = BlueGradient, shape = smallButtonShape),
            colors = ButtonDefaults.buttonColors(Transparent)
        ) {
            Text(text = stringResource(id = R.string.SubmitButton), color = White, fontFamily = OpenSans, fontSize = TitleFontSize, fontWeight = FontWeight.SemiBold)
        }
    }
}

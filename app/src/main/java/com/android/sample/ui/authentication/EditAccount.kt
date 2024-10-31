package com.android.sample.ui.authentication

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.userAccount.*
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.viewmodel.UserAccountViewModel
import java.util.*

@Composable
fun EditAccount(
    userAccountViewModel: UserAccountViewModel = viewModel(factory = UserAccountViewModel.Factory),
    navigationActions: NavigationActions
) {
  val userAccount by userAccountViewModel.userAccount.collectAsState() // Observe the user account
  var profileImageUri by remember { mutableStateOf<Uri?>(null) }
  var originalProfileImageUri by remember {
    mutableStateOf<Uri?>(null)
  } // Hold the original image URI

  // Initialize form fields only if userAccount is not null
  var userId by remember { mutableStateOf("") }
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var height by remember { mutableStateOf("") }
  var weight by remember { mutableStateOf("") }
  var heightUnit by remember { mutableStateOf(HeightUnit.CM) }
  var weightUnit by remember { mutableStateOf(WeightUnit.KG) }
  var gender by remember { mutableStateOf(Gender.MALE) }
  var birthDate by remember { mutableStateOf("") }

  LaunchedEffect(userAccount) {
    userAccount?.let {
      userId = it.userId
      firstName = it.firstName
      lastName = it.lastName
      height = it.height.toString()
      weight = it.weight.toString()
      heightUnit = it.heightUnit
      weightUnit = it.weightUnit
      gender = it.gender
      birthDate =
          it.birthDate.let { timestamp ->
            val calendar = Calendar.getInstance()
            calendar.time = timestamp.toDate()
            "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
          }
      profileImageUri = Uri.parse(it.profileImageUrl)
      originalProfileImageUri = Uri.parse(it.profileImageUrl) // Set the original URI
    }
  }

  val context = LocalContext.current

  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri -> profileImageUri = uri })

  AccountForm(
      profileImageUri = profileImageUri,
      onImageClick = { imagePickerLauncher.launch("image/*") },
      firstName = firstName,
      lastName = lastName,
      onFirstNameChange = { firstName = it },
      onLastNameChange = { lastName = it },
      height = height,
      weight = weight,
      heightUnit = heightUnit,
      weightUnit = weightUnit,
      onHeightChange = { height = it },
      onWeightChange = { weight = it },
      onHeightUnitChange = { heightUnit = it },
      onWeightUnitChange = { weightUnit = it },
      gender = gender,
      onGenderChange = { gender = it },
      birthDate = birthDate,
      onBirthDateChange = { birthDate = it },
      buttonText = "Save Changes",
      onButtonClick = {
        saveAccount(
            isNewAccount = false,
            userAccountViewModel = userAccountViewModel,
            navigationActions = navigationActions,
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            height = height,
            heightUnit = heightUnit,
            weight = weight,
            weightUnit = weightUnit,
            gender = gender,
            birthDate = birthDate,
            profileImageUri = profileImageUri,
            originalProfileImageUri = originalProfileImageUri,
            context = context)
      },
      isButtonEnabled = firstName.isNotBlank(),
      testTag = "editScreen")
}

package com.android.sample.ui.authentication

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.userAccount.*
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.viewmodel.UserAccountViewModel
import com.google.firebase.Timestamp
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

  Column(
      modifier =
          Modifier.padding(16.dp)
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .testTag("addScreen"),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileImagePicker(profileImageUri) { imagePickerLauncher.launch("image/*") }
        Spacer(modifier = Modifier.height(8.dp))
        NameInputFields(
            firstName,
            lastName,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it })
        HeightWeightInput(
            height,
            weight,
            heightUnit,
            weightUnit,
            onHeightChange = { height = it },
            onWeightChange = { weight = it },
            onHeightUnitChange = { heightUnit = it },
            onWeightUnitChange = { weightUnit = it })
        GenderSelection(gender) { gender = it }
        BirthdayInput(birthDate) { birthDate = it }
        ActionButton(
            "Save Changes",
            onClick = {
              if (profileImageUri != null && profileImageUri != originalProfileImageUri) {
                uploadProfileImage(
                    profileImageUri!!,
                    userId,
                    onSuccess = { downloadUrl ->
                      val calendar = GregorianCalendar()
                      val parts = birthDate.split("/")
                      if (parts.size == 3) {
                        try {
                          calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                          userAccountViewModel.updateUserAccount(
                              UserAccount(
                                  userId = userAccount!!.userId,
                                  firstName = firstName,
                                  lastName = lastName,
                                  height = height.toFloatOrNull() ?: 0f,
                                  heightUnit = heightUnit,
                                  weight = weight.toFloatOrNull() ?: 0f,
                                  weightUnit = weightUnit,
                                  gender = gender,
                                  birthDate = Timestamp(calendar.time),
                                  profileImageUrl = downloadUrl))
                          navigationActions.goBack()
                        } catch (e: Exception) {
                          Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
                        }
                      }
                    },
                    onFailure = {
                      Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    })
              } else {
                val calendar = GregorianCalendar()
                val parts = birthDate.split("/")
                if (parts.size == 3) {
                  try {
                    calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                    userAccountViewModel.updateUserAccount(
                        UserAccount(
                            userId = userAccount!!.userId,
                            firstName = firstName,
                            lastName = lastName,
                            height = height.toFloatOrNull() ?: 0f,
                            heightUnit = heightUnit,
                            weight = weight.toFloatOrNull() ?: 0f,
                            weightUnit = weightUnit,
                            gender = gender,
                            birthDate = Timestamp(calendar.time),
                            profileImageUrl = userAccount!!.profileImageUrl))
                    navigationActions.goBack()
                  } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
                  }
                }
              }
            },
            enabled = firstName.isNotBlank())
      }
}

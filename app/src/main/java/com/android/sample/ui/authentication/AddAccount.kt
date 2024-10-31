package com.android.sample.ui.authentication

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.sample.model.userAccount.*
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.viewmodel.UserAccountViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import java.util.GregorianCalendar
import java.util.UUID

@Composable
fun AddAccount(
    userAccountViewModel: UserAccountViewModel = viewModel(factory = UserAccountViewModel.Factory),
    navigationActions: NavigationActions,
    userId: String? = Firebase.auth.currentUser?.uid
) {

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var height by remember { mutableStateOf("") }
  var weight by remember { mutableStateOf("") }
  var heightUnit by remember { mutableStateOf(HeightUnit.CM) }
  var weightUnit by remember { mutableStateOf(WeightUnit.KG) }
  var gender by remember { mutableStateOf(Gender.MALE) }
  var birthDate by remember { mutableStateOf("") }

  var profileImageUri by remember { mutableStateOf<Uri?>(null) }

  val context = LocalContext.current

  // Retrieve current user UID from Firebase Auth
  val actualUserId = userId ?: return // Ensure user is signed in

  // Initialize the image picker launcher outside of the clickable scope
  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri -> profileImageUri = uri })

  Column(
      modifier =
          Modifier.padding(16.dp)
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .testTag("addScreen"), // Made the column scrollable
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
            "Submit",
            onClick = {saveAccount(
                isNewAccount = true,
                userAccountViewModel = userAccountViewModel,
                navigationActions = navigationActions,
                userId = actualUserId,
                firstName = firstName,
                lastName = lastName,
                height = height,
                heightUnit = heightUnit,
                weight = weight,
                weightUnit = weightUnit,
                gender = gender,
                birthDate = birthDate,
                profileImageUri = profileImageUri,
                originalProfileImageUri = null,
                context = context
            )
            },
            enabled = firstName.isNotBlank())
      }
}

@Composable
fun ProfileImagePicker(profileImageUri: Uri?, onImageClick: () -> Unit) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.size(80.dp)
              .clip(CircleShape)
              .border(1.dp, Color.Gray, CircleShape)
              .testTag("profileImage")
              .clickable { onImageClick() }) {
        if (profileImageUri != null) {
          Image(
              painter = rememberAsyncImagePainter(profileImageUri),
              contentDescription = "Profile Image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(80.dp).clip(CircleShape))
        } else {
          Text("Tap to add photo", fontSize = 8.sp, color = Color.Gray)
        }
      }
}

@Composable
fun NameInputFields(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit
) {
  TextField(
      value = firstName,
      onValueChange = onFirstNameChange,
      label = { Text("First Name") },
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).testTag("firstName"))
  TextField(
      value = lastName,
      onValueChange = onLastNameChange,
      label = { Text("Last Name") },
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).testTag("lastName"))
}

@Composable
fun HeightWeightInput(
    height: String,
    weight: String,
    heightUnit: HeightUnit,
    weightUnit: WeightUnit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightUnitChange: (HeightUnit) -> Unit,
    onWeightUnitChange: (WeightUnit) -> Unit
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
        TextField(
            value = height,
            onValueChange = onHeightChange,
            label = { Text("Height") },
            modifier = Modifier.weight(1f).testTag("height"))
        Spacer(modifier = Modifier.width(6.dp))
        DropdownMenuButton(
            selectedOption = heightUnit,
            options = HeightUnit.entries,
            onOptionSelected = onHeightUnitChange,
            modifier = Modifier.width(100.dp).testTag("heightUnit"))
      }
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
        TextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Weight") },
            modifier = Modifier.weight(1f).testTag("weight"))
        Spacer(modifier = Modifier.width(6.dp))
        DropdownMenuButton(
            selectedOption = weightUnit,
            options = WeightUnit.entries,
            onOptionSelected = onWeightUnitChange,
            modifier = Modifier.width(90.dp).testTag("weightUnit"))
      }
}

@Composable
fun GenderSelection(gender: Gender, onGenderChange: (Gender) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().testTag("gender")) {
    Button(
        onClick = { onGenderChange(Gender.MALE) },
        modifier = Modifier.weight(1f).height(40.dp),
        colors =
            ButtonDefaults.buttonColors(
                if (gender == Gender.MALE) MaterialTheme.colorScheme.primary else Color.Gray)) {
          Text("Male", fontSize = 12.sp)
        }
    Spacer(modifier = Modifier.width(6.dp))
    Button(
        onClick = { onGenderChange(Gender.FEMALE) },
        modifier = Modifier.weight(1f).height(40.dp),
        colors =
            ButtonDefaults.buttonColors(
                if (gender == Gender.FEMALE) MaterialTheme.colorScheme.primary else Color.Gray)) {
          Text("Female", fontSize = 12.sp)
        }
  }
}

@Composable
fun BirthdayInput(birthDate: String, onBirthDateChange: (String) -> Unit) {
  OutlinedTextField(
      value = birthDate,
      onValueChange = onBirthDateChange,
      label = { Text("Birthday") },
      placeholder = { Text("DD/MM/YYYY") },
      modifier = Modifier.fillMaxWidth().testTag("birthday"))
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
  Button(
      onClick = onClick,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(48.dp).testTag("submit"),
      enabled = enabled) {
        Text(text, fontSize = 14.sp)
      }
}

fun saveAccount(
    isNewAccount: Boolean,
    userAccountViewModel: UserAccountViewModel,
    navigationActions: NavigationActions,
    userId: String,
    firstName: String,
    lastName: String,
    height: String,
    heightUnit: HeightUnit,
    weight: String,
    weightUnit: WeightUnit,
    gender: Gender,
    birthDate: String,
    profileImageUri: Uri?,
    originalProfileImageUri: Uri?,
    context: android.content.Context
) {
    val calendar = GregorianCalendar()
    val parts = birthDate.split("/")
    if (parts.size == 3) {
        try {
            calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
            val profileImageUrl = profileImageUri?.toString() ?: ""

            val accountData = UserAccount(
                userId = userId,
                firstName = firstName,
                lastName = lastName,
                height = height.toFloatOrNull() ?: 0f,
                heightUnit = heightUnit,
                weight = weight.toFloatOrNull() ?: 0f,
                weightUnit = weightUnit,
                gender = gender,
                birthDate = Timestamp(calendar.time),
                profileImageUrl = profileImageUrl
            )

            if (profileImageUri != null && profileImageUri != originalProfileImageUri) {
                uploadProfileImage(
                    profileImageUri,
                    userId,
                    onSuccess = { downloadUrl ->
                        accountData.profileImageUrl = downloadUrl
                        if (isNewAccount) {
                            userAccountViewModel.createUserAccount(accountData)
                            navigationActions.navigateTo(TopLevelDestinations.MAIN)
                        } else {
                            userAccountViewModel.updateUserAccount(accountData)
                            navigationActions.goBack()
                        }
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                if (isNewAccount) {
                    userAccountViewModel.createUserAccount(accountData)
                    navigationActions.navigateTo(TopLevelDestinations.MAIN)
                } else {
                    userAccountViewModel.updateUserAccount(accountData)
                    navigationActions.goBack()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuButton(
    selectedOption: T,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
    OutlinedTextField(
        value = selectedOption.toString(),
        onValueChange = {},
        readOnly = true,
        label = { Text("Unit") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = modifier.menuAnchor().clickable { expanded = true })

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
            text = { Text(option.toString()) },
            onClick = {
              onOptionSelected(option)
              expanded = false
            })
      }
    }
  }
}

val storageRef = FirebaseStorage.getInstance().reference

fun uploadProfileImage(
    uri: Uri,
    userId: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
  val profileImageRef = storageRef.child("profile_images/${userId}/${UUID.randomUUID()}.jpg")
  profileImageRef
      .putFile(uri)
      .addOnSuccessListener {
        profileImageRef.downloadUrl.addOnSuccessListener { downloadUri ->
          onSuccess(downloadUri.toString())
        }
      }
      .addOnFailureListener { exception -> onFailure(exception) }
}

package com.android.sample.ui.authentication

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@SuppressLint("SuspiciousIndentation")
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
      modifier = Modifier.padding(16.dp).fillMaxSize().testTag("addScreen"),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Profile picture section
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(120.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
                    .testTag("profileImage")
                    .clickable {
                      // Launch the image picker when clicking the box
                      imagePickerLauncher.launch("image/*")
                    }) {
              if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(120.dp).clip(CircleShape))
              } else {
                // Placeholder if no image selected
                Text("Tap to add photo", fontSize = 14.sp, color = Color.Gray)
              }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Aligning remaining fields to the left
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start // Left alignment for the remaining fields
            ) {
              TextField(
                  value = firstName,
                  onValueChange = { firstName = it },
                  label = { Text("First Name") },
                  modifier = Modifier.fillMaxWidth().testTag("firstName"))
              TextField(
                  value = lastName,
                  onValueChange = { lastName = it },
                  label = { Text("Last Name") },
                  modifier = Modifier.fillMaxWidth().testTag("lastName"))

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height") },
                        modifier = Modifier.weight(1f).testTag("height"))
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuButton(
                        selectedOption = heightUnit,
                        options = HeightUnit.entries,
                        onOptionSelected = { heightUnit = it },
                        modifier = Modifier.width(100.dp).testTag("heightUnit"))
                  }

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f).testTag("weight"))
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuButton(
                        selectedOption = weightUnit,
                        options = WeightUnit.entries,
                        onOptionSelected = { weightUnit = it },
                        modifier = Modifier.width(90.dp).testTag("weightUnit"))
                  }

              Row(
                  modifier =
                      Modifier.fillMaxWidth().testTag("gender") // Not sure if the position is right
                  ) {
                    Button(
                        onClick = { gender = Gender.MALE },
                        colors =
                            ButtonDefaults.buttonColors(
                                if (gender == Gender.MALE) MaterialTheme.colorScheme.primary
                                else Color.Gray)) {
                          Text("Male")
                        }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { gender = Gender.FEMALE },
                        colors =
                            ButtonDefaults.buttonColors(
                                if (gender == Gender.FEMALE) MaterialTheme.colorScheme.primary
                                else Color.Gray)) {
                          Text("Female")
                        }
                  }

              OutlinedTextField(
                  value = birthDate,
                  onValueChange = { birthDate = it },
                  label = { Text("Birthday") },
                  placeholder = { Text("DD/MM/YYYY") },
                  modifier = Modifier.fillMaxWidth().testTag("birthday"))

              Button(
                  onClick = {
                    if (profileImageUri != null) {
                      uploadProfileImage(
                          profileImageUri!!,
                          actualUserId,
                          onSuccess = { downloadUrl ->
                            val calendar = GregorianCalendar()
                            val parts = birthDate.split("/")
                            if (parts.size == 3) {
                              try {
                                calendar.set(
                                    parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                                userAccountViewModel.createUserAccount(
                                    UserAccount(
                                        userId = actualUserId,
                                        firstName = firstName,
                                        lastName = lastName,
                                        height = height.toFloatOrNull() ?: 0f,
                                        heightUnit = heightUnit,
                                        weight = weight.toFloatOrNull() ?: 0f,
                                        weightUnit = weightUnit,
                                        gender = gender,
                                        birthDate = Timestamp(calendar.time),
                                        profileImageUrl = downloadUrl))
                                navigationActions.navigateTo(
                                    TopLevelDestinations.MAIN) // Navigate to main screen
                              } catch (e: Exception) {
                                Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT)
                                    .show()
                              }
                            }
                          },
                          onFailure = {
                            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT)
                                .show()
                          })
                    } else {
                      val calendar = GregorianCalendar()
                      val parts = birthDate.split("/")
                      if (parts.size == 3) {
                        try {
                          calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                          userAccountViewModel.createUserAccount(
                              UserAccount(
                                  userId = actualUserId,
                                  firstName = firstName,
                                  lastName = lastName,
                                  height = height.toFloatOrNull() ?: 0f,
                                  heightUnit = heightUnit,
                                  weight = weight.toFloatOrNull() ?: 0f,
                                  weightUnit = weightUnit,
                                  gender = gender,
                                  birthDate = Timestamp(calendar.time),
                                  profileImageUrl = ""))
                          navigationActions.navigateTo(
                              TopLevelDestinations.MAIN) // Navigate to main screen
                        } catch (e: Exception) {
                          Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
                        }
                      }
                    }
                  },
                  modifier = Modifier.fillMaxWidth().testTag("submit"),
                  enabled = firstName.isNotBlank()) {
                    Text("Submit")
                  }
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

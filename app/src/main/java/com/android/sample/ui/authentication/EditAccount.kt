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
              .testTag("editScreen"), // Made the column scrollable
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Profile picture section
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(80.dp) // Changed from 120.dp to 80.dp for smaller screens
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
                    .testTag("profileImage")
                    .clickable { imagePickerLauncher.launch("image/*") }) {
              if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.size(80.dp).clip(CircleShape)) // Changed from 120.dp to 80.dp
              } else {
                Text("Tap to add photo", fontSize = 8.sp, color = Color.Gray) // Reduced font size
              }
            }

        Spacer(modifier = Modifier.height(8.dp)) // Reduced space after profile picture

        // Align remaining fields to the left
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start) {
              TextField(
                  value = firstName,
                  onValueChange = { firstName = it },
                  label = { Text("First Name") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 8.dp, vertical = 4.dp)
                          .testTag("firstName")) // Added padding
              TextField(
                  value = lastName,
                  onValueChange = { lastName = it },
                  label = { Text("Last Name") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 8.dp, vertical = 4.dp)
                          .testTag("lastName")) // Added padding

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 8.dp, vertical = 4.dp)) { // Added padding
                    TextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height") },
                        modifier = Modifier.weight(1f).testTag("height"))
                    Spacer(
                        modifier =
                            Modifier.width(6.dp)) // Reduced space between height and unit fields
                    DropdownMenuButton(
                        selectedOption = heightUnit,
                        options = HeightUnit.entries,
                        onOptionSelected = { heightUnit = it },
                        modifier =
                            Modifier.width(100.dp)
                                .testTag("heightUnit")) // Reduced width for unit dropdown
              }

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 8.dp, vertical = 4.dp)) { // Added padding
                    TextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f).testTag("weight"))
                    Spacer(
                        modifier =
                            Modifier.width(6.dp)) // Reduced space between weight and unit fields
                    DropdownMenuButton(
                        selectedOption = weightUnit,
                        options = WeightUnit.entries,
                        onOptionSelected = { weightUnit = it },
                        modifier =
                            Modifier.width(90.dp)
                                .testTag("weightUnit")) // Reduced width for unit dropdown
              }

              Row(modifier = Modifier.fillMaxWidth().testTag("gender")) {
                Button(
                    onClick = { gender = Gender.MALE },
                    modifier =
                        Modifier.weight(1f).height(40.dp), // Adjusted height for smaller button
                    colors =
                        ButtonDefaults.buttonColors(
                            if (gender == Gender.MALE) MaterialTheme.colorScheme.primary
                            else Color.Gray)) {
                      Text("Male", fontSize = 12.sp) // Reduced font size
                }
                Spacer(modifier = Modifier.width(6.dp)) // Reduced space between gender buttons
                Button(
                    onClick = { gender = Gender.FEMALE },
                    modifier =
                        Modifier.weight(1f).height(40.dp), // Adjusted height for smaller button
                    colors =
                        ButtonDefaults.buttonColors(
                            if (gender == Gender.FEMALE) MaterialTheme.colorScheme.primary
                            else Color.Gray)) {
                      Text("Female", fontSize = 12.sp) // Reduced font size
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
                    if (profileImageUri != null && profileImageUri != originalProfileImageUri) {
                      uploadProfileImage(
                          profileImageUri!!,
                          userId,
                          onSuccess = { downloadUrl ->
                            val calendar = GregorianCalendar()
                            val parts = birthDate.split("/")
                            if (parts.size == 3) {
                              try {
                                calendar.set(
                                    parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
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
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 8.dp)
                          .height(48.dp)
                          .testTag("saveChanges"), // Added padding and adjusted height
                  enabled = firstName.isNotBlank()) {
                    Text(
                        "Save Changes",
                        fontSize = 14.sp) // Slightly reduced font size for better fit
              }
            }
      }
}

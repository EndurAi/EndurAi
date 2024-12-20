package com.android.sample.ui.authentication

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.userAccount.*
import com.android.sample.model.userAccount.UserAccountViewModel
import com.android.sample.ui.composables.TopBar
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.BlueGradient
import com.android.sample.ui.theme.FontSizes.MediumTitleFontSize
import com.android.sample.ui.theme.FontSizes.SubtitleFontSize
import com.android.sample.ui.theme.Grey
import com.android.sample.ui.theme.OpenSans
import com.android.sample.ui.theme.Shape.buttonShape
import com.android.sample.ui.theme.Shape.roundFieldShape
import com.android.sample.ui.theme.Transparent
import com.android.sample.ui.theme.VeryLightBlue
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import java.util.Calendar
import java.util.GregorianCalendar

@Composable
fun AddAccount(
    userAccountViewModel: UserAccountViewModel =
        viewModel(factory = UserAccountViewModel.provideFactory(LocalContext.current)),
    navigationActions: NavigationActions,
    accountExists: Boolean,
    userId: String? = Firebase.auth.currentUser?.uid
) {

  val userAccount by userAccountViewModel.userAccount.collectAsState() // Observe the user account
  var profileImageUri by remember { mutableStateOf<Uri?>(null) }
  var originalProfileImageUri by remember {
    mutableStateOf<Uri?>(null)
  } // Hold the original image URI

  // Initialize form fields only if userAccount is not null
  var userId2 by remember { mutableStateOf("") }
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var height by remember { mutableStateOf("") }
  var weight by remember { mutableStateOf("") }
  var heightUnit by remember { mutableStateOf(HeightUnit.CM) }
  var weightUnit by remember { mutableStateOf(WeightUnit.KG) }
  var gender by remember { mutableStateOf(Gender.MALE) }
  var birthDate by remember { mutableStateOf("") }
  var friends by remember { mutableStateOf(listOf<String>()) }
  var sentRequests by remember { mutableStateOf(listOf<String>()) }
  var receivedRequests by remember { mutableStateOf(listOf<String>()) }

  // Retrieve current user UID from Firebase Auth
  val actualUserId = userId ?: return // Ensure user is signed in
  val context = LocalContext.current

  if (accountExists) {
    LaunchedEffect(userAccount) {
      userAccount?.let {
        userId2 = it.userId
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
              "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                            calendar.get(
                                Calendar.YEAR
                            )
                        }"
            }
        // Check for valid URI before assigning it
        try {
          val parsedUri = Uri.parse(it.profileImageUrl)
          if (parsedUri != null && parsedUri.toString().isNotBlank()) {
            profileImageUri = parsedUri
            originalProfileImageUri = parsedUri // Set the original URI if valid
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Invalid profile image URL", Toast.LENGTH_SHORT).show()
          profileImageUri = null
          originalProfileImageUri = null
        }
        friends = it.friends
        sentRequests = it.sentRequests
        receivedRequests = it.receivedRequests
      }
    }
  }

  // Initialize the image picker launcher outside of the clickable scope
  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri -> profileImageUri = uri })

  Column {
    // Add the TopBar only when editing an account
    if (accountExists) {
      TopBar(navigationActions = navigationActions, title = R.string.EditAccount)
    }
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
        buttonText = if (accountExists) "Save Changes" else "Submit",
        onButtonClick = {
          saveAccount(
              isNewAccount = !accountExists,
              userAccountViewModel = userAccountViewModel,
              navigationActions = navigationActions,
              userId = if (accountExists) userId2 else actualUserId,
              firstName = firstName,
              lastName = lastName,
              height = height,
              heightUnit = heightUnit,
              weight = weight,
              weightUnit = weightUnit,
              gender = gender,
              birthDate = birthDate,
              profileImageUri = profileImageUri,
              originalProfileImageUri = if (accountExists) originalProfileImageUri else null,
              friends = friends,
              sentRequests = sentRequests,
              receivedRequests = receivedRequests,
              context = context)
        },
        isButtonEnabled = firstName.isNotBlank(),
        testTag = if (accountExists) "editScreen" else "addScreen")
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
              modifier = Modifier.shadow(4.dp, shape = CircleShape).size(80.dp).clip(CircleShape))
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
      shape = roundFieldShape,
      textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
      value = firstName,
      onValueChange = onFirstNameChange,
      label = { Text("First Name", fontSize = SubtitleFontSize, fontFamily = OpenSans) },
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .shadow(4.dp, shape = roundFieldShape)
              .testTag("firstName"),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))
  TextField(
      shape = roundFieldShape,
      textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
      value = lastName,
      onValueChange = onLastNameChange,
      label = { Text("Last Name", fontSize = SubtitleFontSize, fontFamily = OpenSans) },
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .shadow(4.dp, shape = roundFieldShape)
              .testTag("lastName"),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))
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
            shape = roundFieldShape,
            value = height,
            textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
            onValueChange = onHeightChange,
            label = { Text("Height", fontFamily = OpenSans, fontSize = SubtitleFontSize) },
            modifier = Modifier.weight(1f).shadow(4.dp, shape = roundFieldShape).testTag("height"),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))
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
            shape = roundFieldShape,
            textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Weight", fontSize = SubtitleFontSize, fontFamily = OpenSans) },
            modifier = Modifier.weight(1f).shadow(4.dp, shape = roundFieldShape).testTag("weight"),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))
        Spacer(modifier = Modifier.width(6.dp))
        DropdownMenuButton(
            selectedOption = weightUnit,
            options = WeightUnit.entries,
            onOptionSelected = onWeightUnitChange,
            modifier = Modifier.width(100.dp).testTag("weightUnit"))
      }
}

@Composable
fun GenderSelection(gender: Gender, onGenderChange: (Gender) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().testTag("gender")) {
    Button(
        onClick = { onGenderChange(Gender.MALE) },
        modifier =
            Modifier.weight(1f)
                .height(40.dp)
                .then(
                    if (gender == Gender.MALE)
                        Modifier.shadow(4.dp, shape = MaterialTheme.shapes.large)
                    else Modifier),
        colors =
            ButtonDefaults.buttonColors(
                if (gender == Gender.MALE) MaterialTheme.colorScheme.primary else Grey)) {
          Text("Male", fontSize = MediumTitleFontSize, fontFamily = OpenSans)
        }
    Spacer(modifier = Modifier.width(6.dp))
    Button(
        onClick = { onGenderChange(Gender.FEMALE) },
        modifier =
            Modifier.weight(1f)
                .height(40.dp)
                .then(
                    if (gender == Gender.FEMALE)
                        Modifier.shadow(4.dp, shape = MaterialTheme.shapes.large)
                    else Modifier),
        colors =
            ButtonDefaults.buttonColors(
                if (gender == Gender.FEMALE) MaterialTheme.colorScheme.primary else Grey)) {
          Text("Female", fontSize = MediumTitleFontSize, fontFamily = OpenSans)
        }
  }
}

@Composable
fun BirthdayInput(birthDate: String, onBirthDateChange: (String) -> Unit) {
  TextField(
      shape = roundFieldShape,
      textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
      value = birthDate,
      onValueChange = onBirthDateChange,
      label = { Text("Birthday", fontFamily = OpenSans, fontSize = SubtitleFontSize) },
      placeholder = { Text("DD/MM/YYYY", fontFamily = OpenSans) },
      modifier = Modifier.fillMaxWidth().shadow(4.dp, shape = roundFieldShape).testTag("birthday"),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
  Button(
      shape = buttonShape,
      colors = ButtonDefaults.buttonColors(Transparent),
      onClick = onClick,
      modifier =
          Modifier.fillMaxWidth(0.7f)
              .padding(horizontal = 8.dp)
              .height(48.dp)
              .shadow(4.dp, shape = buttonShape)
              .background(brush = BlueGradient, shape = buttonShape)
              .testTag("submit"),
      enabled = enabled) {
        Text(text, fontSize = MediumTitleFontSize)
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
    context: android.content.Context,
    friends: List<String>,
    sentRequests: List<String>,
    receivedRequests: List<String>
) {
  val calendar = GregorianCalendar()
  val parts = birthDate.split("/")
  if (parts.size == 3) {
    try {
      calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
      val profileImageUrl = profileImageUri?.toString() ?: ""

      val accountData =
          UserAccount(
              userId = userId,
              firstName = firstName,
              lastName = lastName,
              height = height.toFloatOrNull() ?: 0f,
              heightUnit = heightUnit,
              weight = weight.toFloatOrNull() ?: 0f,
              weightUnit = weightUnit,
              gender = gender,
              birthDate = Timestamp(calendar.time),
              profileImageUrl = profileImageUrl,
              friends = friends,
              sentRequests = sentRequests,
              receivedRequests = receivedRequests)

      if (profileImageUri != null && profileImageUri != originalProfileImageUri) {
        userAccountViewModel.uploadProfileImage(
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
            })
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

@Composable
fun AccountForm(
    profileImageUri: Uri?,
    onImageClick: () -> Unit,
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    height: String,
    weight: String,
    heightUnit: HeightUnit,
    weightUnit: WeightUnit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightUnitChange: (HeightUnit) -> Unit,
    onWeightUnitChange: (WeightUnit) -> Unit,
    gender: Gender,
    onGenderChange: (Gender) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean,
    testTag: String
) {
  Column(
      modifier =
          Modifier.padding(16.dp)
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .testTag(testTag),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileImagePicker(profileImageUri, onImageClick)
        Spacer(modifier = Modifier.height(8.dp))
        NameInputFields(firstName, lastName, onFirstNameChange, onLastNameChange)
        HeightWeightInput(
            height,
            weight,
            heightUnit,
            weightUnit,
            onHeightChange,
            onWeightChange,
            onHeightUnitChange,
            onWeightUnitChange)
        GenderSelection(gender, onGenderChange)
        BirthdayInput(birthDate, onBirthDateChange)
        Spacer(modifier = Modifier.fillMaxHeight(0.3f))
        ActionButton(buttonText, onButtonClick, isButtonEnabled)
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
    TextField(
        value = selectedOption.toString(),
        onValueChange = {},
        textStyle = TextStyle(fontFamily = OpenSans, fontSize = SubtitleFontSize),
        readOnly = true,
        label = { Text("Unit", fontFamily = OpenSans) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = modifier.shadow(4.dp).menuAnchor().clickable { expanded = true },
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = VeryLightBlue, unfocusedContainerColor = VeryLightBlue))

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

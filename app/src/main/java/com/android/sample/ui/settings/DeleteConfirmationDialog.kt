package com.android.sample.ui.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.sample.R

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
      onDismissRequest = { onDismiss() },
      title = { Text(stringResource(id = R.string.ConfirmDeleteTitle)) },
      text = { Text(stringResource(id = R.string.ConfirmDeleteMessage)) },
      confirmButton = {
        TextButton(onClick = { onConfirm() }) { Text(stringResource(id = R.string.Confirm)) }
      },
      dismissButton = {
        TextButton(onClick = { onDismiss() }) { Text(stringResource(id = R.string.Cancel)) }
      })
}

package com.android.sample.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircularButton(onClick: () -> Unit) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.clickable(onClick = onClick)
              .size(60.dp) // Taille du cercle
              .clip(CircleShape) // Forme circulaire
              .background(
                  Color(
                      0xFFD1D5E0)) // Couleur grise claire du cercle // Ombre subtile pour le cercle
      ) {
        Box(
            modifier =
                Modifier.size(25.dp) // Taille du carré arrondi
                    .clip(RoundedCornerShape(3.dp)) // Angles arrondis
                    .background(Color(0xFF7D83AE)) // Couleur violette du carré
            )
      }
}

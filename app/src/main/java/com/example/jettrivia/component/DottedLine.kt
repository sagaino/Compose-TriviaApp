package com.example.jettrivia.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.jettrivia.util.AppColors

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
  ) {
    drawLine(
      color = AppColors.mLightGray,
      start = Offset(x = 0f, y = 0f),
      end = Offset(x = size.width, y = 0f),
      pathEffect = pathEffect
    )
  }
}
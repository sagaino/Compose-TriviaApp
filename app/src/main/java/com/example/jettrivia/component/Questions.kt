package com.example.jettrivia.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screen.QuestionViewModel
import com.example.jettrivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionViewModel) {
  val questions: MutableList<QuestionItem>? = viewModel.data.value.data?.toMutableList()
  val questionIndex = remember {
    mutableIntStateOf(0)
  }
  if (viewModel.data.value.loading == true) {
    CircularProgressIndicator()
    Log.d("LOADING", "Questions: Loading")
  } else {
    val question: QuestionItem? = try {
      questions?.get(questionIndex.intValue)
    } catch (e: Exception) {
      null
    }
    if (questions != null) {
      QuestionDisplay(question = question!!, questionIndex, viewModel) {
        if (questionIndex.intValue + 1 == viewModel.getTotalQuestionCount()) {
          run {}
        } else {
          questionIndex.intValue += 1
        }
      }
    }
  }
}

//@Preview
@Composable
fun QuestionDisplay(
  question: QuestionItem,
  questionIndex: MutableState<Int>,
  viewModel: QuestionViewModel,
  onNextClicked: (Int) -> Unit = {}
) {
  val choiceState = remember(question) {
    question.choices.toMutableList()
  }
  val answerState = remember(question) {
    mutableStateOf<Int?>(null)
  }
  val correctAnswerState = remember(question) {
    mutableStateOf<Boolean?>(null)
  }
  val updateAnswer: (Int) -> Unit = remember(question) {
    {
      answerState.value = it
      correctAnswerState.value = choiceState[it] == question.answer
    }
  }
  val pathEffect: PathEffect = PathEffect.dashPathEffect(
    floatArrayOf(10f, 10f), 0f
  )
  Surface(
    modifier = Modifier.fillMaxSize(), color = AppColors.mDarkPurple
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start,
    ) {
      if (questionIndex.value >= 3) ShowProgress(questionIndex.value + 1)
      QuestionTracker(
        counter = questionIndex.value + 1,
        outOf = viewModel.getTotalQuestionCount(),
      )
      DrawDottedLine(pathEffect)

      Column {
        Text(
          modifier = Modifier
            .padding(6.dp)
            .align(Alignment.Start)
            .fillMaxHeight(0.3f),
          text = question.question,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          lineHeight = 22.sp,
          color = AppColors.mOffWhite,
        )
      }

      //choice
      choiceState.forEachIndexed { index, answerText ->
        Row(
          modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
              width = 4.dp,
              brush = Brush.linearGradient(
                colors = listOf(AppColors.mOffDarkPurple, AppColors.mOffDarkPurple)
              ),
              shape = RoundedCornerShape(15.dp),
            )
            .clip(
              RoundedCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomEndPercent = 50,
                bottomStartPercent = 50,
              )
            )
            .background(Color.Transparent), verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            modifier = Modifier.padding(6.dp),
            colors = RadioButtonDefaults.colors(
              selectedColor = if (correctAnswerState.value == true && index == answerState.value) {
                Color.Green.copy(alpha = 0.2f)
              } else {
                Color.Red.copy(alpha = 0.2f)
              }
            ),
            selected = (answerState.value == index),
            onClick = {
              updateAnswer(index)
            },
          )
          val annotatedString = buildAnnotatedString {
            withStyle(
              style = SpanStyle(
                fontWeight = FontWeight.Light,
                color = if (correctAnswerState.value == true && index == answerState.value) {
                  Color.Green.copy(alpha = 0.2f)
                } else if (correctAnswerState.value == false && index == answerState.value) {
                  Color.Red.copy(alpha = 0.2f)
                } else {
                  AppColors.mOffWhite
                },
                fontSize = 17.sp
              )
            ) {
              append(answerText)
            }
          }
          Text(text = annotatedString, modifier = Modifier.padding(6.dp))
        }
      }

      Button(
        onClick = { onNextClicked(questionIndex.value) },
        modifier = Modifier
          .padding(3.dp)
          .align(alignment = Alignment.CenterHorizontally),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = AppColors.mLightBlue,
        )
      ) {
        Text(
          text = "Next",
          modifier = Modifier.padding(4.dp),
          color = AppColors.mOffWhite,
          fontSize = 17.sp,
        )
      }
    }
  }
}


@Preview
@Composable
fun ShowProgress(score: Int = 220000) {
  val gradient = Brush.linearGradient(
    listOf(
      Color(0xFFF95075),
      Color(0xFFBE6BE5),
    )
  )
  val progressFactor by remember(score) {
    mutableFloatStateOf(score * 0.005f)
  }
  Row(
    modifier = Modifier
      .padding(3.dp)
      .fillMaxWidth()
      .height(45.dp)
      .border(
        width = 4.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            AppColors.mLightPurple, AppColors.mLightPurple
          )
        ),
        shape = RoundedCornerShape(34.dp),
      )
      .clip(
        RoundedCornerShape(
          topStartPercent = 50, topEndPercent = 50, bottomStartPercent = 50, bottomEndPercent = 50
        )
      )
      .background(Color.Transparent),
    verticalAlignment = Alignment.CenterVertically,

    ) {
    Button(
      onClick = { /*TODO*/ },
      contentPadding = PaddingValues(1.dp),
      modifier = Modifier
        .fillMaxWidth(
          progressFactor
        )
        .background(brush = gradient),
      enabled = false,
      elevation = null,
      colors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
      )
    ) {
      Text(
        text = (score * 10).toString(),
        modifier = Modifier
          .clip(shape = RoundedCornerShape(23.dp))
          .fillMaxHeight(0.87f)
          .fillMaxWidth()
          .padding(8.dp),
        color = AppColors.mOffWhite,
        textAlign = TextAlign.Center,
      )
    }
  }
}
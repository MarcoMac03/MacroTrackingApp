package org.example.project
import org.example.project.ui.FoodScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

import macrotracking.composeapp.generated.resources.Res
import macrotracking.composeapp.generated.resources.compose_multiplatform
import org.example.project.ui.DarkBackground
import org.example.project.ui.InsertMeal
import org.example.project.ui.MintGreen

@Composable
@Preview
fun App() {
    var addMeal by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = Typography()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(DarkBackground).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally//, verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FitPath",
                color = MintGreen,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp, top = 32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if(addMeal)
                InsertMeal(onSave = { addMeal = false })
            else {
                Button(
                    onClick = { addMeal = !addMeal },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add Meal")
                }
            }
        }
    }
}
package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.project.NetworkClient
import org.example.project.models.DailyStats
import android.util.Log
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileArea(onBack: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("Marco") }
    var stats by remember { mutableStateOf(DailyStats()) }
    var loading by remember { mutableStateOf(true) }
    //val scope = rememberCoroutineScope()
    LaunchedEffect(stats) {
        try {
            stats = NetworkClient.client.get("${NetworkClient.BASE_URL}/userMeals?userId=0").body()
            loading = false
        } catch (e: Exception) {
            Log.e("Network error", "Errore nella get: ${e.localizedMessage}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MintGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "The button works", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifica")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = MintGreen
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile photo",
                    tint = MintGreen,
                    modifier = Modifier.size(80.dp)
                )
            }
            Text(
                text = "Ciao, $name",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, top = 32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            StatisticSection(title = "Calorie assunte", value = stats.calories.toString() + " kcal")
            StatisticSection(title = "Peso attuale", value = "80 kg")
            StatisticSection(title = "Pasti registrati oggi", value = stats.mealCount.toString())
        }
    }
}

@Composable
fun StatisticSection(title: String, value: String) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = MintGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = value, color = TextWhite, fontSize = 16.sp)
        }
    }
}

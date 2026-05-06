package org.example.project.ui

import android.net.http.HttpResponseCache.install
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import org.example.project.models.FoodRequest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

val DarkBackground = Color(0xFF121212)
val SurfaceColor = Color(0xFF1E1E1E)
val MintGreen = Color(0xFF00E676)
val TextWhite = Color(0xFFFFFFFF)

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var sugars by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var saturatedFats by  remember { mutableStateOf("") }
    var fibers by remember { mutableStateOf("") }

    var foodList by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).padding(4.dp)
    ) {
        Text(
            text = "Add food",
            color = MintGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 32.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 16.dp, end = 16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name", color = Color.Gray, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = sugars,
                    onValueChange = { sugars = it },
                    label = { Text("Sugars (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = proteins,
                    onValueChange = { proteins = it },
                    label = { Text("Proteins (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fats,
                    onValueChange = { fats = it },
                    label = { Text("Fats (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = saturatedFats,
                    onValueChange = { saturatedFats = it },
                    label = { Text("Saturated Fats (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fibers,
                    onValueChange = { fibers = it },
                    label = { Text("Fibers (g)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MintGreen,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                /*
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroInput(value = calories, label = "Kcal", modifier = Modifier.weight(1f)) { calories = it }
                    MacroInput(value = carbs, label = "Carbo (g)", modifier = Modifier.weight(1f)) { carbs = it }
                    MacroInput(value = sugars, label = "Sugars (g)", modifier = Modifier.weight(1f)) { sugars = it }
                    MacroInput(value = proteins, label = "Pro (g)", modifier = Modifier.weight(1f)) { proteins = it }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroInput(value = fats, label = "Fats (g)", modifier = Modifier.weight(1f)) { fats = it }
                    MacroInput(value = saturatedFats, label = "Saturated Fats (g)", modifier = Modifier.weight(1f)) { saturatedFats = it }
                    MacroInput(value = fibers, label = "Fibers (g)", modifier = Modifier.weight(1f)) { fibers = it }
                }

                Spacer(modifier = Modifier.height(8.dp))*/

                /*Button(
                    onClick = {
                        scope.launch {
                            try {
                                val response = client.post("http://192.168.0.255:8080/food") {
                                    contentType(ContentType.Application.Json)
                                    setBody(FoodRequest(
                                        name = name,
                                        brand = null,
                                        calories = calories.toIntOrNull() ?: 0,
                                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                                        sugars = sugars.toDoubleOrNull() ?: 0.0,
                                        protein = proteins.toDoubleOrNull() ?: 0.0,
                                        fats = fats.toDoubleOrNull() ?: 0.0,
                                        saturatedFats = saturatedFats.toDoubleOrNull() ?: 0.0,
                                        fibers = fibers.toDoubleOrNull() ?: 0.0,
                                    ))
                                }
                                if (response.status == HttpStatusCode.OK) {
                                    Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error while adding food!", Toast.LENGTH_SHORT).show()
                                }
                            } catch(e: Exception ) {
                                Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show()
                                println("Errore: ${e.localizedMessage}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = DarkBackground, fontWeight = FontWeight.Bold)
                }*/
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Food list",
            color = TextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(foodList) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item,
                        color = TextWhite,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroInput(value: String, label: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MintGreen,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = MintGreen,
            unfocusedLabelColor = Color.Gray,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
        ),
        modifier = modifier
    )
}
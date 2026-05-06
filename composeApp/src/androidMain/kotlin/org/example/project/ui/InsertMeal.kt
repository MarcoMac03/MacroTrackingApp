package org.example.project.ui

import android.net.http.HttpResponseCache.install
import android.util.Log
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.project.NetworkClient
import org.example.project.models.MealFoodInsert
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertMeal(onSave: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val mealTypes = listOf("Colazione", "Pranzo", "Cena", "Spuntino")
    // tendina per selezionare il type
    var expanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    val addedFoods = remember { mutableStateListOf<FoodRequest>() }
    // per autocompletamento mentre scrivo il cibo
    var searchQuery by remember { mutableStateOf("") }

    var searchResult by remember { mutableStateOf<List<FoodRequest>>(emptyList()) }
    LaunchedEffect(searchQuery) {
        if(searchQuery.length > 2) {
            try {
                searchResult = NetworkClient.client.get("${NetworkClient.BASE_URL}/foods?name=$searchQuery").body()
            } catch(e: Exception) {
                if(e !is CancellationException) {
                    Log.e("Network error", "Errore ricerca: ${e.message}", e)
                }
            }
        } else {
            searchResult = emptyList()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally//, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add Meal",
            color = MintGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp, top = 32.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(bottom = 4.dp, top = 4.dp, start = 16.dp, end = 16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type of meal", color = Color.Gray, fontSize = 14.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = MintGreen,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        mealTypes.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    type = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (addedFoods.isNotEmpty()) {
                    Text("Cibi nel pasto: ", color = MintGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    addedFoods.forEach { food ->
                        Text(text = "- ${food.name}", color = TextWhite, modifier = Modifier.padding(vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Aggiungi cibo", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                    )
                )

                if (searchResult.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                    ) {
                        LazyColumn {
                            items(searchResult) { result ->
                                Text(
                                    text = result.name,
                                    color = TextWhite,
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        addedFoods.add(result)
                                        searchQuery = ""
                                    }.padding(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if(addedFoods.isNotEmpty()){
                    Button(
                        onClick = {
                            Log.d("Meal save", "Adding meal in DB")
                            scope.launch {
                            try {
                                val mealToSave = MealFoodInsert(
                                    type = type,
                                    foodsId = addedFoods.map {it.id}
                                )
                                Log.d("Meal save", "Sending meal to server")
                                val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/meal") {
                                    contentType(ContentType.Application.Json)
                                    setBody(mealToSave)
                                }
                                if(response.status == HttpStatusCode.Created) {
                                    Toast.makeText(context, "Meal Added", Toast.LENGTH_LONG).show()
                                    Log.d("Meal save", "Meal added correctly")
                                    onSave()
                                }
                            } catch(e: Exception ) {
                                Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show()
                                Log.e("Network error","Errore nella post: ${e.localizedMessage}")
                            }
                        }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onSave() },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Back", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }

            }
        }
    }
}
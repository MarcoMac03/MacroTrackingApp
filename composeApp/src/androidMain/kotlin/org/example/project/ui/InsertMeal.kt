package org.example.project.ui

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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import org.example.project.models.FoodRequest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
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
    var expanded by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    val addedFoods = remember { mutableStateListOf<FoodRequest>() }
    var grams by remember { mutableDoubleStateOf(0.0) }
    var showTypeError by remember { mutableStateOf(false) }
    var food by remember { mutableStateOf(FoodRequest()) }
    var searchQuery by remember { mutableStateOf("") }
    var showGrams by remember { mutableStateOf(false) }
    val gramsFocusRequester = remember { FocusRequester() }

    var searchResult by remember { mutableStateOf<List<FoodRequest>>(emptyList()) }
    LaunchedEffect(searchQuery) {
        if (!showGrams && searchQuery.length > 1) {
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
        horizontalAlignment = Alignment.CenterHorizontally
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
                                    showTypeError = false
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                AnimatedVisibility(visible = showTypeError) {
                    Text(
                        text = "Select a type of meal",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (addedFoods.isNotEmpty()) {
                    Text("Cibi nel pasto: ", color = MintGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    addedFoods.forEach { addedFood ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "- ${addedFood.name}, gr = ${addedFood.grams}", color = TextWhite)
                            IconButton(
                                onClick = { addedFoods.remove(addedFood) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; showGrams = false },
                            label = { Text("Aggiungi cibo", color = Color.Gray, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MintGreen,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                            )
                        )
                        if (showGrams) {
                            BadgeGrams(
                                onGramsChange = { grams = it },
                                focusRequester = gramsFocusRequester,
                                modifier = Modifier.width(90.dp)
                            )
                        }
                    }

                    if (searchResult.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp).padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            LazyColumn {
                                items(searchResult) { result ->
                                    Text(
                                        text = result.name,
                                        color = TextWhite,
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            food = result
                                            searchQuery = result.name
                                            showGrams = true
                                            searchResult = emptyList()
                                        }.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (showGrams) {
                        Button(
                            onClick = {
                                addedFoods.add(food.copy(grams = grams))
                                grams = 0.0
                                searchQuery = ""
                                showGrams = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Add", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (addedFoods.isEmpty()) {
                            onSave()
                        } else {
                            scope.launch {
                                if (type.isEmpty()) {
                                    showTypeError = true
                                    return@launch
                                }
                                try {
                                    val mealToSave = MealFoodInsert(
                                        type = type,
                                        foodsId = addedFoods.map { it.id },
                                        quantity = addedFoods.map { it.grams }
                                    )
                                    Log.d("Meal save", "Sending meal to server")
                                    val response = NetworkClient.client.post("${NetworkClient.BASE_URL}/meal") {
                                        contentType(ContentType.Application.Json)
                                        setBody(mealToSave)
                                    }
                                    if (response.status == HttpStatusCode.Created) {
                                        Toast.makeText(context, "Meal Added", Toast.LENGTH_LONG).show()
                                        Log.d("Meal save", "Meal added correctly")
                                        onSave()
                                    }
                                } catch(e: Exception) {
                                    Toast.makeText(context, "Can't connect to server", Toast.LENGTH_SHORT).show()
                                    Log.e("Network error", "Errore nella post: ${e.localizedMessage}")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = if (addedFoods.isEmpty()) "Back" else "Save", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BadgeGrams(
    onGramsChange: (Double) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var g by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = g,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                g = newValue
                onGramsChange(newValue.toDoubleOrNull() ?: 0.0)
            }
        },
        label = { Text("g", color = Color.Gray, fontSize = 12.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.focusRequester(focusRequester),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MintGreen,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
        )
    )
}
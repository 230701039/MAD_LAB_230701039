package com.mealmate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mealmate.app.ui.components.MealMateBottomBar
import com.mealmate.app.viewmodel.MealViewModel
import androidx.compose.runtime.collectAsState
import java.util.Locale

@Composable
fun WeeklyPlannerScreen(
    viewModel: MealViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToPantry: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToShoppingList: () -> Unit
) {
    val plannedMeals by viewModel.plannedMeals.collectAsState()

    data class DayItem(val day: String, val date: String)
    val days = listOf(
        DayItem("Mon", "14"),
        DayItem("Tue", "15"),
        DayItem("Wed", "16"),
        DayItem("Thu", "17"),
        DayItem("Fri", "18"),
        DayItem("Sat", "19")
    )
    val currentDay = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault()).format(java.util.Date())
    var selectedDay by remember { mutableStateOf(currentDay) }
    var showAddDialog by remember { mutableStateOf(false) }
    var currentMealTypeToAdd by remember { mutableStateOf("Breakfast") }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var info by remember { mutableStateOf("") }
        var caloriesText by remember { mutableStateOf("") }
        var proteinText by remember { mutableStateOf("") }
        var carbsText by remember { mutableStateOf("") }
        var fatText by remember { mutableStateOf("") }
        var fiberText by remember { mutableStateOf("") }

        // Update nutrition automatically when title changes
        LaunchedEffect(title) {
            val suggested = viewModel.getSuggestedNutrition(title)
            if (suggested != null) {
                caloriesText = suggested.calories.toString()
                proteinText = String.format(Locale.US, "%.1f", suggested.protein)
                carbsText = String.format(Locale.US, "%.1f", suggested.carbs)
                fatText = String.format(Locale.US, "%.1f", suggested.fat)
                fiberText = String.format(Locale.US, "%.1f", suggested.fiber)
                info = "${suggested.calories} kcal • ${String.format(Locale.US, "%.1f", suggested.protein)}g P"
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Add to $currentMealTypeToAdd") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(text = "Meal Name (e.g. 4 Idli)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = info,
                        onValueChange = { info = it },
                        label = { Text(text = "Details (e.g. 300 kcal • 20 min)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Calories (kcal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = proteinText,
                            onValueChange = { proteinText = it },
                            label = { Text("Prot (g)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = carbsText,
                            onValueChange = { carbsText = it },
                            label = { Text("Carb (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fatText,
                            onValueChange = { fatText = it },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fiberText,
                            onValueChange = { fiberText = it },
                            label = { Text("Fiber (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addPlannedMeal(
                            selectedDay, 
                            currentMealTypeToAdd, 
                            title, 
                            info,
                            caloriesText.toIntOrNull() ?: 0,
                            proteinText.toDoubleOrNull() ?: 0.0,
                            carbsText.toDoubleOrNull() ?: 0.0,
                            fatText.toDoubleOrNull() ?: 0.0,
                            fiberText.toDoubleOrNull() ?: 0.0
                        )
                        showAddDialog = false
                    }
                }) { Text(text = "Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            MealMateBottomBar(
                selectedRoute = "planner",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "pantry" -> onNavigateToPantry()
                        "recipes" -> onNavigateToRecipes()
                        "tracker" -> onNavigateToTracker()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToShoppingList,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Tune, contentDescription = "Shopping List")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.9f), shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Box(contentAlignment = Alignment.Center) { Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White)) }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Meal Mate", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Weekly Planner", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Day picker
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    days.forEach { dayItem ->
                        val isSelected = selectedDay == dayItem.day
                        val dailyPlannedCals = viewModel.getPlannedCaloriesForDay(dayItem.day)
                        Surface(
                            modifier = Modifier.width(56.dp).clickable { selectedDay = dayItem.day },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            shadowElevation = if (isSelected) 4.dp else 1.dp
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(dayItem.day, style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(dayItem.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                                if (dailyPlannedCals > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${dailyPlannedCals}", style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Macro Summary Card
                val dailyCals = viewModel.getPlannedCaloriesForDay(selectedDay)
                if (dailyCals > 0) {
                    val p = viewModel.getPlannedProteinForDay(selectedDay)
                    val c = viewModel.getPlannedCarbsForDay(selectedDay)
                    val f = viewModel.getPlannedFatForDay(selectedDay)
                    val fi = viewModel.getPlannedFiberForDay(selectedDay)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Daily Goal Summary",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "$dailyCals kcal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MacroIndicator("Prot", "${p.toInt()}g", MaterialTheme.colorScheme.primary)
                                MacroIndicator("Carbs", "${c.toInt()}g", Color(0xFFF44336))
                                MacroIndicator("Fat", "${f.toInt()}g", Color(0xFFFF9800))
                                MacroIndicator("Fiber", "${fi.toInt()}g", Color(0xFF4CAF50))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Alerts / Notifications for missing meals
                val missingMeals = viewModel.getMissingPlannedMeals(selectedDay)
                if (missingMeals.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Plan missing for: ${missingMeals.joinToString(", ")}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val mealTypes = listOf(
                    Triple("Breakfast", Icons.Filled.FreeBreakfast, "Plan your Breakfast"),
                    Triple("Lunch", Icons.Filled.LightMode, "Plan your Lunch"),
                    Triple("Dinner", Icons.Filled.DarkMode, "Plan your Dinner"),
                    Triple("Snacks", Icons.Filled.Cookie, "Add a Snack")
                )

                mealTypes.forEach { (type, icon, placeholder) ->
                    MealSection(
                        mealIcon = icon,
                        mealType = type,
                        content = {
                            val meals = plannedMeals.filter { it.day == selectedDay && it.mealType == type }
                            if (meals.isEmpty()) {
                                PlannerEmptySlot(placeholder) {
                                    currentMealTypeToAdd = type
                                    showAddDialog = true
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    meals.forEach { meal ->
                                        PlannerMealCard(title = meal.title, info = meal.info) {
                                            viewModel.removePlannedMeal(meal)
                                        }
                                    }
                                    TextButton(onClick = {
                                        currentMealTypeToAdd = type
                                        showAddDialog = true
                                    }) {
                                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                                        Text(" Add more", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun MacroIndicator(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MealSection(mealIcon: androidx.compose.ui.graphics.vector.ImageVector, mealType: String, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(mealIcon, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(mealType, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(12.dp))
    content()
}

@Composable
private fun PlannerMealCard(title: String, info: String, onDelete: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Restaurant, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(28.dp)) }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(info, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun PlannerEmptySlot(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.AddCircleOutline, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        }
    }
}

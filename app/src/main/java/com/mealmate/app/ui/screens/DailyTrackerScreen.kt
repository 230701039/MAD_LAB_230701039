package com.mealmate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mealmate.app.ui.components.MealMateBottomBar
import com.mealmate.app.viewmodel.MealViewModel
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.collectAsState

@Composable
fun DailyTrackerScreen(
    viewModel: MealViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToPantry: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    var showAddMealDialog by remember { mutableStateOf(false) }
    
    val trackedMeals by viewModel.trackedMeals.collectAsState()
    val plannedMeals by viewModel.plannedMeals.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val totalCalories = viewModel.getTotalCalories()
    val calorieGoal = userProfile?.goalCalories ?: 2000
    val progress = if (calorieGoal > 0) totalCalories.toFloat() / calorieGoal.toFloat() else 0f
    val remainingCalories = maxOf(0, calorieGoal - totalCalories)
    
    val currentDay = SimpleDateFormat("EEE", Locale.getDefault()).format(Date())
    val plannedCals = viewModel.getPlannedCaloriesForDay(currentDay)
    val missingMeals = viewModel.getMissingPlannedMeals(currentDay)

    if (showAddMealDialog) {
        var mealTitle by remember { mutableStateOf("") }
        var mealType by remember { mutableStateOf("Breakfast") }
        var caloriesText by remember { mutableStateOf("") }
        var proteinText by remember { mutableStateOf("") }
        var carbsText by remember { mutableStateOf("") }
        var fatText by remember { mutableStateOf("") }
        var fiberText by remember { mutableStateOf("") }
        var mealTime by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())) }
        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

        // Update nutrition automatically when mealTitle changes
        LaunchedEffect(mealTitle) {
            val suggested = viewModel.getSuggestedNutrition(mealTitle)
            if (suggested != null) {
                caloriesText = suggested.calories.toString()
                proteinText = String.format(Locale.US, "%.1f", suggested.protein)
                carbsText = String.format(Locale.US, "%.1f", suggested.carbs)
                fatText = String.format(Locale.US, "%.1f", suggested.fat)
                fiberText = String.format(Locale.US, "%.1f", suggested.fiber)
            }
        }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = { Text("Add Meal") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = mealTitle,
                        onValueChange = { mealTitle = it },
                        label = { Text("Meal Name (e.g. 4 Idli)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Category", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        mealTypes.forEach { type ->
                            FilterChip(
                                selected = mealType == type,
                                onClick = { mealType = type },
                                label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = mealTime,
                        onValueChange = { mealTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) caloriesText = it },
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
                Button(
                    onClick = {
                        val cals = caloriesText.toIntOrNull() ?: 0
                        if (mealTitle.isNotBlank() && cals > 0) {
                            viewModel.addTrackedMeal(
                                mealTitle, 
                                mealType, 
                                cals,
                                proteinText.toDoubleOrNull() ?: 0.0,
                                carbsText.toDoubleOrNull() ?: 0.0,
                                fatText.toDoubleOrNull() ?: 0.0,
                                fiberText.toDoubleOrNull() ?: 0.0,
                                mealTime
                            )
                            showAddMealDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            MealMateBottomBar(
                selectedRoute = "tracker",
                onItemSelected = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "pantry" -> onNavigateToPantry()
                        "recipes" -> onNavigateToRecipes()
                        "planner" -> onNavigateToPlanner()
                        "tracker" -> { /* Already here */ }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAnalytics,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.BarChart, contentDescription = "Analytics")
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Meal Mate",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        IconButton(onClick = { }) {
                            Icon(Icons.Outlined.Notifications, "Notifications", tint = MaterialTheme.colorScheme.outline)
                        }
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // Summary cards row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Calorie ring card
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress.coerceIn(0f, 1f) },
                                    modifier = Modifier.size(110.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 10.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$remainingCalories",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "kcal left",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }

                    // Macro distribution card
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Macros",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            MacroSummaryItem("Protein", "${viewModel.getTotalProtein().toInt()}g", Color(0xFF4CAF50))
                            MacroSummaryItem("Carbs", "${viewModel.getTotalCarbs().toInt()}g", Color(0xFFFF9800))
                            MacroSummaryItem("Fat", "${viewModel.getTotalFat().toInt()}g", Color(0xFFE91E63))
                            MacroSummaryItem("Fiber", "${viewModel.getTotalFiber().toInt()}g", Color(0xFF2196F3))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Active Plan Summary
                Text(
                    "Today's Plan Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EventNote, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Planned for $currentDay", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Text("$plannedCals kcal total", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(modifier = Modifier.alpha(0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        val mealTypes = listOf("Breakfast", "Lunch", "Dinner")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            mealTypes.forEach { type ->
                                val meal = plannedMeals.firstOrNull { it.day == currentDay && it.mealType == type }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text(type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        if (meal != null) "${meal.calories} kcal" else "Not set",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (meal != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        
                        if (missingMeals.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.NotificationsActive, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Alert: Missing ${missingMeals.joinToString(", ")} plan",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Meals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Today's Meals",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showAddMealDialog = true }) {
                        Icon(Icons.Filled.AddCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Meal", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Meal items
                if (trackedMeals.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No meals tracked today.", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    trackedMeals.forEach { meal ->
                        TrackerMealItem(
                            title = meal.title,
                            subtitle = meal.subtitle,
                            kcal = "${meal.calories} kcal",
                            onDelete = { viewModel.removeTrackedMeal(meal) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Hydration card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Daily Hydration",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "1.8",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    " L",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Log Water", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MacroSummaryItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TrackerMealItem(
    title: String,
    subtitle: String,
    kcal: String,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Restaurant, null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(kcal, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer))
                }
            }
        }
    }
}

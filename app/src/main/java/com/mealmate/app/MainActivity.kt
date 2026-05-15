package com.mealmate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mealmate.app.viewmodel.MealViewModel
import com.mealmate.app.viewmodel.MealViewModelFactory
import com.mealmate.app.navigation.MealMateNavGraph
import com.mealmate.app.ui.theme.MealMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealMateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    
                    // Get the database instance from the application class
                    val database = (application as MealMateApplication).database
                    val viewModel: MealViewModel = viewModel(
                        factory = MealViewModelFactory(database.mealDao())
                    )

                    MealMateNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}

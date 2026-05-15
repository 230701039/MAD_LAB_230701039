package com.mealmate.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mealmate.app.ui.screens.*
import com.mealmate.app.viewmodel.MealViewModel

@Composable
fun MealMateNavGraph(navController: NavHostController, viewModel: MealViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) { _ ->
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) { _ ->
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) { _ ->
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) { _ ->
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) { _ ->
            HomeDashboardScreen(
                viewModel = viewModel,
                onNavigateToRecipeDetail = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onNavigateToPantry = {
                    navController.navigate(Screen.Pantry.route)
                },
                onNavigateToRecipes = {
                    navController.navigate(Screen.Recipes.route)
                },
                onNavigateToPlanner = {
                    navController.navigate(Screen.Planner.route)
                },
                onNavigateToTracker = {
                    navController.navigate(Screen.Tracker.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Pantry.route) { _ ->
            PantryInventoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
            )
        }

        composable(Screen.Recipes.route) { _ ->
            RecipeDiscoveryScreen(
                viewModel = viewModel,
                onNavigateToDetail = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToPantry = { navController.navigate(Screen.Pantry.route) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Planner.route) { _ ->
            WeeklyPlannerScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToPantry = { navController.navigate(Screen.Pantry.route) },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToTracker = { navController.navigate(Screen.Tracker.route) },
                onNavigateToShoppingList = { navController.navigate(Screen.ShoppingList.route) }
            )
        }

        composable(Screen.Tracker.route) { _ ->
            DailyTrackerScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToPantry = { navController.navigate(Screen.Pantry.route) },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
            )
        }

        composable(Screen.Analytics.route) { _ ->
            WeeklyAnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToPantry = { navController.navigate(Screen.Pantry.route) },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) }
            )
        }

        composable(Screen.ShoppingList.route) { _ ->
            ShoppingListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToPantry = { navController.navigate(Screen.Pantry.route) },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToPlanner = { navController.navigate(Screen.Planner.route) },
                onNavigateToTracker = { navController.navigate(Screen.Tracker.route) }
            )
        }

        composable(Screen.Profile.route) { _ ->
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

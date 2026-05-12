package com.mealmate.app.navigation

/**
 * Defines all navigation routes for Meal Mate app
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Pantry : Screen("pantry")
    object Recipes : Screen("recipes")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipe_detail/$recipeId"
    }
    object Planner : Screen("planner")
    object Tracker : Screen("tracker")
    object Analytics : Screen("analytics")
    object ShoppingList : Screen("shopping_list")
    object Profile : Screen("profile")
}

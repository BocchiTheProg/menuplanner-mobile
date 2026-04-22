package com.example.menuplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.menuplanner.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Recipes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tab 1: Meal Plans
            composable(BottomNavItem.MealPlans.route) {
                MealPlanListScreen(
                    onMealClick = { mealPlanId ->
                        navController.navigate("meal_plan_detail/$mealPlanId")
                    }
                )
            }
            // Detail Screen (Meal Plan)
            composable("meal_plan_detail/{mealPlanId}") { backStackEntry ->
                val mealPlanId = backStackEntry.arguments?.getString("mealPlanId")
                MealPlanDetailScreen(
                    mealPlanId = mealPlanId,
                    onNavigateBack = { navController.popBackStack() },
                    onRecipeClick = { recipeId ->
                        navController.navigate("recipe_detail/$recipeId")
                    }
                )
            }

            // Tab 2: Recipes (List Screen)
            composable(BottomNavItem.Recipes.route) {
                RecipeListScreen(
                    navController = navController,
                    onRecipeClick = { recipeId ->
                        navController.navigate("recipe_detail/$recipeId")
                    },
                    onCreateClick = {
                        navController.navigate("recipe_create")
                    }
                )
            }
            // Detail Screen (Recipe)
            composable("recipe_detail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            // Recipe Creation Form
            composable("recipe_create") {
                RecipeCreateScreen(navController = navController)
            }

            // Tab 3: Profile (Additional Screen)
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(BottomNavItem.MealPlans, BottomNavItem.Recipes, BottomNavItem.Profile)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route || currentRoute?.startsWith("recipe_detail") == true && item == BottomNavItem.Recipes,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
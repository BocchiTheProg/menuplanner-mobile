package com.example.menuplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.menuplanner.ui.screens.mealPlans.detail.MealPlanDetailScreen
import com.example.menuplanner.ui.screens.mealPlans.list.MealPlanListScreen
import com.example.menuplanner.ui.screens.mealPlans.update.MealPlanUpdateScreen
import com.example.menuplanner.ui.screens.recipes.create.RecipeCreateScreen
import com.example.menuplanner.ui.screens.recipes.detail.RecipeDetailScreen
import com.example.menuplanner.ui.screens.recipes.list.RecipeListScreen
import com.example.menuplanner.ui.screens.recipes.update.RecipeUpdateScreen
import com.example.menuplanner.ui.screens.livefeed.LiveFeedScreen
import com.example.menuplanner.ui.screens.profile.ProfileScreen
import com.example.menuplanner.ui.screens.security.SecurityScreen

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
                    navController = navController,
                    mealPlanId = mealPlanId,
                    onNavigateBack = { navController.popBackStack() },
                    onRecipeClick = { recipeId ->
                        navController.navigate("recipe_detail/$recipeId")
                    }
                )
            }
            // Update Screen (Meal Plan)
            composable("meal_plan_update/{mealPlanId}") { backStackEntry ->
                val mealPlanId = backStackEntry.arguments?.getString("mealPlanId")
                MealPlanUpdateScreen(
                    mealPlanId = mealPlanId,
                    navController = navController
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
                    navController = navController,
                    recipeId = recipeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            // Update Screen (Recipe General Info)
            composable("recipe_update/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                RecipeUpdateScreen(
                    recipeId = recipeId,
                    navController = navController
                )
            }
            // Recipe Creation Form
            composable("recipe_create") {
                RecipeCreateScreen(navController = navController)
            }

            // Tab 3: Live Community Feed (WebSocket)
            composable(BottomNavItem.LiveFeed.route) {
                LiveFeedScreen()
            }

            // Tab 4: Profile Section (Biometric Gatekeeper Screen)
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateToSecurity = {
                        navController.navigate("security_settings")
                    }
                )
            }

            // Nested Sub-Screen: Security Configurations (Accessed only via Profile Gateway)
            composable("security_settings") {
                SecurityScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.MealPlans,
        BottomNavItem.Recipes,
        BottomNavItem.LiveFeed,
        BottomNavItem.Profile
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route ||
                        (currentRoute?.startsWith("recipe") == true && item == BottomNavItem.Recipes) ||
                        (currentRoute?.startsWith("meal_plan") == true && item == BottomNavItem.MealPlans) ||
                        ((currentRoute == "security_settings") && item == BottomNavItem.Profile),
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
package com.example.menuplanner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object MealPlans : BottomNavItem("meal_plans", "Plans", Icons.Default.DateRange)
    object Recipes : BottomNavItem("recipes", "Recipes", Icons.AutoMirrored.Filled.List)
}
package com.example.menuplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanDetailScreen(mealPlanId: String?, onNavigateBack: () -> Unit) {
    // Find the specific object from the model based on ID
    val mealPlan = DummyData.mealPlans.find { it.id.toString() == mealPlanId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Plan Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (mealPlan != null) {
                Text(text = "Meal plan for ${mealPlan.dayOfWeek}", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "ID: ${mealPlan.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Breakfast: ${mealPlan.breakfast.title}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Lunch: ${mealPlan.lunch.title}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Dinner: ${mealPlan.dinner.title}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Preparation status: ${mealPlan.isCooked}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Meal Plan not found.")
            }
        }
    }
}
package com.example.menuplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanDetailScreen(
    mealPlanId: String?,
    onNavigateBack: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    // Find the specific object from the model based on ID
    val mealPlanIndex = DummyData.mealPlans.indexOfFirst { it.id.toString() == mealPlanId }
    val mealPlan = DummyData.mealPlans.getOrNull(mealPlanIndex)

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

                val recipes = listOf(mealPlan.breakfast, mealPlan.lunch, mealPlan.dinner)
                val totalCalories = recipes.sumOf { it.calories }
                val allVegetarian = recipes.all { it.isVegetarian }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Included recipes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                RecipeLinkRow(label = "Breakfast", recipe = mealPlan.breakfast, onRecipeClick = onRecipeClick)
                RecipeLinkRow(label = "Lunch", recipe = mealPlan.lunch, onRecipeClick = onRecipeClick)
                RecipeLinkRow(label = "Dinner", recipe = mealPlan.dinner, onRecipeClick = onRecipeClick)

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Total calories: $totalCalories Cal", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (allVegetarian) "All recipes are vegetarian" else "Includes non-vegetarian recipes",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (mealPlanIndex >= 0) {
                            DummyData.mealPlans[mealPlanIndex] = mealPlan.copy(isCooked = !mealPlan.isCooked)
                        }
                    }
                ) {
                    Text(if (mealPlan.isCooked) "Mark as Not Cooked" else "Mark as Cooked")
                }
            } else {
                Text("Meal Plan not found.")
            }
        }
    }
}

@Composable
private fun RecipeLinkRow(
    label: String,
    recipe: Recipe,
    onRecipeClick: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRecipeClick(recipe.id.toString()) }
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
}
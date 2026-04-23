package com.example.menuplanner.ui.screens.mealPlans

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanUpdateScreen(
    mealPlanId: String?,
    navController: NavController
) {
    val mealPlanIndex = DummyData.mealPlans.indexOfFirst { it.id.toString() == mealPlanId }
    val mealPlan = DummyData.mealPlans.getOrNull(mealPlanIndex)

    if (mealPlan == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Meal plan not found.")
        }
        return
    }

    // State populated with current recipes
    var breakfast by remember { mutableStateOf(mealPlan.breakfast) }
    var lunch by remember { mutableStateOf(mealPlan.lunch) }
    var dinner by remember { mutableStateOf(mealPlan.dinner) }

    val allRecipes = DummyData.recipes
    val totalCalories = breakfast.calories + lunch.calories + dinner.calories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update ${mealPlan.dayOfWeek}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            RecipeDropdown(label = "Breakfast", selectedRecipe = breakfast, options = allRecipes) { breakfast = it }
            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(label = "Lunch", selectedRecipe = lunch, options = allRecipes) { lunch = it }
            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(label = "Dinner", selectedRecipe = dinner, options = allRecipes) { dinner = it }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total Calories: $totalCalories Cal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Update data source
                    DummyData.mealPlans[mealPlanIndex] = mealPlan.copy(
                        breakfast = breakfast,
                        lunch = lunch,
                        dinner = dinner
                    )

                    // Pass success state back and pop
                    navController.previousBackStackEntry?.savedStateHandle?.set("plan_updated", true)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDropdown(
    label: String,
    selectedRecipe: Recipe,
    options: List<Recipe>,
    onSelectionChange: (Recipe) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = "${selectedRecipe.title} (${selectedRecipe.calories} Cal)",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type=MenuAnchorType.PrimaryNotEditable, enabled=true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { recipe ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = recipe.title,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Text(
                                text = "${recipe.calories} Cal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (recipe.isVegetarian) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = "Vegetarian",
                                    tint = Color(0xFF4CAF50), // Standard green for eco/vegetarian
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelectionChange(recipe)
                        expanded = false
                    }
                )
            }
        }
    }
}
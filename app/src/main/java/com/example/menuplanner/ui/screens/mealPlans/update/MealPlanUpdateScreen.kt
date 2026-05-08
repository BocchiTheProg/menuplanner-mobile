package com.example.menuplanner.ui.screens.mealPlans.update

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuplanner.domain.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanUpdateScreen(
    mealPlanId: String?,
    navController: NavController,
    viewModel: MealPlanUpdateViewModel = hiltViewModel()
) {
    LaunchedEffect(mealPlanId) {
        viewModel.loadMealPlan(mealPlanId)
    }

    val mealPlan by viewModel.mealPlan.collectAsState()
    val allRecipes by viewModel.availableRecipes.collectAsState()

    // Local UI state for selections
    var breakfast by remember { mutableStateOf<Recipe?>(null) }
    var lunch by remember { mutableStateOf<Recipe?>(null) }
    var dinner by remember { mutableStateOf<Recipe?>(null) }

    // Sync UI state when data loads
    LaunchedEffect(mealPlan) {
        mealPlan?.let {
            breakfast = it.breakfast
            lunch = it.lunch
            dinner = it.dinner
        }
    }

    val currentPlan = mealPlan
    val currentBreakfast = breakfast
    val currentLunch = lunch
    val currentDinner = dinner

    if (currentPlan == null || currentBreakfast == null || currentLunch == null || currentDinner == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val totalCalories = currentBreakfast.calories + currentLunch.calories + currentDinner.calories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update ${currentPlan.dayOfWeek}") },
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
            RecipeDropdown(label = "Breakfast", selectedRecipe = currentBreakfast, options = allRecipes) { breakfast = it }
            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(label = "Lunch", selectedRecipe = currentLunch, options = allRecipes) { lunch = it }
            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(label = "Dinner", selectedRecipe = currentDinner, options = allRecipes) { dinner = it }

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
                    viewModel.updateMealPlan(currentBreakfast, currentLunch, currentDinner)
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
                                    tint = Color(0xFF4CAF50),
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
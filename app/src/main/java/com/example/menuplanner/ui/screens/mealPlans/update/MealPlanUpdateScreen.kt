package com.example.menuplanner.ui.screens.mealPlans.update

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    LaunchedEffect(mealPlan, allRecipes) {
        val plan = mealPlan
        if (plan != null) {
            if (breakfast == null) {
                breakfast = plan.breakfast ?: allRecipes.firstOrNull()
            }
            // Lunch
            if (lunch == null) {
                lunch = plan.lunch ?: allRecipes.firstOrNull()
            }
            // Dinner
            if (dinner == null) {
                dinner = plan.dinner ?: allRecipes.firstOrNull()
            }
        }
    }

    if (mealPlan == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            mealPlan?.let {
                TopAppBar(
                    title = { Text("Update ${it.dayOfWeek}") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Configure your menu",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(
                label = "Breakfast",
                selectedRecipe = breakfast,
                options = allRecipes
            ) { breakfast = it }

            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(
                label = "Lunch",
                selectedRecipe = lunch,
                options = allRecipes
            ) { lunch = it }

            Spacer(modifier = Modifier.height(16.dp))

            RecipeDropdown(
                label = "Dinner",
                selectedRecipe = dinner,
                options = allRecipes
            ) { dinner = it }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Calories display (Safe check for nulls)
            val totalCalories = (breakfast?.calories ?: 0) +
                    (lunch?.calories ?: 0) +
                    (dinner?.calories ?: 0)

            Text(
                text = "Total Calories: $totalCalories Cal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.weight(1f))

            // Enabled only if the user has actually selected a recipe for all 3 slots
            Button(
                enabled = breakfast != null && lunch != null && dinner != null,
                onClick = {
                    // Using !! is safe here because of the 'enabled' check above
                    viewModel.updateMealPlan(breakfast!!, lunch!!, dinner!!)
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
    selectedRecipe: Recipe?,
    options: List<Recipe>,
    onSelectionChange: (Recipe) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            // Handles "Not set" logic visually
            value = selectedRecipe?.let { "${it.title} (${it.calories} Cal)" } ?: "Select a recipe",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = if (selectedRecipe == null) {
                OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.error
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No recipes found. Please create one!") },
                    onClick = { expanded = false }
                )
            } else {
                options.forEach { recipe ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(recipe.title, modifier = Modifier.weight(1f))
                                Text(
                                    "${recipe.calories} Cal",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (recipe.isVegetarian) {
                                    Icon(
                                        Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.padding(start = 8.dp).size(16.dp)
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
}
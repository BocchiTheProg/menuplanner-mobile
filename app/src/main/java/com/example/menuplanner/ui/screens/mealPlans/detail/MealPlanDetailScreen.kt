package com.example.menuplanner.ui.screens.mealPlans.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuplanner.domain.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanDetailScreen(
    navController: NavController,
    mealPlanId: String?,
    viewModel: MealPlanDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    LaunchedEffect(mealPlanId) {
        viewModel.loadMealPlan(mealPlanId)
    }

    val mealPlan by viewModel.mealPlan.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

//    val currentPlan = mealPlan

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val updateSuccess = savedStateHandle?.get<Boolean>("plan_updated") ?: false

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar("Menu successfully updated!")
            savedStateHandle["plan_updated"] = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Meal Plan Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    mealPlan?.let {
                        IconButton(onClick = { navController.navigate("meal_plan_update/${it.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Meal Plan")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize()
        ) {
            mealPlan?.let { currentPlan ->
                Text(text = "Meal plan for ${currentPlan.dayOfWeek}", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))

                val recipes = listOfNotNull(currentPlan.breakfast, currentPlan.lunch, currentPlan.dinner)
                val totalCalories = recipes.sumOf { it.calories }
                val allVegetarian = if (recipes.isEmpty()) false else recipes.all { it.isVegetarian }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Included recipes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                RecipeLinkRow(label = "Breakfast", recipe = currentPlan.breakfast, onRecipeClick = onRecipeClick)
                RecipeLinkRow(label = "Lunch", recipe = currentPlan.lunch, onRecipeClick = onRecipeClick)
                RecipeLinkRow(label = "Dinner", recipe = currentPlan.dinner, onRecipeClick = onRecipeClick)

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
                Button(onClick = { viewModel.toggleCookedStatus() }) {
                    Text(if (currentPlan.isCooked) "Mark as Not Cooked" else "Mark as Cooked")
                }
            } ?:
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
        }
    }
}

@Composable
private fun RecipeLinkRow(
    label: String,
    recipe: Recipe?,
    onRecipeClick: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", fontWeight = FontWeight.Medium)
        if (recipe != null) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onRecipeClick(recipe.id.toString()) }
            )
        } else {
            Text("Not set", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}
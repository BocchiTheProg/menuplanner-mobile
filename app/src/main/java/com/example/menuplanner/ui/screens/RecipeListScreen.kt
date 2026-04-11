package com.example.menuplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.Recipe
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color

@Composable
fun RecipeListScreen(
    navController: NavController,
    onRecipeClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val recipes = DummyData.recipes // Fetching data
    val snackbarHostState = remember { SnackbarHostState() }

    // Check if returned from a successful creation
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val recipeCreatedResult = savedStateHandle?.get<Boolean>("recipe_created") ?: false

    LaunchedEffect(recipeCreatedResult) {
        if (recipeCreatedResult) {
            snackbarHostState.showSnackbar("Recipe successfully created!")
            // Reset the state
            savedStateHandle["recipe_created"] = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("All Recipes", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(recipes) { recipe ->
                RecipeCard(recipe, onRecipeClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                // Bottom padding to ensure the FAB doesn't cover the last item
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: (String) -> Unit) {
    // Define readable background colors based on dietary flag
    val cardColor = if (recipe.isVegetarian) {
        Color(0xFFE8F5E9) // Very light green
    } else {
        Color(0xFFFFEBEE) // Very light red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id.toString()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleLarge)
            Text(text = "Prep time: ${recipe.prepTimeMinutes} mins")
            Text(text = if (recipe.isVegetarian) "Vegetarian" else "Contains Meat")
        }
    }
}
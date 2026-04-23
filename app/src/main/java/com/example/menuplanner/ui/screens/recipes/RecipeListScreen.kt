package com.example.menuplanner.ui.screens.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.Recipe
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    onRecipeClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val recipes = DummyData.recipes // Fetching data
    val snackbarHostState = remember { SnackbarHostState() }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

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
            items(items = recipes, key = { it.id }) { recipe ->
                SwipeToDeleteRecipeCard(
                    recipe = recipe,
                    onClick = onRecipeClick,
                    onDeleteRequested = { recipeToDelete = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                // Bottom padding to ensure the FAB doesn't cover the last item
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("Delete recipe?") },
            text = { Text("Are you sure you want to delete \"${recipe.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipes.remove(recipe)
                        recipeToDelete = null
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteRecipeCard(
    recipe: Recipe,
    onClick: (String) -> Unit,
    onDeleteRequested: (Recipe) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequested(recipe)
                false
            } else {
                true
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.35f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete recipe",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            RecipeCard(recipe, onClick)
        }
    )
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
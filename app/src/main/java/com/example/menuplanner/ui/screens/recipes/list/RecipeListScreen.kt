package com.example.menuplanner.ui.screens.recipes.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuplanner.domain.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipeListViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    // Observe recipes from ViewModel
    val recipes by viewModel.recipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("All Recipes", style = MaterialTheme.typography.headlineMedium)

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = { Text("Search recipes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (recipes.isEmpty()) {
                EmptySearchState()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = recipes, key = { it.id }) { recipe ->
                        SwipeToDeleteRecipeCard(
                            recipe = recipe,
                            onClick = onRecipeClick,
                            onDeleteRequested = { recipeToDelete = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
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
                        viewModel.deleteRecipe(recipe)
                        recipeToDelete = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp), // Adjust for FAB
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No matching recipes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Text(text = "Prep time: ${recipe.prepTimeMinutes} mins", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (recipe.isVegetarian) "Vegetarian" else "Contains Meat",
                style = MaterialTheme.typography.bodySmall,
                color = if (recipe.isVegetarian) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}
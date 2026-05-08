package com.example.menuplanner.ui.screens.recipes.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: String?,
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    // Trigger load on entry
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }
    val recipe by viewModel.recipe.collectAsState()

    // State for local editing (initialized when recipe loads)
    var descriptionText by remember { mutableStateOf("") }

    // Sync local state when the recipe is loaded from DB
    LaunchedEffect(recipe) {
        recipe?.let { descriptionText = it.description }
    }

    val currentRecipe = recipe

    val hasUnsavedChanges = recipe != null && descriptionText != recipe?.description
    var showUnsavedDialog by remember { mutableStateOf(false) }

    // State for general update notification
    val snackbarHostState = remember { SnackbarHostState() }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val generalUpdateSuccess = savedStateHandle?.get<Boolean>("recipe_updated") ?: false

    LaunchedEffect(generalUpdateSuccess) {
        if (generalUpdateSuccess) {
            snackbarHostState.showSnackbar("Recipe successfully updated!")
            savedStateHandle["recipe_updated"] = false
        }
    }

    // Intercept back navigation
    val handleBackNavigation = {
        if (hasUnsavedChanges) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Intercept hardware back button
    BackHandler(enabled = hasUnsavedChanges) {
        showUnsavedDialog = true
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes in the description. Are you sure you want to leave without saving?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    onNavigateBack()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = handleBackNavigation) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentRecipe != null) {
                        IconButton(onClick = { navController.navigate("recipe_update/${currentRecipe.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (currentRecipe != null) {
                // General Info Block
                Text(text = currentRecipe.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Nutritional value: ${currentRecipe.calories} Cal", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Preparation Time: ${currentRecipe.prepTimeMinutes} minutes", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Dietary: ${if (currentRecipe.isVegetarian) "Vegetarian" else "Standard"}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Date Added to Recipe list: ${currentRecipe.dateAdded}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Description Header & Save Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                    if (hasUnsavedChanges) {
                        TextButton(
                            onClick = {
                                viewModel.updateDescription(description = descriptionText)
                            }
                        ) {
                            Text("Save changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description Text Block
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    placeholder = { Text("Write your recipe instructions or description here...") },
                    shape = MaterialTheme.shapes.medium
                )
            } else {
                Text("Recipe not found.")
            }
        }
    }
}
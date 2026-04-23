package com.example.menuplanner.ui.screens.recipes

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
import androidx.navigation.NavController
import com.example.menuplanner.data.DummyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: String?,
    onNavigateBack: () -> Unit)
{
    // Find the specific object from the model based on ID
    val recipeIndex = DummyData.recipes.indexOfFirst { it.id.toString() == recipeId }
    val recipe = DummyData.recipes.getOrNull(recipeIndex)

    // State for description editing
    var descriptionText by remember(recipe) { mutableStateOf(recipe?.description ?: "") }
    val hasUnsavedChanges = recipe != null && descriptionText != recipe.description
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
                    if (recipe != null) {
                        IconButton(onClick = { navController.navigate("recipe_update/${recipe.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (recipe != null) {
                // General Info Block
                Text(text = recipe.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Nutritional value: ${recipe.calories} Cal", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Preparation Time: ${recipe.prepTimeMinutes} minutes", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Dietary: ${if (recipe.isVegetarian) "Vegetarian" else "Standard"}", style = MaterialTheme.typography.bodyLarge)
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
                                if (recipeIndex >= 0) {
                                    DummyData.recipes[recipeIndex] = recipe.copy(description = descriptionText)
                                }
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
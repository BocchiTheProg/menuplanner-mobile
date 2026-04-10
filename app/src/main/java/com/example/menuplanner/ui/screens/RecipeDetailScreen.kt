package com.example.menuplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(recipeId: String?, onNavigateBack: () -> Unit) {
    // Find the specific object from the model based on ID
    val recipe = DummyData.recipes.find { it.id.toString() == recipeId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (recipe != null) {
                Text(text = recipe.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "ID: ${recipe.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Nutritional value: ${recipe.calories} Cal", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Preparation Time: ${recipe.prepTimeMinutes} minutes", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Dietary: ${if (recipe.isVegetarian) "Vegetarian" else "Standard"}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Added to DB: ${recipe.dateAdded}", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Recipe not found.")
            }
        }
    }
}
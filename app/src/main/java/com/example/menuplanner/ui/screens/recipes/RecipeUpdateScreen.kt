package com.example.menuplanner.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.menuplanner.data.DummyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeUpdateScreen(
    recipeId: String?,
    navController: NavController
) {
    val recipeIndex = DummyData.recipes.indexOfFirst { it.id.toString() == recipeId }
    val recipe = DummyData.recipes.getOrNull(recipeIndex)

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Recipe not found.")
        }
        return
    }

    // State variables populated with current recipe data
    var title by remember { mutableStateOf(recipe.title) }
    var calories by remember { mutableStateOf(recipe.calories.toString()) }
    var prepTime by remember { mutableStateOf(recipe.prepTimeMinutes.toString()) }
    var isVegetarian by remember { mutableStateOf(recipe.isVegetarian) }

    // Validation state
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Recipe Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Recipe Title") },
                isError = isError && title.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Nutritional value (calories)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError && calories.toIntOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("Preparation Time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError && prepTime.toIntOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isVegetarian,
                    onCheckedChange = { isVegetarian = it }
                )
                Text("Is this recipe vegetarian?")
            }

            if (isError) {
                Text(
                    text = "Please fill in all fields correctly.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val caloriesInt = calories.toIntOrNull()
                    val timeInt = prepTime.toIntOrNull()
                    if (title.isNotBlank() && timeInt != null && caloriesInt != null) {
                        // Update object in data source while preserving ID, date, and description
                        val updatedRecipe = recipe.copy(
                            title = title,
                            calories = caloriesInt,
                            prepTimeMinutes = timeInt,
                            isVegetarian = isVegetarian
                        )
                        DummyData.updateRecipe(updatedRecipe)

                        // Pass success flag back to previous screen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("recipe_updated", true)

                        // Navigate back
                        navController.popBackStack()
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}
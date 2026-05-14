package com.example.menuplanner.ui.screens.recipes.update

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeUpdateScreen(
    recipeId: String?,
    navController: NavController,
    viewModel: RecipeUpdateViewModel = hiltViewModel()
) {
    // Load the data from the repository when the screen opens
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // Observe the recipe from the database
    val recipe by viewModel.recipe.collectAsState()

    // UI Local State for form inputs
    var title by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var isVegetarian by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    // Populate UI state once the database recipe is loaded
    LaunchedEffect(recipe) {
        recipe?.let {
            title = it.title
            calories = it.calories.toString()
            prepTime = it.prepTimeMinutes.toString()
            isVegetarian = it.isVegetarian
        }
    }

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Validation logic
    val calInt = calories.toIntOrNull()
    val timeInt = prepTime.toIntOrNull()
    val isTitleValid = title.isNotBlank() && title.length <= 100
    val isValid = isTitleValid && calInt != null && calInt >= 0 && timeInt != null && timeInt >= 0

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 100) title = it },
                label = { Text("Recipe Title") },
                supportingText = { Text("${title.length}/100") },
                isError = isError && !isTitleValid,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Nutritional value (calories)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError && (calInt == null || calInt < 0),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("Preparation Time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError && (timeInt == null || timeInt < 0),
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
                Text("Please check your inputs (no negative values).", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isValid) {
                        viewModel.updateRecipe(title, calInt, timeInt, isVegetarian)
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("recipe_updated", true)

                        navController.popBackStack()
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Changes")
            }

            // Extra spacer for bottom padding when scrolled
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
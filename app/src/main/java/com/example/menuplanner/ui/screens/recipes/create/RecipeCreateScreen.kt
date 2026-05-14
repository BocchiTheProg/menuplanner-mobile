package com.example.menuplanner.ui.screens.recipes.create

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
fun RecipeCreateScreen(
    navController: NavController,
    viewModel: RecipeCreateViewModel = hiltViewModel()
) {
    // State variables for form inputs
    var title by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var isVegetarian by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    // Validation helper
    val caloriesValue = calories.toIntOrNull()
    val prepTimeValue = prepTime.toIntOrNull()
    val isTitleValid = title.isNotBlank() && title.length <= 100
    val isCaloriesValid = caloriesValue != null && caloriesValue >= 0
    val isPrepTimeValid = prepTimeValue != null && prepTimeValue >= 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Recipe") },
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
                isError = isError && !isCaloriesValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("Preparation Time (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError && !isPrepTimeValid,
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
                    text = "Please fill in all fields correctly (no negative numbers).",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isTitleValid && isCaloriesValid && isPrepTimeValid) {
                        viewModel.saveRecipe(title, caloriesValue, prepTimeValue, isVegetarian)
                        navController.previousBackStackEntry?.savedStateHandle?.set("recipe_created", true)
                        navController.popBackStack()
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Create Recipe")
            }

            // Extra spacer for bottom padding when scrolled
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
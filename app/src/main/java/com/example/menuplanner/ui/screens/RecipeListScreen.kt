package com.example.menuplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.Recipe

@Composable
fun RecipeListScreen(onRecipeClick: (String) -> Unit) {
    val recipes = DummyData.recipes // Fetching data

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("All Recipes", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(recipes) { recipe ->
            RecipeCard(recipe, onRecipeClick)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id.toString()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleLarge)
            Text(text = "Prep time: ${recipe.prepTimeMinutes} mins")
            Text(text = if (recipe.isVegetarian) "Vegetarian" else "Contains Meat")
        }
    }
}
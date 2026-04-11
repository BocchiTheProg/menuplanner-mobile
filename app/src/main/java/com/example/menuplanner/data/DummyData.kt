package com.example.menuplanner.data

import com.example.menuplanner.data.model.MealPlan
import java.util.UUID
import java.util.Date

import androidx.compose.runtime.mutableStateListOf

import com.example.menuplanner.data.model.Recipe

object DummyData {
    val recipes = mutableStateListOf(
        Recipe(UUID.randomUUID(),
            "Spaghetti Bolognese",
            426,
            45,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Avocado Toast",
            195,
            10,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Chicken Curry",
            625,
            60,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Fried Eggs with Sausages",
            250,
            20,
            false,
            Date()),
    )

    val mealPlans = mutableStateListOf(
        MealPlan(UUID.randomUUID(),
            "Monday",
            recipes[1],
            recipes[0],
            recipes[2],
            false),
        MealPlan(UUID.randomUUID(),
            "Tuesday",
            recipes[3],
            recipes[0],
            recipes[2],
            false),
        MealPlan(UUID.randomUUID(),
            "Wednesday",
            recipes[1],
            recipes[0],
            recipes[2],
            false),
    )

    // Helper function to add a new recipe
    fun addRecipe(title: String, calories: Int, prepTime: Int, isVegetarian: Boolean) {
        val newRecipe = Recipe(
            id = UUID.randomUUID(), // Generate unique ID
            title = title,
            calories = calories,
            prepTimeMinutes = prepTime,
            isVegetarian = isVegetarian,
            dateAdded = Date()
        )
        recipes.add(newRecipe)
    }
}
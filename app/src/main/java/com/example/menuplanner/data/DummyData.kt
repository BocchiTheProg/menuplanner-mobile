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
            "A classic Italian pasta dish with a rich, slow-cooked meat sauce.",
            426,
            45,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Avocado Toast",
            "Mashed avocado on toasted bread, often topped with salt, pepper, and a drizzle of olive oil.",
            195,
            10,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Chicken Curry",
            "A savory and spicy Indian dish made with tender chicken pieces simmered in a flavorful sauce.",
            625,
            60,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Fried Eggs with Sausages",
            "A hearty breakfast classic featuring pan-fried eggs and savory sausages.",
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
        MealPlan(UUID.randomUUID(),
            "Thursday",
            recipes[3],
            recipes[1],
            recipes[2],
            true),
        MealPlan(UUID.randomUUID(),
            "Friday",
            recipes[1],
            recipes[3],
            recipes[0],
            false),
        MealPlan(UUID.randomUUID(),
            "Saturday",
            recipes[1],
            recipes[1],
            recipes[3],
            true),
        MealPlan(UUID.randomUUID(),
            "Sunday",
            recipes[3],
            recipes[2],
            recipes[0],
            false),
    )

    // Helper function to add a new recipe
    fun addRecipe(title: String, calories: Int, prepTime: Int, isVegetarian: Boolean) {
        val newRecipe = Recipe(
            id = UUID.randomUUID(), // Generate unique ID
            title = title,
            description = "",
            calories = calories,
            prepTimeMinutes = prepTime,
            isVegetarian = isVegetarian,
            dateAdded = Date()
        )
        recipes.add(newRecipe)
    }

    fun updateRecipe(updatedRecipe: Recipe) {
        // Update the recipe in the main recipes list
        val recipeIndex = recipes.indexOfFirst { it.id == updatedRecipe.id }
        if (recipeIndex != -1) {
            recipes[recipeIndex] = updatedRecipe
        }

        // Cascade the update to any Meal Plans that use this recipe
        for (i in mealPlans.indices) {
            val plan = mealPlans[i]

            // Check if this meal plan contains the updated recipe
            val hasRecipe = plan.breakfast.id == updatedRecipe.id ||
                    plan.lunch.id == updatedRecipe.id ||
                    plan.dinner.id == updatedRecipe.id

            if (hasRecipe) {
                // Replace the old recipe references with the new updatedRecipe
                mealPlans[i] = plan.copy(
                    breakfast = if (plan.breakfast.id == updatedRecipe.id) updatedRecipe else plan.breakfast,
                    lunch = if (plan.lunch.id == updatedRecipe.id) updatedRecipe else plan.lunch,
                    dinner = if (plan.dinner.id == updatedRecipe.id) updatedRecipe else plan.dinner
                )
            }
        }
    }
}
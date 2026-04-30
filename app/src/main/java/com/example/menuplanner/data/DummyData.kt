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
            425,
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
        Recipe(UUID.randomUUID(),
            "Caesar Salad",
            "A fresh salad with romaine lettuce, croutons, Parmesan cheese, and Caesar dressing.",
            220,
            15,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Beef Burgers",
            "Juicy beef patties served on buns, often topped with cheese, lettuce, and tomato.",
            500,
            30,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Margherita Pizza",
            "A simple yet classic pizza topped with tomato sauce, mozzarella, and fresh basil.",
            400,
            25,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Yogurt with Berries",
            "A healthy breakfast or snack featuring yogurt topped with fresh berries and honey.",
            150,
            5,
            true,
            Date()),
        Recipe(UUID.randomUUID(),
            "Beef Tacos",
            "Soft tortillas filled with seasoned ground beef, lettuce, cheese, and salsa.",
            450,
            20,
            false,
            Date()),
        Recipe(UUID.randomUUID(),
            "Tomato Soup",
            "A creamy and comforting soup made from tomatoes, herbs, and sometimes cream.",
            180,
            25,
            true,
            Date())
    )

    val mealPlans = mutableStateListOf(
        MealPlan(UUID.randomUUID(),
            "Monday",
            recipes[1],
            recipes[5],
            recipes[2],
            false),
        MealPlan(UUID.randomUUID(),
            "Tuesday",
            recipes[7],
            recipes[0],
            recipes[8],
            false),
        MealPlan(UUID.randomUUID(),
            "Wednesday",
            recipes[1],
            recipes[4],
            recipes[6],
            false),
        MealPlan(UUID.randomUUID(),
            "Thursday",
            recipes[3],
            recipes[9],
            recipes[2],
            true),
        MealPlan(UUID.randomUUID(),
            "Friday",
            recipes[7],
            recipes[5],
            recipes[0],
            false),
        MealPlan(UUID.randomUUID(),
            "Saturday",
            recipes[1],
            recipes[4],
            recipes[8],
            true),
        MealPlan(UUID.randomUUID(),
            "Sunday",
            recipes[3],
            recipes[6],
            recipes[9],
            false),
    )

    // Copy initial values of lists (for reset function)
    val initialRecipes = recipes.toList()
    val initialMealPlans = mealPlans.toList()

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

    // Reset recipes and mealPlans lists to initial values
    fun resetToDefault() {
        recipes.clear()
        recipes.addAll(initialRecipes)
        mealPlans.clear()
        mealPlans.addAll(initialMealPlans)
    }
}
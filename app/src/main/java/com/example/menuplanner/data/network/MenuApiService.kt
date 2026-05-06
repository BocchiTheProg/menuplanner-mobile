package com.example.menuplanner.data.network

import java.util.UUID
import com.example.menuplanner.data.model.MealPlan
import com.example.menuplanner.data.model.Recipe

interface MenuApiService {
    suspend fun syncRecipe(recipe: Recipe): Result<Recipe>
    suspend fun syncMealPlan(mealPlan: MealPlan): Result<MealPlan>
    suspend fun deleteRecipeOnServer(recipeId: UUID): Result<Unit>
}
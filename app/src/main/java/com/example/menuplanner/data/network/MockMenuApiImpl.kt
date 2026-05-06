package com.example.menuplanner.data.network

import java.util.UUID
import com.example.menuplanner.data.model.MealPlan
import com.example.menuplanner.data.model.Recipe
import kotlinx.coroutines.delay

class MockMenuApiImpl : MenuApiService {
    // Simulates a network call taking 1.5 seconds
    override suspend fun syncRecipe(recipe: Recipe): Result<Recipe> {
        delay(1500)
        return Result.success(recipe)
    }

    override suspend fun syncMealPlan(mealPlan: MealPlan): Result<MealPlan> {
        delay(1500)
        return Result.success(mealPlan)
    }

    override suspend fun deleteRecipeOnServer(recipeId: UUID): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }
}
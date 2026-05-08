package com.example.menuplanner.data.network

import java.util.UUID
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.Recipe
import kotlinx.coroutines.delay

class MockMenuApiImpl : MenuApiService {
    // Simulates a network call taking 1.5 seconds
    override suspend fun syncRecipe(recipe: Recipe): Result<Recipe> {
        delay(200)
        return Result.success(recipe)
    }

    override suspend fun syncMealPlan(mealPlan: MealPlan): Result<MealPlan> {
        delay(200)
        return Result.success(mealPlan)
    }

    override suspend fun deleteRecipeOnServer(recipeId: UUID): Result<Unit> {
        delay(100)
        return Result.success(Unit)
    }
}
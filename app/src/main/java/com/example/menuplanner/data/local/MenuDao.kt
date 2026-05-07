package com.example.menuplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    // Recipes
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    // Meal Plans
    @Transaction
    @Query("SELECT * FROM meal_plans")
    fun getAllMealPlans(): Flow<List<MealPlanWithRecipes>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlanEntity)
}
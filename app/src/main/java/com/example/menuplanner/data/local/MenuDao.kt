package com.example.menuplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MenuDao {
    // Recipes
    @Query("SELECT * FROM recipes ORDER BY lastUpdated DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: UUID): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    // Meal Plans

    @Transaction
    @Query("""
        SELECT * FROM meal_plans 
        ORDER BY CASE dayOfWeek 
            WHEN 'Monday' THEN 1 
            WHEN 'Tuesday' THEN 2 
            WHEN 'Wednesday' THEN 3 
            WHEN 'Thursday' THEN 4 
            WHEN 'Friday' THEN 5 
            WHEN 'Saturday' THEN 6 
            WHEN 'Sunday' THEN 7 
            ELSE 8 
        END ASC
    """)
    fun getAllMealPlans(): Flow<List<MealPlanWithRecipes>>

    @Transaction
    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanById(id: UUID): MealPlanWithRecipes?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlanEntity)
}
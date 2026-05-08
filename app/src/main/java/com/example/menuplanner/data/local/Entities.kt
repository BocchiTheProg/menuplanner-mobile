package com.example.menuplanner.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: UUID,

    val title: String,
    val description: String,
    val calories: Int,
    val prepTimeMinutes: Int,
    val isVegetarian: Boolean,
    val dateAdded: Long,
    val lastUpdated: Long,
    val syncStatus: String
)

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey val id: UUID,

    val dayOfWeek: String,
    val breakfastId: UUID?,
    val lunchId: UUID?,
    val dinnerId: UUID?,
    val isCooked: Boolean,
    val syncStatus: String
)

// Relational Data Class for Room
data class MealPlanWithRecipes(
    @Embedded val mealPlan: MealPlanEntity,
    @Relation(parentColumn = "breakfastId", entityColumn = "id") val breakfast: RecipeEntity?,
    @Relation(parentColumn = "lunchId", entityColumn = "id") val lunch: RecipeEntity?,
    @Relation(parentColumn = "dinnerId", entityColumn = "id") val dinner: RecipeEntity?
)
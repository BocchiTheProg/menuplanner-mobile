package com.example.menuplanner.data.local

import java.util.Date
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.domain.model.SyncStatus

// Recipe Mappers
fun RecipeEntity.toDomainModel(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        calories = this.calories,
        prepTimeMinutes = this.prepTimeMinutes,
        isVegetarian = this.isVegetarian,
        dateAdded = Date(this.dateAdded),
        description = this.description,
        lastUpdated = this.lastUpdated,
        syncStatus = SyncStatus.valueOf(this.syncStatus)
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        title = this.title,
        calories = this.calories,
        prepTimeMinutes = this.prepTimeMinutes,
        isVegetarian = this.isVegetarian,
        dateAdded = this.dateAdded.time,
        description = this.description,
        lastUpdated = System.currentTimeMillis(),
        syncStatus = this.syncStatus.name
    )
}

// Meal Plan Mappers
fun MealPlanWithRecipes.toDomainModel(): MealPlan {
    return MealPlan(
        id = this.mealPlan.id,
        dayOfWeek = this.mealPlan.dayOfWeek,
        breakfast = this.breakfast?.toDomainModel(),
        lunch = this.lunch?.toDomainModel(),
        dinner = this.dinner?.toDomainModel(),
        isCooked = this.mealPlan.isCooked,
        syncStatus = SyncStatus.valueOf(this.mealPlan.syncStatus)
    )
}

fun MealPlan.toEntity(): MealPlanEntity {
    return MealPlanEntity(
        id = this.id,
        dayOfWeek = this.dayOfWeek,
        breakfastId = this.breakfast?.id,
        lunchId = this.lunch?.id,
        dinnerId = this.dinner?.id,
        isCooked = this.isCooked,
        syncStatus = this.syncStatus.name
    )
}
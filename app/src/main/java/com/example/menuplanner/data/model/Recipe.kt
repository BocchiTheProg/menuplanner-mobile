package com.example.menuplanner.data.model

import java.util.Date
import java.util.UUID

// Represents a specific dish(recipe) available in the application that can be used for meal plan.
data class Recipe (
    val id: UUID,
    val title: String,
    val description: String,
    val calories: Int,
    val prepTimeMinutes: Int,
    val isVegetarian: Boolean,
    val dateAdded: Date
)
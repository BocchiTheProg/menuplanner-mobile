package com.example.menuplanner.data.model

import java.util.UUID
import com.example.menuplanner.data.SyncStatus

// Represents a scheduled meals for a specific day.
data class MealPlan(
    val id: UUID,
    val dayOfWeek: String,
    val breakfast: Recipe,
    val lunch: Recipe,
    val dinner: Recipe,
    val isCooked: Boolean,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

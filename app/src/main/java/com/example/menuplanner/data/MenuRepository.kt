package com.example.menuplanner.data

import com.example.menuplanner.data.network.MenuApiService
import com.example.menuplanner.data.local.MenuDao
import com.example.menuplanner.data.local.toDomainModel
import com.example.menuplanner.data.local.toEntity
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val dao: MenuDao,
    private val api: MenuApiService
) {
    // Read Operations
    val allRecipes: Flow<List<Recipe>> = dao.getAllRecipes().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val allMealPlans: Flow<List<MealPlan>> = dao.getAllMealPlans().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun getRecipe(id: UUID): Recipe? = dao.getRecipeById(id)?.toDomainModel()
    suspend fun getMealPlan(id: UUID): MealPlan? = dao.getMealPlanById(id)?.toDomainModel()

    // Write Operations (Offline-First Logic)
    suspend fun saveRecipe(recipe: Recipe) {
        // Save locally as PENDING
        val pendingRecipe = recipe.copy(syncStatus = SyncStatus.PENDING)
        dao.insertRecipe(pendingRecipe.toEntity())

        // Attempt API Sync
        try {
            val result = api.syncRecipe(pendingRecipe)
            if (result.isSuccess) {
                // On success, update local to SYNCED
                dao.insertRecipe(pendingRecipe.copy(syncStatus = SyncStatus.SYNCED).toEntity())
            } else {
                // On fail, mark ERROR
                dao.insertRecipe(pendingRecipe.copy(syncStatus = SyncStatus.ERROR).toEntity())
            }
        } catch (e: Exception) {
            dao.insertRecipe(pendingRecipe.copy(syncStatus = SyncStatus.ERROR).toEntity())
        }
    }

    suspend fun saveMealPlan(mealPlan: MealPlan) {
        val pendingMealPlan = mealPlan.copy(syncStatus = SyncStatus.PENDING)
        dao.insertMealPlan(pendingMealPlan.toEntity())

        try {
            val result = api.syncMealPlan(pendingMealPlan)
            if (result.isSuccess) {
                dao.insertMealPlan(pendingMealPlan.copy(syncStatus = SyncStatus.SYNCED).toEntity())
            } else {
                dao.insertMealPlan(pendingMealPlan.copy(syncStatus = SyncStatus.ERROR).toEntity())
            }
        } catch (e: Exception) {
            dao.insertMealPlan(pendingMealPlan.copy(syncStatus = SyncStatus.ERROR).toEntity())
        }
    }

    // Delete operation
    suspend fun deleteRecipe(recipe: Recipe) {
        // Delete locally
        dao.deleteRecipe(recipe.toEntity())

        // Attempt to delete on server
        api.deleteRecipeOnServer(recipe.id)
    }
}
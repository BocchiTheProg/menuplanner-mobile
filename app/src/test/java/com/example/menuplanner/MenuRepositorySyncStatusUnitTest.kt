package com.example.menuplanner

import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.data.local.MenuDao
import com.example.menuplanner.data.network.MenuApiService
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.domain.model.SyncStatus
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.UUID

class MenuRepositorySyncStatusUnitTest {
    private lateinit var dao: MenuDao
    private lateinit var api: MenuApiService
    private lateinit var repository: MenuRepository

    private val testRecipe = Recipe(
        id = UUID.randomUUID(),
        title = "Test Recipe",
        description = "Test Desc",
        calories = 500,
        prepTimeMinutes = 30,
        isVegetarian = false,
        dateAdded = Date(),
        syncStatus = SyncStatus.PENDING
    )

    @Before
    fun setup() {
        // mockk(relaxed = true) automatically returns empty units/flows for unmocked methods
        dao = mockk(relaxed = true)
        api = mockk()
        repository = MenuRepository(dao, api)
    }

    @Test
    fun `saveRecipe with successful API call saves locally as PENDING then SYNCED`() = runTest {
        // Mock the API to return success
        coEvery { api.syncRecipe(any()) } returns Result.success(testRecipe)

        // Act
        repository.saveRecipe(testRecipe)

        // Assert: Verify the exact order of DAO insertions
        coVerifyOrder {
            // PENDING
            dao.insertRecipe(match { it.syncStatus == SyncStatus.PENDING.name })
            // SYNCED
            dao.insertRecipe(match { it.syncStatus == SyncStatus.SYNCED.name })
        }
    }

    @Test
    fun `saveRecipe with failed API call saves locally as PENDING then ERROR`() = runTest {
        // Mock the API to return a failure
        coEvery { api.syncRecipe(any()) } returns Result.failure(Exception("Network Error"))

        // Act
        repository.saveRecipe(testRecipe)

        // Assert
        coVerifyOrder {
            // PENDING
            dao.insertRecipe(match { it.syncStatus == SyncStatus.PENDING.name })
            // ERROR due to network failure
            dao.insertRecipe(match { it.syncStatus == SyncStatus.ERROR.name })
        }
    }

    @Test
    fun `deleteRecipe deletes locally then calls API`() = runTest {
        // Arrange success on return
        coEvery { api.deleteRecipeOnServer(any()) } returns Result.success(Unit)

        // Act
        repository.deleteRecipe(testRecipe)

        // Assert (check deletion order)
        coVerifyOrder {
            api.deleteRecipeOnServer(testRecipe.id)
            dao.deleteRecipe(any())
        }
    }
}
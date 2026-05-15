package com.example.menuplanner

import app.cash.turbine.test
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.SyncStatus
import com.example.menuplanner.ui.screens.mealPlans.detail.MealPlanDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class MealPlanDetailUnitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: MenuRepository
    private lateinit var viewModel: MealPlanDetailViewModel

    private val mealPlanId = UUID.randomUUID()
    private val testMealPlan = MealPlan(
        id = mealPlanId,
        dayOfWeek = "Monday",
        isCooked = false, // Starts as not cooked
        syncStatus = SyncStatus.SYNCED
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        viewModel = MealPlanDetailViewModel(repository)
    }

    @Test
    fun `loadMealPlan updates StateFlow correctly`() = runTest {
        // Arrange
        coEvery { repository.getMealPlan(mealPlanId) } returns testMealPlan

        // Act
        viewModel.loadMealPlan(mealPlanId.toString())

        // Assert using Turbine to collect the StateFlow
        viewModel.mealPlan.test {
            val loadedPlan = awaitItem() ?: awaitItem()
            assertEquals(testMealPlan.id, loadedPlan?.id)
            assertEquals("Monday", loadedPlan?.dayOfWeek)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleCookedStatus flips status and saves to repository`() = runTest {
        // Load the initial meal plan
        coEvery { repository.getMealPlan(mealPlanId) } returns testMealPlan
        viewModel.loadMealPlan(mealPlanId.toString())

        // Act
        viewModel.toggleCookedStatus()

        // Assert (save isCooked = true)
        coVerify {
            repository.saveMealPlan(match { it.isCooked == true })
        }

        // Check StateFlow was updated
        assertEquals(true, viewModel.mealPlan.value?.isCooked)
    }
}
package com.example.menuplanner

import app.cash.turbine.test
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import com.example.menuplanner.ui.screens.recipes.detail.RecipeDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeUpdateDescriptionUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MenuRepository>(relaxed = true)
    private lateinit var viewModel: RecipeDetailViewModel

    private val testId = UUID.randomUUID()
    private val initialRecipe = Recipe(
        id = testId,
        title = "Fixed Title",
        description = "Initial Description",
        calories = 200,
        prepTimeMinutes = 15,
        isVegetarian = false,
        dateAdded = Date()
    )

    @Before
    fun setup() {
        viewModel = RecipeDetailViewModel(repository)
    }

    @Test
    fun `updateDescription correctly updates repository and local state`() = runTest {
        coEvery { repository.getRecipe(testId) } returns initialRecipe
        viewModel.loadRecipe(testId.toString())

        val newDescription = "Step 1: Boil water. Step 2: Profit."

        // Act
        viewModel.updateDescription(newDescription)

        // Check Repository call
        coVerify {
            repository.saveRecipe(match {
                it.id == testId &&
                        it.description == newDescription &&
                        it.title == "Fixed Title" // Ensure other fields aren't wiped
            })
        }

        // Check that UI state updated immediately
        viewModel.recipe.test {
            assertEquals(newDescription, awaitItem()?.description)
        }
    }
}
package com.example.menuplanner

import app.cash.turbine.test
import com.example.menuplanner.ui.screens.recipes.list.RecipeListViewModel
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SearchBarUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<MenuRepository>()
    private lateinit var viewModel: RecipeListViewModel

    // Sample data setup: 3 recipes with different titles, descriptions, and timestamps
    private val testRecipes = listOf(
        Recipe(
            id = UUID.randomUUID(),
            title = "Spicy Pasta",
            description = "A hot Italian dish",
            calories = 500,
            prepTimeMinutes = 20,
            isVegetarian = true,
            dateAdded = Date(),
            lastUpdated = 3000L // Newest
        ),
        Recipe(
            id = UUID.randomUUID(),
            title = "Green Salad",
            description = "Fresh garden greens",
            calories = 150,
            prepTimeMinutes = 10,
            isVegetarian = true,
            dateAdded = Date(),
            lastUpdated = 2000L
        ),
        Recipe(
            id = UUID.randomUUID(),
            title = "Beef Burger",
            description = "Juicy meat with spicy sauce",
            calories = 800,
            prepTimeMinutes = 15,
            isVegetarian = false,
            dateAdded = Date(),
            lastUpdated = 1000L // Oldest
        )
    )

    @Before
    fun setup() {
        // Repository returns the list in "Most Recent First" order by default
        every { repository.allRecipes } returns flowOf(testRecipes)
        viewModel = RecipeListViewModel(repository)
    }

    @Test
    fun `Initially, all recipes are shown in order`() = runTest {
        viewModel.recipes.test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Spicy Pasta", result[0].title) // Check newest first
            assertEquals("Beef Burger", result[2].title) // Check oldest last
        }
    }

    @Test
    fun `Search by Title filters correctly and is case-insensitive`() = runTest {
        viewModel.recipes.test {
            // Skip the initial state
            awaitItem()

            // When searching for "SALAD"
            viewModel.onSearchQueryChange("SALAD")

            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Green Salad", result[0].title)
        }
    }

    @Test
    fun `Search by Description filters correctly`() = runTest {
        viewModel.recipes.test {
            awaitItem()

            // Searching for "hot" which is in Pasta description
            viewModel.onSearchQueryChange("hot")

            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Spicy Pasta", result[0].title)
        }
    }

    @Test
    fun `Search across multiple fields finds both title and description matches`() = runTest {
        viewModel.recipes.test {
            awaitItem()

            // "spicy" is in Title of Pasta and Description of Burger
            viewModel.onSearchQueryChange("spicy")

            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Spicy Pasta", result[0].title) // Preserves order
            assertEquals("Beef Burger", result[1].title)
        }
    }

    @Test
    fun `Non-matching query returns empty list`() = runTest {
        viewModel.recipes.test {
            awaitItem()

            viewModel.onSearchQueryChange("Pizza")

            val result = awaitItem()
            assertEquals(0, result.size)
        }
    }

    @Test
    fun `Clearing search query restores full list`() = runTest {
        viewModel.recipes.test {
            awaitItem()

            // Search and then clear
            viewModel.onSearchQueryChange("Salad")
            awaitItem() // Consume filtered result

            viewModel.onSearchQueryChange("")
            val result = awaitItem()

            assertEquals(3, result.size)
        }
    }
}
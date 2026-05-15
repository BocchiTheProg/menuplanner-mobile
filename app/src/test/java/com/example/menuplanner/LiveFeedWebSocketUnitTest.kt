package com.example.menuplanner

import app.cash.turbine.test
import com.example.menuplanner.data.websocket.ConnectionState
import com.example.menuplanner.data.websocket.LiveFeedEvent
import com.example.menuplanner.data.websocket.SocketManager
import com.example.menuplanner.ui.screens.livefeed.LiveFeedViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LiveFeedWebSocketUnitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var socketManager: SocketManager
    private lateinit var viewModel: LiveFeedViewModel

    // Controlled reactive pipelines to feed structural mock variants
    private val connectionStateFlow = MutableStateFlow(ConnectionState.Disconnected)
    private val incomingMessagesFlow = MutableSharedFlow<LiveFeedEvent>()

    @Before
    fun setup() {
        socketManager = mockk(relaxed = true)

        // Force our interface mock to bridge into controllable test streams
        every { socketManager.connectionState } returns connectionStateFlow
        every { socketManager.incomingMessages } returns incomingMessagesFlow

        viewModel = LiveFeedViewModel(socketManager)
    }

    @Test
    fun `connect executes socketManager connection pipeline with the expected endpoint`() = runTest {
        // Act
        viewModel.connect()

        // Assert
        verify(exactly = 1) { socketManager.connect("wss://mock.api.menuplanner.com/live") }
    }

    @Test
    fun `disconnect triggers socketManager teardown and completely clears local feed`() = runTest {
        // Push a live event sequence into viewmodel state
        val existingEvent = LiveFeedEvent("id-123",
                                          "Pancakes",
                                          "UserX",
                                          System.currentTimeMillis())
        incomingMessagesFlow.emit(existingEvent)

        // Assert state setup success
        assertEquals(1, viewModel.feedEvents.value.size)

        // Act
        viewModel.disconnect()

        // Assert clean interface isolation and resource termination
        verify(exactly = 1) { socketManager.disconnect() }
        assertEquals(0, viewModel.feedEvents.value.size)
    }

    @Test
    fun `incoming stream emissions are captured and correctly prepended to feedEvents state`() = runTest {
        val eventA = LiveFeedEvent("1",
                                       "Avocado Salad",
                                       "UserA",
                                       1000L)
        val eventB = LiveFeedEvent("2",
                                      "Garlic Soup",
                                      "UserB",
                                      2000L)

        viewModel.feedEvents.test {
            // Initial collected composition state should be blank
            assertEquals(emptyList<LiveFeedEvent>(), awaitItem())

            // Push first event element down the stream pipeline
            incomingMessagesFlow.emit(eventA)
            val intermediateState = awaitItem()
            assertEquals(1, intermediateState.size)
            assertEquals(eventA, intermediateState.first())

            // Emit a consecutive event element down the stream pipeline
            incomingMessagesFlow.emit(eventB)
            val finalState = awaitItem()
            assertEquals(2, finalState.size)

            // Real-time updates must prepend to indices top-down [0]
            assertEquals(eventB, finalState[0]) // Newest item
            assertEquals(eventA, finalState[1]) // Previous item

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connectionState transparently surfaces underlying socket connection drops and retries`() = runTest {
        viewModel.connectionState.test {
            // Initial default verification
            assertEquals(ConnectionState.Disconnected, awaitItem())

            // Emulate 'Connecting' transition state change
            connectionStateFlow.value = ConnectionState.Connecting
            assertEquals(ConnectionState.Connecting, awaitItem())

            // Emulate successful 'Connected' pipeline hook
            connectionStateFlow.value = ConnectionState.Connected
            assertEquals(ConnectionState.Connected, awaitItem())

            // Emulate unexpected connection dropping and engaging 'Reconnecting' loop
            connectionStateFlow.value = ConnectionState.Reconnecting
            assertEquals(ConnectionState.Reconnecting, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
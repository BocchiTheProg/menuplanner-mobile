package com.example.menuplanner.ui.screens.livefeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.websocket.ConnectionState
import com.example.menuplanner.data.websocket.LiveFeedEvent
import com.example.menuplanner.data.websocket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveFeedViewModel @Inject constructor(
    private val socketManager: SocketManager
) : ViewModel() {

    // Expose Connection State to UI directly
    val connectionState: StateFlow<ConnectionState> = socketManager.connectionState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectionState.Disconnected)

    // UI state for the list of live events
    private val _feedEvents = MutableStateFlow<List<LiveFeedEvent>>(emptyList())
    val feedEvents = _feedEvents.asStateFlow()

    init {
        // Subscribe to incoming parsed WS events
        viewModelScope.launch {
            socketManager.incomingMessages.collect { newEvent ->
                // Add new event to the top of the list without manual UI reload
                _feedEvents.update { currentList ->
                    listOf(newEvent) + currentList
                }
            }
        }
    }

    fun connect() {
        socketManager.connect("wss://mock.api.menuplanner.com/live")
    }

    fun disconnect() {
        socketManager.disconnect()
        _feedEvents.value = emptyList() // Clear list on disconnect
    }
}
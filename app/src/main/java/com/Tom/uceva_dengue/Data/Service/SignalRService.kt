package com.Tom.uceva_dengue.Data.Service

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * SignalR Service for real-time case updates
 * Manages WebSocket connection to the backend hub
 */
class SignalRService {

    companion object {
        private const val TAG = "SignalRService"
        private const val HUB_URL = "https://api.prometeondev.com/caseHub"

        // Singleton instance
        @Volatile
        private var instance: SignalRService? = null

        fun getInstance(): SignalRService {
            return instance ?: synchronized(this) {
                instance ?: SignalRService().also { instance = it }
            }
        }
    }

    private var hubConnection: HubConnection? = null

    // Connection state
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Events for new cases, updates, and deletions
    private val _newCaseEvent = MutableStateFlow<Pair<Int, String>?>(null)
    val newCaseEvent: StateFlow<Pair<Int, String>?> = _newCaseEvent

    private val _caseUpdateEvent = MutableStateFlow<Pair<Int, String>?>(null)
    val caseUpdateEvent: StateFlow<Pair<Int, String>?> = _caseUpdateEvent

    private val _caseDeletedEvent = MutableStateFlow<Int?>(null)
    val caseDeletedEvent: StateFlow<Int?> = _caseDeletedEvent

    /**
     * Initialize and start the SignalR connection
     */
    fun connect() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            Log.d(TAG, "Already connected to SignalR hub")
            return
        }

        try {
            hubConnection = HubConnectionBuilder.create(HUB_URL)
                .build()

            // Register event handlers
            setupEventHandlers()

            // Start connection asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    startConnection()
                    _isConnected.value = true
                    Log.d(TAG, "Successfully connected to SignalR hub: $HUB_URL")
                } catch (e: Exception) {
                    _isConnected.value = false
                    Log.e(TAG, "Error connecting to SignalR hub", e)
                }
            }

            // Handle reconnected event
            hubConnection?.onClosed { exception ->
                _isConnected.value = false
                Log.w(TAG, "SignalR connection closed", exception)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error creating SignalR connection", e)
        }
    }

    /**
     * Suspend function to start connection
     */
    private suspend fun startConnection() = suspendCancellableCoroutine<Unit> { continuation ->
        try {
            val completable = hubConnection?.start()
            completable?.subscribe(
                {
                    // On success
                    continuation.resume(Unit)
                },
                { error ->
                    // On error
                    continuation.resumeWithException(error)
                }
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    /**
     * Setup event handlers for SignalR events
     */
    private fun setupEventHandlers() {
        // Handle new case events
        hubConnection?.on("ReceiveNewCase", { caseId: Int, message: String ->
            Log.d(TAG, "Received new case event: $caseId - $message")
            _newCaseEvent.value = Pair(caseId, message)
        }, Int::class.java, String::class.java)

        // Handle case update events
        hubConnection?.on("ReceiveCaseUpdate", { caseId: Int, message: String ->
            Log.d(TAG, "Received case update event: $caseId - $message")
            _caseUpdateEvent.value = Pair(caseId, message)
        }, Int::class.java, String::class.java)

        // Handle case deleted events
        hubConnection?.on("ReceiveCaseDeleted", { caseId: Int ->
            Log.d(TAG, "Received case deleted event: $caseId")
            _caseDeletedEvent.value = caseId
        }, Int::class.java)
    }

    /**
     * Disconnect from SignalR hub
     */
    fun disconnect() {
        try {
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                hubConnection?.stop()
                _isConnected.value = false
                Log.d(TAG, "Disconnected from SignalR hub")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from SignalR hub", e)
        }
    }

    /**
     * Clear event states (call after handling events)
     */
    fun clearNewCaseEvent() {
        _newCaseEvent.value = null
    }

    fun clearCaseUpdateEvent() {
        _caseUpdateEvent.value = null
    }

    fun clearCaseDeletedEvent() {
        _caseDeletedEvent.value = null
    }

    /**
     * Check if currently connected
     */
    fun isHubConnected(): Boolean {
        return hubConnection?.connectionState == HubConnectionState.CONNECTED
    }
}

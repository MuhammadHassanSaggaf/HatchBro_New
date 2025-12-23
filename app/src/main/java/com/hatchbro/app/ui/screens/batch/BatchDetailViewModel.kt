package com.hatchbro.app.ui.screens.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.BatchStatus
import com.hatchbro.app.data.local.entity.EventEntity
import com.hatchbro.app.data.local.entity.EventType
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class BatchDetailUiState(
    val batch: BatchEntity? = null,
    val events: List<EventEntity> = emptyList()
)

@HiltViewModel
class BatchDetailViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatchDetailUiState())
    val uiState: StateFlow<BatchDetailUiState> = _uiState.asStateFlow()

    fun loadBatch(batchId: Long) {
        viewModelScope.launch {
            val batch = repository.getBatchById(batchId)
            _uiState.value = _uiState.value.copy(batch = batch)
            
            repository.getEventsForBatch(batchId).collect { events ->
                _uiState.value = _uiState.value.copy(events = events)
            }
        }
    }

    fun addEvent(batchId: Long, type: EventType, value: String?, notes: String?) {
        viewModelScope.launch {
            repository.addEvent(
                EventEntity(
                    batchId = batchId,
                    timestamp = Date(),
                    type = type,
                    value = value,
                    notes = notes
                )
            )
        }
    }

    fun updateBatchStatus(newStatus: BatchStatus, hatchedCount: Int = 0, discardedCount: Int = 0) {
        viewModelScope.launch {
            val batch = _uiState.value.batch ?: return@launch
            repository.updateBatch(
                batch.copy(
                    status = newStatus,
                    hatchedCount = hatchedCount,
                    discardedCount = discardedCount
                )
            )
            loadBatch(batch.id) // Refresh
        }
    }

    fun updateBatchCounts(hatchedCount: Int, discardedCount: Int) {
        viewModelScope.launch {
            val batch = _uiState.value.batch ?: return@launch
            repository.updateBatch(
                batch.copy(
                    hatchedCount = hatchedCount,
                    discardedCount = discardedCount
                )
            )
            loadBatch(batch.id)
        }
    }

    fun completeBatch(hatchedCount: Int, discardedCount: Int) {
        viewModelScope.launch {
            val batch = _uiState.value.batch ?: return@launch
            repository.updateBatch(
                batch.copy(
                    status = BatchStatus.COMPLETED,
                    hatchedCount = hatchedCount,
                    discardedCount = discardedCount
                )
            )
            loadBatch(batch.id)
        }
    }

    fun addNote(batchId: Long, noteText: String) {
        viewModelScope.launch {
            repository.addEvent(
                EventEntity(
                    batchId = batchId,
                    timestamp = Date(),
                    type = EventType.NOTE,
                    value = null,
                    notes = noteText
                )
            )
        }
    }
}


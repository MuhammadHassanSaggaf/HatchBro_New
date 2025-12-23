package com.hatchbro.app.ui.screens.incubator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.local.entity.TrayEntity
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncubatorDetailUiState(
    val incubator: IncubatorEntity? = null,
    val trays: List<TrayEntity> = emptyList()
)

@HiltViewModel
class IncubatorDetailViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncubatorDetailUiState())
    val uiState: StateFlow<IncubatorDetailUiState> = _uiState.asStateFlow()

    fun loadIncubator(id: Long) {
        viewModelScope.launch {
            val incubator = repository.getIncubatorById(id)
            _uiState.value = _uiState.value.copy(incubator = incubator)
            
            // Collect flow of trays
            repository.getTraysForIncubator(id).collect { trays ->
                _uiState.value = _uiState.value.copy(trays = trays)
            }
        }
    }
    
    fun addTray(incubatorId: Long, capacity: Int) {
         viewModelScope.launch {
             // Calculate next index
             val currentCount = _uiState.value.trays.size
             repository.addTray(
                 TrayEntity(
                     incubatorId = incubatorId,
                     index = currentCount + 1,
                     capacity = capacity
                 )
             )
         }
    }

    fun deleteTray(tray: TrayEntity) {
        viewModelScope.launch {
            repository.deleteTrayWithBatches(tray)
        }
    }

    fun updateTempHumidity(temp: Float, humidity: Float) {
        viewModelScope.launch {
            val incubator = _uiState.value.incubator ?: return@launch
            repository.updateIncubator(
                incubator.copy(
                    currentTemp = temp,
                    currentHumidity = humidity
                )
            )
            loadIncubator(incubator.id)
        }
    }
}

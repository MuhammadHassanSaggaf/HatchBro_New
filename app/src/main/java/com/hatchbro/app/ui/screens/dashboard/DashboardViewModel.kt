package com.hatchbro.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class BatchWithDetails(
    val batch: BatchEntity,
    val speciesName: String,
    val breedName: String
)

data class DashboardUiState(
    val incubators: List<IncubatorEntity> = emptyList(),
    val activeBatches: List<BatchEntity> = emptyList(),
    val batchDetails: Map<Long, Pair<String, String>> = emptyMap() // batchId -> (species, breed)
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.incubators,
        repository.activeBatches
    ) { incubators, activeBatches ->
        // Fetch species and breed details for each batch
        val details = mutableMapOf<Long, Pair<String, String>>()
        activeBatches.forEach { batch ->
            val species = repository.getSpeciesById(batch.speciesId)
            val breed = repository.getBreedById(batch.breedId)
            if (species != null && breed != null) {
                details[batch.id] = Pair(species.name, breed.name)
            }
        }
        DashboardUiState(
            incubators = incubators,
            activeBatches = activeBatches,
            batchDetails = details
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}

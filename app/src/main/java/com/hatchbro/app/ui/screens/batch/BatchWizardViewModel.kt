package com.hatchbro.app.ui.screens.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import com.hatchbro.app.data.local.entity.TrayEntity
import com.hatchbro.app.data.repository.HatcheryRepository
import com.hatchbro.app.domain.calculators.DateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class BatchWizardUiState(
    val incubators: List<IncubatorEntity> = emptyList(),
    val speciesList: List<SpeciesEntity> = emptyList(),
    val selectedIncubator: IncubatorEntity? = null,
    val availableTrays: List<TrayEntity> = emptyList(),
    val selectedTray: TrayEntity? = null,
    val selectedSpecies: SpeciesEntity? = null,
    val availableBreeds: List<BreedEntity> = emptyList(),
    val selectedBreed: BreedEntity? = null,
    val eggsSet: String = "",
    val startDate: Date = Date(), // Default now
    val expectedHatchDate: Date? = null,
    val lockdownDate: Date? = null,
    val errorMessage: String? = null,
    val tempHumidityWarning: String? = null // Warning for temp/humidity out of breed range
)

@HiltViewModel
class BatchWizardViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatchWizardUiState())
    val uiState: StateFlow<BatchWizardUiState> = _uiState

    init {
        // Observe Incubators and Species
        viewModelScope.launch {
            combine(
                repository.incubators,
                repository.allSpecies
            ) { incubators, species ->
                Pair(incubators, species)
            }.collect { (incubators, species) ->
                _uiState.value = _uiState.value.copy(
                    incubators = incubators,
                    speciesList = species
                )
            }
        }
    }

    fun selectIncubator(incubator: IncubatorEntity) {
        _uiState.value = _uiState.value.copy(selectedIncubator = incubator, selectedTray = null)
        viewModelScope.launch {
            repository.getTraysForIncubator(incubator.id).collect { trays ->
                _uiState.value = _uiState.value.copy(availableTrays = trays)
            }
        }
    }

    fun selectTray(tray: TrayEntity) {
        _uiState.value = _uiState.value.copy(selectedTray = tray, errorMessage = null)
        // Validate capacity when eggs are updated
    }

    fun selectSpecies(species: SpeciesEntity) {
        _uiState.value = _uiState.value.copy(selectedSpecies = species, selectedBreed = null)
        viewModelScope.launch {
            repository.getBreedsForSpecies(species.id).collect { breeds ->
                _uiState.value = _uiState.value.copy(availableBreeds = breeds)
            }
        }
    }

    fun selectBreed(breed: BreedEntity) {
        _uiState.value = _uiState.value.copy(selectedBreed = breed)
        calculateDates()
        validateTempHumidity()
    }

    fun updateEggsSet(count: String) {
        _uiState.value = _uiState.value.copy(eggsSet = count, errorMessage = null)
        validateTrayCapacity()
    }

    fun updateStartDate(date: Long) {
        _uiState.value = _uiState.value.copy(startDate = Date(date))
        calculateDates()
    }

    private fun validateTrayCapacity() {
        val tray = _uiState.value.selectedTray ?: return
        val newEggs = _uiState.value.eggsSet.toIntOrNull() ?: return
        
        viewModelScope.launch {
            val currentEggs = repository.getTotalActiveEggsInTray(tray.id)
            val totalAfterAdding = currentEggs + newEggs
            
            if (totalAfterAdding > tray.capacity) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Tray capacity exceeded! Current: $currentEggs, Adding: $newEggs, Capacity: ${tray.capacity}"
                )
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
        }
    }

    private fun calculateDates() {
        val breed = _uiState.value.selectedBreed ?: return
        val start = _uiState.value.startDate
        val hatch = DateCalculator.calculateExpectedHatchDate(start, breed.defaultIncubationDays)
        val lockdown = DateCalculator.calculateLockdownDate(start, breed.defaultIncubationDays, breed.defaultLockdownDays)
        _uiState.value = _uiState.value.copy(
            expectedHatchDate = hatch,
            lockdownDate = lockdown
        )
    }

    private fun validateTempHumidity() {
        val breed = _uiState.value.selectedBreed ?: return
        val incubator = _uiState.value.selectedIncubator ?: return
        
        val warnings = mutableListOf<String>()
        
        // Check temperature
        if (incubator.currentTemp < breed.minTemp || incubator.currentTemp > breed.maxTemp) {
            warnings.add("Temperature ${incubator.currentTemp}°C is outside recommended range (${breed.minTemp}-${breed.maxTemp}°C)")
        }
        
        // Check humidity
        if (incubator.currentHumidity < breed.minHumidity || incubator.currentHumidity > breed.maxHumidity) {
            warnings.add("Humidity ${incubator.currentHumidity}% is outside recommended range (${breed.minHumidity}-${breed.maxHumidity}%)")
        }
        
        _uiState.value = _uiState.value.copy(
            tempHumidityWarning = if (warnings.isNotEmpty()) warnings.joinToString("\n") else null
        )
    }

    fun saveBatch(onSuccess: () -> Unit) {
        val state = _uiState.value
        // Check for validation errors
        if (state.errorMessage != null) return
        
        if (state.selectedTray == null || state.selectedBreed == null || state.selectedSpecies == null) return
        val eggs = state.eggsSet.toIntOrNull() ?: return
        
        val hatchDate = DateCalculator.calculateExpectedHatchDate(state.startDate, state.selectedBreed.defaultIncubationDays)
        val lockdownDate = DateCalculator.calculateLockdownDate(state.startDate, state.selectedBreed.defaultIncubationDays, state.selectedBreed.defaultLockdownDays)
        val discardDate = DateCalculator.calculateDiscardDate(hatchDate, state.selectedBreed.defaultDiscardGraceDays)
        
        viewModelScope.launch {
            repository.addBatch(
                BatchEntity(
                    trayId = state.selectedTray.id,
                    speciesId = state.selectedSpecies.id,
                    breedId = state.selectedBreed.id,
                    eggsSet = eggs,
                    startDate = state.startDate,
                    incubationDays = state.selectedBreed.defaultIncubationDays,
                    expectedHatchDate = hatchDate,
                    lockdownDate = lockdownDate,
                    discardDate = discardDate
                )
            )
            onSuccess()
        }
    }
}

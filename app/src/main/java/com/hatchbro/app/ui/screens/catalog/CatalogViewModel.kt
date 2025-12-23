package com.hatchbro.app.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    val speciesWithBreeds = repository.speciesWithBreeds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addSpecies(name: String) {
        viewModelScope.launch {
            repository.addSpecies(SpeciesEntity(name = name))
        }
    }

    fun deleteSpecies(species: SpeciesEntity) {
        viewModelScope.launch {
            repository.deleteSpecies(species)
        }
    }

    fun addBreed(
        speciesId: Long,
        name: String,
        incubationDays: Int,
        lockdownDays: Int,
        discardDays: Int
    ) {
        viewModelScope.launch {
            repository.addBreed(
                BreedEntity(
                    speciesId = speciesId,
                    name = name,
                    defaultIncubationDays = incubationDays,
                    defaultLockdownDays = lockdownDays,
                    defaultDiscardGraceDays = discardDays,
                    minTemp = 37.5f,
                    maxTemp = 38.0f,
                    minHumidity = 45f,
                    maxHumidity = 65f
                )
            )
        }
    }

    fun deleteBreed(breed: BreedEntity) {
        viewModelScope.launch {
            repository.deleteBreed(breed)
        }
    }
}


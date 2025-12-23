package com.hatchbro.app.ui.screens.incubator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncubatorListViewModel @Inject constructor(
    private val repository: HatcheryRepository
) : ViewModel() {

    val incubators = repository.incubators.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addIncubator(name: String, model: String, location: String, trayCount: Int, temp: Float, humidity: Float) {
        viewModelScope.launch {
            repository.addIncubator(
                IncubatorEntity(
                    name = name,
                    model = model,
                    trayCount = trayCount,
                    location = location,
                    currentTemp = temp,
                    currentHumidity = humidity
                )
            )
        }
    }
}

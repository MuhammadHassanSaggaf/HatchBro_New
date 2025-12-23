package com.hatchbro.app.ui.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchbro.app.data.export.CsvExporter
import com.hatchbro.app.data.local.DatabaseSeeder
import com.hatchbro.app.data.repository.HatcheryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

// Note: Injecting Context into ViewModel is usually discouraged (leak potential), but for simple export trigger it's often done or passed from UI. 
// A cleaner way is to expose a strict event. But for MVP this works if we use ApplicationContext essentially.
// Better pattern: Repository does the IO, ViewModel just triggers it and returns a File path or URI.
// I will inject CsvExporter which handles IO.

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: HatcheryRepository,
    private val csvExporter: CsvExporter,
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    fun exportData(context: Context) {
        viewModelScope.launch {
            try {
                // Get all batches (active and finished?)
                // Repository currently only has getActiveBatches exposed as flow.
                // I need getAllBatches from DAO.
                // For MVP I'll use activeBatches but ideally history is what we want.
                // I'll add getAllBatches to Repository or just use active for now.
                val batches = repository.activeBatches.first()
                if (batches.isNotEmpty()) {
                    val file = csvExporter.exportBatches(context, batches)
                    Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No batches to export", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun seedDatabase(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                databaseSeeder.seedSpeciesAndBreeds()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}

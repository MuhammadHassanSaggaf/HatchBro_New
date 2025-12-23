package com.hatchbro.app.data.repository

import com.hatchbro.app.data.local.dao.BatchDao
import com.hatchbro.app.data.local.dao.CatalogDao
import com.hatchbro.app.data.local.dao.EventDao
import com.hatchbro.app.data.local.dao.IncubatorDao
import com.hatchbro.app.data.local.dao.ReadingDao
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.EventEntity
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.local.entity.ReadingEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import com.hatchbro.app.data.local.entity.TrayEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HatcheryRepository @Inject constructor(
    private val incubatorDao: IncubatorDao,
    private val batchDao: BatchDao,
    private val catalogDao: CatalogDao,
    private val readingDao: ReadingDao,
    private val eventDao: EventDao
) {
    // Incubators
    val incubators = incubatorDao.getAllIncubators()
    
    suspend fun getIncubatorById(id: Long) = incubatorDao.getIncubatorById(id)
    
    suspend fun addIncubator(incubator: IncubatorEntity) = incubatorDao.insertIncubator(incubator)

    suspend fun updateIncubator(incubator: IncubatorEntity) = incubatorDao.updateIncubator(incubator)
    
    suspend fun addTray(tray: TrayEntity) = incubatorDao.insertTray(tray)

    suspend fun deleteTray(tray: TrayEntity) = incubatorDao.deleteTray(tray)

    suspend fun deleteTrayWithBatches(tray: TrayEntity) {
        // Delete all batches in the tray first to avoid foreign key constraint
        batchDao.deleteBatchesInTray(tray.id)
        // Then delete the tray
        incubatorDao.deleteTray(tray)
    }
    
    fun getTraysForIncubator(incubatorId: Long) = incubatorDao.getTraysForIncubator(incubatorId)

    // Catalog
    val allSpecies = catalogDao.getAllSpecies()

    val speciesWithBreeds = catalogDao.getSpeciesWithBreeds()
    
    fun getBreedsForSpecies(speciesId: Long) = catalogDao.getBreedsForSpecies(speciesId)
    
    suspend fun addSpecies(species: SpeciesEntity) = catalogDao.insertSpecies(species)

    suspend fun deleteSpecies(species: SpeciesEntity) = catalogDao.deleteSpecies(species)
    
    suspend fun getSpeciesById(id: Long) = catalogDao.getSpeciesById(id)
    
    suspend fun addBreed(breed: BreedEntity) = catalogDao.insertBreed(breed)

    suspend fun deleteBreed(breed: BreedEntity) = catalogDao.deleteBreed(breed)
    
    suspend fun getBreedById(id: Long) = catalogDao.getBreedById(id)

    // Batches
    val activeBatches = batchDao.getActiveBatches()
    
    suspend fun getBatchById(id: Long) = batchDao.getBatchById(id)
    
    fun getBatchesForTray(trayId: Long) = batchDao.getBatchesForTray(trayId)

    suspend fun getTotalActiveEggsInTray(trayId: Long): Int = batchDao.getTotalActiveEggsInTray(trayId) ?: 0

    suspend fun addBatch(batch: BatchEntity) = batchDao.insertBatch(batch)
    
    suspend fun updateBatch(batch: BatchEntity) = batchDao.updateBatch(batch)

    // Readings & Events
    suspend fun addReading(reading: ReadingEntity) = readingDao.insertReading(reading)
    
    fun getRecentReadings(incubatorId: Long) = readingDao.getRecentReadingsForIncubator(incubatorId)
    
    suspend fun addEvent(event: EventEntity) = eventDao.insertEvent(event)
    
    fun getEventsForBatch(batchId: Long) = eventDao.getEventsForBatch(batchId)
}

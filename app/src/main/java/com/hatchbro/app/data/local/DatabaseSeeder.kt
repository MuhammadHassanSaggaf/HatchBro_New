package com.hatchbro.app.data.local

import com.hatchbro.app.data.local.dao.CatalogDao
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val catalogDao: CatalogDao
) {
    suspend fun seedSpeciesAndBreeds() {
        PrepopulateData.initialData.forEach { initialSpecies ->
            // Insert species
            val speciesId = catalogDao.insertSpecies(
                SpeciesEntity(name = initialSpecies.name)
            )

            // Insert breeds for this species
            initialSpecies.breeds.forEach { initialBreed ->
                catalogDao.insertBreed(
                    BreedEntity(
                        speciesId = speciesId,
                        name = initialBreed.name,
                        defaultIncubationDays = initialBreed.incubationDays,
                        defaultLockdownDays = initialBreed.lockdownDays,
                        defaultDiscardGraceDays = 2,
                        minTemp = 37.5f,
                        maxTemp = 38.0f,
                        minHumidity = 45f,
                        maxHumidity = 65f
                    )
                )
            }
        }
    }
}

package com.hatchbro.app.data.local.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hatchbro.app.data.local.entity.BreedEntity
import com.hatchbro.app.data.local.entity.SpeciesEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction
import com.hatchbro.app.data.local.entity.SpeciesWithBreeds

@Dao
interface CatalogDao {
    @Query("SELECT * FROM species")
    fun getAllSpecies(): Flow<List<SpeciesEntity>>

    @Transaction
    @Query("SELECT * FROM species")
    fun getSpeciesWithBreeds(): Flow<List<SpeciesWithBreeds>>

    @Query("SELECT * FROM breeds WHERE speciesId = :speciesId")
    fun getBreedsForSpecies(speciesId: Long): Flow<List<BreedEntity>>

    @Query("SELECT * FROM species WHERE id = :id")
    suspend fun getSpeciesById(id: Long): SpeciesEntity?

    @Query("SELECT * FROM breeds WHERE id = :id")
    suspend fun getBreedById(id: Long): BreedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecies(species: SpeciesEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreed(breed: BreedEntity): Long

    @Delete
    suspend fun deleteSpecies(species: SpeciesEntity)

    @Delete
    suspend fun deleteBreed(breed: BreedEntity)
}

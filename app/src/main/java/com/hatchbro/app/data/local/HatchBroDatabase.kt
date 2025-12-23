package com.hatchbro.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hatchbro.app.data.local.dao.BatchDao
import com.hatchbro.app.data.local.dao.CatalogDao
import com.hatchbro.app.data.local.dao.EventDao
import com.hatchbro.app.data.local.dao.IncubatorDao
import com.hatchbro.app.data.local.dao.ReadingDao
import com.hatchbro.app.data.local.entity.*

@Database(
    entities = [
        IncubatorEntity::class,
        TrayEntity::class,
        SpeciesEntity::class,
        BreedEntity::class,
        BatchEntity::class,
        ReadingEntity::class,
        EventEntity::class
    ],
    version = 2, // Incremented for temperature/humidity fields
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HatchBroDatabase : RoomDatabase() {
    abstract fun incubatorDao(): IncubatorDao
    abstract fun batchDao(): BatchDao
    abstract fun catalogDao(): CatalogDao
    abstract fun readingDao(): ReadingDao
    abstract fun eventDao(): EventDao

    companion object {
        const val DATABASE_NAME = "hatchbro_db"
    }
}

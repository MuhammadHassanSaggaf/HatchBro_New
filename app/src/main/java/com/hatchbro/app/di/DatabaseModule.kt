package com.hatchbro.app.di

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hatchbro.app.data.local.HatchBroDatabase
import com.hatchbro.app.data.local.PrepopulateData
import com.hatchbro.app.data.local.dao.BatchDao
import com.hatchbro.app.data.local.dao.CatalogDao
import com.hatchbro.app.data.local.dao.EventDao
import com.hatchbro.app.data.local.dao.IncubatorDao
import com.hatchbro.app.data.local.dao.ReadingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HatchBroDatabase {
        return Room.databaseBuilder(
            context,
            HatchBroDatabase::class.java,
            "hatchbro.db"
        )
        .fallbackToDestructiveMigration() // Allow schema changes during development
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate species and breeds
                PrepopulateData.initialData.forEach { species ->
                    val speciesValues = ContentValues().apply {
                        put("name", species.name)
                    }
                    val speciesId = db.insert("species", SQLiteDatabase.CONFLICT_REPLACE, speciesValues)
                    
                    species.breeds.forEach { breed ->
                        val breedValues = ContentValues().apply {
                            put("speciesId", speciesId)
                            put("name", breed.name)
                            put("defaultIncubationDays", breed.incubationDays)
                            put("defaultLockdownDays", breed.lockdownDays)
                            put("defaultDiscardGraceDays", 2)
                            put("minTemp", 37.0f)
                            put("maxTemp", 39.0f)
                            put("minHumidity", 50f)
                            put("maxHumidity", 65f)
                        }
                        db.insert("breeds", SQLiteDatabase.CONFLICT_REPLACE, breedValues)
                    }
                }
            }
        })
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideIncubatorDao(db: HatchBroDatabase): IncubatorDao = db.incubatorDao()

    @Provides
    fun provideBatchDao(db: HatchBroDatabase): BatchDao = db.batchDao()

    @Provides
    fun provideCatalogDao(db: HatchBroDatabase): CatalogDao = db.catalogDao()

    @Provides
    fun provideReadingDao(db: HatchBroDatabase): ReadingDao = db.readingDao()

    @Provides
    fun provideEventDao(db: HatchBroDatabase): EventDao = db.eventDao()
}

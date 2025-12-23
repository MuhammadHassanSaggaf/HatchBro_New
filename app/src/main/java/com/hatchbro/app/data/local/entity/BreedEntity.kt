package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeds",
    foreignKeys = [
        ForeignKey(
            entity = SpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["speciesId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("speciesId")]
)
data class BreedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val speciesId: Long,
    val name: String,
    val defaultIncubationDays: Int,
    val defaultLockdownDays: Int,
    val defaultDiscardGraceDays: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val minHumidity: Float,
    val maxHumidity: Float
)

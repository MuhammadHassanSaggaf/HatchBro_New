package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incubators")
data class IncubatorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val location: String,
    val trayCount: Int,
    val model: String,
    val currentTemp: Float = 37.5f, // Default chicken temp
    val currentHumidity: Float = 55f // Default humidity
)

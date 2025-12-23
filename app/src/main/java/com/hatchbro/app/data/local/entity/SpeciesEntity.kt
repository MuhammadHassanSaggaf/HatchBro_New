package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)

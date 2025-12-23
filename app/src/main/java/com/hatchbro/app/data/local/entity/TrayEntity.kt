package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trays",
    foreignKeys = [
        ForeignKey(
            entity = IncubatorEntity::class,
            parentColumns = ["id"],
            childColumns = ["incubatorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("incubatorId")]
)
data class TrayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val incubatorId: Long,
    val index: Int, // Position/Index in incubator
    val capacity: Int
)

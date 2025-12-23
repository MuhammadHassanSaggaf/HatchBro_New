package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "readings",
    foreignKeys = [
        ForeignKey(
            entity = IncubatorEntity::class,
            parentColumns = ["id"],
            childColumns = ["incubatorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("incubatorId"),
        Index("batchId")
    ]
)
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val incubatorId: Long,
    val batchId: Long? = null, // Optional link to specific batch
    val timestamp: Date,
    val temp: Float,
    val humidity: Float
)

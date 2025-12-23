package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

enum class EventType {
    CANDLING,
    HATCH,
    DISCARD,
    DEATH,
    NOTE,
    ALERT
}

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("batchId")]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val timestamp: Date,
    val type: EventType,
    val value: String? = null, // e.g., "3" for count, or "High Temp"
    val notes: String? = null
)

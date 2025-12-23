package com.hatchbro.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

enum class BatchStatus {
    INCUBATING,
    LOCKDOWN,
    HATCHING,
    COMPLETED,
    DISCARDED
}

@Entity(
    tableName = "batches",
    foreignKeys = [
        ForeignKey(
            entity = TrayEntity::class,
            parentColumns = ["id"],
            childColumns = ["trayId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["speciesId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BreedEntity::class,
            parentColumns = ["id"],
            childColumns = ["breedId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("trayId"),
        Index("speciesId"),
        Index("breedId")
    ]
)
data class BatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trayId: Long,
    val speciesId: Long,
    val breedId: Long,
    val eggsSet: Int,
    val startDate: Date,
    val incubationDays: Int,
    val expectedHatchDate: Date,
    val lockdownDate: Date,
    val discardDate: Date,
    val hatchedCount: Int = 0,
    val discardedCount: Int = 0,
    val status: BatchStatus = BatchStatus.INCUBATING,
    val notes: String? = null
)

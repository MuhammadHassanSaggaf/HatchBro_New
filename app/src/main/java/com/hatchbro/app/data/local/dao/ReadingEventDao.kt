package com.hatchbro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hatchbro.app.data.local.entity.EventEntity
import com.hatchbro.app.data.local.entity.ReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Insert
    suspend fun insertReading(reading: ReadingEntity): Long

    @Query("SELECT * FROM readings WHERE incubatorId = :incubatorId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadingsForIncubator(incubatorId: Long, limit: Int = 50): Flow<List<ReadingEntity>>
}

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity): Long

    @Query("SELECT * FROM events WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getEventsForBatch(batchId: Long): Flow<List<EventEntity>>
}

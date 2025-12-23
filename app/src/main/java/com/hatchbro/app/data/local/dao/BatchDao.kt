package com.hatchbro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hatchbro.app.data.local.entity.BatchEntity
import com.hatchbro.app.data.local.entity.BatchStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches WHERE status NOT IN ('COMPLETED', 'DISCARDED') ORDER BY expectedHatchDate ASC")
    fun getActiveBatches(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE trayId = :trayId")
    fun getBatchesForTray(trayId: Long): Flow<List<BatchEntity>>

    @Query("SELECT SUM(eggsSet) FROM batches WHERE trayId = :trayId AND status NOT IN ('COMPLETED', 'DISCARDED')")
    suspend fun getTotalActiveEggsInTray(trayId: Long): Int?

    @Query("SELECT * FROM batches WHERE id = :id")
    suspend fun getBatchById(id: Long): BatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity): Long

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Query("DELETE FROM batches WHERE trayId = :trayId")
    suspend fun deleteBatchesInTray(trayId: Long)
}

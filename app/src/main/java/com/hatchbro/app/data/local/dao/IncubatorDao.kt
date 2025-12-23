package com.hatchbro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.hatchbro.app.data.local.entity.IncubatorEntity
import com.hatchbro.app.data.local.entity.TrayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubatorDao {
    @Query("SELECT * FROM incubators")
    fun getAllIncubators(): Flow<List<IncubatorEntity>>

    @Query("SELECT * FROM incubators WHERE id = :id")
    suspend fun getIncubatorById(id: Long): IncubatorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncubator(incubator: IncubatorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTray(tray: TrayEntity): Long

    @Query("SELECT * FROM trays WHERE incubatorId = :incubatorId ORDER BY `index` ASC")
    fun getTraysForIncubator(incubatorId: Long): Flow<List<TrayEntity>>

    @Delete
    suspend fun deleteTray(tray: TrayEntity)

    @Update
    suspend fun updateIncubator(incubator: IncubatorEntity)
}

package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_table WHERE dateStr = :dateStr LIMIT 1")
    fun getRoutineByDate(dateStr: String): Flow<RoutineEntity?>

    @Query("SELECT * FROM routine_table ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Query("SELECT COUNT(*) FROM routine_table")
    suspend fun getCount(): Int
}

package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE dateKey = :dateKey LIMIT 1")
    fun getRoutineByDateKey(dateKey: String): Flow<RoutineEntity?>

    @Query("SELECT * FROM routines WHERE dateKey = :dateKey LIMIT 1")
    fun getRoutineByDateKeySync(dateKey: String): RoutineEntity?

    @Query("SELECT COUNT(*) FROM routines")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)
}

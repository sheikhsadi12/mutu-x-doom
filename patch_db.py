import re

with open('app/src/main/java/com/example/data/RoutineEntity.kt', 'w') as f:
    f.write('''package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val dateKey: String, // format "yyyy-MM-dd"
    val displayDate: String,
    val phase: String,
    val morning: String,
    val noon: String,
    val night: String,
    val target: String,
    val ratio: String = "3:1"
)
''')

with open('app/src/main/java/com/example/data/RoutineDao.kt', 'w') as f:
    f.write('''package com.example.data

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
''')

with open('app/src/main/java/com/example/data/AppDatabase.kt', 'r') as f:
    content = f.read()

content = content.replace('version = 1', 'version = 2')
with open('app/src/main/java/com/example/data/AppDatabase.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/RoutineRepository.kt', 'w') as f:
    f.write('''package com.example.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutineByDateKey(dateKey: String): Flow<RoutineEntity?> {
        return routineDao.getRoutineByDateKey(dateKey)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun initializeDataIfNeeded() {
        if (routineDao.getCount() == 0) {
            val initialData = listOf(
                RoutineEntity(dateKey="2026-08-22", displayDate="২২ আগস্ট (শনি)", phase="Phase 1", morning="গতিবিদ্যা-১", noon="রাসায়নিক পরিবর্তন-১ + C-01 ম্যাথ", night="C-01 এর গাণিতিক সমস্যা", target="C-01 গাণিতিক সমস্যা", ratio="3:1"),
                RoutineEntity(dateKey="2026-08-23", displayDate="২৩ আগস্ট (রবি)", phase="Phase 1", morning="গতিবিদ্যা-২", noon="রাসায়নিক পরিবর্তন-২ + C-01 Raw MCQ", night="🎯 C-01 ফুল রিভিশন (কাল এক্সাম)", target="C-01 এক্সাম প্রস্তুতি", ratio="3:1"),
                RoutineEntity(dateKey="2026-08-24", displayDate="২৪ আগস্ট (সোম)", phase="Phase 1", morning="গতিবিদ্যা-৩", noon="🎯 অফলাইন এক্সাম: C-01", night="M-01 (সরলরেখা) সূত্র রিভিউ", target="M-01 রিভিশন", ratio="3:1")
            )
            // Seed a few more days dynamically to ensure it works beyond the 24th
            val mutableData = initialData.toMutableList()
            var currentDate = LocalDate.of(2026, 8, 25)
            for (i in 0 until 14) {
                val dateKeyStr = currentDate.toString()
                mutableData.add(
                    RoutineEntity(
                        dateKey = dateKeyStr,
                        displayDate = "${currentDate.dayOfMonth} Month", // placeholder
                        phase = "Phase 2",
                        morning = "Pending data",
                        noon = "Pending data",
                        night = "Pending data",
                        target = "Next Assignment",
                        ratio = "3:1"
                    )
                )
                currentDate = currentDate.plusDays(1)
            }
            routineDao.insertAll(mutableData)
        }
    }
}
''')


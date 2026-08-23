package com.example.data

import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutineByDateKey(dateKey: String): Flow<RoutineEntity?> {
        return routineDao.getRoutineByDateKey(dateKey)
    }

    suspend fun getRoutineByDateKeySync(dateKey: String): RoutineEntity? {
        return routineDao.getRoutineByDateKeySync(dateKey)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun ensureDataInitialized() {
        if (routineDao.getCount() == 0) {
            AppDatabase.prepopulateDatabase(routineDao)
        }
    }
}

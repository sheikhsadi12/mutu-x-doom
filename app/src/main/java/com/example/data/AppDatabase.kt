package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RoutineEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "routine_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                prepopulateDatabase(database.routineDao())
                            }
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                if (database.routineDao().getCount() == 0) {
                                    prepopulateDatabase(database.routineDao())
                                }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun prepopulateDatabase(dao: RoutineDao) {
            val initialRoutines = listOf(
                RoutineEntity(
                    dateKey = "2026-08-22",
                    displayDate = "২২ আগস্ট (শনি)",
                    phase = "Phase 1",
                    morning = "গতিবিদ্যা-১",
                    noon = "রাসায়নিক পরিবর্তন-১ + C-01 ম্যাথ",
                    night = "C-01 এর গাণিতিক সমস্যা",
                    target = "C-01 গাণিতিক সমস্যা",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-23",
                    displayDate = "২৩ আগস্ট (রবি)",
                    phase = "Phase 1",
                    morning = "গতিবিদ্যা-২",
                    noon = "রাসায়নিক পরিবর্তন-২ + C-01 Raw MCQ",
                    night = "🎯 C-01 ফুল রিভিশন (কাল এক্সাম)",
                    target = "C-01 এক্সাম প্রস্তুতি",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-24",
                    displayDate = "২৪ আগস্ট (সোম)",
                    phase = "Phase 1",
                    morning = "গতিবিদ্যা-৩",
                    noon = "🎯 অফলাইন এক্সাম: C-01",
                    night = "M-01 (সরলরেখা) সূত্র রিভিউ",
                    target = "M-01 রিভিশন",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-25",
                    displayDate = "২৫ আগস্ট (মঙ্গল)",
                    phase = "Phase 1",
                    morning = "কাজ, ক্ষমতা ও শক্তি-১",
                    noon = "M-01 প্র্যাকটিস ও সূত্র প্রয়োগ",
                    night = "🎯 M-01 ফুল রিভিশন (কাল এক্সাম)",
                    target = "M-01 এক্সাম প্রস্তুতি",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-26",
                    displayDate = "২৬ আগস্ট (বুধ)",
                    phase = "Phase 1",
                    morning = "কাজ, ক্ষমতা ও শক্তি-২",
                    noon = "🎯 অফলাইন এক্সাম: M-01",
                    night = "পর্যাবৃত্তিক গতি সূত্র ও রিভিশন",
                    target = "P-01 রিভিশন",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-27",
                    displayDate = "২৭ আগস্ট (বৃহ)",
                    phase = "Phase 1",
                    morning = "মহাকর্ষ ও অভিকর্ষ-১",
                    noon = "P-01 প্র্যাকটিস + Raw MCQ",
                    night = "🎯 P-01 ফুল রিভিশন (কাল এক্সাম)",
                    target = "P-01 এক্সাম প্রস্তুতি",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-28",
                    displayDate = "২৮ আগস্ট (শুক্র)",
                    phase = "Phase 1",
                    morning = "মহাকর্ষ ও অভিকর্ষ-২",
                    noon = "🎯 অফলাইন এক্সাম: P-01",
                    night = "উইকলি রিভিশন ও উইক পয়েন্ট নোট",
                    target = "সাপ্তাহিক মূল্যায়ন",
                    ratio = "3:1"
                ),
                RoutineEntity(
                    dateKey = "2026-08-29",
                    displayDate = "২৯ আগস্ট (শনি)",
                    phase = "Phase 1",
                    morning = "পদার্থের গাঠনিক ধর্ম-১",
                    noon = "🎯 Weekly Exam-01 প্রস্তুতি",
                    night = "🎯 Weekly Exam-01 ফাইনাল রিভিশন",
                    target = "উইকলি এক্সাম টপ স্কোর",
                    ratio = "3:1"
                )
            )
            dao.insertAll(initialRoutines)
        }
    }
}

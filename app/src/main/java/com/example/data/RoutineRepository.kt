package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutineByDate(dateStr: String): Flow<RoutineEntity?> {
        return routineDao.getRoutineByDate(dateStr)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun initializeDataIfNeeded() {
        if (routineDao.getCount() == 0) {
            val initialData = listOf(
                RoutineEntity(dateStr = "Aug 19", phase = "Phase 1", morning = "ভেক্টর-৩", noon = "পরিমাণগত রসায়ন-১ + সরলরেখা-২", night = "P-01 (ভেক্টর) এর প্রশ্নব্যাংক", target = "ভেক্টর ও সরলরেখার বেসিক"),
                RoutineEntity(dateStr = "Aug 20", phase = "Phase 1", morning = "পরিমাণগত রসায়ন-২", noon = "পরিমাণগত রসায়ন-৩ + সরলরেখা-৩", night = "🎯 P-01 ফুল রিভিশন (কাল এক্সাম)", target = "P-01 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Aug 21", phase = "Phase 1", morning = "পরিমাণগত রসায়ন-৪", noon = "🎯 অফলাইন এক্সাম: P-01 (১১টা) + সরলরেখা-৪", night = "C-01 থিওরি ও সূত্র রিভিশন", target = "C-01 থিওরি আয়ত্ত করা"),
                RoutineEntity(dateStr = "Aug 22", phase = "Phase 1", morning = "গতিবিদ্যা-১", noon = "রাসায়নিক পরিবর্তন-১ + C-01 ম্যাথ", night = "C-01 এর গাণিতিক সমস্যা সমাধান", target = "C-01 গাণিতিক সমস্যা"),
                RoutineEntity(dateStr = "Aug 23", phase = "Phase 1", morning = "গতিবিদ্যা-২", noon = "রাসায়নিক পরিবর্তন-২ + C-01 Raw MCQ", night = "🎯 C-01 ফুল রিভিশন (কাল এক্সাম)", target = "C-01 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Aug 24", phase = "Phase 1", morning = "গতিবিদ্যা-৩", noon = "🎯 অফলাইন এক্সাম: C-01 (১১টা) + রাসায়নিক পরি.-৩", night = "M-01 (সরলরেখা) সূত্র রিভিউ", target = "M-01 রিভিশন"),
                RoutineEntity(dateStr = "Aug 25", phase = "Phase 1", morning = "রাসায়নিক পরিবর্তন-৪", noon = "রাসায়নিক পরিবর্তন-৫ + M-01 QB", night = "🎯 M-01 ফুল রিভিশন (কাল এক্সাম)", target = "M-01 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Aug 26", phase = "Phase 1", morning = "রাসায়নিক পরিবর্তন-৬", noon = "🎯 অফলাইন এক্সাম: M-01 (১১টা) + বৃত্ত-১", night = "P-02 (গতিবিদ্যা) থিওরি", target = "P-02 থিওরি"),
                RoutineEntity(dateStr = "Aug 27", phase = "Phase 1", morning = "বৃত্ত-২", noon = "🎯 Weekly Exam-01 + নিউটনীয় বলবিদ্যা-১", night = "🎯 P-02 ফুল রিভিশন (কাল এক্সাম)", target = "উইকলি ও P-02 প্রস্তুতি"),
                RoutineEntity(dateStr = "Aug 28", phase = "Phase 1", morning = "বৃত্ত-৩", noon = "🎯 অফলাইন এক্সাম: P-02 + নিউটনীয় বলবিদ্যা-২", night = "C-02 (রাসায়নিক পরিবর্তন) পড়া", target = "C-02 থিওরি"),
                RoutineEntity(dateStr = "Aug 29", phase = "Phase 1", morning = "নিউটনীয় বলবিদ্যা-৩", noon = "নিউটনীয় বলবিদ্যা-৪ + C-02 Raw MCQ", night = "C-02 প্রশ্নব্যাংক সলভ", target = "C-02 ম্যাথ"),
                RoutineEntity(dateStr = "Aug 30", phase = "Phase 1", morning = "পর্যাবৃত্তিক ধর্ম-১", noon = "পর্যাবৃত্তিক ধর্ম-২ + C-02 থিওরি রিভিশন", night = "🎯 C-02 ফুল রিভিশন (কাল এক্সাম)", target = "C-02 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Aug 31", phase = "Phase 1", morning = "পর্যাবৃত্তিক ধর্ম-৩", noon = "🎯 অফলাইন এক্সাম: C-02 + পর্যাবৃত্তিক ধর্ম-৪", night = "M-02 (বৃত্ত) প্র্যাকটিস শুরু", target = "M-02 ম্যাথ"),
                RoutineEntity(dateStr = "Sep 01", phase = "Phase 2", morning = "পর্যাবৃত্তিক ধর্ম-৫", noon = "পর্যাবৃত্তিক ধর্ম-৬ + M-02 Raw MCQ", night = "🎯 M-02 ফুল রিভিশন (কাল এক্সাম)", target = "M-02 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 02", phase = "Phase 2", morning = "কণিক-১", noon = "🎯 অফলাইন এক্সাম: M-02 + কণিক-২", night = "P-03 (নিউটনীয় বলবিদ্যা) পড়া", target = "P-03 থিওরি রিভিশন"),
                RoutineEntity(dateStr = "Sep 03", phase = "Phase 2", morning = "কণিক-৩", noon = "🎯 Weekly Exam-02 + কাজ ও শক্তি-১", night = "🎯 P-03 ফুল রিভিশন (কাল এক্সাম)", target = "উইকলি ও P-03 প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 04", phase = "Phase 2", morning = "কাজ, ক্ষমতা ও শক্তি-২", noon = "🎯 অফলাইন এক্সাম: P-03 + ফাংশন-১", night = "C-03 (পরিবেশ রসায়ন) পড়া", target = "C-03 থিওরি"),
                RoutineEntity(dateStr = "Sep 05", phase = "Phase 2", morning = "ফাংশন-২", noon = "বাস্তব সংখ্যা ও অসমতা + C-03 ম্যাথ", night = "C-03 প্রশ্নব্যাংক", target = "C-03 ম্যাথ"),
                RoutineEntity(dateStr = "Sep 06", phase = "Phase 2", morning = "বহুপদী-১", noon = "বহুপদী-২ + C-03 Raw MCQ", night = "🎯 C-03 ফুল রিভিশন (কাল এক্সাম)", target = "C-03 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 07", phase = "Phase 2", morning = "মহাকর্ষ-১", noon = "🎯 অফলাইন এক্সাম: C-03 + মহাকর্ষ-২", night = "M-03 (কণিক) সূত্র রিভিউ", target = "M-03 রিভিশন"),
                RoutineEntity(dateStr = "Sep 08", phase = "Phase 2", morning = "Self-Study: গুণগত রসায়ন", noon = "Self-Study: গুণগত রসায়ন + M-03 QB", night = "🎯 M-03 ফুল রিভিশন (কাল এক্সাম)", target = "M-03 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 09", phase = "Phase 2", morning = "Self-Study: গুণগত রসায়ন", noon = "🎯 অফলাইন এক্সাম: M-03 + গুণগত রসায়ন", night = "P-04 (কাজ ও শক্তি) পড়া", target = "P-04 রিভিশন"),
                RoutineEntity(dateStr = "Sep 10", phase = "Phase 2", morning = "Self-Study: জটিল সংখ্যা", noon = "🎯 Weekly Exam-03 + জটিল সংখ্যা", night = "🎯 P-04 ফুল রিভিশন (কাল এক্সাম)", target = "উইকলি ও P-04 প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 11", phase = "Phase 3", morning = "Self-Study: জটিল সংখ্যা", noon = "🎯 অফলাইন এক্সাম: P-04 + জটিল সংখ্যা", night = "C-04 (গুণগত রসায়ন)", target = "C-04 থিওরি"),
                RoutineEntity(dateStr = "Sep 12", phase = "Phase 3", morning = "Self-Study: গাঠনিক ধর্ম", noon = "Self-Study: গাঠনিক ধর্ম + C-04 QB", night = "C-04 Raw MCQ", target = "C-04 প্র্যাকটিস"),
                RoutineEntity(dateStr = "Sep 13", phase = "Phase 3", morning = "Self-Study: গাঠনিক ধর্ম", noon = "পুরো সপ্তাহের পড়া রিভিউ + C-04 প্র্যাকটিস", night = "🎯 C-04 ফুল রিভিশন (কাল এক্সাম)", target = "C-04 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 14", phase = "Phase 3", morning = "M-04 প্র্যাকটিস", noon = "🎯 অফলাইন এক্সাম: C-04 + M-04 থিওরি", night = "M-04 প্রশ্নব্যাংক", target = "M-04 প্র্যাকটিস"),
                RoutineEntity(dateStr = "Sep 15", phase = "Phase 3", morning = "M-04 ম্যাথ প্র্যাকটিস", noon = "M-04 Raw MCQ সলভ", night = "🎯 M-04 ফুল রিভিশন (কাল এক্সাম)", target = "M-04 এক্সাম প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 16", phase = "Phase 3", morning = "P-05 (মহাকর্ষ ও গাঠনিক) পড়া", noon = "🎯 অফলাইন এক্সাম: M-04 + P-05 ম্যাথ", night = "P-05 প্রশ্নব্যাংক", target = "P-05 রিভিশন"),
                RoutineEntity(dateStr = "Sep 17", phase = "Phase 3", morning = "P-05 ম্যাথ প্র্যাকটিস", noon = "🎯 Weekly Exam-04 + P-05 থিওরি", night = "🎯 P-05 ফুল রিভিশন (কাল এক্সাম)", target = "উইকলি ও P-05 প্রস্তুতি"),
                RoutineEntity(dateStr = "Sep 18", phase = "Phase 3", morning = "মান্থলি এক্সাম প্রিপারেশন", noon = "🎯 অফলাইন এক্সাম: P-05 + মান্থলি প্রিপারেশন", night = "মান্থলি এক্সাম প্রিপারেশন", target = "গ্র্যান্ড রিভিশন"),
                RoutineEntity(dateStr = "Sep 19", phase = "Phase 3", morning = "মান্থলি এক্সাম প্রিপারেশন", noon = "ফুল সিলেবাস Raw MCQ", night = "সব বিষয়ের ফাইনাল রিভিশন", target = "ফাইনাল ব্রাশ-আপ"),
                RoutineEntity(dateStr = "Sep 20", phase = "Phase 3", morning = "কুইক ফর্মুলা ও শর্টকাট রিভিউ", noon = "🎯 Monthly Exam-01 (১১টা)", night = "RELAX & RECOVER", target = "এক মাসের পরিশ্রমের মূল্যায়ন!")
            )
            routineDao.insertAll(initialData)
        }
    }
}

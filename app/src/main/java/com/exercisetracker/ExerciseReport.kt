package com.exercisetracker

import java.text.SimpleDateFormat
import java.util.*

/**
 * 运动报表数据模型
 */
data class ExerciseReport(
    val period: String,           // 时间段 (如 "2025-01" 或 "2025")
    val totalDays: Int,           // 总记录天数
    val exerciseDays: Int,        // 运动天数
    val totalDuration: Int,       // 总运动时长（分钟）
    val averageDuration: Double,  // 平均运动时长（分钟）
    val exerciseRate: Double,     // 运动率（百分比）
    val longestStreak: Int,       // 最长连续运动天数
    val currentStreak: Int        // 当前连续运动天数
)

/**
 * 报表生成器
 */
class ExerciseReportGenerator(private val exerciseRecordManager: ExerciseRecordManager) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    
    /**
     * 生成月度报表
     */
    fun generateMonthlyReport(year: Int, month: Int): ExerciseReport {
        val calendar = Calendar.getInstance()
        
        // 获取月份的开始和结束日期
        calendar.set(year, month - 1, 1) // month是1-based，Calendar是0-based
        val startDate = calendar.time
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = calendar.time
        
        val records = exerciseRecordManager.getRecordsInRange(startDate, endDate)
        val period = String.format("%04d-%02d", year, month)
        
        return generateReport(records, period, startDate, endDate)
    }
    
    /**
     * 生成年度报表
     */
    fun generateYearlyReport(year: Int): ExerciseReport {
        val calendar = Calendar.getInstance()
        
        // 获取年份的开始和结束日期
        calendar.set(year, 0, 1) // 1月1日
        val startDate = calendar.time
        
        calendar.set(year, 11, 31) // 12月31日
        val endDate = calendar.time
        
        val records = exerciseRecordManager.getRecordsInRange(startDate, endDate)
        val period = year.toString()
        
        return generateReport(records, period, startDate, endDate)
    }
    
    /**
     * 获取所有可用的年份
     */
    fun getAvailableYears(): List<Int> {
        val allRecords = exerciseRecordManager.getAllRecords()
        val years = mutableSetOf<Int>()
        
        allRecords.forEach { record ->
            try {
                val date = dateFormat.parse(record.date)
                if (date != null) {
                    val calendar = Calendar.getInstance().apply { time = date }
                    years.add(calendar.get(Calendar.YEAR))
                }
            } catch (e: Exception) {
                // 忽略解析错误的日期
            }
        }
        
        return years.sorted().reversed() // 按年份倒序排列
    }
    
    /**
     * 获取指定年份的所有月份
     */
    fun getAvailableMonths(year: Int): List<Int> {
        val allRecords = exerciseRecordManager.getAllRecords()
        val months = mutableSetOf<Int>()
        
        allRecords.forEach { record ->
            try {
                val date = dateFormat.parse(record.date)
                if (date != null) {
                    val calendar = Calendar.getInstance().apply { time = date }
                    if (calendar.get(Calendar.YEAR) == year) {
                        months.add(calendar.get(Calendar.MONTH) + 1) // 转换为1-based
                    }
                }
            } catch (e: Exception) {
                // 忽略解析错误的日期
            }
        }
        
        return months.sorted().reversed() // 按月份倒序排列
    }
    
    /**
     * 生成报表核心逻辑
     */
    private fun generateReport(
        records: List<ExerciseRecord>, 
        period: String, 
        startDate: Date, 
        endDate: Date
    ): ExerciseReport {
        val totalDays = records.size
        val exerciseRecords = records.filter { it.exercised }
        val exerciseDays = exerciseRecords.size
        
        val totalDuration = exerciseRecords.sumOf { it.duration ?: 0 }
        val averageDuration = if (exerciseDays > 0) totalDuration.toDouble() / exerciseDays else 0.0
        val exerciseRate = if (totalDays > 0) (exerciseDays.toDouble() / totalDays) * 100 else 0.0
        
        // 计算连续运动天数
        val (longestStreak, currentStreak) = calculateStreaks(records)
        
        return ExerciseReport(
            period = period,
            totalDays = totalDays,
            exerciseDays = exerciseDays,
            totalDuration = totalDuration,
            averageDuration = averageDuration,
            exerciseRate = exerciseRate,
            longestStreak = longestStreak,
            currentStreak = currentStreak
        )
    }
    
    /**
     * 计算连续运动天数
     */
    private fun calculateStreaks(records: List<ExerciseRecord>): Pair<Int, Int> {
        if (records.isEmpty()) return Pair(0, 0)
        
        // 按日期排序
        val sortedRecords = records.sortedBy { record ->
            try {
                dateFormat.parse(record.date)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
        
        var longestStreak = 0
        var currentStreak = 0
        var tempStreak = 0
        
        sortedRecords.forEach { record ->
            if (record.exercised) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 0
            }
        }
        
        // 计算当前连续天数（从最后一天开始往前计算）
        for (i in sortedRecords.indices.reversed()) {
            if (sortedRecords[i].exercised) {
                currentStreak++
            } else {
                break
            }
        }
        
        return Pair(longestStreak, currentStreak)
    }
}
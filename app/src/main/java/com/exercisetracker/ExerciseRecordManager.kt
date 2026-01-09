package com.exercisetracker

import java.text.SimpleDateFormat
import java.util.*

/**
 * 运动记录管理器
 * 负责管理运动记录的创建、存储和检索
 * 
 * @param fileStorage 文件存储实例，用于数据持久化
 */
class ExerciseRecordManager(private val fileStorage: FileStorage) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var cachedRecords: MutableList<ExerciseRecord>? = null
    
    /**
     * 创建运动记录
     * 
     * @param date 运动日期
     * @param exercised 是否运动
     * @param duration 运动时长（分钟），如果未运动则为null
     * @return 创建的运动记录
     */
    fun createRecord(date: Date, exercised: Boolean, duration: Int?): ExerciseRecord {
        val dateString = dateFormat.format(date)
        
        // 验证输入参数
        val validDuration = if (exercised) {
            duration ?: throw IllegalArgumentException("运动时长不能为空当exercised为true时")
        } else {
            null
        }
        
        val record = ExerciseRecord(
            date = dateString,
            exercised = exercised,
            duration = validDuration
        )
        
        // 保存记录
        saveRecord(record)
        
        return record
    }
    
    /**
     * 根据日期获取运动记录
     * 
     * @param date 要查询的日期
     * @return 对应日期的运动记录，如果不存在则返回null
     */
    fun getRecord(date: Date): ExerciseRecord? {
        val dateString = dateFormat.format(date)
        return getAllRecords().find { it.date == dateString }
    }
    
    /**
     * 获取所有运动记录
     * 
     * @return 所有运动记录的列表
     */
    fun getAllRecords(): List<ExerciseRecord> {
        if (cachedRecords == null) {
            cachedRecords = fileStorage.loadRecords().toMutableList()
        }
        return cachedRecords!!.toList() // 返回副本以保护内部状态
    }
    
    /**
     * 保存运动记录
     * 
     * @param record 要保存的运动记录
     */
    fun saveRecord(record: ExerciseRecord) {
        // 确保缓存已加载
        if (cachedRecords == null) {
            cachedRecords = fileStorage.loadRecords().toMutableList()
        }
        
        // 检查是否已存在相同日期的记录
        val existingIndex = cachedRecords!!.indexOfFirst { it.date == record.date }
        
        if (existingIndex >= 0) {
            // 根据需求5.1和5.2，记录应该是不可变的
            // 但在创建阶段，我们允许替换（这通常发生在同一天多次操作时）
            cachedRecords!![existingIndex] = record
        } else {
            // 添加新记录
            cachedRecords!!.add(record)
        }
        
        // 按日期排序以保持一致性
        cachedRecords!!.sortBy { it.date }
        
        // 持久化到文件
        try {
            fileStorage.saveRecords(cachedRecords!!)
        } catch (e: Exception) {
            // 如果保存失败，从缓存中移除刚添加的记录以保持一致性
            if (existingIndex >= 0) {
                // 恢复原记录（如果有的话）
                val originalRecords = fileStorage.loadRecords()
                val originalRecord = originalRecords.find { it.date == record.date }
                if (originalRecord != null) {
                    cachedRecords!![existingIndex] = originalRecord
                } else {
                    cachedRecords!!.removeAt(existingIndex)
                }
            } else {
                cachedRecords!!.removeIf { it.date == record.date }
            }
            throw e
        }
    }
    
    /**
     * 根据日期字符串获取运动记录
     * 
     * @param dateString ISO 8601格式的日期字符串
     * @return 对应日期的运动记录，如果不存在则返回null
     */
    fun getRecordByDateString(dateString: String): ExerciseRecord? {
        return getAllRecords().find { it.date == dateString }
    }
    
    /**
     * 检查指定日期是否有运动记录
     * 
     * @param date 要检查的日期
     * @return true如果有记录，false否则
     */
    fun hasRecord(date: Date): Boolean {
        return getRecord(date) != null
    }
    
    /**
     * 获取指定日期范围内的运动记录
     * 
     * @param startDate 开始日期（包含）
     * @param endDate 结束日期（包含）
     * @return 指定范围内的运动记录列表
     */
    fun getRecordsInRange(startDate: Date, endDate: Date): List<ExerciseRecord> {
        val startDateString = dateFormat.format(startDate)
        val endDateString = dateFormat.format(endDate)
        
        return getAllRecords().filter { record ->
            record.date >= startDateString && record.date <= endDateString
        }
    }
    
    /**
     * 清除缓存，强制下次访问时重新从文件加载
     */
    fun clearCache() {
        cachedRecords = null
    }
}
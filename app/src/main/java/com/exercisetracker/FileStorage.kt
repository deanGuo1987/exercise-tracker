package com.exercisetracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * 文件存储类
 * 负责处理运动记录的JSON文件读写操作和数据持久化
 * 
 * @param context Android应用上下文，用于访问内部存储
 */
class FileStorage(private val context: Context) {
    
    private val gson = Gson()
    private val fileName = "exercise_records.json"
    
    /**
     * 保存运动记录列表到JSON文件
     * 
     * @param records 要保存的运动记录列表
     * @throws IOException 当文件写入失败时抛出
     */
    fun saveRecords(records: List<ExerciseRecord>) {
        try {
            val json = gson.toJson(records)
            val file = getStorageFile()
            file.writeText(json)
        } catch (e: IOException) {
            throw IOException("Failed to save exercise records: ${e.message}", e)
        }
    }
    
    /**
     * 从JSON文件加载运动记录列表
     * 
     * @return 运动记录列表，如果文件不存在或读取失败则返回空列表
     */
    fun loadRecords(): List<ExerciseRecord> {
        return try {
            val file = getStorageFile()
            if (!file.exists()) {
                return emptyList()
            }
            
            val json = file.readText()
            if (json.isBlank()) {
                return emptyList()
            }
            
            val type = object : TypeToken<List<ExerciseRecord>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: FileNotFoundException) {
            emptyList()
        } catch (e: Exception) {
            // 记录错误但返回空列表以保持应用稳定
            emptyList()
        }
    }
    
    /**
     * 确保数据恢复能力
     * 检查存储文件是否可访问和可写
     * 
     * @return true如果数据可以正常恢复，false否则
     */
    fun ensureDataRecovery(): Boolean {
        return try {
            val file = getStorageFile()
            val parentDir = file.parentFile
            
            // 确保父目录存在
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            
            // 检查文件是否可写
            if (file.exists()) {
                file.canRead() && file.canWrite()
            } else {
                // 尝试创建文件来测试写入权限
                file.createNewFile()
                val canWrite = file.canWrite()
                if (file.length() == 0L) {
                    file.delete() // 清理测试文件
                }
                canWrite
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取存储文件对象
     * 使用应用的内部存储目录确保数据安全性
     * 
     * @return 存储文件的File对象
     */
    private fun getStorageFile(): File {
        return File(context.filesDir, fileName)
    }
}
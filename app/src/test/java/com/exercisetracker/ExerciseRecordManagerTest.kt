package com.exercisetracker

import android.content.Context
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.*

/**
 * ExerciseRecordManager属性测试
 * Feature: exercise-tracker, Property 3: 运动记录创建完整性
 * **验证: 需求 2.5**
 */
class ExerciseRecordManagerTest : StringSpec({
    
    "Property 3: 运动记录创建完整性 - 对于任何有效的日期和运动选择组合，创建的运动记录应该包含正确的日期、运动状态和时长信息" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("exercise_manager_test")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val manager = ExerciseRecordManager(fileStorage)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        checkAll(100, validExerciseInputArb()) { (date, exercised, duration) ->
            // 创建记录
            val record = manager.createRecord(date, exercised, duration)
            
            // 验证记录完整性
            record.date shouldBe dateFormat.format(date)
            record.exercised shouldBe exercised
            
            if (exercised) {
                record.duration shouldBe duration
                record.duration shouldNotBe null
            } else {
                record.duration shouldBe null
            }
            
            // 验证记录可以被检索
            val retrievedRecord = manager.getRecord(date)
            retrievedRecord shouldBe record
        }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "创建记录后应该能够通过日期检索" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("exercise_manager_retrieval_test")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val manager = ExerciseRecordManager(fileStorage)
        
        checkAll(50, validExerciseInputArb()) { (date, exercised, duration) ->
            // 创建记录
            val createdRecord = manager.createRecord(date, exercised, duration)
            
            // 通过日期检索
            val retrievedRecord = manager.getRecord(date)
            
            // 验证检索到的记录与创建的记录相同
            retrievedRecord shouldBe createdRecord
        }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "同一日期的记录应该被替换而不是重复" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("exercise_manager_replacement_test")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val manager = ExerciseRecordManager(fileStorage)
        
        val testDate = Date()
        
        // 创建第一个记录
        val firstRecord = manager.createRecord(testDate, true, 20)
        
        // 创建同一日期的第二个记录
        val secondRecord = manager.createRecord(testDate, true, 30)
        
        // 验证只有最新的记录存在
        val retrievedRecord = manager.getRecord(testDate)
        retrievedRecord shouldBe secondRecord
        
        // 验证总记录数为1
        val allRecords = manager.getAllRecords()
        val recordsForDate = allRecords.filter { it.date == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(testDate) }
        recordsForDate.size shouldBe 1
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "未运动记录的时长应该为null" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("exercise_manager_no_exercise_test")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val manager = ExerciseRecordManager(fileStorage)
        
        val testDate = Date()
        
        // 创建未运动记录
        val record = manager.createRecord(testDate, false, null)
        
        // 验证记录属性
        record.exercised shouldBe false
        record.duration shouldBe null
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
})

/**
 * 生成有效的运动输入参数
 */
private fun validExerciseInputArb(): Arb<Triple<Date, Boolean, Int?>> = arbitrary { rs ->
    val date = Arb.long(
        System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000, // 一年前
        System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000  // 一年后
    ).map { Date(it) }.bind()
    
    val exercised = Arb.bool().bind()
    val duration = if (exercised) {
        Arb.int(1..120).bind() // 1到120分钟
    } else {
        null
    }
    
    Triple(date, exercised, duration)
}
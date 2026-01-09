package com.exercisetracker

import android.content.Context
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.*
import java.util.*

/**
 * MainActivity属性测试
 * Feature: exercise-tracker, Property 1: 日历显示信息准确性
 * Feature: exercise-tracker, Property 2: 日期点击交互一致性
 * Feature: exercise-tracker, Property 6: 记录不可变性保证
 * **验证: 需求 1.3, 2.1, 5.1, 5.2**
 */
class MainActivityTest : StringSpec({
    
    "Property 1: 日历显示信息准确性 - 对于任何已记录运动的日期，日历上显示的信息应该与存储的运动记录中的时长信息完全匹配" {
        checkAll(100, exerciseRecordArb()) { (date, exercised, duration) ->
            // 创建测试环境
            val mockContext = mockk<Context>()
            val testDir = createTempDir("calendar_display_test_${System.currentTimeMillis()}")
            
            every { mockContext.filesDir } returns testDir
            
            // 创建真实的组件实例
            val fileStorage = FileStorage(mockContext)
            val exerciseRecordManager = ExerciseRecordManager(fileStorage)
            
            // 创建MainActivity的spy对象
            val mainActivity = spyk<MainActivity>()
            
            // 使用反射设置私有字段
            val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
            exerciseRecordManagerField.isAccessible = true
            exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
            
            // 创建运动记录
            val record = exerciseRecordManager.createRecord(date, exercised, duration)
            
            // 验证记录的显示文本与预期匹配
            val expectedDisplayText = if (exercised && duration != null) {
                "已运动 ${duration}分钟"
            } else {
                ""
            }
            
            // 验证ExerciseRecord的getDisplayText方法返回正确的文本
            record.getDisplayText() shouldBe expectedDisplayText
            
            // 验证记录管理器能正确检索记录
            val retrievedRecord = exerciseRecordManager.getRecord(date)
            retrievedRecord shouldBe record
            
            // 如果有运动记录，验证显示文本的一致性
            if (exercised && duration != null) {
                retrievedRecord?.getDisplayText() shouldBe expectedDisplayText
                
                // 验证记录包含正确的信息
                retrievedRecord?.exercised shouldBe true
                retrievedRecord?.duration shouldBe duration
            } else {
                // 对于未运动的记录，显示文本应该为空
                retrievedRecord?.getDisplayText() shouldBe ""
                retrievedRecord?.exercised shouldBe false
                retrievedRecord?.duration shouldBe null
            }
            
            // 清理测试目录
            testDir.deleteRecursively()
        }
    }
    
    "Property 2: 日期点击交互一致性 - 对于任何日历上可点击的日期，点击后都应该弹出运动选择界面" {
        checkAll(100, validDateArb()) { date ->
            // 创建测试环境
            val mockContext = mockk<Context>()
            val testDir = createTempDir("main_activity_test_${System.currentTimeMillis()}")
            
            every { mockContext.filesDir } returns testDir
            
            // 创建MainActivity的spy对象以便验证方法调用
            val mainActivity = spyk<MainActivity>()
            
            // 模拟showExerciseDialog方法调用
            every { mainActivity.showExerciseDialog(any()) } just Runs
            
            // 创建真实的FileStorage和ExerciseRecordManager实例
            val fileStorage = FileStorage(mockContext)
            val exerciseRecordManager = ExerciseRecordManager(fileStorage)
            
            // 使用反射设置私有字段
            val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
            exerciseRecordManagerField.isAccessible = true
            exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
            
            // 确保开始时没有记录
            val existingRecord = exerciseRecordManager.getRecord(date)
            
            if (existingRecord == null) {
                // 测试场景: 没有现有记录的日期 - 应该显示对话框
                mainActivity.onCalendarDateClick(date)
                
                // 验证showExerciseDialog被调用
                verify(exactly = 1) { mainActivity.showExerciseDialog(date) }
            } else {
                // 测试场景: 已有记录的日期 - 不应该显示对话框（保持不可变性）
                mainActivity.onCalendarDateClick(date)
                
                // 验证showExerciseDialog没有被调用（记录不可变）
                verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
            }
            
            // 清理测试目录
            testDir.deleteRecursively()
        }
    }
    
    "Property 6: 记录不可变性保证 - 对于任何已创建的运动记录，系统不应提供修改或删除该记录的功能" {
        checkAll(100, exerciseRecordArb()) { (date, exercised, duration) ->
            // 创建测试环境
            val mockContext = mockk<Context>()
            val testDir = createTempDir("immutability_test_${System.currentTimeMillis()}")
            
            every { mockContext.filesDir } returns testDir
            
            // 创建真实的组件实例
            val fileStorage = FileStorage(mockContext)
            val exerciseRecordManager = ExerciseRecordManager(fileStorage)
            
            // 创建MainActivity的spy对象
            val mainActivity = spyk<MainActivity>()
            
            // 模拟showExistingRecordInfo方法（私有方法，通过反射验证行为）
            every { mainActivity.showExerciseDialog(any()) } just Runs
            
            // 使用反射设置私有字段
            val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
            exerciseRecordManagerField.isAccessible = true
            exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
            
            // 1. 创建运动记录
            val originalRecord = exerciseRecordManager.createRecord(date, exercised, duration)
            exerciseRecordManager.saveRecord(originalRecord)
            
            // 2. 验证记录已存在
            val existingRecord = exerciseRecordManager.getRecord(date)
            existingRecord shouldBe originalRecord
            
            // 3. 测试不可变性：点击已有记录的日期不应触发编辑对话框
            mainActivity.onCalendarDateClick(date)
            
            // 4. 验证showExerciseDialog没有被调用（保持不可变性）
            verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
            
            // 5. 验证记录内容保持不变
            val recordAfterClick = exerciseRecordManager.getRecord(date)
            recordAfterClick shouldBe originalRecord
            recordAfterClick?.date shouldBe originalRecord.date
            recordAfterClick?.exercised shouldBe originalRecord.exercised
            recordAfterClick?.duration shouldBe originalRecord.duration
            
            // 6. 验证记录的显示文本保持一致
            recordAfterClick?.getDisplayText() shouldBe originalRecord.getDisplayText()
            
            // 7. 验证系统中没有提供删除记录的方法
            // 通过检查ExerciseRecordManager类确保没有delete方法
            val deleteMethod = try {
                ExerciseRecordManager::class.java.getDeclaredMethod("deleteRecord", Date::class.java)
                true
            } catch (e: NoSuchMethodException) {
                false
            }
            deleteMethod shouldBe false // 不应该有删除方法
            
            // 8. 验证系统中没有提供修改记录的方法
            val updateMethod = try {
                ExerciseRecordManager::class.java.getDeclaredMethod("updateRecord", Date::class.java, Boolean::class.java, Int::class.java)
                true
            } catch (e: NoSuchMethodException) {
                false
            }
            updateMethod shouldBe false // 不应该有更新方法
            
            // 清理测试目录
            testDir.deleteRecursively()
        }
    }
    
    "日历点击应该正确处理已存在记录的日期" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("main_activity_existing_record_test")
        
        every { mockContext.filesDir } returns testDir
        
        val mainActivity = spyk<MainActivity>()
        every { mainActivity.showExerciseDialog(any()) } just Runs
        
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 使用反射设置私有字段
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        val testDate = Date()
        
        // 创建一个现有记录
        exerciseRecordManager.createRecord(testDate, true, 30)
        
        // 点击已有记录的日期
        mainActivity.onCalendarDateClick(testDate)
        
        // 验证showExerciseDialog没有被调用（保持记录不可变性）
        verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "日历点击应该正确处理没有记录的日期" {
        val mockContext = mockk<Context>()
        val testDir = createTempDir("main_activity_no_record_test")
        
        every { mockContext.filesDir } returns testDir
        
        val mainActivity = spyk<MainActivity>()
        every { mainActivity.showExerciseDialog(any()) } just Runs
        
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 使用反射设置私有字段
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        val testDate = Date()
        
        // 确保没有现有记录
        val existingRecord = exerciseRecordManager.getRecord(testDate)
        existingRecord shouldBe null
        
        // 点击没有记录的日期
        mainActivity.onCalendarDateClick(testDate)
        
        // 验证showExerciseDialog被调用
        verify(exactly = 1) { mainActivity.showExerciseDialog(testDate) }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
})

/**
 * 生成有效的日期参数
 */
private fun validDateArb(): Arb<Date> = arbitrary { rs ->
    val currentTime = System.currentTimeMillis()
    val oneYearAgo = currentTime - 365L * 24 * 60 * 60 * 1000
    val oneYearLater = currentTime + 365L * 24 * 60 * 60 * 1000
    
    Arb.long(oneYearAgo, oneYearLater).map { Date(it) }.bind()
}

/**
 * 生成运动记录的参数组合
 * @return Triple<Date, Boolean, Int?> 包含日期、是否运动、运动时长
 */
private fun exerciseRecordArb(): Arb<Triple<Date, Boolean, Int?>> = arbitrary { rs ->
    val date = validDateArb().bind()
    val exercised = Arb.bool().bind()
    val duration = if (exercised) {
        Arb.choice(Arb.int(20, 20), Arb.int(30, 30), Arb.int(40, 40)).bind()
    } else {
        null
    }
    
    Triple(date, exercised, duration)
}
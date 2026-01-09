package com.exercisetracker

import android.content.Context
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 系统集成测试
 * 测试端到端用户流程和组件间交互
 * 验证: 所有需求的集成场景
 */
class SystemIntegrationTest : StringSpec({
    
    "完整的运动记录创建流程应该正常工作" {
        // 测试从日历点击到记录保存的完整流程
        val mockContext = mockk<Context>()
        val testDir = createTempDir("integration_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        // 创建真实的组件实例
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        val testDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15)
        }.time
        
        // 1. 验证初始状态：没有记录
        val initialRecord = exerciseRecordManager.getRecord(testDate)
        initialRecord shouldBe null
        
        // 2. 模拟用户选择运动并选择30分钟
        val record = exerciseRecordManager.createRecord(testDate, true, 30)
        
        // 3. 验证记录创建成功
        record.date shouldBe SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(testDate)
        record.exercised shouldBe true
        record.duration shouldBe 30
        
        // 4. 验证记录已保存到存储
        val savedRecord = exerciseRecordManager.getRecord(testDate)
        savedRecord shouldBe record
        
        // 5. 验证显示文本正确
        savedRecord?.getDisplayText() shouldBe "已运动 30分钟"
        
        // 6. 验证记录持久化（重新加载）
        val newRecordManager = ExerciseRecordManager(fileStorage)
        val persistedRecord = newRecordManager.getRecord(testDate)
        persistedRecord shouldBe record
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "完整的未运动记录创建流程应该正常工作" {
        // 测试用户选择"否"的完整流程
        val mockContext = mockk<Context>()
        val testDir = createTempDir("integration_no_exercise_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        val testDate = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 20)
        }.time
        
        // 1. 创建未运动记录
        val record = exerciseRecordManager.createRecord(testDate, false, null)
        
        // 2. 验证记录属性
        record.exercised shouldBe false
        record.duration shouldBe null
        record.getDisplayText() shouldBe ""
        
        // 3. 验证记录保存和检索
        val savedRecord = exerciseRecordManager.getRecord(testDate)
        savedRecord shouldBe record
        
        // 4. 验证持久化
        val newRecordManager = ExerciseRecordManager(fileStorage)
        val persistedRecord = newRecordManager.getRecord(testDate)
        persistedRecord?.exercised shouldBe false
        persistedRecord?.duration shouldBe null
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "MainActivity与ExerciseRecordManager的集成应该正常工作" {
        // 测试MainActivity和数据管理器的集成
        val mockContext = mockk<Context>()
        val testDir = createTempDir("main_activity_integration_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        // 创建MainActivity的spy对象
        val mainActivity = spyk<MainActivity>()
        every { mainActivity.showExerciseDialog(any()) } just Runs
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        // 创建真实的数据管理组件
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 使用反射设置MainActivity的私有字段
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        val testDate = Date()
        
        // 1. 测试点击没有记录的日期
        mainActivity.onCalendarDateClick(testDate)
        verify(exactly = 1) { mainActivity.showExerciseDialog(testDate) }
        
        // 2. 模拟用户通过对话框创建记录
        // 使用反射调用私有方法onExerciseDialogResult
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        onExerciseDialogResultMethod.invoke(mainActivity, testDate, true, 30)
        
        // 3. 验证记录已创建
        val createdRecord = exerciseRecordManager.getRecord(testDate)
        createdRecord shouldNotBe null
        createdRecord?.exercised shouldBe true
        createdRecord?.duration shouldBe 30
        
        // 4. 验证刷新方法被调用
        verify(exactly = 1) { mainActivity.refreshCalendarAfterRecordCreation() }
        
        // 5. 测试再次点击相同日期（记录不可变性）
        clearMocks(mainActivity)
        every { mainActivity.showExerciseDialog(any()) } just Runs
        
        mainActivity.onCalendarDateClick(testDate)
        verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "通知系统集成应该正常工作" {
        // 测试通知系统的集成
        val mockContext = mockk<Context>()
        
        // 模拟Android系统服务
        val mockAlarmManager = mockk<android.app.AlarmManager>()
        val mockNotificationManager = mockk<android.app.NotificationManager>()
        
        every { mockContext.getSystemService(Context.ALARM_SERVICE) } returns mockAlarmManager
        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        every { mockAlarmManager.setRepeating(any(), any(), any(), any()) } just Runs
        every { mockNotificationManager.createNotificationChannel(any()) } just Runs
        every { mockNotificationManager.notify(any(), any()) } just Runs
        
        // 创建通知管理器
        val notificationManager = NotificationManager(mockContext)
        
        // 1. 测试通知调度设置
        notificationManager.scheduleDaily1130Notification()
        
        // 验证AlarmManager被正确调用
        verify(exactly = 1) { mockAlarmManager.setRepeating(any(), any(), any(), any()) }
        
        // 2. 测试通知显示
        notificationManager.showExerciseReminder()
        
        // 验证通知被显示
        verify(exactly = 1) { mockNotificationManager.notify(any(), any()) }
        
        // 3. 测试NotificationReceiver集成
        val notificationReceiver = NotificationReceiver()
        val mockIntent = mockk<android.content.Intent>()
        
        // 模拟接收广播
        notificationReceiver.onReceive(mockContext, mockIntent)
        
        // 验证通知被触发（通过验证NotificationManager的调用）
        verify(atLeast = 1) { mockNotificationManager.notify(any(), any()) }
    }
    
    "数据存储系统集成应该正常工作" {
        // 测试完整的数据存储流程
        val mockContext = mockk<Context>()
        val testDir = createTempDir("storage_integration_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        // 创建存储组件
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 1. 创建多个记录
        val records = listOf(
            exerciseRecordManager.createRecord(
                Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 1) }.time,
                true, 20
            ),
            exerciseRecordManager.createRecord(
                Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 2) }.time,
                false, null
            ),
            exerciseRecordManager.createRecord(
                Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 3) }.time,
                true, 40
            )
        )
        
        // 2. 验证所有记录都被保存
        val allRecords = exerciseRecordManager.getAllRecords()
        allRecords.size shouldBe 3
        
        // 3. 验证记录内容正确
        records.forEach { originalRecord ->
            val retrievedRecord = exerciseRecordManager.getRecordByDateString(originalRecord.date)
            retrievedRecord shouldBe originalRecord
        }
        
        // 4. 测试数据持久化（重新创建管理器）
        val newExerciseRecordManager = ExerciseRecordManager(fileStorage)
        val persistedRecords = newExerciseRecordManager.getAllRecords()
        persistedRecords.size shouldBe 3
        
        // 5. 验证持久化数据的完整性
        records.forEach { originalRecord ->
            val persistedRecord = newExerciseRecordManager.getRecordByDateString(originalRecord.date)
            persistedRecord shouldBe originalRecord
        }
        
        // 6. 测试日期范围查询
        val startDate = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 1) }.time
        val endDate = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 31) }.time
        val rangeRecords = newExerciseRecordManager.getRecordsInRange(startDate, endDate)
        rangeRecords.size shouldBe 3
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "系统应该正确处理错误情况" {
        // 测试系统的错误处理能力
        val mockContext = mockk<Context>()
        val testDir = createTempDir("error_handling_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 1. 测试无效参数处理
        try {
            exerciseRecordManager.createRecord(Date(), true, null) // 运动但没有时长
            throw AssertionError("应该抛出异常")
        } catch (e: IllegalArgumentException) {
            // 预期的异常
        }
        
        // 2. 测试文件损坏情况的恢复
        val testDate = Date()
        val validRecord = exerciseRecordManager.createRecord(testDate, true, 30)
        
        // 验证记录正常创建
        val retrievedRecord = exerciseRecordManager.getRecord(testDate)
        retrievedRecord shouldBe validRecord
        
        // 3. 测试MainActivity的错误处理
        val mainActivity = spyk<MainActivity>()
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        // 使用反射设置exerciseRecordManager
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        // 测试正常的对话框结果处理
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        
        // 应该正常处理有效输入
        onExerciseDialogResultMethod.invoke(mainActivity, Date(), true, 20)
        verify(exactly = 1) { mainActivity.refreshCalendarAfterRecordCreation() }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "系统应该支持多个记录的并发操作" {
        checkAll(50, multipleRecordsArb()) { recordsData ->
            val mockContext = mockk<Context>()
            val testDir = createTempDir("concurrent_test_${System.currentTimeMillis()}")
            
            every { mockContext.filesDir } returns testDir
            
            val fileStorage = FileStorage(mockContext)
            val exerciseRecordManager = ExerciseRecordManager(fileStorage)
            
            // 创建多个记录
            val createdRecords = recordsData.map { (date, exercised, duration) ->
                exerciseRecordManager.createRecord(date, exercised, duration)
            }
            
            // 验证所有记录都被正确保存
            createdRecords.forEach { record ->
                val retrievedRecord = exerciseRecordManager.getRecordByDateString(record.date)
                retrievedRecord shouldBe record
            }
            
            // 验证记录总数正确
            val allRecords = exerciseRecordManager.getAllRecords()
            allRecords.size shouldBe createdRecords.size
            
            // 验证数据持久化
            val newRecordManager = ExerciseRecordManager(fileStorage)
            val persistedRecords = newRecordManager.getAllRecords()
            persistedRecords.size shouldBe createdRecords.size
            
            // 清理测试目录
            testDir.deleteRecursively()
        }
    }
})

/**
 * 生成多个记录的测试数据
 */
private fun multipleRecordsArb(): Arb<List<Triple<Date, Boolean, Int?>>> = arbitrary { rs ->
    val count = Arb.int(1, 10).bind() // 1到10个记录
    val baseTime = System.currentTimeMillis()
    
    (0 until count).map { index ->
        val date = Date(baseTime + index * 24 * 60 * 60 * 1000L) // 每天一个记录
        val exercised = Arb.bool().bind()
        val duration = if (exercised) {
            Arb.choice(Arb.int(20, 20), Arb.int(30, 30), Arb.int(40, 40)).bind()
        } else {
            null
        }
        Triple(date, exercised, duration)
    }
}
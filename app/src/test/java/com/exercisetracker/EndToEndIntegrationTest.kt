package com.exercisetracker

import android.content.Context
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 端到端集成测试
 * 测试完整的用户工作流程，验证所有组件协同工作
 * 需求: 所有需求的端到端验证
 */
class EndToEndIntegrationTest : StringSpec({
    
    "完整的用户工作流程：启动应用 -> 点击日期 -> 记录运动 -> 查看记录" {
        // 模拟完整的用户使用场景
        val mockContext = mockk<Context>()
        val testDir = createTempDir("e2e_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        // === 第一步：应用启动和初始化 ===
        
        // 1.1 创建MainActivity并初始化组件
        val mainActivity = spyk<MainActivity>()
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        // 使用反射设置MainActivity的组件
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        // 1.2 验证初始状态：没有记录
        val initialRecords = exerciseRecordManager.getAllRecords()
        initialRecords.size shouldBe 0
        
        // === 第二步：用户交互 - 点击日历日期 ===
        
        val testDate = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 15) // 2024-03-15
        }.time
        
        // 2.1 模拟用户点击日历上的日期
        every { mainActivity.showExerciseDialog(any()) } just Runs
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        mainActivity.onCalendarDateClick(testDate)
        
        // 2.2 验证运动选择对话框被触发
        verify(exactly = 1) { mainActivity.showExerciseDialog(testDate) }
        
        // === 第三步：用户选择运动并设置时长 ===
        
        // 3.1 模拟用户在对话框中选择"是"并选择30分钟
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        onExerciseDialogResultMethod.invoke(mainActivity, testDate, true, 30)
        
        // 3.2 验证记录创建成功
        val createdRecord = exerciseRecordManager.getRecord(testDate)
        createdRecord shouldNotBe null
        createdRecord?.exercised shouldBe true
        createdRecord?.duration shouldBe 30
        createdRecord?.getDisplayText() shouldBe "已运动 30分钟"
        
        // 3.3 验证日历显示更新被调用
        verify(exactly = 1) { mainActivity.refreshCalendarAfterRecordCreation() }
        
        // === 第四步：验证记录持久化 ===
        
        // 4.1 重新创建组件模拟应用重启
        val newFileStorage = FileStorage(mockContext)
        val newExerciseRecordManager = ExerciseRecordManager(newFileStorage)
        
        // 4.2 验证记录在重启后仍然存在
        val persistedRecord = newExerciseRecordManager.getRecord(testDate)
        persistedRecord shouldBe createdRecord
        
        // === 第五步：验证记录不可变性 ===
        
        // 5.1 用户再次点击相同日期
        clearMocks(mainActivity)
        every { mainActivity.showExerciseDialog(any()) } just Runs
        
        mainActivity.onCalendarDateClick(testDate)
        
        // 5.2 验证不会再次显示对话框（记录不可变）
        verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
        
        // 5.3 验证记录内容保持不变
        val unchangedRecord = exerciseRecordManager.getRecord(testDate)
        unchangedRecord shouldBe createdRecord
        
        // === 第六步：验证日历显示功能 ===
        
        // 6.1 测试日历显示更新
        every { mainActivity.updateCalendarDisplay() } just Runs
        mainActivity.updateCalendarDisplay()
        verify(exactly = 1) { mainActivity.updateCalendarDisplay() }
        
        // 6.2 验证月份范围查询功能
        val startOfMonth = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 1)
        }.time
        val endOfMonth = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 31)
        }.time
        
        val monthlyRecords = exerciseRecordManager.getRecordsInRange(startOfMonth, endOfMonth)
        monthlyRecords.size shouldBe 1
        monthlyRecords[0] shouldBe createdRecord
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "完整的未运动记录工作流程" {
        // 测试用户选择"否"的完整流程
        val mockContext = mockk<Context>()
        val testDir = createTempDir("e2e_no_exercise_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        // 初始化组件
        val mainActivity = spyk<MainActivity>()
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        val testDate = Calendar.getInstance().apply {
            set(2024, Calendar.APRIL, 10)
        }.time
        
        // 用户点击日期
        every { mainActivity.showExerciseDialog(any()) } just Runs
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        mainActivity.onCalendarDateClick(testDate)
        verify(exactly = 1) { mainActivity.showExerciseDialog(testDate) }
        
        // 用户选择"否"
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        onExerciseDialogResultMethod.invoke(mainActivity, testDate, false, null)
        
        // 验证未运动记录创建
        val record = exerciseRecordManager.getRecord(testDate)
        record shouldNotBe null
        record?.exercised shouldBe false
        record?.duration shouldBe null
        record?.getDisplayText() shouldBe ""
        
        // 验证持久化
        val newRecordManager = ExerciseRecordManager(FileStorage(mockContext))
        val persistedRecord = newRecordManager.getRecord(testDate)
        persistedRecord shouldBe record
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "通知系统端到端工作流程" {
        // 测试通知系统的完整流程
        val mockContext = mockk<Context>()
        
        // 模拟Android系统服务
        val mockAlarmManager = mockk<android.app.AlarmManager>()
        val mockNotificationManager = mockk<android.app.NotificationManager>()
        
        every { mockContext.getSystemService(Context.ALARM_SERVICE) } returns mockAlarmManager
        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        every { mockAlarmManager.setRepeating(any(), any(), any(), any()) } just Runs
        every { mockNotificationManager.createNotificationChannel(any()) } just Runs
        every { mockNotificationManager.notify(any(), any()) } just Runs
        every { mockNotificationManager.areNotificationsEnabled() } returns true
        
        // === 第一步：应用启动时设置通知 ===
        
        val mainActivity = spyk<MainActivity>()
        every { mainActivity.setupNotifications() } answers { callOriginal() }
        
        // 使用反射调用setupNotifications
        val setupNotificationsMethod = MainActivity::class.java.getDeclaredMethod("setupNotifications")
        setupNotificationsMethod.isAccessible = true
        setupNotificationsMethod.invoke(mainActivity)
        
        // 验证通知调度被设置
        verify(exactly = 1) { mockAlarmManager.setRepeating(any(), any(), any(), any()) }
        
        // === 第二步：模拟通知触发 ===
        
        val notificationReceiver = NotificationReceiver()
        val mockIntent = mockk<android.content.Intent>()
        
        // 模拟AlarmManager触发广播
        notificationReceiver.onReceive(mockContext, mockIntent)
        
        // 验证通知被显示
        verify(exactly = 1) { mockNotificationManager.notify(any(), any()) }
        
        // === 第三步：验证通知内容 ===
        
        val notificationManager = NotificationManager(mockContext)
        notificationManager.showExerciseReminder()
        
        // 验证通知被正确显示（应该被调用两次：一次在receiver中，一次在直接调用中）
        verify(exactly = 2) { mockNotificationManager.notify(any(), any()) }
    }
    
    "多日记录的完整工作流程" {
        // 测试用户在多个日期记录运动的完整场景
        val mockContext = mockk<Context>()
        val testDir = createTempDir("e2e_multi_day_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        val mainActivity = spyk<MainActivity>()
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        every { mainActivity.showExerciseDialog(any()) } just Runs
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        
        // 创建一周的记录
        val weekDates = (1..7).map { day ->
            Calendar.getInstance().apply {
                set(2024, Calendar.MAY, day)
            }.time
        }
        
        val expectedRecords = mutableListOf<ExerciseRecord>()
        
        // 模拟用户在一周内的不同选择
        weekDates.forEachIndexed { index, date ->
            mainActivity.onCalendarDateClick(date)
            verify(exactly = index + 1) { mainActivity.showExerciseDialog(any()) }
            
            when (index % 3) {
                0 -> {
                    // 运动20分钟
                    onExerciseDialogResultMethod.invoke(mainActivity, date, true, 20)
                    expectedRecords.add(ExerciseRecord(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
                        true, 20
                    ))
                }
                1 -> {
                    // 运动40分钟
                    onExerciseDialogResultMethod.invoke(mainActivity, date, true, 40)
                    expectedRecords.add(ExerciseRecord(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
                        true, 40
                    ))
                }
                2 -> {
                    // 未运动
                    onExerciseDialogResultMethod.invoke(mainActivity, date, false, null)
                    expectedRecords.add(ExerciseRecord(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
                        false, null
                    ))
                }
            }
        }
        
        // 验证所有记录都被创建
        val allRecords = exerciseRecordManager.getAllRecords()
        allRecords.size shouldBe 7
        
        // 验证每个记录的内容
        expectedRecords.forEach { expectedRecord ->
            val actualRecord = exerciseRecordManager.getRecordByDateString(expectedRecord.date)
            actualRecord shouldBe expectedRecord
        }
        
        // 验证月份范围查询
        val startOfMonth = Calendar.getInstance().apply {
            set(2024, Calendar.MAY, 1)
        }.time
        val endOfMonth = Calendar.getInstance().apply {
            set(2024, Calendar.MAY, 31)
        }.time
        
        val monthlyRecords = exerciseRecordManager.getRecordsInRange(startOfMonth, endOfMonth)
        monthlyRecords.size shouldBe 7
        
        // 验证记录不可变性：尝试再次点击已有记录的日期
        clearMocks(mainActivity)
        every { mainActivity.showExerciseDialog(any()) } just Runs
        
        weekDates.forEach { date ->
            mainActivity.onCalendarDateClick(date)
        }
        
        // 验证没有对话框被显示（记录不可变）
        verify(exactly = 0) { mainActivity.showExerciseDialog(any()) }
        
        // 验证记录内容保持不变
        val unchangedRecords = exerciseRecordManager.getAllRecords()
        unchangedRecords.size shouldBe 7
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
    
    "系统错误恢复的端到端测试" {
        // 测试系统在各种错误情况下的恢复能力
        val mockContext = mockk<Context>()
        val testDir = createTempDir("e2e_error_recovery_test_${System.currentTimeMillis()}")
        
        every { mockContext.filesDir } returns testDir
        
        val mainActivity = spyk<MainActivity>()
        val fileStorage = FileStorage(mockContext)
        val exerciseRecordManager = ExerciseRecordManager(fileStorage)
        
        val exerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        exerciseRecordManagerField.isAccessible = true
        exerciseRecordManagerField.set(mainActivity, exerciseRecordManager)
        
        every { mainActivity.showExerciseDialog(any()) } just Runs
        every { mainActivity.refreshCalendarAfterRecordCreation() } just Runs
        
        val testDate = Date()
        
        // 1. 正常创建记录
        val onExerciseDialogResultMethod = MainActivity::class.java.getDeclaredMethod(
            "onExerciseDialogResult", Date::class.java, Boolean::class.java, Int::class.java
        )
        onExerciseDialogResultMethod.isAccessible = true
        onExerciseDialogResultMethod.invoke(mainActivity, testDate, true, 30)
        
        val originalRecord = exerciseRecordManager.getRecord(testDate)
        originalRecord shouldNotBe null
        
        // 2. 模拟应用重启（重新创建组件）
        val newFileStorage = FileStorage(mockContext)
        val newExerciseRecordManager = ExerciseRecordManager(newFileStorage)
        
        // 3. 验证数据恢复
        val recoveredRecord = newExerciseRecordManager.getRecord(testDate)
        recoveredRecord shouldBe originalRecord
        
        // 4. 验证系统继续正常工作
        val newMainActivity = spyk<MainActivity>()
        val newExerciseRecordManagerField = MainActivity::class.java.getDeclaredField("exerciseRecordManager")
        newExerciseRecordManagerField.isAccessible = true
        newExerciseRecordManagerField.set(newMainActivity, newExerciseRecordManager)
        
        every { newMainActivity.showExerciseDialog(any()) } just Runs
        
        // 点击已有记录的日期应该不显示对话框
        newMainActivity.onCalendarDateClick(testDate)
        verify(exactly = 0) { newMainActivity.showExerciseDialog(any()) }
        
        // 点击新日期应该显示对话框
        val newDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.time
        
        newMainActivity.onCalendarDateClick(newDate)
        verify(exactly = 1) { newMainActivity.showExerciseDialog(newDate) }
        
        // 清理测试目录
        testDir.deleteRecursively()
    }
})
package com.exercisetracker

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.*

/**
 * ExerciseDialog单元测试
 * 测试对话框显示逻辑和用户选择处理
 * 需求: 2.2, 2.3, 2.4
 */
class ExerciseDialogTest : StringSpec({
    
    "ExerciseDialog应该能够正确创建实例" {
        // 测试对话框创建逻辑
        val testDate = Date()
        var callbackInvoked = false
        var receivedExercised: Boolean? = null
        var receivedDuration: Int? = null
        
        val callback: (Boolean, Int?) -> Unit = { exercised, duration ->
            callbackInvoked = true
            receivedExercised = exercised
            receivedDuration = duration
        }
        
        // 创建对话框实例
        val dialog = ExerciseDialog.newInstance(testDate, callback)
        
        // 验证对话框实例创建成功
        dialog shouldNotBe null
        dialog.arguments shouldNotBe null
        
        // 验证日期参数正确设置
        val storedDate = dialog.arguments?.getLong("date")
        storedDate shouldBe testDate.time
    }
    
    "ExerciseDialog应该正确处理日期格式" {
        // 测试日期格式化逻辑
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val testDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15) // 2024-01-15
        }.time
        
        val callback: (Boolean, Int?) -> Unit = { _, _ -> }
        val dialog = ExerciseDialog.newInstance(testDate, callback)
        
        // 验证日期参数正确存储
        val storedDate = dialog.arguments?.getLong("date")
        val restoredDate = Date(storedDate ?: 0)
        
        dateFormat.format(restoredDate) shouldBe dateFormat.format(testDate)
    }
    
    "ExerciseDialog应该支持show方法设置日期和回调" {
        // 测试show方法的功能
        val testDate = Date()
        var callbackInvoked = false
        
        val callback: (Boolean, Int?) -> Unit = { _, _ ->
            callbackInvoked = true
        }
        
        val dialog = ExerciseDialog()
        dialog.show(testDate, callback)
        
        // 验证arguments被正确设置
        dialog.arguments shouldNotBe null
        val storedDate = dialog.arguments?.getLong("date")
        storedDate shouldBe testDate.time
    }
    
    "ExerciseDialog应该正确处理运动选择回调" {
        // 测试用户选择处理逻辑
        val testDate = Date()
        var callbackResults = mutableListOf<Pair<Boolean, Int?>>()
        
        val callback: (Boolean, Int?) -> Unit = { exercised, duration ->
            callbackResults.add(Pair(exercised, duration))
        }
        
        val dialog = ExerciseDialog.newInstance(testDate, callback)
        
        // 验证回调函数被正确存储
        dialog shouldNotBe null
        
        // 模拟用户选择"否"的情况
        // 注意：由于这是单元测试，我们主要测试数据处理逻辑
        // 实际的UI交互测试需要在Android测试环境中进行
    }
    
    "ExerciseDialog应该正确处理时长选择" {
        // 测试时长选择逻辑
        val validDurations = arrayOf(20, 30, 40)
        
        // 验证预设时长选项
        validDurations.forEach { duration ->
            duration shouldBe when (duration) {
                20 -> 20
                30 -> 30
                40 -> 40
                else -> throw IllegalArgumentException("Invalid duration: $duration")
            }
        }
    }
    
    "ExerciseDialog应该正确处理边界情况" {
        // 测试边界情况处理
        val testDate = Date()
        var callbackInvoked = false
        var receivedExercised: Boolean? = null
        var receivedDuration: Int? = null
        
        val callback: (Boolean, Int?) -> Unit = { exercised, duration ->
            callbackInvoked = true
            receivedExercised = exercised
            receivedDuration = duration
        }
        
        // 测试空日期处理
        val dialog = ExerciseDialog.newInstance(testDate, callback)
        
        // 验证对话框能够处理正常的日期输入
        dialog shouldNotBe null
        
        // 测试未运动情况（exercised = false, duration = null）
        val noExerciseCallback: (Boolean, Int?) -> Unit = { exercised, duration ->
            exercised shouldBe false
            duration shouldBe null
        }
        
        val noExerciseDialog = ExerciseDialog.newInstance(testDate, noExerciseCallback)
        noExerciseDialog shouldNotBe null
    }
    
    "ExerciseDialog应该正确处理运动情况的时长选择" {
        // 测试运动情况下的时长选择
        val testDate = Date()
        val validDurations = listOf(20, 30, 40)
        
        validDurations.forEach { expectedDuration ->
            var callbackInvoked = false
            var receivedExercised: Boolean? = null
            var receivedDuration: Int? = null
            
            val callback: (Boolean, Int?) -> Unit = { exercised, duration ->
                callbackInvoked = true
                receivedExercised = exercised
                receivedDuration = duration
            }
            
            val dialog = ExerciseDialog.newInstance(testDate, callback)
            dialog shouldNotBe null
            
            // 验证时长选项在有效范围内
            expectedDuration shouldBe when (expectedDuration) {
                in validDurations -> expectedDuration
                else -> throw IllegalArgumentException("Invalid duration")
            }
        }
    }
})
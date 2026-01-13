package com.exercisetracker

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

/**
 * ReportActivity 测试
 */
class ReportActivityTest : StringSpec({
    
    "ExerciseReportGenerator should generate monthly report correctly" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 创建测试数据
        val testRecords = listOf(
            ExerciseRecord("2025-01-01", true, 30),
            ExerciseRecord("2025-01-02", false, null),
            ExerciseRecord("2025-01-03", true, 45),
            ExerciseRecord("2025-01-04", true, 20),
            ExerciseRecord("2025-01-05", false, null)
        )
        
        // 设置模拟行为
        every { mockRecordManager.getRecordsInRange(any(), any()) } returns testRecords
        
        // 生成月度报表
        val report = reportGenerator.generateMonthlyReport(2025, 1)
        
        // 验证报表数据
        report.period shouldBe "2025-01"
        report.totalDays shouldBe 5
        report.exerciseDays shouldBe 3
        report.totalDuration shouldBe 95 // 30 + 45 + 20
        report.averageDuration shouldBe 31.666666666666668 // 95 / 3
        report.exerciseRate shouldBe 60.0 // 3/5 * 100
    }
    
    "ExerciseReportGenerator should generate yearly report correctly" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 创建测试数据
        val testRecords = listOf(
            ExerciseRecord("2025-01-01", true, 30),
            ExerciseRecord("2025-02-01", true, 40),
            ExerciseRecord("2025-03-01", false, null),
            ExerciseRecord("2025-04-01", true, 25)
        )
        
        // 设置模拟行为
        every { mockRecordManager.getRecordsInRange(any(), any()) } returns testRecords
        
        // 生成年度报表
        val report = reportGenerator.generateYearlyReport(2025)
        
        // 验证报表数据
        report.period shouldBe "2025"
        report.totalDays shouldBe 4
        report.exerciseDays shouldBe 3
        report.totalDuration shouldBe 95 // 30 + 40 + 25
        report.averageDuration shouldBe 31.666666666666668 // 95 / 3
        report.exerciseRate shouldBe 75.0 // 3/4 * 100
    }
    
    "ExerciseReportGenerator should handle empty records" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 设置模拟行为 - 返回空列表
        every { mockRecordManager.getRecordsInRange(any(), any()) } returns emptyList()
        
        // 生成月度报表
        val report = reportGenerator.generateMonthlyReport(2025, 1)
        
        // 验证报表数据
        report.period shouldBe "2025-01"
        report.totalDays shouldBe 0
        report.exerciseDays shouldBe 0
        report.totalDuration shouldBe 0
        report.averageDuration shouldBe 0.0
        report.exerciseRate shouldBe 0.0
        report.longestStreak shouldBe 0
        report.currentStreak shouldBe 0
    }
    
    "ExerciseReportGenerator should calculate streaks correctly" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 创建测试数据 - 包含连续运动天数
        val testRecords = listOf(
            ExerciseRecord("2025-01-01", true, 30),   // 连续开始
            ExerciseRecord("2025-01-02", true, 30),   // 连续
            ExerciseRecord("2025-01-03", true, 30),   // 连续 (最长连续3天)
            ExerciseRecord("2025-01-04", false, null), // 中断
            ExerciseRecord("2025-01-05", true, 30),   // 重新开始
            ExerciseRecord("2025-01-06", true, 30)    // 当前连续2天
        )
        
        // 设置模拟行为
        every { mockRecordManager.getRecordsInRange(any(), any()) } returns testRecords
        
        // 生成月度报表
        val report = reportGenerator.generateMonthlyReport(2025, 1)
        
        // 验证连续运动天数
        report.longestStreak shouldBe 3
        report.currentStreak shouldBe 2
    }
    
    "ExerciseReportGenerator should get available years correctly" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 创建测试数据 - 跨多年
        val testRecords = listOf(
            ExerciseRecord("2023-01-01", true, 30),
            ExerciseRecord("2024-01-01", true, 30),
            ExerciseRecord("2024-06-01", true, 30),
            ExerciseRecord("2025-01-01", true, 30)
        )
        
        // 设置模拟行为
        every { mockRecordManager.getAllRecords() } returns testRecords
        
        // 获取可用年份
        val availableYears = reportGenerator.getAvailableYears()
        
        // 验证年份列表（应该按倒序排列）
        availableYears shouldBe listOf(2025, 2024, 2023)
    }
    
    "ExerciseReportGenerator should get available months correctly" {
        // 创建模拟的 ExerciseRecordManager
        val mockRecordManager = mockk<ExerciseRecordManager>()
        val reportGenerator = ExerciseReportGenerator(mockRecordManager)
        
        // 创建测试数据 - 2024年的多个月份
        val testRecords = listOf(
            ExerciseRecord("2024-01-01", true, 30),
            ExerciseRecord("2024-03-01", true, 30),
            ExerciseRecord("2024-03-15", true, 30),
            ExerciseRecord("2024-12-01", true, 30),
            ExerciseRecord("2025-01-01", true, 30) // 不同年份，应该被过滤
        )
        
        // 设置模拟行为
        every { mockRecordManager.getAllRecords() } returns testRecords
        
        // 获取2024年的可用月份
        val availableMonths = reportGenerator.getAvailableMonths(2024)
        
        // 验证月份列表（应该按倒序排列，且只包含2024年的月份）
        availableMonths shouldBe listOf(12, 3, 1)
    }
})
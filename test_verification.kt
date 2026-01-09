// Simple verification script to check basic functionality
// This is not a replacement for proper testing but can help verify basic logic

import java.text.SimpleDateFormat
import java.util.*

// Mock classes for basic verification
class MockContext {
    val filesDir = createTempDir("test_verification")
}

fun main() {
    println("开始验证数据层功能...")
    
    try {
        // Test 1: ExerciseRecord creation
        println("测试 1: ExerciseRecord 创建")
        val record1 = ExerciseRecord("2024-01-15", true, 30)
        val record2 = ExerciseRecord("2024-01-16", false, null)
        
        assert(record1.getDisplayText() == "已运动 30分钟") { "运动记录显示文本错误" }
        assert(record2.getDisplayText() == "") { "未运动记录显示文本应为空" }
        println("✓ ExerciseRecord 创建测试通过")
        
        // Test 2: FileStorage basic functionality
        println("测试 2: FileStorage 基本功能")
        val mockContext = MockContext()
        val fileStorage = FileStorage(mockContext)
        
        val testRecords = listOf(record1, record2)
        fileStorage.saveRecords(testRecords)
        val loadedRecords = fileStorage.loadRecords()
        
        assert(loadedRecords.size == testRecords.size) { "加载的记录数量不匹配" }
        assert(loadedRecords[0].date == record1.date) { "第一条记录日期不匹配" }
        assert(loadedRecords[1].exercised == record2.exercised) { "第二条记录运动状态不匹配" }
        println("✓ FileStorage 基本功能测试通过")
        
        // Test 3: ExerciseRecordManager basic functionality
        println("测试 3: ExerciseRecordManager 基本功能")
        val manager = ExerciseRecordManager(fileStorage)
        val testDate = Date()
        
        val createdRecord = manager.createRecord(testDate, true, 25)
        val retrievedRecord = manager.getRecord(testDate)
        
        assert(retrievedRecord != null) { "无法检索创建的记录" }
        assert(retrievedRecord!!.exercised == true) { "检索的记录运动状态不正确" }
        assert(retrievedRecord.duration == 25) { "检索的记录时长不正确" }
        println("✓ ExerciseRecordManager 基本功能测试通过")
        
        // Cleanup
        mockContext.filesDir.deleteRecursively()
        
        println("✅ 所有基本功能验证通过！")
        
    } catch (e: Exception) {
        println("❌ 验证失败: ${e.message}")
        e.printStackTrace()
    }
}
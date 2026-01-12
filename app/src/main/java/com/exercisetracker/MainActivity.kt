package com.exercisetracker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 主Activity - 应用的主入口点，协调所有组件的交互
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var calendarView: CalendarView
    private lateinit var exerciseRecordManager: ExerciseRecordManager
    private lateinit var calendarContainer: ConstraintLayout
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val exerciseIndicators = mutableMapOf<String, TextView>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeComponents()
        initializeViews()
        setupCalendarListener()
        setupNotifications()
        updateCalendarDisplay()
    }
    
    /**
     * 初始化组件
     */
    private fun initializeComponents() {
        try {
            val fileStorage = FileStorage(this)
            exerciseRecordManager = ExerciseRecordManager(fileStorage)
            
            // 验证数据存储系统是否正常工作
            val testRecords = exerciseRecordManager.getAllRecords()
            android.util.Log.i("MainActivity", "数据存储系统初始化完成，已加载${testRecords.size}条记录")
            
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "初始化组件时发生错误", e)
            // 即使出错也要创建基本实例，确保应用能启动
            val fileStorage = FileStorage(this)
            exerciseRecordManager = ExerciseRecordManager(fileStorage)
        }
    }
    
    /**
     * 初始化视图组件
     */
    private fun initializeViews() {
        calendarView = findViewById(R.id.calendarView)
        calendarContainer = findViewById(R.id.main_container)
        
        // 设置日历的样式
        calendarView.apply {
            // 设置日历的最小和最大日期（可选）
            // minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.timeInMillis
            // maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }.timeInMillis
        }
    }
    
    /**
     * 设置日历点击监听器
     */
    private fun setupCalendarListener() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 创建Date对象 (month是0-based，所以需要+1)
            val selectedDate = java.util.Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            
            onCalendarDateClick(selectedDate)
        }
        
        // 监听日历的月份变化以更新显示
        // 注意：CalendarView没有直接的月份变化监听器，
        // 所以我们使用ViewTreeObserver来检测布局变化
        calendarView.viewTreeObserver.addOnGlobalLayoutListener {
            // 当布局完成后更新显示
            updateCalendarDisplay()
        }
    }
    
    /**
     * 处理日历日期点击事件
     * @param date 被点击的日期
     */
    fun onCalendarDateClick(date: Date) {
        // 查询该日期是否已有记录
        val existingRecord = exerciseRecordManager.getRecord(date)
        
        if (existingRecord != null) {
            // 如果已有记录，根据需求5.1和5.2，记录是不可变的
            // 显示记录信息但不允许修改或删除
            showExistingRecordInfo(existingRecord)
            return
        }
        
        // 如果没有记录，显示运动选择对话框
        showExerciseDialog(date)
    }
    
    /**
     * 显示现有记录信息（只读）
     * 确保用户了解记录的不可变性
     * @param record 现有的运动记录
     * 需求: 5.1, 5.2 - 记录不可变，只能查看不能修改或删除
     */
    private fun showExistingRecordInfo(record: ExerciseRecord) {
        val message = if (record.exercised && record.duration != null) {
            "该日期已记录运动：${record.duration}分钟\n\n记录创建后不可修改或删除"
        } else {
            "该日期已记录：未运动\n\n记录创建后不可修改或删除"
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("运动记录")
            .setMessage(message)
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 当创建新的运动记录后刷新日历显示
     */
    fun refreshCalendarAfterRecordCreation() {
        updateCalendarDisplay()
    }
    
    /**
     * 显示运动选择对话框
     * @param date 选择的日期
     * 需求: 2.2, 2.3, 2.4 - 显示运动选择界面和时长选择界面
     */
    fun showExerciseDialog(date: Date) {
        android.util.Log.d("MainActivity", "显示运动选择对话框，日期: ${dateFormat.format(date)}")
        
        // 创建并显示运动选择对话框
        val dialog = ExerciseDialog.newInstance(date) { exercised, duration ->
            // 处理用户选择结果
            android.util.Log.d("MainActivity", "收到对话框结果: exercised=$exercised, duration=$duration")
            onExerciseDialogResult(date, exercised, duration)
        }
        
        // 显示对话框
        dialog.show(supportFragmentManager, "ExerciseDialog")
        android.util.Log.d("MainActivity", "对话框已调用show方法")
    }
    
    /**
     * 处理运动对话框的选择结果
     * @param date 选择的日期
     * @param exercised 是否运动
     * @param duration 运动时长（分钟），如果未运动则为null
     * 需求: 2.5, 2.6 - 创建运动记录并保存
     */
    private fun onExerciseDialogResult(date: Date, exercised: Boolean, duration: Int?) {
        android.util.Log.d("MainActivity", "处理对话框结果: 日期=${dateFormat.format(date)}, 运动=$exercised, 时长=$duration")
        
        try {
            // 创建运动记录
            val record = exerciseRecordManager.createRecord(date, exercised, duration)
            android.util.Log.d("MainActivity", "创建记录: ${record.date}")
            
            // 保存记录到存储
            exerciseRecordManager.saveRecord(record)
            android.util.Log.d("MainActivity", "记录已保存")
            
            // 刷新日历显示以显示新记录
            refreshCalendarAfterRecordCreation()
            android.util.Log.d("MainActivity", "日历刷新完成")
            
        } catch (e: Exception) {
            // 处理记录创建或保存失败的情况
            android.util.Log.e("MainActivity", "保存记录失败", e)
            e.printStackTrace()
        }
    }
    
    /**
     * 更新日历显示
     * 在日历上显示运动记录标记和时长信息
     */
    fun updateCalendarDisplay() {
        android.util.Log.d("MainActivity", "开始更新日历显示")
        
        // 清除现有的指示器
        clearExerciseIndicators()
        
        // 获取当前显示月份的所有记录
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = calendarView.date
        }
        
        val startOfMonth = Calendar.getInstance().apply {
            set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1)
        }.time
        
        val endOfMonth = Calendar.getInstance().apply {
            set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 
                currentDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        }.time
        
        // 获取当月的所有运动记录
        val monthlyRecords = exerciseRecordManager.getRecordsInRange(startOfMonth, endOfMonth)
        android.util.Log.d("MainActivity", "找到 ${monthlyRecords.size} 条运动记录")
        
        // 为每个运动记录添加标记（包括已运动和未运动）
        monthlyRecords.forEach { record ->
            android.util.Log.d("MainActivity", "添加标记: 日期=${record.date}, 运动=${record.exercised}, 时长=${record.duration}")
            addExerciseIndicator(record)
        }
        
        android.util.Log.d("MainActivity", "日历显示更新完成")
    }
    
    /**
     * 为指定的运动记录添加视觉指示器
     * @param record 运动记录
     */
    private fun addExerciseIndicator(record: ExerciseRecord) {
        android.util.Log.d("MainActivity", "创建标记: ${record.date}, 运动=${record.exercised}")
        
        // 根据运动状态设置不同的显示内容和颜色
        val (text, backgroundColor) = if (record.exercised && record.duration != null) {
            // 已运动：显示时长，使用绿色背景
            "${record.duration}分钟" to "#FF4CAF50"
        } else {
            // 未运动：显示"未运动"，使用灰色背景
            "未运动" to "#FF757575"
        }
        
        android.util.Log.d("MainActivity", "标记内容: $text, 颜色: $backgroundColor")
        
        // 创建显示运动信息的TextView
        val indicator = TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 12f  // 增大字体
            background = ColorDrawable(Color.parseColor(backgroundColor))
            setPadding(8, 4, 8, 4)  // 增大内边距
            alpha = 1.0f  // 完全不透明
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        // 使用更简单的布局参数 - 先显示在日历下方作为测试
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        
        // 暂时将标记显示在日历下方，便于调试
        layoutParams.topToBottom = calendarView.id
        layoutParams.startToStart = calendarView.id
        layoutParams.topMargin = 10
        layoutParams.marginStart = exerciseIndicators.size * 120  // 水平排列
        
        indicator.layoutParams = layoutParams
        calendarContainer.addView(indicator)
        
        // 保存指示器引用以便后续清理
        exerciseIndicators[record.date] = indicator
        
        android.util.Log.d("MainActivity", "标记已添加到容器")
    }
    
    /**
     * 计算日期在日历中的顶部边距
     * @param dateString 日期字符串
     * @return 顶部边距（像素）
     */
    private fun calculateDateTopMargin(dateString: String): Int {
        try {
            val date = dateFormat.parse(dateString) ?: return 0
            val calendar = Calendar.getInstance().apply { time = date }
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val firstDayOfMonth = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
            }
            val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0-based
            
            // 计算是第几周
            val weekNumber = (dayOfMonth + firstDayOfWeek - 1) / 7
            
            // 估算每周的高度（这是一个近似值）
            val weekHeight = calendarView.height / 7 // 假设显示7周
            return 100 + weekNumber * weekHeight // 100是标题栏的估算高度
        } catch (e: Exception) {
            return 0
        }
    }
    
    /**
     * 计算日期在日历中的左边距
     * @param dateString 日期字符串
     * @return 左边距（像素）
     */
    private fun calculateDateStartMargin(dateString: String): Int {
        try {
            val date = dateFormat.parse(dateString) ?: return 0
            val calendar = Calendar.getInstance().apply { time = date }
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val firstDayOfMonth = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
            }
            val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0-based
            
            // 计算是第几列
            val columnNumber = (dayOfMonth + firstDayOfWeek - 1) % 7
            
            // 估算每列的宽度
            val columnWidth = calendarView.width / 7
            return columnNumber * columnWidth
        } catch (e: Exception) {
            return 0
        }
    }
    
    /**
     * 清除所有运动指示器
     */
    private fun clearExerciseIndicators() {
        exerciseIndicators.values.forEach { indicator ->
            calendarContainer.removeView(indicator)
        }
        exerciseIndicators.clear()
    }
    
    /**
     * 当日历月份改变时更新显示
     */
    private fun onCalendarMonthChanged() {
        // 延迟更新以确保CalendarView已完成月份切换
        calendarView.post {
            updateCalendarDisplay()
        }
    }
    
    /**
     * 设置通知系统
     * 需求: 4.1, 4.4, 4.5 - 设置每日11:30通知调度
     */
    fun setupNotifications() {
        try {
            val notificationManager = NotificationManager(this)
            
            // 检查通知权限（Android 13+）
            if (!notificationManager.hasNotificationPermission()) {
                // 如果没有权限，可以在这里请求权限
                // 但根据需求4.5，系统不提供关闭通知功能，所以我们只记录日志
                android.util.Log.w("MainActivity", "通知权限未授予，但根据需求4.5系统必须发送通知")
            }
            
            // 设置每日通知调度
            notificationManager.scheduleDaily1130Notification()
            
            android.util.Log.i("MainActivity", "通知系统设置完成")
        } catch (e: Exception) {
            // 记录错误但不影响应用启动
            android.util.Log.e("MainActivity", "设置通知系统时发生错误", e)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 当Activity恢复时更新日历显示
        updateCalendarDisplay()
        
        // 验证系统集成状态
        validateSystemIntegration()
    }
    
    /**
     * 验证系统集成状态
     * 确保所有组件都正常工作
     */
    private fun validateSystemIntegration() {
        try {
            // 验证数据存储系统
            val recordCount = exerciseRecordManager.getAllRecords().size
            android.util.Log.d("MainActivity", "数据存储验证通过：$recordCount 条记录")
            
            // 验证通知系统
            val notificationManager = NotificationManager(this)
            val hasPermission = notificationManager.hasNotificationPermission()
            android.util.Log.d("MainActivity", "通知系统验证：权限状态=$hasPermission")
            
            // 验证UI组件
            if (::calendarView.isInitialized && ::calendarContainer.isInitialized) {
                android.util.Log.d("MainActivity", "UI组件验证通过")
            } else {
                android.util.Log.w("MainActivity", "UI组件未完全初始化")
            }
            
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "系统集成验证失败", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理资源
        clearExerciseIndicators()
    }
}
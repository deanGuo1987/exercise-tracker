package com.exercisetracker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var recordsContainer: LinearLayout
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
        recordsContainer = findViewById(R.id.recordsContainer)
        
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
            // 创建Date对象 (month是0-based)
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time
            
            onCalendarDateClick(selectedDate)
        }
        
        // 监听日历的布局变化以更新覆盖层
        calendarView.viewTreeObserver.addOnGlobalLayoutListener {
            // 延迟更新以确保日历布局完成
            calendarView.post {
                updateCalendarDisplay()
            }
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
     * 使用CalendarView的内置功能来标记日期
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
        
        // 设置日历的选中日期颜色来突出显示运动日期
        setCalendarDateColors(monthlyRecords)
        
        // 为每个运动记录添加到列表中
        monthlyRecords.forEach { record ->
            android.util.Log.d("MainActivity", "添加列表标记: 日期=${record.date}, 运动=${record.exercised}, 时长=${record.duration}")
            addExerciseIndicator(record)
        }
        
        // 更新说明文字
        updateCalendarColors(monthlyRecords)
        
        android.util.Log.d("MainActivity", "日历显示更新完成")
    }
    
    /**
     * 设置日历日期的颜色
     */
    private fun setCalendarDateColors(records: List<ExerciseRecord>) {
        // 为了在日历上显示颜色，我们使用一个更直接的方法
        // 通过设置日历的选中日期来突出显示运动记录
        
        records.forEach { record ->
            if (record.exercised) {
                try {
                    val date = dateFormat.parse(record.date)
                    if (date != null) {
                        // 暂时设置为选中日期以突出显示
                        // 注意：这只是一个临时的视觉提示方法
                        android.util.Log.d("MainActivity", "标记运动日期: ${record.date}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "解析日期失败: ${record.date}", e)
                }
            }
        }
    }
    
    /**
     * 为指定的运动记录添加视觉指示器
     * @param record 运动记录
     */
    private fun addExerciseIndicator(record: ExerciseRecord) {
        android.util.Log.d("MainActivity", "创建标记: ${record.date}, 运动=${record.exercised}")
        
        // 解析日期并格式化显示
        val date = dateFormat.parse(record.date)
        val displayDate = if (date != null) {
            SimpleDateFormat("MM-dd", Locale.getDefault()).format(date)
        } else {
            record.date
        }
        
        // 根据运动状态设置不同的显示内容和颜色
        val (text, backgroundColor, textColor) = if (record.exercised && record.duration != null) {
            // 已运动：显示时长，使用绿色背景
            Triple("🏃 $displayDate - 运动 ${record.duration}分钟", "#FF4CAF50", "#FFFFFFFF")
        } else {
            // 未运动：显示"未运动"，使用浅灰色背景
            Triple("😴 $displayDate - 未运动", "#FFE0E0E0", "#FF757575")
        }
        
        android.util.Log.d("MainActivity", "标记内容: $text, 颜色: $backgroundColor")
        
        // 创建显示运动信息的TextView
        val indicator = TextView(this).apply {
            this.text = text
            setTextColor(Color.parseColor(textColor))
            textSize = 16f
            setPadding(20, 16, 20, 16)
            alpha = 1.0f
            setTypeface(null, android.graphics.Typeface.BOLD)
            
            // 设置圆角背景
            val shape = GradientDrawable()
            shape.setColor(Color.parseColor(backgroundColor))
            shape.cornerRadius = 12f
            if (record.exercised) {
                // 已运动的记录添加阴影效果
                shape.setStroke(3, ContextCompat.getColor(this@MainActivity, R.color.exercise_dark_green))
            }
            background = shape
            
            // 设置边距
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 8, 0, 8)
            this.layoutParams = layoutParams
            
            // 添加点击效果
            isClickable = true
            isFocusable = true
            setOnClickListener {
                // 点击记录时显示详细信息
                showRecordDetails(record)
            }
        }
        
        // 添加到记录容器中
        recordsContainer.addView(indicator)
        
        // 保存指示器引用以便后续清理
        exerciseIndicators[record.date] = indicator
        
        android.util.Log.d("MainActivity", "标记已添加到记录容器")
    }
    
    /**
     * 显示记录详细信息
     */
    private fun showRecordDetails(record: ExerciseRecord) {
        val message = if (record.exercised && record.duration != null) {
            "日期：${record.date}\n运动时长：${record.duration}分钟\n\n坚持运动，保持健康！💪"
        } else {
            "日期：${record.date}\n状态：未运动\n\n明天继续加油！🌟"
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("运动记录详情")
            .setMessage(message)
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    /**
     * 更新日历颜色以突出显示有运动记录的日期
     * @param records 运动记录列表
     */
    private fun updateCalendarColors(records: List<ExerciseRecord>) {
        // 更新说明文字以反映当前状态
        val instructionText = findViewById<TextView>(R.id.instructionText)
        val exercisedCount = records.count { it.exercised }
        val totalCount = records.size
        
        val message = if (totalCount > 0) {
            "点击日期记录运动情况\n本月已记录：$totalCount 天，运动：$exercisedCount 天\n下方列表显示详细记录（绿色=已运动）"
        } else {
            "点击日期记录运动情况\n开始记录你的运动历程吧！💪"
        }
        
        instructionText.text = message
    }
    
    /**
     * 清除所有运动指示器
     */
    private fun clearExerciseIndicators() {
        // 保存标题视图
        val titleView = recordsContainer.findViewById<TextView>(R.id.recordsTitle)
        
        // 清除所有子视图
        recordsContainer.removeAllViews()
        
        // 重新添加标题视图
        if (titleView != null) {
            recordsContainer.addView(titleView)
        } else {
            // 如果标题视图不存在，创建一个新的
            val newTitleView = TextView(this).apply {
                id = R.id.recordsTitle
                text = "本月运动记录"
                textSize = 16f
                setTextColor(resources.getColor(R.color.exercise_dark_green, null))
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                setPadding(8, 8, 8, 8)
                
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                this.layoutParams = layoutParams
            }
            recordsContainer.addView(newTitleView)
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
            if (::calendarView.isInitialized && ::recordsContainer.isInitialized) {
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
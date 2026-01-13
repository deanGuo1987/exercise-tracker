package com.exercisetracker

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * 运动报表Activity
 * 显示按月和按年的运动统计信息
 */
class ReportActivity : AppCompatActivity() {
    
    private lateinit var exerciseRecordManager: ExerciseRecordManager
    private lateinit var reportGenerator: ExerciseReportGenerator
    
    // UI组件
    private lateinit var reportTypeSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var generateButton: Button
    private lateinit var reportContainer: LinearLayout
    
    // 报表类型
    private val reportTypes = arrayOf("月度报表", "年度报表")
    private var currentReportType = 0 // 0=月度，1=年度
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        
        // 设置ActionBar返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "运动报表"
        
        initializeComponents()
        initializeViews()
        setupSpinners()
        setupListeners()
        
        // 默认生成当前月份的报表
        generateCurrentMonthReport()
    }
    
    /**
     * 初始化组件
     */
    private fun initializeComponents() {
        val fileStorage = FileStorage(this)
        exerciseRecordManager = ExerciseRecordManager(fileStorage)
        reportGenerator = ExerciseReportGenerator(exerciseRecordManager)
    }
    
    /**
     * 初始化视图组件
     */
    private fun initializeViews() {
        reportTypeSpinner = findViewById(R.id.reportTypeSpinner)
        yearSpinner = findViewById(R.id.yearSpinner)
        monthSpinner = findViewById(R.id.monthSpinner)
        generateButton = findViewById(R.id.generateButton)
        reportContainer = findViewById(R.id.reportContainer)
    }
    
    /**
     * 设置下拉选择器
     */
    private fun setupSpinners() {
        // 设置报表类型选择器
        val reportTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportTypes)
        reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        reportTypeSpinner.adapter = reportTypeAdapter
        
        // 设置年份选择器
        setupYearSpinner()
        
        // 设置月份选择器
        setupMonthSpinner()
    }
    
    /**
     * 设置年份选择器
     */
    private fun setupYearSpinner() {
        val availableYears = reportGenerator.getAvailableYears()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        // 如果没有数据，至少显示当前年份
        val years = if (availableYears.isEmpty()) {
            listOf(currentYear)
        } else {
            availableYears
        }
        
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter
        
        // 默认选择当前年份
        val currentYearIndex = years.indexOf(currentYear)
        if (currentYearIndex >= 0) {
            yearSpinner.setSelection(currentYearIndex)
        }
    }
    
    /**
     * 设置月份选择器
     */
    private fun setupMonthSpinner() {
        val months = arrayOf(
            "1月", "2月", "3月", "4月", "5月", "6月",
            "7月", "8月", "9月", "10月", "11月", "12月"
        )
        
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
        
        // 默认选择当前月份
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        monthSpinner.setSelection(currentMonth)
    }
    
    /**
     * 设置监听器
     */
    private fun setupListeners() {
        // 报表类型选择监听器
        reportTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentReportType = position
                updateMonthSpinnerVisibility()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // 年份选择监听器
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (currentReportType == 0) { // 月度报表
                    updateMonthSpinnerForYear()
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // 生成报表按钮监听器
        generateButton.setOnClickListener {
            generateReport()
        }
    }
    
    /**
     * 更新月份选择器的可见性
     */
    private fun updateMonthSpinnerVisibility() {
        if (currentReportType == 0) { // 月度报表
            monthSpinner.visibility = View.VISIBLE
            findViewById<TextView>(R.id.monthLabel).visibility = View.VISIBLE
        } else { // 年度报表
            monthSpinner.visibility = View.GONE
            findViewById<TextView>(R.id.monthLabel).visibility = View.GONE
        }
    }
    
    /**
     * 根据选择的年份更新月份选择器
     */
    private fun updateMonthSpinnerForYear() {
        val selectedYear = yearSpinner.selectedItem as Int
        val availableMonths = reportGenerator.getAvailableMonths(selectedYear)
        
        if (availableMonths.isNotEmpty()) {
            // 只显示有数据的月份
            val monthNames = availableMonths.map { "${it}月" }
            val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthNames)
            monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthSpinner.adapter = monthAdapter
        }
    }
    
    /**
     * 生成当前月份的报表（默认）
     */
    private fun generateCurrentMonthReport() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH是0-based
        
        try {
            val report = reportGenerator.generateMonthlyReport(currentYear, currentMonth)
            displayReport(report)
        } catch (e: Exception) {
            showEmptyReport("当前月份暂无运动记录")
        }
    }
    
    /**
     * 生成报表
     */
    private fun generateReport() {
        try {
            val selectedYear = yearSpinner.selectedItem as Int
            
            val report = if (currentReportType == 0) { // 月度报表
                val monthText = monthSpinner.selectedItem as String
                val selectedMonth = monthText.replace("月", "").toInt()
                reportGenerator.generateMonthlyReport(selectedYear, selectedMonth)
            } else { // 年度报表
                reportGenerator.generateYearlyReport(selectedYear)
            }
            
            displayReport(report)
            
        } catch (e: Exception) {
            android.util.Log.e("ReportActivity", "生成报表失败", e)
            showEmptyReport("生成报表失败，请检查数据")
        }
    }
    
    /**
     * 显示报表
     */
    private fun displayReport(report: ExerciseReport) {
        reportContainer.removeAllViews()
        
        // 创建报表标题
        val titleView = createTitleView(report.period)
        reportContainer.addView(titleView)
        
        // 创建统计卡片
        val statsCards = createStatsCards(report)
        statsCards.forEach { card ->
            reportContainer.addView(card)
        }
        
        // 创建详细信息
        val detailsView = createDetailsView(report)
        reportContainer.addView(detailsView)
    }
    
    /**
     * 显示空报表
     */
    private fun showEmptyReport(message: String) {
        reportContainer.removeAllViews()
        
        val emptyView = TextView(this).apply {
            text = message
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(32, 64, 32, 64)
            setTextColor(resources.getColor(R.color.exercise_dark_green, null))
        }
        
        reportContainer.addView(emptyView)
    }
    
    /**
     * 创建报表标题
     */
    private fun createTitleView(period: String): View {
        val titleView = TextView(this).apply {
            text = "${period} 运动报表"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(resources.getColor(R.color.exercise_dark_green, null))
            gravity = android.view.Gravity.CENTER
            setPadding(16, 24, 16, 16)
        }
        
        return titleView
    }
    
    /**
     * 创建统计卡片
     */
    private fun createStatsCards(report: ExerciseReport): List<View> {
        val cards = mutableListOf<View>()
        
        // 运动天数卡片
        cards.add(createStatCard(
            "运动天数",
            "${report.exerciseDays}/${report.totalDays} 天",
            "运动率: ${String.format("%.1f", report.exerciseRate)}%",
            R.color.exercise_green
        ))
        
        // 运动时长卡片
        cards.add(createStatCard(
            "运动时长",
            "${report.totalDuration} 分钟",
            "平均: ${String.format("%.1f", report.averageDuration)} 分钟/次",
            R.color.exercise_light_green
        ))
        
        // 连续运动卡片
        cards.add(createStatCard(
            "连续运动",
            "最长: ${report.longestStreak} 天",
            "当前: ${report.currentStreak} 天",
            R.color.exercise_dark_green
        ))
        
        return cards
    }
    
    /**
     * 创建单个统计卡片
     */
    private fun createStatCard(title: String, mainValue: String, subValue: String, colorRes: Int): View {
        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            
            // 设置卡片背景
            val shape = android.graphics.drawable.GradientDrawable()
            shape.setColor(resources.getColor(colorRes, null))
            shape.cornerRadius = 16f
            background = shape
            
            // 设置边距
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(16, 8, 16, 8)
            this.layoutParams = layoutParams
        }
        
        // 标题
        val titleView = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(resources.getColor(R.color.white, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        // 主要数值
        val mainValueView = TextView(this).apply {
            text = mainValue
            textSize = 24f
            setTextColor(resources.getColor(R.color.white, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 4)
        }
        
        // 副数值
        val subValueView = TextView(this).apply {
            text = subValue
            textSize = 12f
            setTextColor(resources.getColor(R.color.white, null))
            alpha = 0.9f
        }
        
        cardLayout.addView(titleView)
        cardLayout.addView(mainValueView)
        cardLayout.addView(subValueView)
        
        return cardLayout
    }
    
    /**
     * 创建详细信息视图
     */
    private fun createDetailsView(report: ExerciseReport): View {
        val detailsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            
            // 设置背景
            setBackgroundColor(resources.getColor(R.color.white, null))
            
            // 设置边距
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(16, 16, 16, 16)
            this.layoutParams = layoutParams
        }
        
        // 详细信息标题
        val detailsTitle = TextView(this).apply {
            text = "详细统计"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(resources.getColor(R.color.exercise_dark_green, null))
            setPadding(0, 0, 0, 16)
        }
        
        detailsLayout.addView(detailsTitle)
        
        // 添加详细统计项
        val details = listOf(
            "记录天数" to "${report.totalDays} 天",
            "运动天数" to "${report.exerciseDays} 天",
            "未运动天数" to "${report.totalDays - report.exerciseDays} 天",
            "总运动时长" to "${report.totalDuration} 分钟",
            "平均运动时长" to "${String.format("%.1f", report.averageDuration)} 分钟",
            "运动率" to "${String.format("%.1f", report.exerciseRate)}%",
            "最长连续运动" to "${report.longestStreak} 天",
            "当前连续运动" to "${report.currentStreak} 天"
        )
        
        details.forEach { (label, value) ->
            val itemView = createDetailItem(label, value)
            detailsLayout.addView(itemView)
        }
        
        return detailsLayout
    }
    
    /**
     * 创建详细信息项
     */
    private fun createDetailItem(label: String, value: String): View {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 8)
        }
        
        val labelView = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(resources.getColor(R.color.exercise_dark_green, null))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val valueView = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(resources.getColor(R.color.exercise_dark_green, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.END
        }
        
        itemLayout.addView(labelView)
        itemLayout.addView(valueView)
        
        return itemLayout
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
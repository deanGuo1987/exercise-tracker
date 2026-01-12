package com.exercisetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 运动选择对话框
 * 职责: 显示运动记录输入界面
 */
class ExerciseDialog : DialogFragment() {
    
    private var selectedDate: Date? = null
    private var callback: ((Boolean, Int?) -> Unit)? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        private const val ARG_DATE = "date"
        
        /**
         * 创建ExerciseDialog实例
         * @param date 选择的日期
         * @param callback 选择结果回调函数
         */
        fun newInstance(date: Date, callback: (Boolean, Int?) -> Unit): ExerciseDialog {
            return ExerciseDialog().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATE, date.time)
                }
                this.callback = callback
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 从arguments中恢复日期
        arguments?.let { args ->
            val dateTime = args.getLong(ARG_DATE)
            selectedDate = Date(dateTime)
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return showExerciseOptions()
    }
    
    /**
     * 显示运动选择界面 - 将所有选项放在一个对话框中
     * 需求: 2.2, 2.3, 2.4, 2.6 - 提供运动选择和时长选择
     */
    private fun showExerciseOptions(): AlertDialog {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        // 选项数组：包含"未运动"和三个运动时长选项
        val options = arrayOf(
            "未运动",
            "运动 20分钟",
            "运动 30分钟", 
            "运动 40分钟"
        )
        
        // 添加调试日志
        android.util.Log.d("ExerciseDialog", "创建对话框，日期: $dateString, 选项数量: ${options.size}")
        
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exercise_dialog_title))
            .setMessage("$dateString\n请选择今天的运动情况：")
            .setItems(options) { _, which ->
                android.util.Log.d("ExerciseDialog", "用户选择了选项: $which")
                when (which) {
                    0 -> {
                        // 用户选择"未运动"
                        android.util.Log.d("ExerciseDialog", "选择：未运动")
                        callback?.invoke(false, null)
                    }
                    1 -> {
                        // 用户选择"运动 20分钟"
                        android.util.Log.d("ExerciseDialog", "选择：运动 20分钟")
                        callback?.invoke(true, 20)
                    }
                    2 -> {
                        // 用户选择"运动 30分钟"
                        android.util.Log.d("ExerciseDialog", "选择：运动 30分钟")
                        callback?.invoke(true, 30)
                    }
                    3 -> {
                        // 用户选择"运动 40分钟"
                        android.util.Log.d("ExerciseDialog", "选择：运动 40分钟")
                        callback?.invoke(true, 40)
                    }
                }
                dismiss()
            }
            .setCancelable(true)
            .setNegativeButton("取消") { _, _ ->
                android.util.Log.d("ExerciseDialog", "用户取消了对话框")
                dismiss()
            }
            .create()
    }
}
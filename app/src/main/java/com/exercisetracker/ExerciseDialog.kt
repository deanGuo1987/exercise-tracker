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
     * 显示运动选择界面 - 使用按钮而不是列表
     * 需求: 2.2, 2.3, 2.4, 2.6 - 提供运动选择和时长选择
     */
    private fun showExerciseOptions(): AlertDialog {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        // 添加调试日志
        android.util.Log.d("ExerciseDialog", "创建对话框，日期: $dateString")
        
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exercise_dialog_title))
            .setMessage("$dateString\n请选择今天的运动情况：")
            .setPositiveButton("运动 20分钟") { _, _ ->
                android.util.Log.d("ExerciseDialog", "选择：运动 20分钟")
                callback?.invoke(true, 20)
                dismiss()
            }
            .setNegativeButton("未运动") { _, _ ->
                android.util.Log.d("ExerciseDialog", "选择：未运动")
                callback?.invoke(false, null)
                dismiss()
            }
            .setNeutralButton("更多选项") { _, _ ->
                // 显示更多运动时长选项
                showMoreDurationOptions()
            }
            .setCancelable(true)
            .create()
    }
    
    /**
     * 显示更多运动时长选项
     */
    private fun showMoreDurationOptions() {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        android.util.Log.d("ExerciseDialog", "显示更多时长选项")
        
        AlertDialog.Builder(requireContext())
            .setTitle("选择运动时长")
            .setMessage("$dateString\n请选择运动时长：")
            .setPositiveButton("30分钟") { _, _ ->
                android.util.Log.d("ExerciseDialog", "选择：运动 30分钟")
                callback?.invoke(true, 30)
                dismiss()
            }
            .setNegativeButton("40分钟") { _, _ ->
                android.util.Log.d("ExerciseDialog", "选择：运动 40分钟")
                callback?.invoke(true, 40)
                dismiss()
            }
            .setNeutralButton("返回") { _, _ ->
                // 重新显示主选择对话框
                showExerciseOptions().show()
            }
            .setCancelable(true)
            .show()
    }
}
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
     * 显示运动选择界面（是/否）
     * 需求: 2.2 - 提供"是"和"否"两个选项供用户选择是否运动
     */
    private fun showExerciseOptions(): AlertDialog {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exercise_dialog_title))
            .setMessage("${dateString}\n${getString(R.string.exercise_question)}")
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                // 用户选择"是"，显示时长选择界面
                showDurationOptions()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // 用户选择"否"，创建未运动记录
                // 需求: 2.6 - 当用户选择"否"时，创建标记为未运动的运动记录
                callback?.invoke(false, null)
                dismiss()
            }
            .setCancelable(true)
            .create()
    }
    
    /**
     * 显示运动时长选择界面（20/30/40分钟）
     * 需求: 2.3, 2.4 - 显示运动时长选择界面，提供20分钟、30分钟、40分钟三个预设选项
     */
    private fun showDurationOptions() {
        // 不要dismiss当前对话框，而是在同一个fragment中创建新的对话框
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        val durationOptions = arrayOf(
            getString(R.string.duration_20),
            getString(R.string.duration_30),
            getString(R.string.duration_40)
        )
        
        val durationValues = arrayOf(20, 30, 40)
        
        // 使用post确保在UI线程中执行
        requireActivity().runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.duration_question))
                .setMessage("选择 $dateString 的运动时长：")
                .setItems(durationOptions) { _, which ->
                    // 用户选择了时长，创建运动记录
                    val selectedDuration = durationValues[which]
                    callback?.invoke(true, selectedDuration)
                    dismiss()
                }
                .setCancelable(true)
                .setNegativeButton("返回") { _, _ ->
                    // 如果用户取消时长选择，回到运动选择界面
                    showExerciseOptionsAgain()
                }
                .show()
        }
    }
    
    /**
     * 重新显示运动选择界面
     */
    private fun showExerciseOptionsAgain() {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        requireActivity().runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.exercise_dialog_title))
                .setMessage("${dateString}\n${getString(R.string.exercise_question)}")
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    showDurationOptions()
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    callback?.invoke(false, null)
                    dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }
}
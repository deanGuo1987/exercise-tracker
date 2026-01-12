package com.exercisetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
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
        return createCustomDialog()
    }
    
    /**
     * 创建自定义对话框
     * 使用自定义布局确保所有选项都能正确显示
     */
    private fun createCustomDialog(): AlertDialog {
        val dateString = selectedDate?.let { dateFormat.format(it) } ?: "未知日期"
        
        android.util.Log.d("ExerciseDialog", "创建自定义对话框，日期: $dateString")
        
        // 加载自定义布局
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_exercise_selection, null)
        
        // 设置日期信息
        val messageTextView = dialogView.findViewById<TextView>(R.id.dialog_message)
        messageTextView.text = "$dateString\n请选择今天的运动情况："
        
        // 创建对话框
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // 设置按钮点击事件
        setupButtonClickListeners(dialogView, dialog)
        
        return dialog
    }
    
    /**
     * 设置按钮点击事件
     */
    private fun setupButtonClickListeners(dialogView: android.view.View, dialog: AlertDialog) {
        // 未运动按钮
        dialogView.findViewById<Button>(R.id.btn_no_exercise).setOnClickListener {
            android.util.Log.d("ExerciseDialog", "选择：未运动")
            callback?.invoke(false, null)
            dialog.dismiss()
        }
        
        // 运动 20分钟按钮
        dialogView.findViewById<Button>(R.id.btn_exercise_20).setOnClickListener {
            android.util.Log.d("ExerciseDialog", "选择：运动 20分钟")
            callback?.invoke(true, 20)
            dialog.dismiss()
        }
        
        // 运动 30分钟按钮
        dialogView.findViewById<Button>(R.id.btn_exercise_30).setOnClickListener {
            android.util.Log.d("ExerciseDialog", "选择：运动 30分钟")
            callback?.invoke(true, 30)
            dialog.dismiss()
        }
        
        // 运动 40分钟按钮
        dialogView.findViewById<Button>(R.id.btn_exercise_40).setOnClickListener {
            android.util.Log.d("ExerciseDialog", "选择：运动 40分钟")
            callback?.invoke(true, 40)
            dialog.dismiss()
        }
        
        // 取消按钮
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            android.util.Log.d("ExerciseDialog", "用户取消了对话框")
            dialog.dismiss()
        }
    }
}
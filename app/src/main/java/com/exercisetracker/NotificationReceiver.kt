package com.exercisetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 通知接收器 - 处理AlarmManager触发的通知事件
 * 
 * 职责:
 * - 接收AlarmManager触发的广播
 * - 调用NotificationManager显示通知
 */
class NotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // 当AlarmManager触发时，显示运动提醒通知
        showNotification(context)
    }
    
    /**
     * 显示通知
     * @param context 上下文
     */
    private fun showNotification(context: Context) {
        // 创建NotificationManager实例并显示提醒
        val notificationManager = NotificationManager(context)
        notificationManager.showExerciseReminder()
    }
}
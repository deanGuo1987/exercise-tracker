package com.exercisetracker

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * 通知管理器 - 管理每日11:30的运动提醒通知
 * 
 * 职责:
 * - 设置每日11:30通知调度
 * - 创建通知渠道
 * - 管理AlarmManager集成
 */
class NotificationManager(private val context: Context) {
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "exercise_reminder_channel"
        const val NOTIFICATION_CHANNEL_NAME = "运动提醒"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "每日运动提醒通知"
        const val NOTIFICATION_ID = 1001
        const val ALARM_REQUEST_CODE = 2001
        
        // 通知时间：每日11:30
        const val NOTIFICATION_HOUR = 11
        const val NOTIFICATION_MINUTE = 30
    }
    
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    
    private val androidNotificationManager: AndroidNotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
    }
    
    /**
     * 设置每日11:30通知调度
     * 需求: 4.1 - 系统应每天11:30准时发送通知
     * 需求: 4.4 - 使用固定的11:30时间，不允许用户自定义
     * 需求: 4.5 - 持续每日发送通知，不提供关闭功能
     */
    fun scheduleDaily1130Notification() {
        // 创建通知渠道（Android 8.0+需要）
        createNotificationChannel()
        
        // 创建PendingIntent用于触发通知
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 计算下一个11:30的时间
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            set(Calendar.MINUTE, NOTIFICATION_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // 如果当前时间已经过了今天的11:30，则设置为明天的11:30
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        try {
            // 设置重复的每日闹钟
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, // 每日重复
                pendingIntent
            )
        } catch (e: SecurityException) {
            // 处理权限不足的情况
            // 在Android 6.0+可能需要特殊权限
            e.printStackTrace()
        }
    }
    
    /**
     * 创建通知渠道
     * Android 8.0 (API 26) 及以上版本需要通知渠道
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NOTIFICATION_CHANNEL_DESCRIPTION
                // 设置通知的默认行为
                enableLights(true)
                enableVibration(true)
            }
            
            androidNotificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 显示运动提醒通知
     * 这个方法将被NotificationReceiver调用
     */
    fun showExerciseReminder() {
        // 创建点击通知时的Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 构建通知
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(context)
        }.apply {
            setSmallIcon(android.R.drawable.ic_dialog_info) // 使用系统默认图标
            setContentTitle("运动提醒")
            setContentText("哥哥，到时间运动了，今天是否运动？") // 需求: 4.2
            setContentIntent(pendingIntent) // 需求: 4.3 - 点击通知打开应用
            setAutoCancel(true) // 点击后自动取消通知
            setPriority(android.app.Notification.PRIORITY_DEFAULT)
        }.build()
        
        // 显示通知
        androidNotificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * 取消已设置的通知调度
     * 注意：根据需求4.5，系统不应提供关闭通知功能
     * 这个方法仅用于测试或特殊情况
     */
    fun cancelNotificationSchedule() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * 检查通知权限是否已授予
     * Android 13+ 需要显式的通知权限
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidNotificationManager.areNotificationsEnabled()
        } else {
            true // 较低版本默认有权限
        }
    }
}
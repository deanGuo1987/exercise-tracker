package com.exercisetracker

/**
 * 运动记录数据类
 * 
 * @param date 日期，ISO 8601格式: "2024-01-15"
 * @param exercised 是否运动
 * @param duration 运动时长（分钟），null表示未运动
 */
data class ExerciseRecord(
    val date: String,        // ISO 8601格式: "2024-01-15"
    val exercised: Boolean,  // 是否运动
    val duration: Int?       // 运动时长（分钟），null表示未运动
) {
    /**
     * 获取显示文本
     * @return 如果运动了返回"已运动 X分钟"，否则返回空字符串
     */
    fun getDisplayText(): String {
        return if (exercised && duration != null) {
            "已运动 ${duration}分钟"
        } else {
            ""
        }
    }
}
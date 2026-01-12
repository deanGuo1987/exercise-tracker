package com.exercisetracker

import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.HashSet

/**
 * 运动日期装饰器
 * 用于在日历上标记已运动的日期
 */
class ExerciseDateDecorator(
    private val drawable: Drawable,
    private val exerciseDates: Collection<CalendarDay>
) : DayViewDecorator {
    
    private val dates = HashSet(exerciseDates)
    
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }
    
    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable)
    }
    
    /**
     * 更新运动日期列表
     */
    fun updateDates(newDates: Collection<CalendarDay>) {
        dates.clear()
        dates.addAll(newDates)
    }
}
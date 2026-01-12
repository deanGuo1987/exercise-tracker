package com.exercisetracker

import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.HashSet

/**
 * 未运动日期装饰器
 * 用于在日历上标记未运动的日期
 */
class NoExerciseDateDecorator(
    private val drawable: Drawable,
    private val noExerciseDates: Collection<CalendarDay>
) : DayViewDecorator {
    
    private val dates = HashSet(noExerciseDates)
    
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }
    
    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable)
    }
    
    /**
     * 更新未运动日期列表
     */
    fun updateDates(newDates: Collection<CalendarDay>) {
        dates.clear()
        dates.addAll(newDates)
    }
}
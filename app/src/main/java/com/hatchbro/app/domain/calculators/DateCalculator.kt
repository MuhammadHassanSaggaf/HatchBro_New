package com.hatchbro.app.domain.calculators

import java.util.Calendar
import java.util.Date

object DateCalculator {

    fun calculateExpectedHatchDate(startDate: Date, incubationDays: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_YEAR, incubationDays)
        return calendar.time
    }

    fun calculateLockdownDate(startDate: Date, incubationDays: Int, lockdownDays: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        // Lockdown starts X days before expected hatch
        // So if incubation is 21 days and lockdown is 3 days before, it starts on day 18
        calendar.add(Calendar.DAY_OF_YEAR, incubationDays - lockdownDays)
        return calendar.time
    }

    fun calculateDiscardDate(expectedHatchDate: Date, discardGraceDays: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = expectedHatchDate
        calendar.add(Calendar.DAY_OF_YEAR, discardGraceDays)
        return calendar.time
    }
}

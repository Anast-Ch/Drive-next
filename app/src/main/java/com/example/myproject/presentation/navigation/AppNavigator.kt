package com.example.myproject.presentation.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.myproject.MainActivity
import com.example.myproject.data.local.AuthPreferences
import com.example.myproject.data.local.OnboardingPreferences
import com.example.myproject.presentation.auth.GettingStartedActivity
import com.example.myproject.presentation.onboarding.OnboardingActivity

object AppNavigator {

    /**
     * Решает, какой экран показать после заставки / проверки сети:
     * 1) онбординг не пройден -> OnboardingActivity
     * 2) онбординг пройден, но нет сессии -> GettingStartedActivity
     * 3) онбординг пройден и сессия есть -> MainActivity
     */
    fun getStartDestination(context: Context): Class<out Activity> {
        val onboardingPreferences = OnboardingPreferences(context)
        val authPreferences = AuthPreferences(context)

        return when {
            !onboardingPreferences.isOnboardingCompleted() -> OnboardingActivity::class.java
            !authPreferences.isLoggedIn() -> GettingStartedActivity::class.java
            else -> MainActivity::class.java
        }
    }

    /** Открывает стартовый экран и закрывает текущий. */
    fun openStartDestination(activity: Activity) {
        val intent = Intent(activity, getStartDestination(activity))
        activity.startActivity(intent)
        activity.finish()
    }

    /**
     * Открывает MainActivity и полностью очищает стек экранов.
     * После этого кнопка «Назад» не вернёт пользователя на Login / регистрацию.
     * Используем на шаге 2 (Login) и на шаге 6 (Congratulations).
     */
    fun openMainClearingStack(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity.startActivity(intent)
    }
}
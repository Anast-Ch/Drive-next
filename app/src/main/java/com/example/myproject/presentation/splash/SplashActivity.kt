package com.example.myproject.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myproject.MainActivity
import com.example.myproject.R
import com.example.myproject.data.local.OnboardingPreferences
import com.example.myproject.presentation.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val splashDelayMillis = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, splashDelayMillis)
    }

    private fun navigateNext() {
        val connectivityChecker = com.example.myproject.data.local.NetworkConnectivityChecker(this)

        if (!connectivityChecker.isConnected()) {
            val intent = Intent(this, com.example.myproject.presentation.no_connection.NoConnectionActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val onboardingPreferences = OnboardingPreferences(this)

        val destination = if (onboardingPreferences.isOnboardingCompleted()) {
            MainActivity::class.java
        } else {
            OnboardingActivity::class.java
        }

        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}

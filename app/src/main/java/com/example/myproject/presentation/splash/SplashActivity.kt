package com.example.myproject.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myproject.R

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
        // Временно переходим на MainActivity.
        // Позже здесь будет логика выбора экрана: Onboarding / Login / Main
        val intent = Intent(this, com.example.myproject.MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
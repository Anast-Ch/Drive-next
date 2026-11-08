package com.example.myproject.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myproject.R
import com.example.myproject.data.local.NetworkConnectivityChecker
import com.example.myproject.presentation.navigation.AppNavigator
import com.example.myproject.presentation.no_connection.NoConnectionActivity

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
        val connectivityChecker = NetworkConnectivityChecker(this)

        if (!connectivityChecker.isConnected()) {
            val intent = Intent(this, NoConnectionActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        AppNavigator.openStartDestination(this)
    }
}
package com.example.myproject.presentation.no_connection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myproject.MainActivity
import com.example.myproject.R
import com.example.myproject.data.local.OnboardingPreferences
import com.example.myproject.presentation.onboarding.OnboardingActivity

class NoConnectionActivity : AppCompatActivity() {

    private lateinit var viewModel: NoConnectionViewModel
    private lateinit var btnRetry: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        viewModel = ViewModelProvider(this)[NoConnectionViewModel::class.java]

        btnRetry = findViewById(R.id.btnRetry)
        progressBar = findViewById(R.id.progressBar)

        btnRetry.setOnClickListener {
            viewModel.checkConnection()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnRetry.isEnabled = !isLoading
            btnRetry.text = if (isLoading) "" else getString(R.string.btn_retry)
        }

        viewModel.connectionResult.observe(this) { isConnected ->
            if (isConnected) {
                navigateNext()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.no_connection_still_offline),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateNext() {
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

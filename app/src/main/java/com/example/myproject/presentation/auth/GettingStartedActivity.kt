package com.example.myproject.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.myproject.R
import com.example.myproject.presentation.registration.SignUpStep1Activity

class GettingStartedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_getting_started)

        applySystemBarsInsets()

        findViewById<View>(R.id.btnLogin).setOnClickListener { openLogin() }
        findViewById<View>(R.id.btnRegister).setOnClickListener { openRegistration() }
    }

    private fun applySystemBarsInsets() {
        val root = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            insets
        }
    }

    private fun openLogin() {
        // finish() не вызываем: кнопка «Назад» на Login вернёт сюда
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun openRegistration() {
        // finish() не вызываем: кнопка «Назад» на регистрации вернёт сюда
        startActivity(Intent(this, SignUpStep1Activity::class.java))
    }
}

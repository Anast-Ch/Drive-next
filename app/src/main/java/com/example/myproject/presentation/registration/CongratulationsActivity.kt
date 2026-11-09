package com.example.myproject.presentation.registration

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.myproject.R
import com.example.myproject.data.local.AuthPreferences
import com.example.myproject.presentation.navigation.AppNavigator
import com.google.android.material.button.MaterialButton
import java.util.UUID

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var btnNext: MaterialButton
    private lateinit var authPreferences: AuthPreferences

    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_congratulations)

        authPreferences = AuthPreferences(this)
        email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        findViews()
        applySystemBarsInsets()
        blockBackNavigation()

        btnNext.setOnClickListener { onNextClicked() }
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun applySystemBarsInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
            insets
        }
    }

    private fun blockBackNavigation() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Намеренно пусто.
                }
            }
        )
    }

    private fun onNextClicked() {
        btnNext.isEnabled = false

        val token = "registration_fake_token_" + UUID.randomUUID().toString()
        authPreferences.saveSession(token, email)

        AppNavigator.openMainClearingStack(this)
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}

package com.example.myproject.presentation.auth

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import android.content.Intent
import com.example.myproject.presentation.registration.SignUpStep1Activity
import com.example.myproject.presentation.validation.InputValidator
import com.example.myproject.R
import com.example.myproject.data.local.AuthPreferences
import com.example.myproject.presentation.navigation.AppNavigator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvLoginError: TextView
    private lateinit var btnForgotPassword: MaterialButton
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogle: MaterialButton
    private lateinit var btnRegister: MaterialButton

    private lateinit var authPreferences: AuthPreferences

    private val handler = Handler(Looper.getMainLooper())

    private var isPasswordVisible = false
    private var isRequestInProgress = false
    private var lastPasswordText = ""
    private var passwordTypeface: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        authPreferences = AuthPreferences(this)

        findViews()
        applySystemBarsInsets()
        setupPasswordToggle()
        setupTextWatchers()
        setupClickListeners()
        updateLoginButtonState()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        tvLoginError = findViewById(R.id.tvLoginError)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun applySystemBarsInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updatePadding(
                left = bars.left,
                right = bars.right,
                bottom = maxOf(bars.bottom, ime.bottom)
            )
            insets
        }
    }


    private fun setupPasswordToggle() {
        passwordTypeface = etPassword.typeface
        applyPasswordVisibility()

        tilPassword.setEndIconOnClickListener {
            isPasswordVisible = !isPasswordVisible
            applyPasswordVisibility()
        }
    }

    private fun applyPasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            tilPassword.setEndIconDrawable(R.drawable.ic_eye_on)
        } else {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            tilPassword.setEndIconDrawable(R.drawable.ic_eye_off)
        }
        passwordTypeface?.let { etPassword.typeface = it }
        etPassword.setSelection(etPassword.text?.length ?: 0)
    }

    private fun setupTextWatchers() {
        etEmail.doAfterTextChanged {
            clearError(tilEmail)
            hideLoginError()
            updateLoginButtonState()
        }

        etPassword.doAfterTextChanged { text ->
            val current = text?.toString().orEmpty()
            if (current != lastPasswordText) {
                lastPasswordText = current
                clearError(tilPassword)
                hideLoginError()
            }
            updateLoginButtonState()
        }
    }

    private fun updateLoginButtonState() {
        val emailFilled = !etEmail.text.isNullOrBlank()
        val passwordFilled = !etPassword.text.isNullOrEmpty()
        btnLogin.isEnabled = emailFilled && passwordFilled && !isRequestInProgress
    }


    private fun setupClickListeners() {
        btnLogin.setOnClickListener { attemptLogin() }

        btnGoogle.setOnClickListener { signInWithGoogleSimulation() }

        btnForgotPassword.setOnClickListener {
            Toast.makeText(this, R.string.login_forgot_stub, Toast.LENGTH_SHORT).show()
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpStep1Activity::class.java))
        }
    }


    private fun attemptLogin() {
        hideKeyboard()
        hideLoginError()

        val email = etEmail.text?.toString().orEmpty().trim()
        val password = etPassword.text?.toString().orEmpty()

        if (!isEmailValid(email)) {
            showError(tilEmail, getString(R.string.error_email_invalid))
            return
        }

        performFakeLogin(email, password)
    }

    private fun isEmailValid(email: String): Boolean {
        return InputValidator.isEmailValid(email)
    }

    private fun performFakeLogin(email: String, password: String) {
        setLoading(true)

        handler.postDelayed({
            val isSuccess = password.length >= FAKE_MIN_PASSWORD_LENGTH

            if (isSuccess) {
                val token = "fake_token_" + UUID.randomUUID().toString()
                authPreferences.saveSession(token, email)
                AppNavigator.openMainClearingStack(this)
            } else {
                setLoading(false)
                showLoginError(getString(R.string.login_error_credentials))
            }
        }, FAKE_NETWORK_DELAY_MS)
    }


    private fun signInWithGoogleSimulation() {
        hideKeyboard()
        hideLoginError()
        setLoading(true)

        handler.postDelayed({
            // В реальном приложении тут был бы ID-токен от Google и ответ вашего сервера.
            // Мы генерируем фейковый токен и сохраняем его в SharedPreferences.
            val fakeToken = "google_fake_token_" + UUID.randomUUID().toString()
            authPreferences.saveSession(fakeToken, FAKE_GOOGLE_EMAIL)

            Toast.makeText(this, R.string.login_google_success, Toast.LENGTH_SHORT).show()
            AppNavigator.openMainClearingStack(this)
        }, FAKE_GOOGLE_DELAY_MS)
    }

    private fun setLoading(loading: Boolean) {
        isRequestInProgress = loading
        btnGoogle.isEnabled = !loading
        updateLoginButtonState()
    }

    private fun showError(layout: TextInputLayout, message: String) {
        layout.isErrorEnabled = true
        layout.error = message
    }

    private fun clearError(layout: TextInputLayout) {
        layout.error = null
        layout.isErrorEnabled = false
    }

    private fun showLoginError(message: String) {
        tvLoginError.text = message
        tvLoginError.visibility = View.VISIBLE
    }

    private fun hideLoginError() {
        tvLoginError.visibility = View.GONE
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, root).hide(WindowInsetsCompat.Type.ime())
    }

    companion object {
        private const val FAKE_MIN_PASSWORD_LENGTH = 6
        private const val FAKE_NETWORK_DELAY_MS = 1000L
        private const val FAKE_GOOGLE_DELAY_MS = 1200L
        private const val FAKE_GOOGLE_EMAIL = "google.user@gmail.com"
    }
}
package com.example.myproject.presentation.registration

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.example.myproject.R
import com.example.myproject.presentation.validation.InputValidator
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpStep1Activity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var btnBack: ImageButton
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilRepeatPassword: TextInputLayout
    private lateinit var etRepeatPassword: TextInputEditText
    private lateinit var cbAgree: CheckBox
    private lateinit var tvAgreement: TextView
    private lateinit var tvAgreementError: TextView
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_step1)

        findViews()
        applySystemBarsInsets()
        setupPasswordToggle(tilPassword, etPassword)
        setupPasswordToggle(tilRepeatPassword, etRepeatPassword)
        setupTextWatchers()
        setupClickListeners()
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        btnBack = findViewById(R.id.btnBack)
        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        tilRepeatPassword = findViewById(R.id.tilRepeatPassword)
        etRepeatPassword = findViewById(R.id.etRepeatPassword)
        cbAgree = findViewById(R.id.cbAgree)
        tvAgreement = findViewById(R.id.tvAgreement)
        tvAgreementError = findViewById(R.id.tvAgreementError)
        btnNext = findViewById(R.id.btnNext)
    }


    private fun applySystemBarsInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = maxOf(bars.bottom, ime.bottom)
            )
            insets
        }
    }


    private fun setupPasswordToggle(layout: TextInputLayout, editText: TextInputEditText) {
        var isVisible = false
        val typeface = editText.typeface

        fun render() {
            if (isVisible) {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                layout.setEndIconDrawable(R.drawable.ic_eye_on)
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                layout.setEndIconDrawable(R.drawable.ic_eye_off)
            }
            editText.typeface = typeface
            editText.setSelection(editText.text?.length ?: 0)
        }

        render()

        layout.setEndIconOnClickListener {
            isVisible = !isVisible
            render()
        }
    }


    private fun setupTextWatchers() {
        etEmail.onRealTextChange {
            clearError(tilEmail)
        }

        etPassword.onRealTextChange {
            clearError(tilPassword)
            // Пароль поменялся — старая ошибка «не совпадают» уже неактуальна
            clearError(tilRepeatPassword)
        }

        etRepeatPassword.onRealTextChange {
            clearError(tilRepeatPassword)
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvAgreementError.visibility = View.GONE
            }
        }
    }

    // Вызывает action, только если текст ДЕЙСТВИТЕЛЬНО изменился.
    // Смена глаза у пароля тоже дёргает слушатель, но текст при этом тот же.
    private fun TextInputEditText.onRealTextChange(action: () -> Unit) {
        var lastText = text?.toString().orEmpty()
        doAfterTextChanged { editable ->
            val current = editable?.toString().orEmpty()
            if (current != lastText) {
                lastText = current
                action()
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnNext.setOnClickListener { onNextClicked() }
    }

    private fun onNextClicked() {
        hideKeyboard()
        if (validateForm()) {
            openNextStep()
        }
    }

    private fun validateForm(): Boolean {
        val email = etEmail.text?.toString().orEmpty().trim()
        val password = etPassword.text?.toString().orEmpty()
        val repeatPassword = etRepeatPassword.text?.toString().orEmpty()

        var isValid = true

        if (!InputValidator.isEmailValid(email)) {
            showError(tilEmail, getString(R.string.error_email_invalid))
            isValid = false
        }

        if (!InputValidator.isPasswordLongEnough(password)) {
            showError(tilPassword, getString(R.string.error_password_short))
            isValid = false
        }

        if (password != repeatPassword) {
            showError(tilRepeatPassword, getString(R.string.error_passwords_mismatch))
            isValid = false
        }

        if (!cbAgree.isChecked) {
            tvAgreementError.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }

    private fun openNextStep() {
        // Заглушка до шага 4, когда появится SignUpStep2Activity.
        // Там будет startActivity БЕЗ finish(): так данные этого экрана сохранятся при возврате.
        Toast.makeText(this, R.string.signup_next_stub, Toast.LENGTH_SHORT).show()
    }

    private fun showError(layout: TextInputLayout, message: String) {
        layout.isErrorEnabled = true
        layout.error = message
    }

    private fun clearError(layout: TextInputLayout) {
        layout.error = null
        layout.isErrorEnabled = false
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, root).hide(WindowInsetsCompat.Type.ime())
    }
}

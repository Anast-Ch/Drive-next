package com.example.myproject.presentation.registration

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RadioGroup
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
import com.example.myproject.presentation.util.DateUtils
import com.example.myproject.presentation.validation.InputValidator
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate

class SignUpStep2Activity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var btnBack: ImageButton

    private lateinit var etLastName: TextInputEditText
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etMiddleName: TextInputEditText

    private lateinit var tilBirthDate: TextInputLayout
    private lateinit var etBirthDate: TextInputEditText

    private lateinit var rgGender: RadioGroup
    private lateinit var tvFormError: TextView
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_step2)

        findViews()
        applySystemBarsInsets()
        setupTextWatchers()
        setupClickListeners()
        restoreDatePickerListener()
        updateNextButtonState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        updateNextButtonState()
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        btnBack = findViewById(R.id.btnBack)

        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)

        tilBirthDate = findViewById(R.id.tilBirthDate)
        etBirthDate = findViewById(R.id.etBirthDate)

        rgGender = findViewById(R.id.rgGender)
        tvFormError = findViewById(R.id.tvFormError)
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

    private fun setupTextWatchers() {
        etLastName.doAfterTextChanged {
            hideFormError()
            updateNextButtonState()
        }

        etFirstName.doAfterTextChanged {
            hideFormError()
            updateNextButtonState()
        }

        etMiddleName.doAfterTextChanged {
            hideFormError()
        }

        etBirthDate.doAfterTextChanged {
            clearError(tilBirthDate)
            hideFormError()
            updateNextButtonState()
        }

        rgGender.setOnCheckedChangeListener { _, _ ->
            hideFormError()
            updateNextButtonState()
        }
    }

    private fun isGenderSelected(): Boolean {
        val selectedId = rgGender.checkedRadioButtonId

        return selectedId == R.id.rbMale || selectedId == R.id.rbFemale
    }

    private fun areRequiredFieldsFilled(): Boolean {
        val lastNameFilled = !etLastName.text.isNullOrBlank()
        val firstNameFilled = !etFirstName.text.isNullOrBlank()
        val birthDateFilled = !etBirthDate.text.isNullOrBlank()
        val genderSelected = isGenderSelected()

        return lastNameFilled &&
            firstNameFilled &&
            birthDateFilled &&
            genderSelected
    }

    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tilBirthDate.setStartIconOnClickListener {
            showBirthDatePicker()
        }

        etBirthDate.setOnClickListener {
            showBirthDatePicker()
        }

        btnNext.setOnClickListener {
            onNextClicked()
        }
    }

    private fun showBirthDatePicker() {
        val fragmentManager = supportFragmentManager

        if (fragmentManager.isStateSaved) {
            return
        }

        if (fragmentManager.findFragmentByTag(DATE_PICKER_TAG) != null) {
            return
        }

        hideKeyboard()

        val today = LocalDate.now()
        val todayUtcMillis = DateUtils.toUtcMillis(today)

        val selectedDate = DateUtils.parse(
            etBirthDate.text?.toString().orEmpty()
        )?.takeIf { date ->
            !date.isAfter(today)
        }

        val selectedUtcMillis = selectedDate?.let { date ->
            DateUtils.toUtcMillis(date)
        }

        val constraints = CalendarConstraints.Builder()
            .setEnd(todayUtcMillis)
            .setOpenAt(selectedUtcMillis ?: todayUtcMillis)
            .setValidator(
                DateValidatorPointBackward.before(todayUtcMillis)
            )
            .build()

        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.signup_birth_date_picker_title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(constraints)

        if (selectedUtcMillis != null) {
            builder.setSelection(selectedUtcMillis)
        }

        val picker = builder.build()

        attachBirthDatePickerListener(picker)
        picker.show(fragmentManager, DATE_PICKER_TAG)
    }

    private fun attachBirthDatePickerListener(
        picker: MaterialDatePicker<Long>
    ) {
        picker.clearOnPositiveButtonClickListeners()

        picker.addOnPositiveButtonClickListener { selectedUtcMillis ->
            etBirthDate.setText(
                DateUtils.formatUtcMillis(selectedUtcMillis)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreDatePickerListener() {
        val restoredPicker = supportFragmentManager
            .findFragmentByTag(DATE_PICKER_TAG) as? MaterialDatePicker<Long>

        if (restoredPicker != null) {
            attachBirthDatePickerListener(restoredPicker)
        }
    }

    private fun onNextClicked() {
        hideKeyboard()

        if (validateForm()) {
            openNextStep()
        }
    }

    private fun validateForm(): Boolean {
        hideFormError()
        clearError(tilBirthDate)

        if (!areRequiredFieldsFilled()) {
            tvFormError.visibility = View.VISIBLE
            return false
        }

        val birthDate = etBirthDate.text?.toString().orEmpty()

        if (!InputValidator.isBirthDateValid(birthDate)) {
            showError(
                tilBirthDate,
                getString(R.string.error_birth_date_invalid)
            )
            return false
        }

        return true
    }

    private fun openNextStep() {
        // SignUpStep3Activity будет создана на шаге 5.
        // Тогда здесь появится startActivity без finish().
        // На MainActivity с этого экрана не переходим.
        Toast.makeText(
            this,
            R.string.signup2_next_stub,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showError(layout: TextInputLayout, message: String) {
        layout.isErrorEnabled = true
        layout.error = message
    }

    private fun clearError(layout: TextInputLayout) {
        layout.error = null
        layout.isErrorEnabled = false
    }

    private fun hideFormError() {
        tvFormError.visibility = View.GONE
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, root)
            .hide(WindowInsetsCompat.Type.ime())
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"

        private const val DATE_PICKER_TAG = "birth_date_picker"
    }
}

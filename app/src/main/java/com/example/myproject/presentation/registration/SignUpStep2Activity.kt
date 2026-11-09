package com.example.myproject.presentation.registration

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.example.myproject.R
import com.example.myproject.presentation.util.DateUtils
import com.example.myproject.presentation.validation.InputValidator
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpStep2Activity : AppCompatActivity() {

    private lateinit var root: View
    private lateinit var btnBack: ImageButton
    private lateinit var tilLastName: TextInputLayout
    private lateinit var etLastName: TextInputEditText
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var etFirstName: TextInputEditText
    private lateinit var tilBirthDate: TextInputLayout
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var tvGenderLabel: TextView
    private lateinit var rgGender: RadioGroup
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var tvFormError: TextView
    private lateinit var btnNext: MaterialButton

    private var isRequiredHighlightActive = false

    private val errorColor: Int by lazy {
        ContextCompat.getColor(this, R.color.error_color)
    }

    private val labelColor: Int by lazy {
        ContextCompat.getColor(this, R.color.text_label_color)
    }

    private val errorStrokeColors: ColorStateList by lazy {
        ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()),
            intArrayOf(errorColor, errorColor)
        )
    }

    private val normalStrokeColors: ColorStateList by lazy {
        ContextCompat.getColorStateList(this, R.color.input_stroke_color_selector)!!
    }

    private val normalRadioColors: ColorStateList by lazy {
        ContextCompat.getColorStateList(this, R.color.radio_tint_selector)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_step2)

        isRequiredHighlightActive =
            savedInstanceState?.getBoolean(KEY_HIGHLIGHT_ACTIVE) ?: false

        findViews()
        applySystemBarsInsets()
        setupTextWatchers()
        setupTouchListeners()
        setupClickListeners()
        restoreDatePickerListener()
        updateNextButtonState()
        updateRequiredHighlight()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_HIGHLIGHT_ACTIVE, isRequiredHighlightActive)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateNextButtonState()
        updateRequiredHighlight()
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        btnBack = findViewById(R.id.btnBack)
        tilLastName = findViewById(R.id.tilLastName)
        etLastName = findViewById(R.id.etLastName)
        tilFirstName = findViewById(R.id.tilFirstName)
        etFirstName = findViewById(R.id.etFirstName)
        tilBirthDate = findViewById(R.id.tilBirthDate)
        etBirthDate = findViewById(R.id.etBirthDate)
        tvGenderLabel = findViewById(R.id.tvGenderLabel)
        rgGender = findViewById(R.id.rgGender)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
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
            updateRequiredHighlight()
            updateNextButtonState()
        }
        etFirstName.doAfterTextChanged {
            updateRequiredHighlight()
            updateNextButtonState()
        }
        etBirthDate.doAfterTextChanged {
            updateBirthDateError()
            updateRequiredHighlight()
            updateNextButtonState()
        }
        rgGender.setOnCheckedChangeListener { _, _ ->
            updateRequiredHighlight()
            updateNextButtonState()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListeners() {
        val touchListener = View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                activateRequiredHighlight()
            }
            false
        }

        etLastName.setOnTouchListener(touchListener)
        etFirstName.setOnTouchListener(touchListener)
        etBirthDate.setOnTouchListener(touchListener)
        rbMale.setOnTouchListener(touchListener)
        rbFemale.setOnTouchListener(touchListener)
    }

    private fun activateRequiredHighlight() {
        if (isRequiredHighlightActive) {
            return
        }
        isRequiredHighlightActive = true
        updateRequiredHighlight()
    }

    // Подсвечивает красным только пустые обязательные поля.
    // Как только поле заполнено, оно возвращается к обычному виду.
    private fun updateRequiredHighlight() {
        val lastNameEmpty = isRequiredHighlightActive && etLastName.text.isNullOrBlank()
        val firstNameEmpty = isRequiredHighlightActive && etFirstName.text.isNullOrBlank()
        val birthDateEmpty = isRequiredHighlightActive && etBirthDate.text.isNullOrBlank()
        val genderEmpty = isRequiredHighlightActive && !isGenderSelected()

        setFieldHighlighted(tilLastName, lastNameEmpty)
        setFieldHighlighted(tilFirstName, firstNameEmpty)
        setFieldHighlighted(tilBirthDate, birthDateEmpty)
        setGenderHighlighted(genderEmpty)

        tvFormError.visibility = if (lastNameEmpty || firstNameEmpty || birthDateEmpty || genderEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setFieldHighlighted(layout: TextInputLayout, highlighted: Boolean) {
        layout.setBoxStrokeColorStateList(
            if (highlighted) errorStrokeColors else normalStrokeColors
        )
    }

    private fun setGenderHighlighted(highlighted: Boolean) {
        val radioColors = if (highlighted) {
            ColorStateList.valueOf(errorColor)
        } else {
            normalRadioColors
        }
        rbMale.buttonTintList = radioColors
        rbFemale.buttonTintList = radioColors
        tvGenderLabel.setTextColor(if (highlighted) errorColor else labelColor)
    }

    private fun updateBirthDateError() {
        val text = etBirthDate.text?.toString().orEmpty()
        if (text.isNotBlank() && !InputValidator.isBirthDateValid(text)) {
            showError(tilBirthDate, getString(R.string.error_birth_date_invalid))
        } else {
            clearError(tilBirthDate)
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
            activateRequiredHighlight()
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

        val selectedUtcMillis = DateUtils.parse(
            etBirthDate.text?.toString().orEmpty()
        )?.let { date ->
            DateUtils.toUtcMillis(date)
        }

        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.signup_birth_date_picker_title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)

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
        updateBirthDateError()

        val birthDate = etBirthDate.text?.toString().orEmpty()

        return areRequiredFieldsFilled() && InputValidator.isBirthDateValid(birthDate)
    }

    private fun openNextStep() {
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        val nextIntent = Intent(this, SignUpStep3Activity::class.java)
            .putExtra(SignUpStep3Activity.EXTRA_EMAIL, email)
        startActivity(nextIntent)
    }

    private fun showError(layout: TextInputLayout, message: String) {
        if (layout.isErrorEnabled && layout.error?.toString() == message) return
        layout.isErrorEnabled = true
        layout.error = message
    }

    private fun clearError(layout: TextInputLayout) {
        if (!layout.isErrorEnabled) return
        layout.error = null
        layout.isErrorEnabled = false
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, root)
            .hide(WindowInsetsCompat.Type.ime())
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        private const val DATE_PICKER_TAG = "birth_date_picker"
        private const val KEY_HIGHLIGHT_ACTIVE = "key_highlight_active"
    }
}
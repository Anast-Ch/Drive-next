package com.example.myproject.presentation.registration

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.res.ColorStateList
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.BundleCompat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File

class SignUpStep3Activity : AppCompatActivity() {

    private enum class PhotoSlot { AVATAR, LICENSE, PASSPORT }

    private lateinit var root: View
    private lateinit var btnBack: ImageButton
    private lateinit var ivAvatar: ShapeableImageView
    private lateinit var ivAvatarAdd: ImageView
    private lateinit var tilLicenseNumber: TextInputLayout
    private lateinit var etLicenseNumber: TextInputEditText
    private lateinit var tilIssueDate: TextInputLayout
    private lateinit var etIssueDate: TextInputEditText
    private lateinit var btnUploadLicense: MaterialButton
    private lateinit var tvLicensePhotoStatus: TextView
    private lateinit var btnUploadPassport: MaterialButton
    private lateinit var tvPassportPhotoStatus: TextView
    private lateinit var tvFormError: TextView
    private lateinit var tvPhotosError: TextView
    private lateinit var btnNext: MaterialButton

    private var email: String = ""
    private var avatarUri: Uri? = null
    private var licensePhotoUri: Uri? = null
    private var passportPhotoUri: Uri? = null
    private var pendingSlot: PhotoSlot? = null
    private var pendingCameraUri: Uri? = null
    private var isRequiredHighlightActive = false

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoReceived(uri)
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            onPhotoReceived(uri)
        }
    }

    private val errorColor: Int by lazy {
        ContextCompat.getColor(this, R.color.error_color)
    }

    private val secondaryColor: Int by lazy {
        ContextCompat.getColor(this, R.color.text_secondary_color)
    }

    private val primaryColor: Int by lazy {
        ContextCompat.getColor(this, R.color.button_primary_background)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_step3)

        email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        restoreState(savedInstanceState)
        findViews()
        makeAvatarRound()
        applySystemBarsInsets()
        setupTextWatchers()
        setupTouchListeners()
        setupClickListeners()
        restoreDatePickerListener()

        avatarUri?.let { uri -> showAvatar(uri) }
        updateNextButtonState()
        updateRequiredHighlight()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_HIGHLIGHT_ACTIVE, isRequiredHighlightActive)
        outState.putParcelable(KEY_AVATAR_URI, avatarUri)
        outState.putParcelable(KEY_LICENSE_URI, licensePhotoUri)
        outState.putParcelable(KEY_PASSPORT_URI, passportPhotoUri)
        outState.putParcelable(KEY_PENDING_URI, pendingCameraUri)
        outState.putString(KEY_PENDING_SLOT, pendingSlot?.name)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateNextButtonState()
        updateRequiredHighlight()
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }
        isRequiredHighlightActive = savedInstanceState.getBoolean(KEY_HIGHLIGHT_ACTIVE, false)
        avatarUri = BundleCompat.getParcelable(savedInstanceState, KEY_AVATAR_URI, Uri::class.java)
        licensePhotoUri = BundleCompat.getParcelable(savedInstanceState, KEY_LICENSE_URI, Uri::class.java)
        passportPhotoUri = BundleCompat.getParcelable(savedInstanceState, KEY_PASSPORT_URI, Uri::class.java)
        pendingCameraUri = BundleCompat.getParcelable(savedInstanceState, KEY_PENDING_URI, Uri::class.java)
        pendingSlot = savedInstanceState.getString(KEY_PENDING_SLOT)?.let { name ->
            PhotoSlot.valueOf(name)
        }
    }

    private fun findViews() {
        root = findViewById(R.id.root)
        btnBack = findViewById(R.id.btnBack)
        ivAvatar = findViewById(R.id.ivAvatar)
        ivAvatarAdd = findViewById(R.id.ivAvatarAdd)
        tilLicenseNumber = findViewById(R.id.tilLicenseNumber)
        etLicenseNumber = findViewById(R.id.etLicenseNumber)
        tilIssueDate = findViewById(R.id.tilIssueDate)
        etIssueDate = findViewById(R.id.etIssueDate)
        btnUploadLicense = findViewById(R.id.btnUploadLicense)
        tvLicensePhotoStatus = findViewById(R.id.tvLicensePhotoStatus)
        btnUploadPassport = findViewById(R.id.btnUploadPassport)
        tvPassportPhotoStatus = findViewById(R.id.tvPassportPhotoStatus)
        tvFormError = findViewById(R.id.tvFormError)
        tvPhotosError = findViewById(R.id.tvPhotosError)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun makeAvatarRound() {
        ivAvatar.shapeAppearanceModel = ivAvatar.shapeAppearanceModel
            .toBuilder()
            .setAllCornerSizes(ShapeAppearanceModel.PILL)
            .build()
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
        etLicenseNumber.doAfterTextChanged {
            updateRequiredHighlight()
            updateNextButtonState()
        }
        etIssueDate.doAfterTextChanged {
            updateIssueDateError()
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

        etLicenseNumber.setOnTouchListener(touchListener)
        etIssueDate.setOnTouchListener(touchListener)
        btnUploadLicense.setOnTouchListener(touchListener)
        tvLicensePhotoStatus.setOnTouchListener(touchListener)
        btnUploadPassport.setOnTouchListener(touchListener)
        tvPassportPhotoStatus.setOnTouchListener(touchListener)
    }

    private fun activateRequiredHighlight() {
        if (isRequiredHighlightActive) {
            return
        }
        isRequiredHighlightActive = true
        updateRequiredHighlight()
    }

    private fun isLicenseNumberFilled(): Boolean {
        return etLicenseNumber.text?.length == LICENSE_NUMBER_LENGTH
    }

    private fun updateRequiredHighlight() {
        val active = isRequiredHighlightActive

        val licenseNumberMissing = active && !isLicenseNumberFilled()
        val issueDateMissing = active && etIssueDate.text.isNullOrBlank()
        val licensePhotoMissing = active && licensePhotoUri == null
        val passportPhotoMissing = active && passportPhotoUri == null

        setFieldHighlighted(tilLicenseNumber, licenseNumberMissing)
        setFieldHighlighted(tilIssueDate, issueDateMissing)

        renderPhotoBlock(
            button = btnUploadLicense,
            status = tvLicensePhotoStatus,
            uploaded = licensePhotoUri != null,
            highlighted = licensePhotoMissing
        )
        renderPhotoBlock(
            button = btnUploadPassport,
            status = tvPassportPhotoStatus,
            uploaded = passportPhotoUri != null,
            highlighted = passportPhotoMissing
        )

        tvFormError.visibility = if (licenseNumberMissing || issueDateMissing) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvPhotosError.visibility = if (licensePhotoMissing || passportPhotoMissing) {
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

    private fun renderPhotoBlock(
        button: MaterialButton,
        status: TextView,
        uploaded: Boolean,
        highlighted: Boolean
    ) {
        status.setText(if (uploaded) R.string.photo_uploaded else R.string.upload_photo)

        val textColor = when {
            highlighted -> errorColor
            uploaded -> primaryColor
            else -> secondaryColor
        }
        status.setTextColor(textColor)

        val controlColor = ColorStateList.valueOf(
            if (highlighted) errorColor else secondaryColor
        )
        button.strokeColor = controlColor
        button.iconTint = controlColor
    }

    private fun updateIssueDateError() {
        val text = etIssueDate.text?.toString().orEmpty()
        if (text.isNotBlank() && !InputValidator.isIssueDateValid(text)) {
            showError(tilIssueDate, getString(R.string.error_issue_date_invalid))
        } else {
            clearError(tilIssueDate)
        }
    }

    private fun areRequiredFieldsFilled(): Boolean {
        val licenseNumberFilled = isLicenseNumberFilled()
        val issueDateFilled = !etIssueDate.text.isNullOrBlank()
        val licensePhotoUploaded = licensePhotoUri != null
        val passportPhotoUploaded = passportPhotoUri != null

        return licenseNumberFilled &&
            issueDateFilled &&
            licensePhotoUploaded &&
            passportPhotoUploaded
    }

    private fun updateNextButtonState() {
        btnNext.isEnabled = areRequiredFieldsFilled()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ivAvatar.setOnClickListener { showPhotoSourceDialog(PhotoSlot.AVATAR) }
        ivAvatarAdd.setOnClickListener { showPhotoSourceDialog(PhotoSlot.AVATAR) }

        btnUploadLicense.setOnClickListener { showPhotoSourceDialog(PhotoSlot.LICENSE) }
        tvLicensePhotoStatus.setOnClickListener { showPhotoSourceDialog(PhotoSlot.LICENSE) }

        btnUploadPassport.setOnClickListener { showPhotoSourceDialog(PhotoSlot.PASSPORT) }
        tvPassportPhotoStatus.setOnClickListener { showPhotoSourceDialog(PhotoSlot.PASSPORT) }

        tilIssueDate.setStartIconOnClickListener {
            activateRequiredHighlight()
            showIssueDatePicker()
        }
        etIssueDate.setOnClickListener {
            showIssueDatePicker()
        }

        btnNext.setOnClickListener {
            onNextClicked()
        }
    }

    private fun showPhotoSourceDialog(slot: PhotoSlot) {
        val items = arrayOf(
            getString(R.string.photo_source_gallery),
            getString(R.string.photo_source_camera)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.photo_source_title)
            .setItems(items) { _, which ->
                pendingSlot = slot
                if (which == 0) {
                    openGallery()
                } else {
                    openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        pickPhoto.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun openCamera() {
        val outputUri = createCameraOutputUri()
        pendingCameraUri = outputUri
        try {
            takePhoto.launch(outputUri)
        } catch (_: ActivityNotFoundException) {
        }
    }

    private fun createCameraOutputUri(): Uri {
        val directory = File(cacheDir, CAMERA_DIRECTORY).apply { mkdirs() }
        val file = File(directory, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }

    private fun onPhotoReceived(uri: Uri) {
        when (pendingSlot) {
            PhotoSlot.AVATAR -> {
                avatarUri = uri
                showAvatar(uri)
            }
            PhotoSlot.LICENSE -> licensePhotoUri = uri
            PhotoSlot.PASSPORT -> passportPhotoUri = uri
            null -> return
        }
        pendingSlot = null

        updateRequiredHighlight()
        updateNextButtonState()
    }

    private fun showAvatar(uri: Uri) {
        try {
            val source = ImageDecoder.createSource(contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val maxSide = maxOf(info.size.width, info.size.height)
                decoder.setTargetSampleSize(maxOf(1, maxSide / AVATAR_MAX_SIZE_PX))
            }
            ivAvatar.setImageBitmap(bitmap)
        } catch (_: Exception) {
        }
    }

    private fun showIssueDatePicker() {
        val fragmentManager = supportFragmentManager

        if (fragmentManager.isStateSaved) {
            return
        }
        if (fragmentManager.findFragmentByTag(DATE_PICKER_TAG) != null) {
            return
        }

        hideKeyboard()

        val selectedUtcMillis = DateUtils.parse(
            etIssueDate.text?.toString().orEmpty()
        )?.let { date ->
            DateUtils.toUtcMillis(date)
        }

        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.signup_issue_date_picker_title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)

        if (selectedUtcMillis != null) {
            builder.setSelection(selectedUtcMillis)
        }

        val picker = builder.build()
        attachIssueDatePickerListener(picker)
        picker.show(fragmentManager, DATE_PICKER_TAG)
    }

    private fun attachIssueDatePickerListener(
        picker: MaterialDatePicker<Long>
    ) {
        picker.clearOnPositiveButtonClickListeners()
        picker.addOnPositiveButtonClickListener { selectedUtcMillis ->
            etIssueDate.setText(
                DateUtils.formatUtcMillis(selectedUtcMillis)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreDatePickerListener() {
        val restoredPicker = supportFragmentManager
            .findFragmentByTag(DATE_PICKER_TAG) as? MaterialDatePicker<Long>

        if (restoredPicker != null) {
            attachIssueDatePickerListener(restoredPicker)
        }
    }

    private fun onNextClicked() {
        hideKeyboard()
        if (validateForm()) {
            openNextStep()
        }
    }

    private fun validateForm(): Boolean {
        updateIssueDateError()

        val issueDate = etIssueDate.text?.toString().orEmpty()

        return areRequiredFieldsFilled() && InputValidator.isIssueDateValid(issueDate)
    }

    private fun openNextStep() {
        val nextIntent = Intent(this, CongratulationsActivity::class.java)
            .putExtra(CongratulationsActivity.EXTRA_EMAIL, email)
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

        private const val LICENSE_NUMBER_LENGTH = 10
        private const val AVATAR_MAX_SIZE_PX = 480
        private const val CAMERA_DIRECTORY = "camera"
        private const val DATE_PICKER_TAG = "issue_date_picker"

        private const val KEY_HIGHLIGHT_ACTIVE = "key_highlight_active"
        private const val KEY_AVATAR_URI = "key_avatar_uri"
        private const val KEY_LICENSE_URI = "key_license_uri"
        private const val KEY_PASSPORT_URI = "key_passport_uri"
        private const val KEY_PENDING_URI = "key_pending_uri"
        private const val KEY_PENDING_SLOT = "key_pending_slot"
    }
}

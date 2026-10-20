package com.example.myproject.presentation.onboarding

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myproject.R
import com.example.myproject.presentation.common.dpToPx

class OnboardingPageFragment : Fragment(R.layout.fragment_onboarding_page) {

    companion object {
        private const val ARG_IMAGE_RES = "arg_image_res"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_SUBTITLE = "arg_subtitle"
        private const val ARG_FILL_WIDTH = "arg_fill_width"
        private const val ARG_IMAGE_WIDTH_DP = "arg_image_width_dp"
        private const val ARG_IMAGE_OFFSET_DP = "arg_image_offset_dp"

        fun newInstance(
            imageRes: Int,
            title: String,
            subtitle: String,
            fillWidth: Boolean,
            imageWidthDp: Float = 0f,
            imageOffsetDp: Float = 0f
        ): OnboardingPageFragment {
            val fragment = OnboardingPageFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RES, imageRes)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_SUBTITLE, subtitle)
            args.putBoolean(ARG_FILL_WIDTH, fillWidth)
            args.putFloat(ARG_IMAGE_WIDTH_DP, imageWidthDp)
            args.putFloat(ARG_IMAGE_OFFSET_DP, imageOffsetDp)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val imageRes = args.getInt(ARG_IMAGE_RES)
        val title = args.getString(ARG_TITLE)
        val subtitle = args.getString(ARG_SUBTITLE)
        val fillWidth = args.getBoolean(ARG_FILL_WIDTH)
        val imageWidthDp = args.getFloat(ARG_IMAGE_WIDTH_DP)
        val imageOffsetDp = args.getFloat(ARG_IMAGE_OFFSET_DP)

        val ivImage = view.findViewById<ImageView>(R.id.ivOnboardingImage)
        val tvTitle = view.findViewById<TextView>(R.id.tvOnboardingTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvOnboardingSubtitle)

        ivImage.setImageResource(imageRes)
        tvTitle.text = title
        tvSubtitle.text = subtitle

        val context = requireContext()
        val params = ivImage.layoutParams

        if (fillWidth) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            ivImage.layoutParams = params
            ivImage.scaleType = ImageView.ScaleType.FIT_CENTER
            ivImage.translationX = 0f
        } else {
            params.width = context.dpToPx(imageWidthDp)
            ivImage.layoutParams = params
            ivImage.scaleType = ImageView.ScaleType.FIT_CENTER
            ivImage.translationX = context.dpToPx(imageOffsetDp).toFloat()
        }
    }
}

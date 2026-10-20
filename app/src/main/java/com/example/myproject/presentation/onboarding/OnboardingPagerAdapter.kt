package com.example.myproject.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myproject.R

class OnboardingPagerAdapter(
    private val activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private val pagesCount = 3

    override fun getItemCount(): Int = pagesCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPageFragment.newInstance(
                imageRes = R.drawable.img_onboarding_1,
                title = activity.getString(R.string.onboarding_title_1),
                subtitle = activity.getString(R.string.onboarding_subtitle_1),
                fillWidth = true
            )
            1 -> OnboardingPageFragment.newInstance(
                imageRes = R.drawable.img_onboarding_2,
                title = activity.getString(R.string.onboarding_title_2),
                subtitle = activity.getString(R.string.onboarding_subtitle_2),
                fillWidth = false,
                imageWidthDp = 341f,
                imageOffsetDp = 25f
            )
            else -> OnboardingPageFragment.newInstance(
                imageRes = R.drawable.img_onboarding_3,
                title = activity.getString(R.string.onboarding_title_3),
                subtitle = activity.getString(R.string.onboarding_subtitle_3),
                fillWidth = true
            )
        }
    }
}

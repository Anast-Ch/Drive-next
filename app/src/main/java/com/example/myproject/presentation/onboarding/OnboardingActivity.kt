package com.example.myproject.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myproject.MainActivity
import com.example.myproject.R
import com.example.myproject.data.local.OnboardingPreferences

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button
    private lateinit var dots: List<View>
    private lateinit var onboardingPreferences: OnboardingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        onboardingPreferences = OnboardingPreferences(this)

        viewPager = findViewById(R.id.vpOnboarding)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        dots = listOf(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3)
        )

        viewPager.adapter = OnboardingPagerAdapter(this)

        updateDots(0)
        updateButtonText(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                updateButtonText(position)
            }
        })

        btnSkip.setOnClickListener {
            finishOnboarding()
        }

        btnNext.setOnClickListener {
            val lastPageIndex = dots.size - 1
            if (viewPager.currentItem < lastPageIndex) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                finishOnboarding()
            }
        }
    }

    private fun updateDots(activePosition: Int) {
        dots.forEachIndexed { index, dot ->
            val isActive = index == activePosition

            val drawableRes = if (isActive) R.drawable.dot_active else R.drawable.dot_inactive
            dot.setBackgroundResource(drawableRes)

            val widthRes = if (isActive) R.dimen.dot_width_active else R.dimen.dot_width_inactive
            val params = dot.layoutParams
            params.width = resources.getDimensionPixelSize(widthRes)
            dot.layoutParams = params
        }
    }

    private fun updateButtonText(position: Int) {
        val lastPageIndex = dots.size - 1
        btnNext.text = if (position == lastPageIndex) {
            getString(R.string.btn_start)
        } else {
            getString(R.string.btn_next)
        }
    }

    private fun finishOnboarding() {
        btnNext.isEnabled = false
        btnSkip.isEnabled = false

        onboardingPreferences.setOnboardingCompleted()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

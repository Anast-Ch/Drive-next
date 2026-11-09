package com.example.myproject.presentation.validation

import com.example.myproject.presentation.util.DateUtils
import java.time.LocalDate

object InputValidator {

    const val MIN_PASSWORD_LENGTH = 6

    private val EMAIL_REGEX = Regex(
        "[A-Za-z0-9._%+\\-]+@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.(com|ru)",
        RegexOption.IGNORE_CASE
    )

    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    fun isPasswordLongEnough(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    fun isBirthDateValid(text: String): Boolean {
        val date = DateUtils.parse(text) ?: return false
        return !date.isAfter(LocalDate.now())
    }

    fun isIssueDateValid(text: String): Boolean {
        val date = DateUtils.parse(text) ?: return false
        return !date.isAfter(LocalDate.now())
    }
}
package com.example.myproject.presentation.validation

object InputValidator {

    const val MIN_PASSWORD_LENGTH = 6

    // name@domain.com или name@domain.ru
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
}

package com.example.myproject.presentation.no_connection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myproject.data.local.NetworkConnectivityChecker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoConnectionViewModel(application: Application) : AndroidViewModel(application) {

    private val connectivityChecker = NetworkConnectivityChecker(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _connectionResult = MutableLiveData<Boolean>()
    val connectionResult: LiveData<Boolean> = _connectionResult

    companion object {
        private const val CHECK_DELAY_MILLIS = 600L
    }

    fun checkConnection() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true
            delay(CHECK_DELAY_MILLIS)
            val isConnected = connectivityChecker.isConnected()
            _isLoading.value = false
            _connectionResult.value = isConnected
        }
    }
}

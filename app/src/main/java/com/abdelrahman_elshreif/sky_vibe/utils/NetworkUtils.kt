package com.abdelrahman_elshreif.sky_vibe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkUtils private constructor(private val context: Context) {

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    init {
        initializeNetworkAvailability()
        registerNetworkCallback()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: NetworkUtils? = null

        fun getInstance(context: Context): NetworkUtils {
            return instance ?: synchronized(this) {
                instance ?: NetworkUtils(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun initializeNetworkAvailability() {
        _isNetworkAvailable.value = checkNetworkAvailability()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun registerNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    _isNetworkAvailable.value = false
                }
            })
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun checkNetworkAvailability(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }
}
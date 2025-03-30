package com.abdelrahman_elshreif.sky_vibe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.core.os.ConfigurationCompat
import java.util.Locale

object LanguageUtil {
    @SuppressLint("ObsoleteSdkInt")
    fun setLocale(context: Context?, languageCode: String): Context {
        val locale = Locale(languageCode)
        val config = Configuration(context?.resources?.configuration)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return context!!.createConfigurationContext(config)
        } else {
            Locale.setDefault(locale)
            config.locale = locale
            config.setLayoutDirection(locale)
            context?.resources?.updateConfiguration(config, context.resources.displayMetrics)
        }
        return context!!
    }

    fun getCurrentLanguage(context: Context): String {
        return ConfigurationCompat
            .getLocales(context.resources.configuration)
            .get(0)
            ?.language
            ?: "en"
    }
}
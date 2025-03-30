package com.abdelrahman_elshreif.sky_vibe

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.RetrofitHelper
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import androidx.lifecycle.lifecycleScope
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var locationUtilities: LocationUtilities

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // initializing ViewModels
        locationUtilities = LocationUtilities(this)
        val homeFactory = HomeViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice),
                ForecastingLocalDataSource()
            ),
            locationUtilities
        )
        val settingFactory = SettingViewModelFactory(SettingDataStore(this))
        val settingViewModel: SettingViewModel by viewModels { settingFactory }
        val homeViewModel: HomeViewModel by viewModels { homeFactory }
        // End initializing ViewModels
        lifecycleScope.launch {
            settingViewModel.languageChangeEvent.collect { languageCode ->
                SettingDataStore(this@MainActivity).saveLanguageToSharedPrefs(languageCode)
                recreate()
            }
        }
        setContent {
            SkyVibeApp(homeViewModel, settingViewModel)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase ?: return super.attachBaseContext(null)
        val languageCode = SettingDataStore(context).getLanguageFromSharedPrefs()
        super.attachBaseContext(LanguageUtil.setLocale(context, languageCode))
    }
}
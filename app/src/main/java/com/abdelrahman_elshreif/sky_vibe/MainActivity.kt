package com.abdelrahman_elshreif.sky_vibe

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.RetrofitHelper
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeDatabase
import com.abdelrahman_elshreif.sky_vibe.data.remote.OSMHelper
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModel
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModelFactory
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
                ForecastingRemoteDataSource(RetrofitHelper.apiservice, OSMHelper.apiService),
                SkyVibeLocalDataSource(
                    SkyVibeDatabase.getInstance(this).getFavouriteLocationDao(),
                    SkyVibeDatabase.getInstance(this).getAlertsDao()
                )
            ),
            locationUtilities,
            SettingDataStore(this)
        )
        val favouriteFactory = FavouriteViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice, OSMHelper.apiService),
                SkyVibeLocalDataSource(
                    SkyVibeDatabase.getInstance(this).getFavouriteLocationDao(),
                    SkyVibeDatabase.getInstance(this).getAlertsDao()
                )
            ),
            locationUtilities
        )

        val favWeatherDetailFactory = FavouriteWeatherDetailsViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice, OSMHelper.apiService),
                SkyVibeLocalDataSource(
                    SkyVibeDatabase.getInstance(this).getFavouriteLocationDao(),
                    SkyVibeDatabase.getInstance(this).getAlertsDao()
                )
            ),
            locationUtilities, SettingDataStore(this)
        )
        val alarmViewModelFactory = AlarmViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice, OSMHelper.apiService),
                SkyVibeLocalDataSource(
                    SkyVibeDatabase.getInstance(this).getFavouriteLocationDao(),
                    SkyVibeDatabase.getInstance(this).getAlertsDao()
                )
            ),
            WorkManager.getInstance(this)
        )
        val settingFactory = SettingViewModelFactory(SettingDataStore(this))
        val settingViewModel: SettingViewModel by viewModels { settingFactory }
        val homeViewModel: HomeViewModel by viewModels { homeFactory }
        val favouriteViewModel: FavouriteViewModel by viewModels { favouriteFactory }
        val favWeatherDetailViewModel: FavouriteWeatherDetailsViewModel by viewModels { favWeatherDetailFactory }
        val alarmViewModel: AlarmViewModel by viewModels { alarmViewModelFactory }
        // End initializing ViewModels
        lifecycleScope.launch {
            settingViewModel.languageChangeEvent.collect { languageCode ->
                SettingDataStore(this@MainActivity).saveLanguageToSharedPrefs(languageCode)
                recreate()
            }
        }
        setContent {
            SkyVibeApp(
                homeViewModel,
                settingViewModel,
                favouriteViewModel,
                favWeatherDetailViewModel,
                alarmViewModel,
                locationUtilities = locationUtilities
            )
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase ?: return super.attachBaseContext(null)
        val languageCode = SettingDataStore(context).getLanguageFromSharedPrefs()
        super.attachBaseContext(LanguageUtil.setLocale(context, languageCode))
    }
}
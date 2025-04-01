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
import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeDatabase
import com.abdelrahman_elshreif.sky_vibe.data.remote.OSMApiServices
import com.abdelrahman_elshreif.sky_vibe.data.remote.OSMHelper
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
                ForecastingRemoteDataSource(RetrofitHelper.apiservice,OSMHelper.apiService),
                SkyVibeLocalDataSource(SkyVibeDatabase.getInstance(this).getFavouriteLocationDao())
            ),
            locationUtilities,
            SettingDataStore(this)
        )
        val favouriteFactory = FavouriteViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice,OSMHelper.apiService),
                SkyVibeLocalDataSource(SkyVibeDatabase.getInstance(this).getFavouriteLocationDao())
            )
        )
        val settingFactory = SettingViewModelFactory(SettingDataStore(this))
        val settingViewModel: SettingViewModel by viewModels { settingFactory }
        val homeViewModel: HomeViewModel by viewModels { homeFactory }
        val favouriteViewModel: FavouriteViewModel by viewModels { favouriteFactory }
        // End initializing ViewModels
        lifecycleScope.launch {
            settingViewModel.languageChangeEvent.collect { languageCode ->
                SettingDataStore(this@MainActivity).saveLanguageToSharedPrefs(languageCode)
                recreate()
            }
        }
        setContent {
            SkyVibeApp(homeViewModel, settingViewModel, favouriteViewModel, locationUtilities = locationUtilities)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase ?: return super.attachBaseContext(null)
        val languageCode = SettingDataStore(context).getLanguageFromSharedPrefs()
        super.attachBaseContext(LanguageUtil.setLocale(context, languageCode))
    }
}
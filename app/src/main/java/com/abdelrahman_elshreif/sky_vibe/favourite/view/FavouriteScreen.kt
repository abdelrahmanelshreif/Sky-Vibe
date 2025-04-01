package com.abdelrahman_elshreif.sky_vibe.favourite.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel

@Composable
fun FavouriteScreen(favViewModel: FavouriteViewModel, navController: NavController) {
    MapScreen(favViewModel)
}
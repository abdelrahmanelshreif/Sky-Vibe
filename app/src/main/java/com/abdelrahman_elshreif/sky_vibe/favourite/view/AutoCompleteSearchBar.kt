package com.abdelrahman_elshreif.sky_vibe.favourite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel

@Composable
fun AutoCompleteSearchBar(
    viewModel: FavouriteViewModel,
    onSuggestionClick: (NominatimLocation) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val suggestedLocations = viewModel.locationSuggestions.collectAsState()

    LaunchedEffect (query){
        if (query.length > 2) {
            viewModel.searchLocations(query)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Search for your location...") }
        )

        if (query.isNotEmpty()&& suggestedLocations.value.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(suggestedLocations.value) { suggestion ->
                    Text(
                        suggestion.displayName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
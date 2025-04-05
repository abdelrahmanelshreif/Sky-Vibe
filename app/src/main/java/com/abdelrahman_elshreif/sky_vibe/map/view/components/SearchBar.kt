package com.abdelrahman_elshreif.sky_vibe.map.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.map.model.SearchBarEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.SearchBarState

@Composable
fun SearchBar(
    state: SearchBarState,
    onEvent: (SearchBarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { query ->
                onEvent(SearchBarEvent.OnQueryChanged(query))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_for_location)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )

        if (state.suggestedLocations.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(state.suggestedLocations) { location ->
                    SuggestionItem(
                        location = location,
                        onClick = {
                            onEvent(SearchBarEvent.OnSuggestionSelected(location))
                        }
                    )
                }
            }
        }
    }
}
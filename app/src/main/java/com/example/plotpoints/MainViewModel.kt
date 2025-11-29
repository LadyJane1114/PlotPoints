package com.example.plotpoints


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    val placeAutocomplete = PlaceAutocomplete.create()

    private val _searchResults = MutableLiveData<List<PlaceAutocompleteSuggestion>>()
    val searchResults: LiveData<List<PlaceAutocompleteSuggestion>> = _searchResults

    private val _searchError = MutableLiveData<String>()
    val searchError: LiveData<String> = _searchError

    private val _selectedPlace = MutableLiveData<PlaceAutocompleteResult>()
    val selectedPlace: LiveData<PlaceAutocompleteResult> = _selectedPlace

    fun search(query: String) {
        viewModelScope.launch {
            val response = placeAutocomplete.suggestions(query = query)

            response.onValue { suggestions ->
                // Update LiveData so UI can observe
                _searchResults.value = suggestions
            }.onError { e ->
                Log.e("MainViewModel", "Search error", e)
                _searchError.value = "Failed to fetch suggestions"
            }
        }
    }

    fun selectSuggestion(suggestion: PlaceAutocompleteSuggestion) {
        viewModelScope.launch {
            val selectionResponse = placeAutocomplete.select(suggestion)

            selectionResponse.onValue { result ->
                // result is PlaceAutocompleteResult
                _selectedPlace.value = result
            }.onError { e ->
                _searchError.value = e.message ?: "Error selecting place"
            }
        }
    }

}

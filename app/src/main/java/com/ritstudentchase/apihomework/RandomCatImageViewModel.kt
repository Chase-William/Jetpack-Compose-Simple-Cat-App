package com.ritstudentchase.apihomework

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class RandomCatImageViewModel: ViewModel() {
    private val cats = mutableStateListOf<RandomCatImageModel>()
    var errorMsg: String by mutableStateOf("")

    fun getCatList(): List<RandomCatImageModel> = cats

    /**
     * Gets a random cat image and inserts it into the front of the list
     */
    fun getRandomCatImage() {
        // for async via coroutines
        viewModelScope.launch {
            Log.d("Coroutine", "Launching getRandomCatImage Coroutine")
            val apiService = CatAPIService.getInstance()
            try {
                cats.addAll(apiService.getRandomCatImage())
                // Log.d("Cats Collection", "Count: ${cats.count()}")
            }
            catch (ex: Exception) {
                errorMsg = ex.localizedMessage ?: ""
            }
        }
    }
}
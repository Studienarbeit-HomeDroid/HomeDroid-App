package com.homedroidv2.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel zum Managen des Bottom Sheets in der App.
 */

class BottomSheetViewModel : ViewModel() {

    /**
     * Speichet den aktullen Status de aufgerufener Bottom Sheet.
     * */

    var openBottomSheet = mutableStateOf(false)
        private set

    /**
     * Aktualisiert den Status des Bottom Sheets.
     */
    fun toggleBottomSheet() {
        openBottomSheet.value = !openBottomSheet.value
    }

    /**
     * Gibt den aktuellen Status des Bottom Sheets zurück.
     */
    fun getBottomSheetValue(): Boolean {
        return openBottomSheet.value
    }
}
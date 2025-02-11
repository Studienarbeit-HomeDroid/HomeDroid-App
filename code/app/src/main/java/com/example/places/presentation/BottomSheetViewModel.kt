import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel to manage the state of a ModalBottomSheet.
 */

class BottomSheetViewModel : ViewModel() {

    /** Mutable state to track whether the bottom sheet is open or closed. */

    var openBottomSheet = mutableStateOf(false)
        private set

    fun toggleBottomSheet() {
        openBottomSheet.value = !openBottomSheet.value
    }

    fun getBottomSheetValue(): Boolean {
        return openBottomSheet.value
    }
}
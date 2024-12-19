import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel

class BottomSheetViewModel : ViewModel() {
    var openBottomSheet = mutableStateOf(false)
        private set

    fun toggleBottomSheet() {
        openBottomSheet.value = !openBottomSheet.value
    }

    fun getBottomSheetValue(): Boolean
    {
        return openBottomSheet.value
    }
}
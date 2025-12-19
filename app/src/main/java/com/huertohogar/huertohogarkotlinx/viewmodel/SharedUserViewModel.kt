package com.huertohogar.huertohogarkotlinx.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.data.model.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class SharedUserViewModel(application: Application) : AndroidViewModel(application) {

    private val _formData = MutableStateFlow<FormModel?>(null)
    val formData = _formData.asStateFlow()

    private val _popupChecked = MutableStateFlow(false)
    val popupChecked = _popupChecked.asStateFlow()

    private val _showWelcomePopup = MutableStateFlow(true)
    val showWelcomePopup = _showWelcomePopup.asStateFlow()

    fun saveFormData(data: FormModel) {
        _formData.value = data
    }

    fun onProfilePictureTaken(bitmap: Bitmap?) {
        bitmap ?: return
        val file = File(getApplication<Application>().cacheDir, "profile_picture.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        _formData.update { it?.copy(profileImageUri = file.toURI().toString()) ?: FormModel(profileImageUri = file.toURI().toString()) }
    }

    fun deleteProfilePicture() {
        val currentUri = _formData.value?.profileImageUri
        if (currentUri != null) {
            try {
                val file = File(Uri.parse(currentUri).path!!)
                if (file.exists()) file.delete()
            } catch (e: Exception) { /* Ignorar error si el archivo no existe */ }
        }
        _formData.update { it?.copy(profileImageUri = null) }
    }

    fun updateUserDataFromDto(userDto: UserDto) {
        _formData.update { currentData ->
            currentData?.copy(
                nombre = userDto.username,
                email = userDto.email
            ) ?: FormModel(
                nombre = userDto.username,
                email = userDto.email
            )
        }
    }

    fun setPopupChecked(isChecked: Boolean) {
        _popupChecked.value = isChecked
    }

    fun handlePopupDismissal(shouldNeverShowAgain: Boolean, navigateToProfile: () -> Unit) {
        if (shouldNeverShowAgain) {
            // Aquí guardarías en DataStore que el usuario no quiere ver más el popup
        }
        if (navigateToProfile != {}) {
            navigateToProfile()
        }
        _showWelcomePopup.value = false
    }

    fun dismissWelcomePopup() {
        _showWelcomePopup.value = false
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SharedUserViewModel(application) as T
        }
    }
}
package com.huertohogar.huertohogarkotlinx.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.huertohogar.huertohogarkotlinx.data.local.AppDatabase
import com.huertohogar.huertohogarkotlinx.data.local.AppSettingsDataStore
import com.huertohogar.huertohogarkotlinx.data.model.FormModel
import com.huertohogar.huertohogarkotlinx.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class SharedUserViewModel(application: Application, private val repository: UserRepository) : AndroidViewModel(application) {

    private val _formData = MutableStateFlow<FormModel?>(null)
    val formData: StateFlow<FormModel?> = _formData.asStateFlow()

    private val _showWelcomePopup = MutableStateFlow(false)
    val showWelcomePopup: StateFlow<Boolean> = _showWelcomePopup.asStateFlow()

    private val _popupChecked = MutableStateFlow(false)
    val popupChecked: StateFlow<Boolean> = _popupChecked.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFormData().collect { formModel ->
                _formData.value = formModel
            }
        }
        checkPopupStatus()
    }

    private fun checkPopupStatus() {
        viewModelScope.launch {
            repository.hasSeenIntro().collect { hasSeen ->
                if (!hasSeen) {
                    _showWelcomePopup.value = true
                }
            }
        }
    }

    fun handlePopupDismissal(shouldNeverShowAgain: Boolean, navigateToProfile: () -> Unit) {
        viewModelScope.launch {
            _showWelcomePopup.value = false
            if (shouldNeverShowAgain) {
                repository.setSeenIntro(true)
            }
            navigateToProfile()
        }
    }

    fun setPopupChecked(checked: Boolean) {
        _popupChecked.value = checked
    }

    fun saveFormData(data: FormModel) {
        viewModelScope.launch {
            repository.saveFormData(data)
        }
    }

    // --- LÓGICA FINAL Y ROBUSTA PARA FOTO DE PERFIL ---

    fun onProfilePictureTaken(bitmap: Bitmap?) {
        bitmap ?: return
        viewModelScope.launch {
            // 1. Guardar la nueva foto en almacenamiento y obtener su URI.
            val newImageUri = saveBitmapToInternalStorage(bitmap)

            // 2. Obtener los datos MÁS RECIENTES directamente de la base de datos.
            val currentData = repository.getFormData().first() ?: FormModel()

            // 3. Crear el modelo actualizado, preservando los datos existentes.
            val updatedUserData = currentData.copy(profileImageUri = newImageUri.toString())

            // 4. Guardar el objeto completo en la base de datos.
            repository.saveFormData(updatedUserData)
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            val currentData = repository.getFormData().first() ?: return@launch

            currentData.profileImageUri?.let {
                try {
                    val file = File(Uri.parse(it).path!!)
                    if (file.exists()) file.delete()
                } catch (_: Exception) { /* Ignorar si falla */ }
            }

            val updatedUserData = currentData.copy(profileImageUri = null)
            repository.saveFormData(updatedUserData)
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri {
        val context = getApplication<Application>().applicationContext
        val wrapper = context.getDir("images", Context.MODE_PRIVATE)
        val file = File(wrapper, "${UUID.randomUUID()}.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        return Uri.fromFile(file)
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getDatabase(application)
                    val userDao = database.userDao()
                    val dataStore = AppSettingsDataStore(application)
                    val repository = UserRepository(userDao, dataStore)
                    return SharedUserViewModel(application, repository) as T
                }
            }
        }
    }
}
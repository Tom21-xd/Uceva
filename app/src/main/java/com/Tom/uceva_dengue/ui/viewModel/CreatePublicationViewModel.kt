package com.Tom.uceva_dengue.ui.viewModel

import RetrofitClient
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Service.PublicationService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CreatePublicationViewModel() : ViewModel() {

    fun createPost(
        context: Context,
        title: String,
        description: String,
        userId: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
                val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                val userIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

                val imagePart = imageUri?.let {
                    val file = File(it.path ?: return@let null)
                    MultipartBody.Part.createFormData(
                        name = "imagen",
                        filename = file.name,
                        body = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    )
                }

                val response = RetrofitClient.publicationService.createPublication(
                    titulo = titlePart,
                    descripcion = descriptionPart,
                    imagen = imagePart ?: return@launch onError("La imagen es requerida"),
                    usuarioId = userIdPart
                )

                onSuccess()

            } catch (e: Exception) {
                Log.e("CreatePublicationViewModel", "Error al crear la publicación", e)
                onError("Error al crear la publicación: ${e.message}")
            }
        }
    }
}
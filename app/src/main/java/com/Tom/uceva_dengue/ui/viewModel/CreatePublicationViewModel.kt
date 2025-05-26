package com.Tom.uceva_dengue.ui.viewModel

import RetrofitClient
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Service.PublicationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CreatePublicationViewModel : ViewModel() {

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
                if (title.isBlank() || description.isBlank() || userId.isBlank()) {
                    onError("Todos los campos son obligatorios.")
                    return@launch
                }

                val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
                val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                val userIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)

                val imagePart = imageUri?.let { uri ->
                    try {
                        val file = copyUriToTempFile(context, uri)
                        MultipartBody.Part.createFormData(
                            "imagen",
                            file.name,
                            RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
                        )
                    } catch (e: Exception) {
                        onError("Error al procesar la imagen: ${e.message}")
                        return@launch
                    }
                } ?: run {
                    onError("La imagen es requerida")
                    return@launch
                }

                val response = RetrofitClient.publicationService.createPublication(
                    titulo = titlePart,
                    descripcion = descriptionPart,
                    imagen = imagePart,
                    usuarioId = userIdPart
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    onError("Error en la publicación: $errorBody")
                }

            } catch (e: Exception) {
                Log.e("CreatePublicationViewModel", "Error al crear la publicación", e)
                onError("Error al crear la publicación: ${e.message}")
            }
        }
    }

    private suspend fun copyUriToTempFile(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir).apply {
                deleteOnExit()
            }
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("CreatePublicationViewModel", "Error al copiar el archivo URI", e)
            throw e
        }
    }
}
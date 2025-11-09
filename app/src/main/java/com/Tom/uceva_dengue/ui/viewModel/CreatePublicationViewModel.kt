package com.Tom.uceva_dengue.ui.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
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
        categoriaId: Int? = null,
        etiquetasIds: List<Int>? = null,
        prioridad: String? = null,
        fijada: Boolean = false,
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

                // Nuevos parámetros opcionales
                val categoriaPart = categoriaId?.let {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString())
                }

                val etiquetasPart = etiquetasIds?.let {
                    if (it.isNotEmpty()) {
                        RequestBody.create("text/plain".toMediaTypeOrNull(), it.joinToString(","))
                    } else null
                }

                val prioridadPart = prioridad?.let {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), it)
                }

                val fijadaPart = RequestBody.create("text/plain".toMediaTypeOrNull(), fijada.toString())

                // Obtener ubicación automáticamente
                val location = getCurrentLocation(context)
                val latitudPart = location?.first?.let {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString())
                }
                val longitudPart = location?.second?.let {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString())
                }

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
                    usuarioId = userIdPart,
                    categoriaId = categoriaPart,
                    etiquetasIds = etiquetasPart,
                    prioridad = prioridadPart,
                    fijada = fijadaPart,
                    latitud = latitudPart,
                    longitud = longitudPart
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

    /**
     * Obtiene la ubicación actual del dispositivo usando coroutines
     * Retorna (latitud, longitud) o null si no está disponible
     */
    private suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            // Verificar permisos
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.w("CreatePublicationViewModel", "Permisos de ubicación no concedidos")
                return@withContext null
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Intentar obtener la última ubicación conocida
            val providers = locationManager.getProviders(true)
            var bestLocation: android.location.Location? = null

            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }

            return@withContext bestLocation?.let {
                Pair(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            Log.e("CreatePublicationViewModel", "Error al obtener ubicación", e)
            return@withContext null
        }
    }
}
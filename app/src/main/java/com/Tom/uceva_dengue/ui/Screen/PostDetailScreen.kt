package com.Tom.uceva_dengue.ui.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.Tom.uceva_dengue.Data.Model.PublicationModel

@Composable
fun PostDetailScreen(publicacion: PublicationModel) {
    val imageUrl = "https://api.prometeondev.com/api/image/getImage/${publicacion.IMAGEN_PUBLICACION}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen destacada
        AsyncImage(
            model = imageUrl,
            contentDescription = "Imagen de la publicación",
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = publicacion.TITULO_PUBLICACION,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = publicacion.DESCRIPCION_PUBLICACION,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = Color(0xFF333333),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👤 ${publicacion.NOMBRE_USUARIO}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = publicacion.FECHA_PUBLICACION,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

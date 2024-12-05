package com.Tom.uceva_dengue.ui.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Tom.uceva_dengue.ui.Components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen (){
    Column {
        NotificationList(
            notifications = listOf(
                NotificationItem("Notificación 1", "Este es el contenido de la notificación 1.", "04/04/2024"),
                NotificationItem("Notificación 2", "Este es el contenido de la notificación 2.", "04/04/2024"),
                NotificationItem("Notificación 3", "Este es el contenido de la notificación 3.", "04/04/2024"),
                NotificationItem("Notificación 4", "Este es el contenido de la notificación 4.", "04/04/2024")
            ),
            contentPadding = PaddingValues(10.dp) // Pasar el padding a la lista
        )
    }

}
data class NotificationItem(
    val title: String,
    val content: String,
    val date: String
)

@Composable
fun NotificationList(notifications: List<NotificationItem>, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding), // Aplicar el padding a la lista de notificaciones
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(notifications) { notification ->
            NotificationCard(notification)
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = notification.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = notification.content)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Publicado", fontSize = 12.sp, color = Color.Gray)
                Text(text = notification.date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    NotificationScreen()
}
package com.example.gestiondereservas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.theme.color

@Composable
fun AreaChip(area: Area) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(area.color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(area.color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = area.nombre,
            style = MaterialTheme.typography.labelSmall,
            color = area.color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
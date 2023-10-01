package ua.polytech.testingtask.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.polytech.testingtask.ui.theme.md_theme_light_onSurfaceVariant

@Composable
fun ErrorScreen(
    errorMessage: String,
    onTryAgainClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = md_theme_light_onSurfaceVariant.copy(alpha = 0.8F),
                    shape = RoundedCornerShape(20)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 24.sp,
                modifier = Modifier.padding(24.dp)
            )

            Button(
                onClick = { onTryAgainClick() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Try Again",
                    fontSize = 24.sp
                )
            }
        }
    }

}
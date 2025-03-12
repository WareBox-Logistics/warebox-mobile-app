package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.presentation.theme.Create
import com.example.lp_logistics.presentation.theme.LightCreme
import androidx.compose.material3.Text


@Composable
fun SlimButton(
    onClick: () -> Unit,
    iconRes: Int,
    iconColor: Color,
    primaryText: String,
    secondaryText: String? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = Create,
    textColor: Color = Color(0xFF009045)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .height(50.dp)
            .background(shape = RoundedCornerShape(10.dp), color = containerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = "Leading icon",
                tint = iconColor,
                modifier = Modifier
                    .size(24.dp) // Ensure the icon size is appropriate
                    .weight(0.1f)
            )

            Spacer(modifier = Modifier.weight(0.1f))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(0.8f)
            ) {
                Text(
                    text = primaryText,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (secondaryText != null) {
                    Text(
                        text = secondaryText,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

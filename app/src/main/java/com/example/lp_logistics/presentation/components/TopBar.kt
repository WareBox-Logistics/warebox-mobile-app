package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.presentation.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar (title: String, color: Boolean) {
    // i need to add an icon of back button
    TopAppBar(
        colors = if (!color) androidx.compose.material3.TopAppBarDefaults.topAppBarColors(Color.Transparent) else androidx.compose.material3.TopAppBarDefaults.topAppBarColors(Orange),
        title = { Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        ) }
    )
}

//@Preview
//@Composable
//private fun PreviewTopBar() {
//    TopBar(title = "Daily route")
//}
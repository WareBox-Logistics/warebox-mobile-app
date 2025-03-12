package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun SearchBar(
    textSearch: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier) {
    TextField(
        value = textSearch,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search Icon",
                tint = Orange
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Orange,
            unfocusedLabelColor = Orange,
            cursorColor = Orange,
            disabledContainerColor = LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        ),
        placeholder = { Text(text = placeholder,  fontSize = 12.sp,
            color = LightGray,
            fontWeight = FontWeight.SemiBold,) },
        modifier = modifier
            .fillMaxWidth()
            .border(
            color = Color.White,
            width = 4.dp,
            shape = RoundedCornerShape(10.dp)
        )
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
    )
}
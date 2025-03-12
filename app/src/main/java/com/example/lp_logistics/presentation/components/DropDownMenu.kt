package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.LightOrange

@Composable
fun SelectableDropdown(
    selectedText: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    textSize: Int = 14,
    dropdownWidth: Int = 152,
    boxHeight: Int = 60,
    boxBackgroundColor: Color = Color.White,
    boxShape: RoundedCornerShape = RoundedCornerShape(10.dp)
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = boxBackgroundColor,
                shape = boxShape
            )
            .height(boxHeight.dp)
            .clickable { expanded = true }
    ) {
        Text(
            text = selectedText,
            fontSize = if(selectedText == "Select a Company" || selectedText == "Select a Warehouse") 12.sp else 14.sp,
            color = if (selectedText == "Select a Company" || selectedText == "Select a Warehouse") Color.Gray else Color.Black,
            fontWeight = if (selectedText == "Select a Company" || selectedText == "Select a Warehouse") FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    LightOrange,
                    shape = RoundedCornerShape(10.dp)
                )
                .width(dropdownWidth.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontSize = textSize.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

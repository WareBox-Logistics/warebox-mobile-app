package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun OrangeTextFields(
    value: MutableState<String>,
    label: String,
    keyboardOptions: KeyboardOptions,
    //focusManager: FocusManager
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
   Column(
       modifier = modifier
   ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Orange,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = value.value,
            onValueChange = { newValue -> value.value = newValue },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
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
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
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
}


@Composable
fun NumericOrangeTextField(
    value: MutableState<String>,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Orange,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = value.value,
            onValueChange = { newText ->

                val filteredText = newText.filter { it.isDigit() }

                if (filteredText.isNotEmpty()) {
                    val number = filteredText.toIntOrNull()
                    if (number != null && number > 0) {
                        value.value = filteredText
                    }
                } else {
                    value.value = ""
                }
            },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
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
            placeholder = {
                Text(
                    text = placeholder, fontSize = 12.sp,
                    color = LightGray,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
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
}

//field containeres
@Composable
fun NumericOrangeTextFieldContainers(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Orange,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = value,
            onValueChange = {
            },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
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
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
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
}
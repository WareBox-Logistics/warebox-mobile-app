package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.R
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun ItemBox(
    boxID: String
) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(8.dp)
            .height(62.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ){
            Icon(
                painter = painterResource(R.drawable.box),
                contentDescription = "Box icon",
                tint = Orange,
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)

            )

            Text(
                text = boxID,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,

            )

            Spacer(modifier = Modifier.padding(end = 65.dp))

            Button(
                onClick = {
                    //Show the bottom sheet with the information of a box
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "View",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun ItemBoxPreview() {
    ItemBox(boxID = "BOX 001")
}
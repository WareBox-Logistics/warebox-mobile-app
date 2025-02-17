package com.example.lp_logistics.presentation.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun TripsCard(
    origin: String,
    destination: String,
    date: String,
    time: String,
    image: String,
    disabled: Boolean = false,
    navController: NavController = NavController(LocalContext.current)
) {
    val imageResId = when (image) {
        "1" -> R.drawable.truck1
        "2" -> R.drawable.truck2
        "3" -> R.drawable.truck3
        else -> R.drawable.truck1
    }

    val colorMatrix = ColorMatrix().apply {
        setToSaturation(0f)
    }

    Box(
        modifier = Modifier
            .height(125.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("navigation") } // redo this better in the future
            .background(color = if (disabled) LightGray else Color.White, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ){
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()){
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    text = "$origin - $destination",
                    fontSize = 16.sp,
                    fontWeight  = FontWeight.Bold,
                    color = if(disabled) Color.Gray else Orange
                )

                Text(
                    text = time,
                    fontSize = 40.sp,
                    fontWeight  = FontWeight.SemiBold,
                    color = if(disabled) Color.Gray else Orange
                )

                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontWeight  = FontWeight.Bold,
                    color = if(disabled) Color.Gray else LightBlue
                )
            }
            Column{
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .height(122.dp)
                        .width(158.dp),
                    colorFilter = if (disabled) ColorFilter.colorMatrix(colorMatrix) else null
                )
            }
        }

    }
}

//@Preview
//@Composable
//private fun PreviewTripsCard() {
//    TripsCard(
//        origin = "Nairobi",
//        destination = "Nakuru",
//        date = "January 10th",
//        time = "10:00 AM",
//        image = "1",
//        disabled = true
//    )
//}
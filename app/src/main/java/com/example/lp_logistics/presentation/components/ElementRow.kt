package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lp_logistics.data.remote.requests.User
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun UserInfoBoxes(user: User) {
    //we could add the string of the role itself here like if its 1 its admin...
    val userProperties = listOf(user.first_name, user.last_name, user.email, user.role)
    println(userProperties)
    ElementRow(list = userProperties)
}

@Composable
fun ElementRow( list: List<String> = emptyList()) {
    Box(
        modifier = Modifier
            .heightIn(min = 80.dp, max = 800.dp)
            .width(307.dp)
            .background(LightGray, shape = RoundedCornerShape(10.dp))
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(15.dp)
        ) {
            list.forEach { item ->
                Box(
                    modifier = Modifier
                        .height(55.dp)
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = item,
                        color = Orange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

    }
}

@Preview
@Composable
private fun PreviewElementRow() {
    ElementRow()
}
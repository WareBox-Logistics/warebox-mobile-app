package com.example.lp_logistics.presentation.screens.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.data.local.UserManager
import com.example.lp_logistics.data.local.UserManager.isWarehouse
import com.example.lp_logistics.data.remote.requests.User
import com.example.lp_logistics.presentation.components.BottomBar
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.UserInfoBoxes
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.LightOrange
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun ProfileScreen(navController: NavController, context: Context, viewModel: ProfileViewModel = hiltViewModel()) {
    var user by remember { mutableStateOf<User?>(null) }
    val isWarehouse = remember(user) { user?.isWarehouse() ?: false }

    LaunchedEffect(Unit) {
        user = UserManager.getUser(context)
    }

    Scaffold(
        topBar = {
            TopBar(title = "Profile", color = false)
        },
        content = {innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(color = LightOrange)) {

                Box(modifier = Modifier.padding(innerPadding)) {
                    Box(
                        modifier = Modifier
                            .height(157.dp)
                            .width(157.dp)
                            .background(color = LightBlue, shape = RoundedCornerShape(100.dp))
                            .zIndex(1f)
                            .align(Alignment.TopCenter)
                    ) {

                        Image(
                            painterResource(R.drawable.wb_icon),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                        )

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 90.dp)
                            .height(580.dp)
                            .padding(15.dp)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .zIndex(0f)
                    ) {

                        LazyColumn(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth()
                        ) {

                            item {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(top = 70.dp), thickness = 4.dp, color = LightGray
                                )

                                Text(
                                    text = "Information",
                                    color = Orange,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)

                                )
                            }

                            item {
                                user?.let { UserInfoBoxes(it) } ?: Text("Loading...")

                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            item{
                                Button(
                                    onClick = { viewModel.logout(context, navController) },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = LightBlue
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(start = 35.dp, end = 35.dp)
                                ) {
                                    Text(
                                        text="Log Out",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                        }

                    }
                }
            }
        },
        bottomBar = {
            BottomBar(navController, isWarehouse)
        }
    )

}

@Preview
@Composable
private fun PreviewProfileScreen() {
    ProfileScreen(navController = NavController(LocalContext.current), context = LocalContext.current)
}
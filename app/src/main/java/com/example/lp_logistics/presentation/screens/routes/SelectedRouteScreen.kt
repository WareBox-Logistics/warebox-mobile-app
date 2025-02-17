package com.example.lp_logistics.presentation.screens.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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
import com.example.lp_logistics.presentation.components.TopBar
import com.example.lp_logistics.presentation.components.TripsCard
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightGray
import com.example.lp_logistics.presentation.theme.Orange

@Composable
fun SelectedRouteScreen(modifier: Modifier = Modifier) {
    Scaffold (
        topBar = {
            TopBar(title = "Selected route", color = false)
        },
        content = { innerPadding ->
            // Screen content
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.padding(innerPadding)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(750.dp)
                            .padding(15.dp)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 15.dp))
                        {
                            item{
                                TripsCard(
                                    origin = "Nairobi",
                                    destination = "Nakuru",
                                    date = "January 10th",
                                    time = "10:00 AM",
                                    image = "1",
                                )
                            }
                            // line thingy
                            item{
                                HorizontalDivider(
                                    modifier = Modifier
                                        .width(250.dp)
                                        .padding(top = 20.dp), thickness = 4.dp, color = LightGray
                                )

                                Text(
                                    text = "Semi-Trailer Details",
                                    color = Orange,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)

                                )
                            }

                           // item { ElementRow() }

                            item{
                                Text(
                                    text = "Docking Area",
                                    color = Orange,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)

                                )
                            }

                            //item{ ElementRow(isPort = true) }

                            item{
                                Text(
                                    text = "Trailer Parking Area",
                                    color = Orange,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)

                                )
                            }

                          //  item{ ElementRow(isPort = true) }

                            item { Spacer(modifier = Modifier.height(20.dp)) }

                            item{
                                Button(
                                    onClick = { /*TODO*/ },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = LightBlue
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(start = 28.dp, end = 28.dp)
                                ) {
                                    //it should be a button that changes from "Start Docking" to "Start Trip"
                                    Text("Start Docking")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


@Preview
@Composable
private fun PreviewSelectedRouteScreen() {
    SelectedRouteScreen()
}
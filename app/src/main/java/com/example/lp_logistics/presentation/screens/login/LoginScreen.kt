package com.example.lp_logistics.presentation.screens.login

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lp_logistics.R
import com.example.lp_logistics.presentation.theme.DarkGray
import com.example.lp_logistics.presentation.theme.LightBlue
import com.example.lp_logistics.presentation.theme.LightOrange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.lp_logistics.presentation.theme.Orange


@Composable
fun LoginScreen(
    context: Context,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
    ) {

    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val loginError by viewModel.loginError


    val isLoggedIn = viewModel.isLoggedIn.value

    LaunchedEffect(viewModel.isLoggedIn.value){
        if(isLoggedIn){
            navController.navigate("home"){
                popUpTo("login"){
                    inclusive = true
                }
            }
        }
    }

    Scaffold  (
        content = { innerPadding ->
            Box(contentAlignment = Alignment.Center,modifier = Modifier
                .fillMaxSize()
                .background(color = LightOrange)) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(571.dp)
                        .width(343.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .background(
                            Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                    ) {

                        //Logo
                        Image(
                            painter = painterResource(id = R.drawable.wb_icon_text),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .height(200.dp)
                                .width(300.dp)
                        )

                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = DarkGray,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )

                        Text(
                            text = "Enter your email and password to log in",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = {
                                Text(
                                    text = "Email",
                                    color= DarkGray
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Email Icon",
                                    tint = Orange
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                                .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                .border(
                                    color = Color.White,
                                    width = 1.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )


                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = {
                                Text(
                                    text = "Password",
                                    color = DarkGray
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Password,
                                    contentDescription = "Password Icon",
                                    tint = Orange
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Password Visibility Toggle",
                                        tint = Orange
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { viewModel.loginEmployee(context, email, password) }
                            ),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                .border(
                                    color = Color.White,
                                    width = 1.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )

                        )

                        Spacer(modifier = Modifier
                            .height(20.dp)
                            .padding(innerPadding))

                        Button(
                            onClick = { viewModel.loginEmployee(context, email, password) },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = LightBlue
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text( text="Log In",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (!loginError.isNullOrEmpty()) {
                    Text(
                        text = loginError ?: "Login error",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 90.dp)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen( context = LocalContext.current, navController = NavController(LocalContext.current),viewModel = hiltViewModel())
}
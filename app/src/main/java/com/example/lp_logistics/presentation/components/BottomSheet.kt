package com.example.lp_logistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lp_logistics.presentation.theme.LightCreme
import com.example.lp_logistics.presentation.theme.LightOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = LightOrange,
    columnColor: Color = LightCreme,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = containerColor
    ) {
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
                .padding(25.dp)
                .verticalScroll(scrollState)
                .background(columnColor,RoundedCornerShape(10.dp))) {
            sheetContent()
        }
    }
}

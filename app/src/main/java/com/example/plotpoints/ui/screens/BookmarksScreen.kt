package com.example.plotpoints.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plotpoints.R
import com.example.plotpoints.ui.theme.BookGreenMedDark
import com.example.plotpoints.ui.theme.CompassFrameHighlight
import com.example.plotpoints.ui.theme.GreenText



@Composable
fun BookmarksScreen (){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(CompassFrameHighlight)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text (
            text = "More features coming soon!",
            fontSize = 30.sp,
            color = GreenText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_pp_round),
            contentDescription = "PlotPoints Logo",
            modifier = Modifier
                .border(1.dp, BookGreenMedDark, shape = CircleShape)
        )
    }
}
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
import com.example.compose.onSecondaryContainerLight
import com.example.compose.outlineLight
import com.example.compose.secondaryContainerLight
import com.example.compose.secondaryLight
import com.example.compose.surfaceVariantLight
import com.example.plotpoints.R




@Composable
fun BookmarksScreen (){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceVariantLight)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text (
            text = "More features coming soon!",
            fontSize = 30.sp,
            color = onSecondaryContainerLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_pp_round),
            contentDescription = "PlotPoints Logo",
            modifier = Modifier
                .border(1.dp, outlineLight, shape = CircleShape)
        )
    }
}
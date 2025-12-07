package com.example.plotpoints.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plotpoints.R
import com.example.plotpoints.bookmarksDB.BookmarkPlace
import com.example.plotpoints.bookmarksDB.DBProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Composable
fun BookmarksScreen (){
    val dao = DBProvider.getDatabase(LocalContext.current).bookmarkPlaceDao()
    val bookmarksFlow = remember { dao.getAllBookmarks() }
    val bookmarks by bookmarksFlow.collectAsState(initial = emptyList())
    val sortedBookmarks = bookmarks.sortedBy { it.name.lowercase() }


    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ){
        LazyColumn() {
            items(sortedBookmarks) { place ->
                PlotPointDetails(place = place, onRemove = {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.removeBookmark(it)
                    }
                })
            }
        }
    }
}

@Composable
fun PlotPointDetails(place: BookmarkPlace, onRemove: (BookmarkPlace) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp,top = 5.dp, end = 7.dp, bottom = 5.dp)
            .border(2.dp, MaterialTheme.colorScheme.onTertiaryContainer,RoundedCornerShape(15.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { isExpanded = !isExpanded },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (!isExpanded) {
                Text(
                    text = "Tap for details",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = place.address ?: "No address",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "ETA: ${place.etaMinutes} minutes",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Distance: ${place.distanceMeters}m",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { onRemove(place) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text(
                            text = "Remove Bookmark",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
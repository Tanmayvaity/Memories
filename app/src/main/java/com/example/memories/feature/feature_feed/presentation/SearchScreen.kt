package com.example.memories.feature.feature_feed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.navigation.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navigateToShared : (AppScreen.Shared) -> Unit = {},
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchBar(
                tonalElevation = 10.dp,
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                ,
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text.toString(),
                        onQueryChange = { it :String ->
                            text = it
                        },
                        onSearch = {},
                        expanded = expanded,
                        onExpandedChange = {it:Boolean->
                            expanded = it
                        },
                        placeholder = {
                            Text("Search")
                        },
                        trailingIcon = {
                            if(text.isNotEmpty()){
                                IconButton(
                                    onClick = {
                                        text = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "",
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()



                    )
                },
                expanded =  expanded,
                onExpandedChange = { it : Boolean ->
                    expanded = it
                },
                ) {
            }
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                item(
                    span = {
                        GridItemSpan(maxCurrentLineSpan)
                    }
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(10.dp)
                            .clickable{
                                navigateToShared(AppScreen.Shared)
                            }
                    ){
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(20.dp))
                            ,
                            painter = painterResource(R.drawable.pinned_image),
                            contentDescription = "Shared Image Folder Icon",
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Shared",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.BottomStart)
                                .shadow(5.dp)
                            ,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp

                        )
                    }

                }
            }

        }



    }



}

@Composable
fun SearchBarElement(modifier: Modifier = Modifier) {
    
}


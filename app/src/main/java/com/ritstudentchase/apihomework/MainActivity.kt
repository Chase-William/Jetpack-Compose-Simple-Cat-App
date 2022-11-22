@file:OptIn(ExperimentalMaterial3Api::class) // Yes, the whole damn thing...

package com.ritstudentchase.apihomework

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.* // Material Theme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = RandomCatImageViewModel()
        setContent {
            AppThemeView(vm)
        }
    }
}

@Composable
fun AppThemeView(vm: RandomCatImageViewModel) {
    // https://semicolonspace.com/status-bar-jetpack-compose/ -- Guide
    val systemUIController = rememberSystemUiController()
    // https://developer.android.com/jetpack/compose/designsystems/material3 -- Guide
    // For dark theme
    val darkColors = darkColorScheme(
        primary = Color(0xFFF3A712),
        onPrimary = Color(0xFFC03221),
        primaryContainer = Color(0xFFD4F4DD),
        onPrimaryContainer = Color(0xFFC03221),
        background = Color(0xFF02020A)
    )
    // For light theme
    val lightColors = lightColorScheme(
        primary = Color(0xFFDDFFF7),
        onPrimary = Color(0xFF93E1D8),
        primaryContainer = Color(0xFFFFA69E),
        onPrimaryContainer = Color(0xFFAA4465),
        background = Color(0xFF462255)
    )
    // Color scheme to use determined from device system theme
    val scheme = if (isSystemInDarkTheme()) darkColors else lightColors
    // Set ui status bar to primary of respective system theme
    systemUIController.setStatusBarColor(scheme.primary)

    MaterialTheme(
        colorScheme = scheme // set app theme
    ) {
        RandomCatView(vm)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RandomCatView(vm: RandomCatImageViewModel) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit, block = {
        vm.getRandomCatImage()
    })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                  Row {
                      Text("Meow'n Pow")
                  } // Row
                }
            ) // TopAppBar
        }, // topBar
        content = {
            /**
             *
             * I do not know why, but some of my layout prefers to hide under the damn TopAppBar..
             * This has only been a probablem since adopting more *experimental* APIs..
             * I have *fixed*.. *cough*, this by using a top padding of 55.dp.. not great, but works for this example
             *
             */
            Column(modifier = Modifier.padding(top = 55.dp)) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Load all the cats you want!",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Spamming will result in http: 429 or 'too many request'.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        this.items(vm.getCatList()) { cat -> // cat instance foreach
                            if (vm.errorMsg.isEmpty()) {
                                // https://proandroiddev.com/async-image-loading-the-jetpack-compose-way-2686d1ac5a53
                                // https://bumptech.github.io/glide/int/compose.html -- Docs showing GlideImage
                                Column() {
                                    GlideImage(
                                        model = cat.url,
                                        contentDescription = "A random cat!",
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .border(
                                                2.dp,
                                                MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(3.dp)
                                            )
                                            .shadow(2.dp)
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Id: ${cat.id}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight(700),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        CatImageInfoTextView("Width", cat.width.toString())
                                        CatImageInfoTextView("Height", cat.height.toString())
                                    }
                                }
                            }
                            else {
                                Text("An error occurred when fetching cats: ${vm.errorMsg}")
                            }
                        } // items
                    }// LazyColumn
                } // Column
            } // Column
        }, // content
        floatingActionButton = {
            /**
             * Fetch another cat
             */
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        vm.getRandomCatImage() // Get random cat image via coroutine
                    }
                }) {
                Text(
                    text = "Another One!",
                    modifier = Modifier.padding(10.dp)
                )
            } // FloatingActionBtn
        } // floatingActionBtn
    ) // Scaffold
} // RandomCatView

@Composable
fun CatImageInfoTextView(title: String, info: String, modifier: Modifier = Modifier) {
    Text(
        text = "$title: $info",
        fontSize = 10.sp,
        fontFamily = FontFamily.Cursive,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val vm = RandomCatImageViewModel()
    AppThemeView(vm)
}
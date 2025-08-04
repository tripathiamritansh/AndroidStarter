package com.sample.starter.ui

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.feed.ui.theme.StarterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    HamburgerMenu(drawerState) {
        Scaffold(topBar = {
            Topbar(scope, drawerState)
        }, bottomBar = {
            BottomBar()
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(scope: CoroutineScope, drawerState: DrawerState) {
    CenterAlignedTopAppBar(title = {
        Text("Translate")
    }, navigationIcon = {
        Icon(
            Icons.Default.Menu, contentDescription = null, modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.open()
                }
            })
        )
    }, actions = {
        Surface(
            shape = CircleShape, color = Color.LightGray
        ) {
            Icon(Icons.Default.Person, contentDescription = null)
        }
    })
}

@Composable
fun BottomBar() {
    // Use a standard Row with custom content
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        // SpaceAround distributes items evenly with space around them
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomBarItem(icon = Icons.Default.Person, text = "Conversation")

        // This is the custom, larger central button
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFA8C7FA)) // A color similar to the original
                .clickable { /* TODO: Mic Action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Microphone",
                tint = Color(0xFF003366),
                modifier = Modifier.size(32.dp)
            )
        }

        BottomBarItem(icon = Icons.Default.Favorite, text = "Camera")

    }
}

// A helper composable for the smaller items to avoid repetition
@Composable
fun BottomBarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO */ }
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Text(text = text)
    }
}

@Composable
fun Content() {
    var inputValue by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomEnd = 50.dp, bottomStart = 50.dp))
        ) {
            TextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    TODO()
                }, modifier = Modifier.weight(1f)) {
                    Text("English")
                }
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                )
                Button(onClick = {
                    TODO()
                }, modifier = Modifier.weight(1f)) {
                    Text("Spanish")
                }
            }
        }
    }

}

@Composable
fun HamburgerMenu(drawerState: DrawerState, content: @Composable () -> Unit) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                Text("Item 1")
                Text("Item 2")
                Text("Item 3")
            }
        }) {
        Box { content() }
    }
}

@Preview
@Composable
fun ScreenPreview() {
    StarterTheme(
        darkTheme = true
    ) {
        TranslateScreen()
    }
}
package com.sample.starter

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val viewModel: FeedViewModel = viewModel()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val listState = remember { LazyListState() }
    val derivedState = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Social Feeb")
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.shufflePost()
                        }
                    ) { Text("Shuffle") }
                }
            )
        },
        floatingActionButton = {
            if (derivedState.value) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Text("Scroll to Top")
                }
            }

        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(items = posts, key = { index, item -> item.id }) { index, post ->
                PostItem(post, Modifier.animateItem()) {
                    viewModel.toggleLike(post, index)
                }
            }
        }
    }

}

@Composable
fun PostItem(post: Post, modifier: Modifier, onLiked: () -> Unit) {

    val likedColor by animateColorAsState(if (post.isLiked) Color.Red else Color.Black)
    Card(modifier = modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            Text(post.author)
            Text(post.text)
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = likedColor,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(
                        onClick = onLiked
                    )
            )
        }
    }

}

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val posts = remember { generatePosts().toMutableStateList() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Social Feeb")
                },
                actions = {
                    Button(
                        onClick = {
                            posts.shuffle()
                        }
                    ) { Text("Shuffle") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(items = posts, key = { index, item -> item.id }) { index, post ->
                PostItem(post, Modifier.animateItem()) {
                    posts[index] = post.copy(
                        isLiked = !post.isLiked,
                        likeCount = if (!post.isLiked) post.likeCount.inc() else post.likeCount.dec()
                    )

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


data class Post(
    // A unique and stable ID for each post
    val id: UUID = UUID.randomUUID(),
    val author: String,
    val text: String,
    val likeCount: Int,
    // Note this piece of state for the item
    val isLiked: Boolean
)

fun generatePosts(): List<Post> {
    val authors = listOf("Maria", "Alex", "Sam", "Chloe", "Ben")
    val texts = listOf(
        "Just saw a beautiful sunset! 🌅",
        "What's everyone's favorite Compose feature?",
        "My new side project is finally live!",
        "Debugging state issues is... fun. #androiddev",
        "Remember to use keys in your LazyLists!"
    )
    return List(50) {
        Post(
            author = authors.random(),
            text = texts.random(),
            likeCount = (10..500).random(),
            isLiked = false
        )
    }
}
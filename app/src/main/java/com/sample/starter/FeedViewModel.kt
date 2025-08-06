package com.sample.starter

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FeedViewModel : ViewModel() {
    private val _checked = MutableStateFlow(false)
    val checked = _checked.asStateFlow()
    val _posts = MutableStateFlow(generatePosts())
    val posts: StateFlow<List<Post>> = _posts
    fun shufflePost() {
        _posts.value = _posts.value.shuffled()
    }

     fun toggle() {
        _checked.value = !_checked.value
    }

    fun toggleLike(post: Post, index: Int) {
        val currentPosts = _posts.value.toMutableStateList()
        currentPosts[index] = post.copy(
            isLiked = !post.isLiked,
            likeCount = if (!post.isLiked) post.likeCount.inc() else post.likeCount.dec()
        )
        _posts.value = currentPosts

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

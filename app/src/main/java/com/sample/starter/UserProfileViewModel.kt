package com.sample.starter

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserProfileViewModel : ViewModel() {
    private val state = MutableStateFlow<List<UserProfile>>(
        mutableListOf(
            UserProfile(
                name = "Alex Riley",
                isFeatured = true,
                isOnline = true,
                profileView = 1245
            ),
            UserProfile(
                name = "Jordan Smith",
                isFeatured = false,
                isOnline = true,
                profileView = 873
            ),
            UserProfile(
                name = "Casey Garcia",
                isFeatured = false,
                isOnline = false,
                profileView = 24
            ),
            UserProfile(
                name = "Jamie Chen",
                isFeatured = true,
                isOnline = false,
                profileView = 4890
            ),
            UserProfile(
                name = "Morgan Williams",
                isFeatured = false,
                isOnline = true,
                profileView = 150
            ),
            UserProfile(
                name = "Taylor Patel",
                isFeatured = true,
                isOnline = true,
                profileView = 8871
            ),
            UserProfile(
                name = "Skyler Davis",
                isFeatured = false,
                isOnline = false,
                profileView = 99
            ),
            UserProfile(
                name = "Avery Jones",
                isFeatured = true,
                isOnline = true,
                profileView = 3201
            ),
            UserProfile(
                name = "Cameron Lee",
                isFeatured = true,
                isOnline = false,
                profileView = 560
            ),
            UserProfile(
                name = "Riley Martinez",
                isFeatured = false,
                isOnline = false,
                profileView = 0
            ),
            UserProfile(
                name = "Quinn Miller",
                isFeatured = false,
                isOnline = true,
                profileView = 1123
            ),
            UserProfile(
                name = "Drew Wilson",
                isFeatured = true,
                isOnline = true,
                profileView = 7432
            ),
            UserProfile(
                name = "Logan Moore",
                isFeatured = false,
                isOnline = false,
                profileView = 455
            ),
            UserProfile(
                name = "Parker Anderson",
                isFeatured = true,
                isOnline = false,
                profileView = 987
            ),
            UserProfile(
                name = "Rowan Jackson",
                isFeatured = false,
                isOnline = true,
                profileView = 672
            ),
            UserProfile(
                name = "Jesse Thomas",
                isFeatured = true,
                isOnline = true,
                profileView = 6543
            ),
            UserProfile(
                name = "Blake Nguyen",
                isFeatured = false,
                isOnline = true,
                profileView = 2398
            ),
            UserProfile(
                name = "Hayden Rodriguez",
                isFeatured = true,
                isOnline = false,
                profileView = 129
            ),
            UserProfile(
                name = "Charlie Kim",
                isFeatured = true,
                isOnline = true,
                profileView = 5100
            ),
            UserProfile(
                name = "Finley Brown",
                isFeatured = false,
                isOnline = false,
                profileView = 312
            )
        )
    )

    val userProfiles = state.asStateFlow()


    fun markFeatured(index: Int) {
        val currentState = state.value.toMutableStateList()
        val updatedState = currentState.mapIndexed { currIndex, state ->
            if (index == currIndex) {
                state.copy(isFeatured = true)
            } else {
                state.copy(isFeatured = false)
            }
        }
        state.value = updatedState
    }

    fun updateViewCount(index: Int) {
        updateIndex(index) { state, _ ->
            state.copy(profileView = state.profileView.inc())
        }
    }

    fun resetViewCount(index: Int) {
        updateIndex(index) { state, _ ->
            state.copy(profileView = 0)
        }
    }

    fun toggleIsOnline(index: Int) {
        updateIndex(index) { state, _ ->
            state.copy(isOnline = !state.isOnline)
        }
    }

    private fun updateIndex(index: Int, updateState: (UserProfile, Int) -> UserProfile) {
        val currentState = state.value.toMutableStateList()
        val updateState: List<UserProfile> = currentState.mapIndexed { currIndex, state ->
            if (index == currIndex) {
                updateState(state, currIndex)
            } else {
                state
            }
        }
        state.value = updateState
    }


    fun shuffle() {
        state.value = state.value.shuffled()
    }
}
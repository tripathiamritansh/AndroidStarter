package com.sample.starter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

data class UserProfile(
    val name: String, val isFeatured: Boolean, val isOnline: Boolean, val profileView: Int
)

@Composable
fun UserProfileComposable() {
    val viewModel: UserProfileViewModel = viewModel()
    val userProfiles by viewModel.userProfiles.collectAsStateWithLifecycle()
    // 1. Derive a single value representing the currently featured user.
    val featuredUser = userProfiles.find { it.isFeatured }

    val snackbarHostState = remember { SnackbarHostState() }
    // 2. Use that derived value as the key for a SINGLE LaunchedEffect.
    //    We also check for null to avoid issues if no one is featured.
    LaunchedEffect(featuredUser) {
        if (featuredUser != null) {
            snackbarHostState.showSnackbar("${featuredUser.name} is now featured!")
        }
    }

    Scaffold(
        // Remember to connect the SnackbarHostState to the Scaffold's SnackbarHost
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        viewModel.shuffle()
                    },
                ) {
                    Text("Shuffle")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(userProfiles, key = { index, profile -> profile.name }) { index, profile ->
                UserProfile(
                    userProfile = profile, switchOnline = {
                    viewModel.toggleIsOnline(index)
                }, setProfileView = {
                    viewModel.updateViewCount(index)
                }, resetProfileVIew = {
                    viewModel.resetViewCount(index)
                }, makeFeatured = {
                    viewModel.markFeatured(index)
                },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun UserProfile(
    userProfile: UserProfile,
    switchOnline: () -> Unit,
    setProfileView: () -> Unit,
    resetProfileVIew: () -> Unit,
    makeFeatured: () -> Unit,
    modifier: Modifier
) {
    Card(modifier = modifier.padding(8.dp)) {
        Box(
        ) {
            if (userProfile.isFeatured) {
                Text(
                    "★Featured",
                    Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp, Color.Gray, RoundedCornerShape(8.dp)
                        )
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)

                )
            } else {
                Text(
                    "Make Featured",
                    Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)
                        .clickable(onClick = makeFeatured)
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userProfile.name, style = MaterialTheme.typography.bodyLarge
                    )

                    // This Spacer takes up all available space, pushing the Box to the end
                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(12.dp) // Use a specific size, not an interactive one
                            .clip(CircleShape)
                            .background(if (userProfile.isOnline) Color.Green else Color.Gray)
                    )
                }
                Text("Profile Views ${userProfile.profileView}")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        enabled = userProfile.isOnline,
                        onClick = setProfileView,
                    ) { Text("Increase View") }

                    Button(
                        enabled = userProfile.isOnline,
                        onClick = resetProfileVIew,
                    ) { Text("Reset") }
                }

                Switch(
                    checked = userProfile.isOnline, onCheckedChange = {
                        switchOnline()
                    }, modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
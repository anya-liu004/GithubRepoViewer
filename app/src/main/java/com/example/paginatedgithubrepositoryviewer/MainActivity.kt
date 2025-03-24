// MainActivity.kt
package com.example.paginatedgithubrepositoryviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepositorySearchScreen()
        }
    }
}

@Composable
fun RepositorySearchScreen(viewModel: RepoViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("GitHub Username") }
        )
        Button(onClick = { viewModel.fetchRepositories(username) }) {
            Text("Search")
        }

        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success -> RepositoryList(
                repos = (uiState as UiState.Success).repositories,
                onLoadMore = { viewModel.loadMore() },
                hasMore = (uiState as UiState.Success).hasMore
            )
            is UiState.Error -> Text("Error: ${(uiState as UiState.Error).message}")
            UiState.Initial -> {}
        }
    }
}

@Composable
fun RepositoryList(repos: List<Repo>, onLoadMore: () -> Unit, hasMore: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(repos) { repo ->
            Text(repo.name, style = MaterialTheme.typography.bodyLarge)
            Text(repo.description ?: "No description", style = MaterialTheme.typography.bodySmall)
        }
        if (hasMore) {
            item {
                Button(onClick = onLoadMore, modifier = Modifier.padding(16.dp)) {
                    Text("Load More")
                }
            }
        }
    }
}
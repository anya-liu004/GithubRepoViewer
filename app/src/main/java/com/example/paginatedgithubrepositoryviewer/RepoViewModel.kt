// RepoViewModel.kt
package com.example.paginatedgithubrepositoryviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RepoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    private var currentPage = 1
    private var currentUsername: String = ""
    private var hasMorePages = true
    private val allRepos = mutableListOf<Repo>()

    fun fetchRepositories(username: String) {
        currentUsername = username
        currentPage = 1
        hasMorePages = true
        allRepos.clear()
        loadMore()
    }

    fun loadMore() {
        if (!hasMorePages) return
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val repos = ApiClient.apiService.getRepos(currentUsername, currentPage)
                if (repos.isEmpty()) hasMorePages = false
                allRepos.addAll(repos)
                _uiState.value = UiState.Success(allRepos, hasMorePages)
                currentPage++
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val repositories: List<Repo>, val hasMore: Boolean) : UiState()
    data class Error(val message: String) : UiState()
}

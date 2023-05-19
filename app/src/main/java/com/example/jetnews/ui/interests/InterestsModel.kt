package com.example.jetnews.ui.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.InterestSection
import com.example.jetnews.Result
import com.example.jetnews.InterestsRepository
import com.example.jetnews.TopicSelection
import com.example.jetnews.successOr
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InterestsState(
    val topics: List<InterestSection> = emptyList(),
    val people: List<String> = emptyList(),
    val publications: List<String> = emptyList(),
    val loading: Boolean = false,
)

class InterestsModel(private val repository: InterestsRepository) : ViewModel() {

    val flow: MutableStateFlow<InterestsState> = MutableStateFlow(InterestsState(loading = true))
    val selectedTopicsFlow: StateFlow<Set<TopicSelection>> = repository.observeTopicsSelected().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val selectedPeopleFlow: StateFlow<Set<String>> = repository.observePeopleSelected().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
    val selectedPublicationsFlow: StateFlow<Set<String>> = repository.observePublicationSelected().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        refreshAll()
    }

    fun toggleTopicSelection(topic: TopicSelection) = viewModelScope.launch {
        repository.toggleTopicSelection(topic)
    }

    fun togglePersonSelected(person: String) = viewModelScope.launch {
        repository.togglePersonSelected(person)
    }

    fun togglePublicationSelected(publication: String) = viewModelScope.launch {
        repository.togglePublicationSelected(publication)
    }

    private fun refreshAll() {
        flow.update {
            it.copy(loading = true)
        }

        viewModelScope.launch {
            // Trigger repository requests in parallel
            val topicsDeferred: Deferred<Result<List<InterestSection>>> = async { repository.getTopics() }
            val peopleDeferred: Deferred<Result<List<String>>> = async { repository.getPeople() }
            val publicationsDeferred: Deferred<Result<List<String>>> = async { repository.getPublications() }

            // Wait for all requests to finish
            val topics: List<InterestSection> = topicsDeferred.await().successOr(emptyList())
            val people: List<String> = peopleDeferred.await().successOr(emptyList())
            val publications: List<String> = publicationsDeferred.await().successOr(emptyList())

            flow.update {
                it.copy(loading = false, topics = topics, people = people, publications = publications)
            }
        }
    }

    companion object {
        fun provideFactory(repository: InterestsRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = InterestsModel(repository) as T
        }
    }

}

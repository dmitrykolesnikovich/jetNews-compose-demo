package com.example.jetnews

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface InterestsRepository {
    suspend fun getTopics(): Result<List<InterestSection>>
    suspend fun getPeople(): Result<List<String>>
    suspend fun getPublications(): Result<List<String>>
    suspend fun toggleTopicSelection(topic: TopicSelection)
    suspend fun togglePersonSelected(person: String)
    suspend fun togglePublicationSelected(publication: String)
    fun observeTopicsSelected(): Flow<Set<TopicSelection>>
    fun observePeopleSelected(): Flow<Set<String>>
    fun observePublicationSelected(): Flow<Set<String>>
}

class InterestsRepositoryImpl : InterestsRepository {

    private val topics: List<InterestSection> by lazy {
        listOf(
            InterestSection("Android", listOf("Jetpack Compose", "Kotlin", "Jetpack")),
            InterestSection("Programming", listOf("Kotlin", "Declarative UIs", "Java", "Unidirectional Data Flow", "C++")),
            InterestSection("Technology", listOf("Pixel", "Google"))
        )
    }

    private val people: List<String> by lazy {
        listOf(
            "Kobalt Toral",
            "K'Kola Uvarek",
            "Kris Vriloc",
            "Grala Valdyr",
            "Kruel Valaxar",
            "L'Elij Venonn",
            "Kraag Solazarn",
            "Tava Targesh",
            "Kemarrin Muuda"
        )
    }

    private val publications: List<String> by lazy {
        listOf(
            "Kotlin Vibe",
            "Compose Mix",
            "Compose Breakdown",
            "Android Pursue",
            "Kotlin Watchman",
            "Jetpack Ark",
            "Composeshack",
            "Jetpack Point",
            "Compose Tribune"
        )
    }

    // for now, keep the selections in memory
    private val selectedTopics: MutableStateFlow<Set<TopicSelection>> = MutableStateFlow(setOf<TopicSelection>())
    private val selectedPeople: MutableStateFlow<Set<String>> = MutableStateFlow(setOf<String>())
    private val selectedPublications: MutableStateFlow<Set<String>> = MutableStateFlow(setOf<String>())

    override suspend fun getTopics(): Result<List<InterestSection>> {
        return Result.Success(topics)
    }

    override suspend fun getPeople(): Result<List<String>> {
        return Result.Success(people)
    }

    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(publications)
    }

    override suspend fun toggleTopicSelection(topic: TopicSelection) = selectedTopics.update { selections: Set<TopicSelection> ->
        selections.addOrRemove(topic)
    }

    override suspend fun togglePersonSelected(person: String) = selectedPeople.update { selections: Set<String> ->
        selections.addOrRemove(person)
    }

    override suspend fun togglePublicationSelected(publication: String) = selectedPublications.update { selections: Set<String> ->
        selections.addOrRemove(publication)
    }

    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> {
        return selectedTopics
    }

    override fun observePeopleSelected(): Flow<Set<String>> {
        return selectedPeople
    }

    override fun observePublicationSelected(): Flow<Set<String>> {
        return selectedPublications
    }

}

class EmptyInterestsRepository : InterestsRepository {
    override suspend fun getTopics(): Result<List<InterestSection>> = error("stub")
    override suspend fun getPeople(): Result<List<String>> = error("stub")
    override suspend fun getPublications(): Result<List<String>> = error("stub")
    override suspend fun toggleTopicSelection(topic: TopicSelection) = error("stub")
    override suspend fun togglePersonSelected(person: String) = error("stub")
    override suspend fun togglePublicationSelected(publication: String) = error("stub")
    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> = error("stub")
    override fun observePeopleSelected(): Flow<Set<String>> = error("stub")
    override fun observePublicationSelected(): Flow<Set<String>> = error("stub")
}

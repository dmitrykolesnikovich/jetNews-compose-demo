package com.example.jetnews

fun TestMainContext(): MainContext = MainContext(BlockingFakePostsRepository(), InterestsRepositoryImpl())

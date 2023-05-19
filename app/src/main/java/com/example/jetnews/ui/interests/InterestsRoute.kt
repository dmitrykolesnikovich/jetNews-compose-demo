package com.example.jetnews.ui.interests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.jetnews.Tab

@Composable
fun InterestsRoute(model: InterestsModel, isExpanded: Boolean, openDrawer: () -> Unit) {
    val tabContent: List<Tab> = rememberTabs(model)
    val (currentSection, onTabChange) = rememberSaveable { mutableStateOf(tabContent.first().section) }

    InterestsScreen(tabContent, currentSection, isExpanded, onTabChange, openDrawer)
}
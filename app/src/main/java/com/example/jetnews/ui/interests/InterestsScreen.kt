package com.example.jetnews.ui.interests

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetnews.AppTheme
import com.example.jetnews.InterestSection
import com.example.jetnews.R
import com.example.jetnews.Section
import com.example.jetnews.Tab
import com.example.jetnews.TopicSelection
import com.example.jetnews.tabContainerModifier
import com.example.jetnews.AdaptiveLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(tabs: List<Tab>, currentSection: Section, isScreenExpanded: Boolean, selectSection: (Section) -> Unit, openDrawer: () -> Unit) {
    val context: Context = LocalContext.current
    val hostState: SnackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = hostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.cd_interests), style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    if (!isScreenExpanded) {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                painter = painterResource(R.drawable.ic_jetnews_logo),
                                contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Search is not yet implemented in this configuration", Toast.LENGTH_LONG).show()
                        },
                        content = {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.cd_search))
                        }
                    )
                }
            )
        },
        content = { paddings ->
            InterestLayout(currentSection, isScreenExpanded, selectSection, tabs, Modifier.padding(paddings))
        }
    )
}

@Composable
fun rememberTabs(model: InterestsModel): List<Tab> {
    val state: InterestsState by model.flow.collectAsStateWithLifecycle()

    val topicsSection: Tab = Tab(Section.Topics) {
        val selectedTopics by model.selectedTopicsFlow.collectAsStateWithLifecycle()
        TabWithSections(
            sections = state.topics,
            selectedTopics = selectedTopics,
            selectTopic = { model.toggleTopicSelection(it) }
        )
    }

    val peopleSection = Tab(Section.People) {
        val selectedPeople by model.selectedPeopleFlow.collectAsStateWithLifecycle()
        TabWithTopics(
            topics = state.people,
            selectedTopics = selectedPeople,
            selectTopic = { model.togglePersonSelected(it) }
        )
    }

    val publicationSection = Tab(Section.Publications) {
        val selectedPublications by model.selectedPublicationsFlow
            .collectAsStateWithLifecycle()
        TabWithTopics(
            topics = state.publications,
            selectedTopics = selectedPublications,
            selectTopic = { model.togglePublicationSelected(it) }
        )
    }

    return listOf(topicsSection, peopleSection, publicationSection)
}

@Composable
fun InterestLayout(currentSection: Section, isScreenExpanded: Boolean, updateSection: (Section) -> Unit, tabs: List<Tab>, modifier: Modifier = Modifier) {
    val selectedTabIndex: Int = tabs.indexOfFirst { it.section == currentSection }
    
    Column(modifier) {
        InterestsTabRow(selectedTabIndex, updateSection, tabs, isScreenExpanded)
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        Box(modifier = Modifier.weight(1f), content = { tabs[selectedTabIndex].content() })
    }
}

@Composable
fun TabWithTopics(topics: List<String>, selectedTopics: Set<String> = emptySet(), selectTopic: (String) -> Unit = {}) {
    AdaptiveLayout(topPadding = 16.dp, modifier = tabContainerModifier.verticalScroll(rememberScrollState())) {
        for (topic in topics) {
            TopicLayout(title = topic, selected = selectedTopics.contains(topic), toggle = { selectTopic(topic) })
        }
    }
}

@Composable
fun TabWithSections(sections: List<InterestSection>, selectedTopics: Set<TopicSelection> = emptySet(), selectTopic: (TopicSelection) -> Unit = {}) {
    Column(tabContainerModifier.verticalScroll(rememberScrollState())) {
        for ((section, topics) in sections) {
            Text(text = section, modifier = Modifier.padding(16.dp).semantics { heading() }, style = MaterialTheme.typography.titleMedium)
            AdaptiveLayout {
                for (topic in topics) {
                    TopicLayout(
                        title = topic,
                        selected = selectedTopics.contains(TopicSelection(section, topic)),
                        toggle = { selectTopic(TopicSelection(section, topic)) }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicLayout(title: String, selected: Boolean, toggle: () -> Unit, modifier: Modifier = Modifier) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(modifier.toggleable(value = selected, onValueChange = { toggle() }), verticalAlignment = Alignment.CenterVertically) {            
            Image(
                painter = painterResource(R.drawable.placeholder_1_1),
                contentDescription = null, // decorative
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp))
            )
            Text(title, modifier = Modifier.padding(16.dp).weight(1f), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(16.dp))
            SelectTopicButton(selected = selected)
        }
        Divider(modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    }
}

@Composable
fun InterestsTabRow(selectedTabIndex: Int, updateSection: (Section) -> Unit, tabs: List<Tab>, isScreenExpanded: Boolean) {
    if (isScreenExpanded) {
        ScrollableTabRow(selectedTabIndex, contentColor = MaterialTheme.colorScheme.primary, edgePadding = 0.dp) {
            InterestsTabRowLayout(selectedTabIndex, updateSection, tabs, Modifier.padding(horizontal = 8.dp))
        }
    } else {
        TabRow(selectedTabIndex, contentColor = MaterialTheme.colorScheme.primary) {
            InterestsTabRowLayout(selectedTabIndex, updateSection, tabs)
        }
    }    
}

@Composable
private fun InterestsTabRowLayout(selectedTabIndex: Int, updateSection: (Section) -> Unit, tab: List<Tab>, modifier: Modifier = Modifier) {
    tab.forEachIndexed { index, content ->
        val colorScheme: ColorScheme = AppTheme.colorScheme()
        val color: Color = if (selectedTabIndex == index) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.8f)
        Tab(
            selected = selectedTabIndex == index,
            onClick = { updateSection(content.section) },
            modifier = Modifier.heightIn(min = 48.dp),
            content = {
                Text(
                    text = stringResource(content.section.titleResId),
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = modifier.paddingFromBaseline(top = 20.dp)
                )
            }
        )
    }
}

@Composable
fun SelectTopicButton(modifier: Modifier = Modifier, selected: Boolean = false) {
    val icon: ImageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add
    val iconColor: Color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val borderColor: Color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val backgroundColor: Color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary

    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.size(36.dp, 36.dp), 
        content = {
            Image(imageVector = icon, colorFilter = ColorFilter.tint(iconColor), modifier = Modifier.padding(8.dp), contentDescription = null)
        }
    )
}

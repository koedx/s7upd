package com.softfog.s7upd

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch

data class ManualSection(val title: String, val markdown: String)

private val placeholderSections = listOf(
    ManualSection(
        title = "Section 1: Getting Started",
        markdown = "# Getting Started\n\nPlaceholder content rendered by **Markwon**.\n\n- Item one\n- Item two"
    ),
    ManualSection(
        title = "Section 2: Maintenance",
        markdown = "# Maintenance\n\nMore placeholder content.\n\n1. Step one\n2. Step two"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ManualScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(placeholderSections.first()) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Manual Sections", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                placeholderSections.forEach { section ->
                    NavigationDrawerItem(
                        label = { Text(section.title) },
                        selected = section == selectedSection,
                        onClick = {
                            selectedSection = section
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("s7 updater") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open manual sections")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                MarkdownContent(markdown = selectedSection.markdown)
            }
        }
    }
}

@Composable
fun MarkdownContent(markdown: String) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }
    AndroidView(
        factory = { TextView(it) },
        update = { it2 -> markwon.setMarkdown(it2, markdown) },
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}

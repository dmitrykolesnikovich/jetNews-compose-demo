package com.example.jetnews

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class MainApplication : Application() {

    lateinit var context: MainContext

    override fun onCreate() {
        super.onCreate()
        context = MainContext(FakePostsRepository(), InterestsRepositoryImpl())
    }

}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val app: MainApplication = application as MainApplication
        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            AppNavigation(app.context, widthSizeClass)
        }
    }

}

class MainContext(val postsRepository: PostsRepository, val interestsRepository: InterestsRepository) {
    val navController: NavHostController @Composable get() = rememberNavController()
}

fun EmptyMainContext(): MainContext = MainContext(EmptyPostsRepository(), EmptyInterestsRepository())

package ua.polytech.testingtask


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.polytech.testingtask.catalog.CatalogScreen
import ua.polytech.testingtask.books.ListOfBooksScreen
import ua.polytech.testingtask.common.checkInternet.isConnectedToInternet
import ua.polytech.testingtask.db.DbUpdateService
import ua.polytech.testingtask.ui.theme.AppTheme
import ua.polytech.testingtask.ui.theme.SetImageBackground

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startUpdateDB()
        setContent {
            AppTheme {
                SetImageBackground()
                MyApp(context = this)
            }
        }
    }

    private fun startUpdateDB() {
        if (isConnectedToInternet(this)) {
            val intent = Intent(this, DbUpdateService::class.java)
            startService(intent)
        }
    }

}


@Composable
fun MyApp(context: Context) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Catalog.route,
    ) {
        composable(route = Catalog.route) {
            CatalogScreen(onClickCategory = { listNameEncoded ->
                navController.navigateSingleTopTo(ListOfBooks.route + "?listNameEncoded=$listNameEncoded")
            })
        }
        composable(route = ListOfBooks.route + "?listNameEncoded={listNameEncoded}") { backStackEntry ->
            val listNameEncoded = backStackEntry.arguments?.getString("listNameEncoded") ?: ""
            ListOfBooksScreen(listNameEncoded = listNameEncoded,
                onClickBuy = { url ->
                    val customTabsIntent: CustomTabsIntent =
                        CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                })
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
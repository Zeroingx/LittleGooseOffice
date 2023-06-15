package little.goose.home

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_HOME_PAGE
import little.goose.home.ui.HomeScreen
import little.goose.home.ui.HomeViewModel
import little.goose.search.SearchType
import little.goose.ui.screen.LittleGooseEmptyScreen
import java.util.Date

const val ROUTE_HOME = "home"

const val KEY_INIT_HOME_PAGE = "init_home_page"

fun NavController.navigateToHome(initPage: Int) {
    navigate("$ROUTE_HOME/$initPage") {
        popUpTo("$ROUTE_HOME/{$KEY_INIT_HOME_PAGE}")
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeRoute(
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialDialog: (memorialId: Long) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    composable(
        route = "$ROUTE_HOME/{$KEY_INIT_HOME_PAGE}",
        arguments = listOf(
            navArgument(KEY_INIT_HOME_PAGE) {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) {
        HomeRoute(
            modifier = Modifier.fillMaxSize(),
            initHomePage = it.arguments?.getInt(KEY_INIT_HOME_PAGE, -1) ?: -1,
            onNavigateToMemorialAdd = onNavigateToMemorialAdd,
            onNavigateToMemorialDialog = onNavigateToMemorialDialog,
            onNavigateToNote = onNavigateToNote,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToTransactionDialog = onNavigateToTransactionDialog,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
            onNavigateToScheduleDialog = onNavigateToScheduleDialog
        )
    }
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    initHomePage: Int,
    onNavigateToMemorialAdd: () -> Unit,
    onNavigateToMemorialDialog: (memorialId: Long) -> Unit,
    onNavigateToTransactionDialog: (transactionId: Long) -> Unit,
    onNavigateToTransaction: (id: Long?, time: Date?) -> Unit,
    onNavigateToNote: (noteId: Long?) -> Unit,
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToAccountAnalysis: () -> Unit,
    onNavigateToScheduleDialog: (Long?) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    if (initHomePage == -1) {
        LittleGooseEmptyScreen(modifier = modifier)
    } else {
        val pagerState = rememberPagerState(initialPage = initHomePage)
        HomeScreen(
            modifier = modifier.fillMaxSize(),
            pagerState = pagerState,
            onNavigateToNote = onNavigateToNote,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToMemorialAdd = onNavigateToMemorialAdd,
            onNavigateToMemorialDialog = onNavigateToMemorialDialog,
            onNavigateToAccountAnalysis = onNavigateToAccountAnalysis,
            onNavigateToTransactionDialog = onNavigateToTransactionDialog,
            onNavigateToScheduleDialog = onNavigateToScheduleDialog
        )

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        DisposableEffect(context, scope) {
            val activity = (context as ComponentActivity)
            val listener = Consumer<Intent> { intent: Intent? ->
                val newHomePage = intent?.getIntExtra(KEY_HOME_PAGE, -1)
                    ?.takeIf { it != -1 } ?: return@Consumer

                if (pagerState.currentPage != newHomePage) {
                    scope.launch(Dispatchers.Main.immediate) {
                        pagerState.scrollToPage(newHomePage)
                    }
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }

        DisposableEffect(pagerState.currentPage) {
            viewModel.updateHomePage(pagerState.currentPage)
            onDispose { }
        }
    }

}
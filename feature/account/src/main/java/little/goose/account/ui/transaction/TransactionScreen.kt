package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.ui.component.IconsBoard
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    val viewModel: TransactionViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0)

    val transaction by viewModel.transaction.collectAsState()

    var expenseSelectedIcon by remember {
        mutableStateOf(
            if (transaction.type == EXPENSE)
                TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }!!
            else TransactionIconHelper.expenseIconList.first()
        )
    }
    var incomeSelectedIcon by remember {
        mutableStateOf(
            if (transaction.type == INCOME)
                TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }!!
            else TransactionIconHelper.incomeIconList.first()
        )
    }

    LaunchedEffect(transaction) {
        when (transaction.type) {
            EXPENSE -> {
                expenseSelectedIcon =
                    TransactionIconHelper.expenseIconList.find { it.id == transaction.icon_id }!!
                pagerState.animateScrollToPage(0)
            }

            INCOME -> {
                incomeSelectedIcon =
                    TransactionIconHelper.incomeIconList.find { it.id == transaction.icon_id }!!
                pagerState.animateScrollToPage(1)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            if (it == 0) {
                viewModel.setTransaction(
                    transaction.copy(
                        type = EXPENSE,
                        content = expenseSelectedIcon.name,
                        icon_id = expenseSelectedIcon.id
                    )
                )
            } else {
                viewModel.setTransaction(
                    transaction.copy(
                        type = INCOME,
                        content = incomeSelectedIcon.name,
                        icon_id = incomeSelectedIcon.id
                    )
                )
            }
        }
    }

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                TransactionViewModel.Event.WriteSuccess -> {
                    onFinished()
                }

                TransactionViewModel.Event.CantBeZero -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.money_cant_be_zero),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Snackbar(snackbarData = it)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    TabRow(
                        modifier = Modifier.width(120.dp),
                        selectedTabIndex = pagerState.currentPage,
                        divider = {}
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.expense),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.income),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onFinished) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        content = {
            HorizontalPager(
                modifier = Modifier.padding(it),
                pageCount = 2,
                state = pagerState
            ) { page ->
                if (page == 0) {
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.expenseIconList,
                        onIconClick = { icon ->
                            expenseSelectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = expenseSelectedIcon
                    )
                } else {
                    IconsBoard(
                        modifier = Modifier.fillMaxSize(),
                        icons = TransactionIconHelper.incomeIconList,
                        onIconClick = { icon ->
                            incomeSelectedIcon = icon
                            viewModel.setTransaction(
                                transaction.copy(icon_id = icon.id, content = icon.name)
                            )
                        },
                        selectedIcon = incomeSelectedIcon
                    )
                }
            }
        },
        bottomBar = {
            TransactionEditSurface(
                modifier = Modifier.navigationBarsPadding(),
                transaction = transaction,
                onTransactionChange = viewModel::setTransaction,
                onAgainClick = { viewModel.writeDatabase(it, isAgain = true) },
                onDoneClick = { viewModel.writeDatabase(it, isAgain = false) }
            )
        }
    )
}
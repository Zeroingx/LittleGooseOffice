package little.goose.account.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.MoneyCalculator
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseMonthDayTime
import little.goose.design.system.component.dialog.TimeSelectorBottomDialog
import little.goose.design.system.component.dialog.rememberBottomSheetDialogState
import java.math.BigDecimal
import kotlin.math.pow

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TransactionEditSurface(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onTransactionChange: (Transaction) -> Unit,
    onAgainClick: (Transaction) -> Unit,
    onDoneClick: (Transaction) -> Unit
) {
    val moneyCalculator = remember { MoneyCalculator(transaction.money) }

    DisposableEffect(transaction.money) {
        moneyCalculator.setMoney(transaction.money)
        onDispose { }
    }

    val currentTransaction by rememberUpdatedState(newValue = transaction)
    val isContainOperator by moneyCalculator.isContainOperator.collectAsState()
    val money by moneyCalculator.money.collectAsState()

    LaunchedEffect(moneyCalculator) {
        moneyCalculator.money.collect { moneyStr ->
            runCatching {
                BigDecimal(moneyStr)
            }.getOrNull()?.let { money ->
                onTransactionChange(currentTransaction.copy(money = money))
            }
        }
    }

    Column(modifier = modifier.animateContentSize(animationSpec = tween(200))) {

        val iconAndContent = remember(transaction.icon_id, transaction.content) {
            IconAndContent(transaction.icon_id, transaction.content)
        }
        TransactionContentItem(
            modifier = Modifier.fillMaxWidth(),
            iconAndContent = iconAndContent,
            money = money
        )

        val (isDescriptionEdit, onIsDescriptionEditChange) = remember { mutableStateOf(false) }
        TransactionContentEditBar(
            transaction = transaction,
            isDescriptionEdit = isDescriptionEdit,
            onIsDescriptionEditChange = onIsDescriptionEditChange,
            onTransactionChange = onTransactionChange
        )

        val density = LocalDensity.current
        Calculator(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (WindowInsets.isImeVisible && isDescriptionEdit) {
                    with(density) {
                        val bottom = WindowInsets.imeAnimationTarget
                            .exclude(WindowInsets.navigationBars)
                            .getBottom(density)
                            .toDp()
                        if (bottom > 0.dp) bottom else 288.dp
                    }
                } else 288.dp),
            onNumClick = {
                moneyCalculator.appendMoneyEnd(it.digitToChar())
            },
            onAgainClick = {
                moneyCalculator.operate()
                onAgainClick(transaction.copy(money = BigDecimal(moneyCalculator.money.value)))
            },
            onDoneClick = {
                moneyCalculator.operate()
                onDoneClick(transaction.copy(money = BigDecimal(moneyCalculator.money.value)))
            },
            onOperatorClick = moneyCalculator::modifyOther,
            isContainOperator = isContainOperator
        )

    }
}

private data class IconAndContent(
    val iconId: Int,
    val content: String
)

@Composable
private fun TransactionContentItem(
    modifier: Modifier = Modifier,
    iconAndContent: IconAndContent,
    money: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = iconAndContent,
                transitionSpec = {
                    val inDurationMillis = 180
                    val outDurationMillis = 160
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = inDurationMillis,
                            delayMillis = 36,
                            easing = LinearOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        towards = AnimatedContentScope.SlideDirection.Down,
                        animationSpec = tween(
                            durationMillis = inDurationMillis,
                            delayMillis = 36,
                            easing = LinearOutSlowInEasing
                        ),
                        initialOffset = { it / 2 }
                    ) with fadeOut(
                        animationSpec = tween(outDurationMillis, easing = LinearOutSlowInEasing)
                    ) + slideOutOfContainer(
                        towards = AnimatedContentScope.SlideDirection.Down,
                        animationSpec = tween(outDurationMillis, easing = LinearOutSlowInEasing),
                        targetOffset = { it / 2 }
                    )
                },
                label = "transaction content item"
            ) { iac ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(
                            id = TransactionIconHelper.getIconPath(iac.iconId)
                        ),
                        contentDescription = iac.content
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = iac.content)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = money, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun TransactionContentEditBar(
    modifier: Modifier = Modifier,
    isDescriptionEdit: Boolean,
    onIsDescriptionEditChange: (Boolean) -> Unit,
    transaction: Transaction,
    onTransactionChange: (Transaction) -> Unit
) {
    val timeSelectorDialogState = rememberBottomSheetDialogState()
    TimeSelectorBottomDialog(
        state = timeSelectorDialogState,
        initTime = transaction.time,
        type = TimeType.DATE_TIME,
        onConfirm = { onTransactionChange(transaction.copy(time = it)) }
    )

    val scope = rememberCoroutineScope()
    val isDescriptionEditUpdateTransition = updateTransition(
        targetState = isDescriptionEdit, label = "is description edit"
    )
    val contentBarHeight by isDescriptionEditUpdateTransition.animateDp(
        label = "content bar height",
        transitionSpec = {
            tween(60, delayMillis = if (targetState) 140 else 0)
        }
    ) { if (it) 84.dp else 42.dp }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(contentBarHeight)
    ) {
        val dateScale by isDescriptionEditUpdateTransition.animateFloat(
            label = "date scale",
            transitionSpec = { tween(140) }
        ) { if (it) 0.88F else 1F }
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .zIndex(1F),
            onClick = {
                scope.launch(Dispatchers.Main.immediate) {
                    if (timeSelectorDialogState.isClosed) {
                        timeSelectorDialogState.open()
                    } else {
                        timeSelectorDialogState.close()
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(dateScale)
                    .alpha(dateScale.pow(5))
                    .padding(top = 8.dp, bottom = 8.dp, start = 20.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Rounded.CalendarToday, contentDescription = "Calendar")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = transaction.time.toChineseMonthDayTime(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val configuration = LocalConfiguration.current
        val descriptionWidth by isDescriptionEditUpdateTransition.animateDp(
            label = "description width",
            transitionSpec = { tween(140) }
        ) { if (it) configuration.screenWidthDp.dp else (configuration.screenWidthDp / 2).dp }
        Surface(
            modifier = Modifier
                .width(descriptionWidth)
                .align(Alignment.CenterEnd)
                .zIndex(2F),
            onClick = { onIsDescriptionEditChange(!isDescriptionEdit) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (!isDescriptionEdit) {
                    Text(
                        text = transaction.description.ifBlank { "Description..." },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    val focusRequester = remember { FocusRequester() }
                    var textFieldValue by remember {
                        mutableStateOf(
                            TextFieldValue(
                                text = transaction.description,
                                selection = TextRange(
                                    start = 0,
                                    end = transaction.description.length
                                )
                            )
                        )
                    }

                    BasicTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(end = 32.dp),
                        value = textFieldValue,
                        textStyle = MaterialTheme.typography.bodySmall,
                        onValueChange = {
                            textFieldValue = it
                            onTransactionChange(transaction.copy(description = it.text))
                        },
                        maxLines = 2
                    )

                    DisposableEffect(transaction) {
                        if (textFieldValue.text != transaction.description) {
                            textFieldValue = textFieldValue.copy(text = transaction.description)
                        }
                        onDispose { }
                    }

                    DisposableEffect(focusRequester) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }

                    Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                        IconButton(onClick = { onIsDescriptionEditChange(false) }) {
                            Icon(imageVector = Icons.Rounded.Done, contentDescription = "Done")
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=380dp,height=480dp,dpi=440")
@Composable
private fun PreviewTransactionEditSurface() {
    TransactionEditSurface(
        transaction = Transaction(description = "description", content = "content"),
        onTransactionChange = {},
        onAgainClick = {},
        onDoneClick = {}
    )
}
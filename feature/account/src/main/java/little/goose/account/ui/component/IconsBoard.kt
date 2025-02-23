package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Composable
fun IconsBoard(
    modifier: Modifier,
    icons: List<TransactionIcon>,
    onIconClick: (TransactionIcon) -> Unit,
    selectedIcon: TransactionIcon
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(8.dp),
        reverseLayout = true
    ) {
        items(
            items = icons,
            key = { it.id }
        ) { transactionIcon ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .padding(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { onIconClick(transactionIcon) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIcon == transactionIcon) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = transactionIcon.path),
                            contentDescription = transactionIcon.name
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=380dp,height=400dp,dpi=440")
@Composable
private fun PreviewIconsBoard() {
    IconsBoard(
        modifier = Modifier.fillMaxSize(),
        icons = TransactionIconHelper.expenseIconList,
        onIconClick = {},
        selectedIcon = TransactionIconHelper.expenseIconList.first()
    )
}
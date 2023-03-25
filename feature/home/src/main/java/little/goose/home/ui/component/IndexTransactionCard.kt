package little.goose.home.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.TransactionActivity
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toDate
import java.math.BigDecimal
import java.time.LocalDate

data class IndexTransactionCardState(
    val expense: BigDecimal,
    val income: BigDecimal,
    val transactions: List<Transaction>,
    val currentTime: LocalDate
)

@Composable
internal fun IndexTransactionCard(
    modifier: Modifier,
    state: IndexTransactionCardState
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
    ) {
        if (state.transactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 10.dp)
            ) {
                Text(text = "收入 ${state.income}")
                Spacer(modifier = Modifier.weight(1F))
                Text(text = "支出 -${state.expense}")
            }
            IndexTransactionColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                transactions = state.transactions
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "今天没有记账")
                Spacer(modifier = Modifier.weight(1F))
                TextButton(onClick = {
                    TransactionActivity.openAdd(context, state.currentTime.toDate())
                }) {
                    Text(text = "记一笔")
                }
            }
        }
    }
}

@Composable
private fun IndexTransactionColumn(
    modifier: Modifier = Modifier,
    transactions: List<Transaction>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(transactions) { transaction ->
            IndexTransactionItem(
                modifier = Modifier.fillMaxWidth(),
                transaction = transaction
            )
        }
    }
}

@Composable
private fun IndexTransactionItem(
    modifier: Modifier = Modifier,
    transaction: Transaction
) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = TransactionIconHelper.getIconPath(transaction.icon_id)),
            modifier = Modifier.size(24.dp),
            contentDescription = transaction.content
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = transaction.content)
        Spacer(modifier = Modifier.weight(1F))
        Text(text = transaction.money.toPlainString())
    }
}
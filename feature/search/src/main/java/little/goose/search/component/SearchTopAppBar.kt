package little.goose.search.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme
import little.goose.design.system.theme.RoundedCornerShape32

@Composable
internal fun SearchTopAppBar(
    modifier: Modifier = Modifier,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = {
            val focusRequester = remember { FocusRequester() }
            TextField(
                value = keyword,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = onKeywordChange,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape32,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (keyword.isNotEmpty()) {
                        IconButton(
                            onClick = { onKeywordChange("") },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
            )
            DisposableEffect(focusRequester) {
                focusRequester.requestFocus()
                onDispose {}
            }
        }
    )
}

@Preview
@Composable
fun PreviewSearchTopAppBar() = AccountTheme {
    SearchTopAppBar(
        keyword = "Search keyword",
        onKeywordChange = {},
        onBack = {}
    )
}
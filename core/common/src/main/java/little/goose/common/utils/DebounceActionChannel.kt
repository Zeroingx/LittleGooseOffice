package little.goose.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
class DebounceActionChannel<T>(
    coroutineScope: CoroutineScope,
    debounceTime: Long = 500L,
    preEach: suspend (T) -> Unit = {},
    action: suspend (T) -> Unit,
) : Channel<T> by Channel(
    capacity = 0,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
) {
    init {
        consumeAsFlow()
            .onEach(preEach)
            .debounce(debounceTime)
            .onEach(action)
            .launchIn(coroutineScope)
    }
}
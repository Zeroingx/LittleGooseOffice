package little.goose.memorial.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_DELETE_ITEM
import little.goose.common.constants.NOTIFY_DELETE_MEMORIAL
import little.goose.common.localBroadcastManager
import little.goose.common.utils.UIUtils
import little.goose.common.utils.parcelable
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.R
import little.goose.memorial.data.constants.KEY_MEMORIAL
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.MemorialRepository
import little.goose.memorial.ui.widget.MemorialCard
import javax.inject.Inject

@AndroidEntryPoint
class MemorialDialogFragment : DialogFragment() {

    @Inject
    lateinit var memorialRepository: MemorialRepository

    private val memorial: Memorial by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.parcelable(KEY_MEMORIAL) ?: Memorial(null, "null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountTheme {
                    val deleteDialogState = rememberDialogState()
                    MaterialDialogScreen(
                        modifier = Modifier.fillMaxWidth(),
                        memorial = memorial,
                        onDelete = deleteDialogState::show,
                        onEdit = ::edit
                    )
                    DeleteDialog(
                        state = deleteDialogState,
                        onConfirm = ::deleteMemorial
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()
    }

    private fun deleteMemorial() {
        lifecycleScope.launch(NonCancellable) {
            val intent = Intent(NOTIFY_DELETE_MEMORIAL).apply {
                setPackage(`package`)
                putExtra(KEY_DELETE_ITEM, memorial)
            }
            memorialRepository.deleteMemorial(memorial)
            requireContext().localBroadcastManager.sendBroadcast(intent)
            dismiss()
        }
    }

    private fun edit() {
        MemorialShowActivity.open(requireContext(), memorial)
        dismiss()
    }

    private fun initWindow() {
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.window?.attributes?.apply {
            width = UIUtils.getWidthPercentPixel(0.78F)
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
    }

    companion object {
        fun newInstance(memorial: Memorial): MemorialDialogFragment {
            val bundle = Bundle().also { it.putParcelable(KEY_MEMORIAL, memorial) }
            return MemorialDialogFragment().apply { arguments = bundle }
        }
    }
}

@Composable
private fun MaterialDialogScreen(
    modifier: Modifier,
    memorial: Memorial,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier.clip(RoundedCornerShape(24.dp))) {
        MemorialCard(
            memorial = memorial,
            shape = RectangleShape
        )
        Row(modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onDelete,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onEdit,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        }
    }
}
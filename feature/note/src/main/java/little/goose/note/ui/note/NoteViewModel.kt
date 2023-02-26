package little.goose.note.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import little.goose.common.constants.TYPE_ADD
import little.goose.common.constants.TYPE_MODIFY
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val note: StateFlow<Note> = savedStateHandle.getStateFlow(KEY_NOTE, Note())

    var type = if (note.value.id == null) TYPE_ADD else TYPE_MODIFY

    private fun isNoteBlank() = note.value.title.isBlank() && note.value.content.isBlank()

    fun setNote(note: Note) {
        savedStateHandle[KEY_NOTE] = note
    }

    fun updateDatabase() {
        viewModelScope.launch {
            if (!isNoteBlank()) {
                noteRepository.updateNote(note.value)
            } else {
                noteRepository.deleteNote(note.value)
            }
        }
    }

    fun insertDatabase() {
        if (!isNoteBlank()) {
            viewModelScope.launch {
                val id = noteRepository.addNote(note.value)
                if (isActive) {
                    savedStateHandle[KEY_NOTE] = note.value.copy(id = id)
                    type = TYPE_MODIFY
                }
            }
        }
    }

}
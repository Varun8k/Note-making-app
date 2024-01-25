package com.example.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.models.NoteRequest
import com.example.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {
    val statuslivedata get() = repository.statusLiveData
    val noteslivedata get() = repository.notesLiveData

    fun getnotes() {
        viewModelScope.launch {
            repository.getnotes()
        }
    }
    fun createnotes(noteRequest: NoteRequest) {
        viewModelScope.launch {
            repository.createNote(noteRequest)
        }
    }
    fun deletenotes(noteId: String) {
        viewModelScope.launch {
            repository.deletenotes(noteId)
        }
    }
    fun updatenotes(noteId: String, noteRequest: NoteRequest) {
        viewModelScope.launch {
            repository.updatenotes(noteId, noteRequest)
        }
    }
}

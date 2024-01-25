package com.example.notes.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notes.api.NoteApi
import com.example.notes.models.NoteRequest
import com.example.notes.models.NoteResponse
import com.example.notes.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NotesRepository @Inject constructor(private val noteApi: NoteApi) {
    private val _statusLiveData = MutableLiveData<NetworkResult<String>>()
    val statusLiveData: LiveData<NetworkResult<String>> get() = _statusLiveData

    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData: LiveData<NetworkResult<List<NoteResponse>>>get() = _notesLiveData

    suspend fun getnotes() {
        _notesLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.getNotes()

        if (response.isSuccessful && response.body() != null) {
            _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val error = JSONObject(response.errorBody()!!.charStream().readText())
            _notesLiveData.postValue(NetworkResult.Error(error.getString("message")))
        } else {
            _notesLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.createNote(noteRequest)
        handleResponse(response, "Note Created")
    }
    suspend fun deletenotes(noteId: String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.deleteNote(noteId)
        handleResponse(response, "Note Deleted")
    }
    suspend fun updatenotes(noteId: String, noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.updateNote(noteId, noteRequest)
        handleResponse(response, "Note Updated")
    }
    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(message))
        } else {
            _statusLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }
}

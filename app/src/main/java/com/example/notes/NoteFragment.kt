package com.example.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentNoteBinding
import com.example.notes.models.NoteRequest
import com.example.notes.models.NoteResponse
import com.example.notes.utils.NetworkResult
import com.example.notes.viewmodel.NotesViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel by viewModels<NotesViewModel>()
    private var note: NoteResponse? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notejson = arguments?.getString("note")
        note = Gson().fromJson(notejson, NoteResponse::class.java)
        val noteheader = binding.addEditText
        val btndel = binding.btnDelete
        if (note != null) {
            btndel.visibility = View.VISIBLE
            noteheader.text = "Edit note"
            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.des)
            }
        } else {
            noteheader.text = "Add note"
            btndel.visibility = View.GONE
        }

        binding.btnDelete.setOnClickListener {
            notesViewModel.deletenotes(note!!._id)
        }
        binding.btnSubmit.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val des = binding.txtDescription.text.toString()
            val noteRequest = NoteRequest(title, des)
            if (note == null) {
                notesViewModel.createnotes(noteRequest)
            } else {
                notesViewModel.updatenotes(note!!._id, noteRequest)
            }
        }
        notesViewModel.statuslivedata.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        // Handle loading state, show progress, etc.
                    }
                    is NetworkResult.Success -> {
                        // Handle success, navigate back
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

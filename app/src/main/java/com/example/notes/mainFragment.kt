package com.example.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.api.NoteApi
import com.example.notes.databinding.FragmentMainBinding
import com.example.notes.models.NoteResponse
import com.example.notes.recyclerview.NotesAdapter
import com.example.notes.utils.Constant.TAG
import com.example.notes.utils.NetworkResult
import com.example.notes.utils.TokenManager
import com.example.notes.viewmodel.NotesViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class mainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val notesViewModel by viewModels<NotesViewModel>()
    private lateinit var adapter: NotesAdapter

    @Inject
    lateinit var noteApi: NoteApi

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = NotesAdapter(listOf(), ::onNoteClicked)
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make a network request using CoroutineScope
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = noteApi.getNotes()

                withContext(Dispatchers.Main) {
                    Log.d(TAG, response.body().toString())
                    notesViewModel.getnotes()
                    val re = binding.recyclerview
                    re.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    re.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching notes", e)
            }
        }

        notesViewModel.noteslivedata.observe(
            viewLifecycleOwner,
            Observer {
                binding.progressBar.isVisible = false
                when (it) {
                    is NetworkResult.Error -> Toast.makeText(
                        requireContext(),
                        it.data.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    is NetworkResult.Loading -> binding.progressBar.isVisible = true
                    is NetworkResult.Success -> {
                        it.data?.let { it1 -> adapter.updateList(it1) }
                    }
                }
            }
        )
        binding.logout.setOnClickListener { showPopupMenu(it) }
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_logout) // Create a menu resource file (res/menu/menu_logout.xml)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    tokenManager.deleteToken()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.loginFragment)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
    fun onNoteClicked(noteResponse: NoteResponse) {
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_mainFragment_to_noteFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

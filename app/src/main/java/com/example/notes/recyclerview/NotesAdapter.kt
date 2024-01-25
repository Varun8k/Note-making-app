package com.example.notes.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.models.NoteResponse

class NotesAdapter(private var dataSet: List<NoteResponse>, val onNoteClicked: (NoteResponse) -> Unit) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle = view.findViewById<TextView>(R.id.note_title)
        val noteDes = view.findViewById<TextView>(R.id.note_des)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.notes_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(Holder: ViewHolder, position: Int) {
        val currentnote = dataSet[position]
        Holder.noteTitle.text = currentnote.title
        Holder.noteDes.text = currentnote.des
        Holder.itemView.setOnClickListener {
            onNoteClicked(currentnote)
        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
    }
    fun updateList(newList: List<NoteResponse>) {
        dataSet = newList
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

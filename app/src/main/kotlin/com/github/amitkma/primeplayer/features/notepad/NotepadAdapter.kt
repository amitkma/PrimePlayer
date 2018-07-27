package com.github.amitkma.primeplayer.features.notepad

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.notepad.domain.model.Note
import javax.inject.Inject
import kotlin.properties.Delegates

class NotepadAdapter
@Inject constructor() :
        RecyclerView.Adapter<NotepadAdapter.ViewHolder>() {

    internal var list: List<Note> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (Note) -> Unit = { _ -> }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = list[position]
        holder.title.text = note.title
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notepad, parent, false)
        val viewHolder = ViewHolder(itemView)
        itemView.setOnClickListener { clickListener(list[viewHolder.adapterPosition]) }
        return viewHolder
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.noteTitleTextView)

    }

}
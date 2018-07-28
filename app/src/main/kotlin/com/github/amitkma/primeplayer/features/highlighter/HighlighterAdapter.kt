package com.github.amitkma.primeplayer.features.bookmarks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.framework.extension.loadFromUrl
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by falcon on 17/1/18.
 */
class HighlighterAdapter
@Inject constructor() :
        RecyclerView.Adapter<HighlighterAdapter.ViewHolder>() {

    internal var list: List<HighlightedItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (HighlightedItem) -> Unit = { _ -> }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = list[position]
        holder.poster.loadFromUrl(bookmark.thumbnail)
        holder.title.text = bookmark.name
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_row_highlighted, parent, false)
        val viewHolder = ViewHolder(itemView)
        itemView.setOnClickListener { clickListener(list[viewHolder.adapterPosition]) }
        return viewHolder
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var poster: ImageView = itemView.findViewById(R.id.highlightedPoster)
        var title: TextView = itemView.findViewById(R.id.highlightedTitle)

    }

}
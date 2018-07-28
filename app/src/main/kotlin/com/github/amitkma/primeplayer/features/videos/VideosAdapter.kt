package com.github.amitkma.primeplayer.features.videos

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.framework.extension.loadFromUrl
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by falcon on 15/1/18.
 */
class VideosAdapter
@Inject constructor() :
        RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    internal var list: List<Video> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (Video) -> Unit = { _ -> }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = list[position]
        holder.poster.loadFromUrl(video.thumbnail)
    }

    override fun getItemCount(): Int = list.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_row_video, parent, false)
        val viewHolder = ViewHolder(itemView)
        itemView.setOnClickListener { clickListener(list[viewHolder.adapterPosition]) }
        return viewHolder
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var poster: ImageView = itemView.findViewById(R.id.videoPoster)

    }

}
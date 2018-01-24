package com.github.amitkma.primeplayer.features.addbookmarks.domain.model

/**
 * Created by falcon on 23/1/18.
 */
data class BookmarkParam(
        var path: String,
        var name: String,
        var thumbnail: String,
        var resumeWindow: Int,
        var resumePosition: Long)
package com.github.amitkma.primeplayer.features.videos.data

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import javax.inject.Inject

/**
 * Created by falcon on 15/1/18.
 *
 * Repository to provide videos from storage.
 */
class VideosRepository
@Inject constructor(private val context: Context) {

    /**
     * Empty list of [Video].
     */
    private var videos = mutableListOf<Video>()

    /**
     * @return List of [Video] fetched from storage.
     */
    fun videos(): List<Video> {
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.TITLE)

        // Sort on the basis of date taken ie from most recent to least recent.
        val orderBy = MediaStore.Video.Media.DATE_MODIFIED
        val cursor = context.contentResolver.query(uri, projection, null, null,
                orderBy + " DESC")

        val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val columnIndexThumbnail = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        val columnIndexTitle = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
        while (cursor.moveToNext()) {
            val video = Video(cursor.getString(columnIndexTitle),
                    cursor.getString(columnIndexData), cursor.getString(columnIndexThumbnail))
            videos.add(video)
        }
        cursor.close() // Close and invalidate the cursor.
        return videos
    }
}
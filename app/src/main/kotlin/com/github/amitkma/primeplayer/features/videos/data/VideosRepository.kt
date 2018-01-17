package com.github.amitkma.primeplayer.features.videos.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import javax.inject.Inject

/**
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
        val position = 0
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val columnId: Int
        val thumbnail: Int

        var absolutePathOfVideo: String?

        val projection = arrayOf(MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.TITLE)

        // Sort on the basis of date taken ie from most recent to least recent.
        val orderBy = MediaStore.Video.Media.DATE_MODIFIED
        val cursor = context.contentResolver.query(uri, projection, null, null,
                orderBy + " DESC")

        val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val columnIndexFolderName = cursor.getColumnIndexOrThrow(
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val columnIndexThumbnail = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        val columnIndexDisplayName = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
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
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
        val cursor: Cursor
        val columnIndexData: Int
        val columnIndexFolderName: Int
        val columnId: Int
        val thumbnail: Int

        var absolutePathOfVideo: String?

        val projection = arrayOf(MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA)

        // Sort on the basis of date taken ie from most recent to least recent.
        val orderBy = MediaStore.Video.Media.DATE_MODIFIED
        cursor = context.contentResolver.query(uri, projection, null, null,
                orderBy + " DESC")

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        columnIndexFolderName = cursor.getColumnIndexOrThrow(
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        columnId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thumbnail = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)

        while (cursor.moveToNext()) {
            absolutePathOfVideo = cursor.getString(columnIndexData)
            Log.e("Column", absolutePathOfVideo)
            Log.e("Folder", cursor.getString(columnIndexFolderName))
            Log.e("column_id", cursor.getString(columnId))
            Log.e("thum", cursor.getString(thumbnail))

            val video = Video(
                    absolutePathOfVideo, cursor.getString(thumbnail))
            videos.add(video)
        }
        cursor.close() // Close and invalidate the cursor.
        return videos
    }
}
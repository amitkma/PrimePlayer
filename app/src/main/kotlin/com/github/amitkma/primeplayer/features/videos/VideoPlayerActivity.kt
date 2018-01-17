package com.github.amitkma.primeplayer.features.videos

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.bookmark.AddBookmarkDialog
import com.github.amitkma.primeplayer.features.bookmark.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.bookmark.domain.usecase.AddBookmarkUseCase
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.Util
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 */
class VideoPlayerActivity : AppCompatActivity(), AddBookmarkDialog.AddBookmarkDialogListener {

    @Inject lateinit var addBookmarkUsecase: AddBookmarkUseCase

    private var exoPlayer: SimpleExoPlayer? = null

    private var shouldAutoPlay: Boolean = true

    private lateinit var videoName: String

    private lateinit var thumbnail: String

    private lateinit var path: String

    private var resumePosition: Long = 0

    private var resumeWindow: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        releasePlayer()
        shouldAutoPlay = true
        setContentView(R.layout.activity_video_player)
        setSupportActionBar(toolbar)
        if(intent != null){
            initializePlayer()
        }

    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector())
        exoPlayer!!.addListener(eventListener)

        path = intent.getStringExtra("video_path")
        val uri = Uri.fromFile(File(path))

        videoName = intent.getStringExtra("video_name")
        title = videoName
        thumbnail = intent.getStringExtra("video_thumb")
        resumeWindow = intent.getIntExtra("resume_window", C.INDEX_UNSET)
        resumePosition = intent.getLongExtra("resume_position", C.TIME_UNSET)


        val dataSpec = DataSpec(uri)
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSource.FileDataSourceException) {
            e.printStackTrace()
        }

        val factory: DataSource.Factory = DataSource.Factory { fileDataSource }
        val videoSource: MediaSource = ExtractorMediaSource.Factory(factory).createMediaSource(uri)
        playerView.player = exoPlayer
        exoPlayer!!.playWhenReady = shouldAutoPlay
        val haveResumePosition: Boolean = resumeWindow != C.INDEX_UNSET
        if (haveResumePosition) {
            exoPlayer!!.seekTo(resumeWindow, resumePosition)
        }
        exoPlayer!!.prepare(videoSource, !haveResumePosition, false)

    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            shouldAutoPlay = exoPlayer!!.playWhenReady
            exoPlayer!!.release()
            exoPlayer = null
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            Timber.d("playbackParameters " + String.format(
                    "[speed=%.2f, pitch=%.2f]", playbackParameters!!.speed,
                    playbackParameters.pitch))
        }

        override fun onSeekProcessed() {
            Timber.d("seekProcessed")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?) {
            Timber.d("onTracksChanged")
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Timber.e(error, "playerError")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Timber.d("Loading [$isLoading]")
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Timber.d("positionDiscontinuity [$reason]")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Timber.d("repeatMode [$repeatMode]")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Timber.d("shuffleModeEnabled [$shuffleModeEnabled]")
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Timber.d("onTimelineChanged")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Timber.d("state [playWhenReady = $playWhenReady, playbackState = $playbackState]")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_video_player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.item_bookmark_add_menu -> {
                resumeWindow = exoPlayer!!.currentWindowIndex
                resumePosition = Math.max(0, exoPlayer!!.contentPosition)
                exoPlayer!!.playWhenReady = false
                val dialogFragment = AddBookmarkDialog.newInstance(
                        videoName + " Bookmark")
                dialogFragment.show(fragmentManager, "bookmark_dialog")
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onDialogAddClick(bookmarkName: String) {
        val bookmark = AddBookmarkUseCase.BookmarkParam(path, videoName, thumbnail, resumeWindow, resumePosition)
        exoPlayer!!.playWhenReady = true
        addBookmarkUsecase.execute(bookmark, UseCaseCallbackWrapper(bookmarkName))

    }

    override fun onDialogCancelClick() {
        exoPlayer!!.playWhenReady = true
        Toast.makeText(this, "Bookmark cancelled", Toast.LENGTH_SHORT).show()
    }
    inner class UseCaseCallbackWrapper(val name: String) : UseCase.UseCaseCallback<UseCase.None> {
        override fun onSuccess(response: UseCase.None) {
            Toast.makeText(this@VideoPlayerActivity, "$name added", Toast.LENGTH_SHORT).show()
        }

        override fun onError(message: String) {
        }
    }
}
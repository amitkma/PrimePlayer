package com.github.amitkma.primeplayer.features.videoplayer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.Toast
import com.github.amitkma.calculator.Calculator
import com.github.amitkma.dictionary.Dictionary
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.addbookmarks.AddBookmarkDialog
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.framework.extension.convertToPx
import com.github.amitkma.primeplayer.framework.extension.convertToString
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.item_playback_control.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 */
class VideoPlayerActivity : AppCompatActivity(), AddBookmarkDialog.AddBookmarkDialogListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var videoPlayerViewModel: VideoPlayerViewModel

    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var videoName: String

    private lateinit var thumbnail: String

    private lateinit var path: String

    private var resumePosition: Long = 0

    private var resumeWindow: Int = 0

    private var handler: Handler? = null

    private var mBound: Boolean = false
    @Inject
    lateinit var popupBookmarkAdapter: PopupBookmarkAdapter

    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        AndroidInjection.inject(this)
        releasePlayer()
        videoPlayerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VideoPlayerViewModel::class.java)
        if (intent != null) {
            initializePlayer()
        }
        initializeView()
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }


    private fun initializeView() {
        playerView.setOnTouchListener({ _, e ->
            if (mBound) {
                unbindService(connection)
                mBound = false
            } else if (popupWindow != null) {
                Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show()
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                playerView.onTouchEvent(e)
            }
            true
        })
        addBookmarkImageButton.setOnClickListener {
            resumeWindow = exoPlayer!!.currentWindowIndex
            resumePosition = Math.max(0, exoPlayer!!.contentPosition)
            setPlayPause(false)
            val dialogFragment = AddBookmarkDialog.newInstance(
                    videoName + " Bookmark")
            dialogFragment.show(fragmentManager, "bookmark_dialog")
        }

        calculatorImageView.setOnClickListener {
            if (!mBound) {
                val intent = Intent(this, Calculator::class.java)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            } else if (popupWindow != null) {
                Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show()
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                unbindService(connection)
                mBound = false
            }
        }

        dictionaryImageView.setOnClickListener {
            if (!mBound) {
                val intent = Intent(this, Dictionary::class.java)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            } else if (popupWindow != null) {
                Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show()
                popupWindow!!.dismiss()
                popupWindow = null

            } else {
                unbindService(connection)
                mBound = false
            }
        }

        bookmarkListImageView.setOnClickListener {
            if (mBound) {
                unbindService(connection)
                mBound = false
            } else if (popupWindow != null) {
                Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show()
                popupWindow!!.dismiss()
                popupWindow = null
            } else {
                createBookmarkPopupWindow()
            }
        }
    }

    private fun createBookmarkPopupWindow() {
        try {
            val inflater: LayoutInflater = this.getSystemService(
                    android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.popup_bookmark, null)
            popupWindow = PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true)
            popupWindow!!.width = 360.convertToPx().toInt()
            val recyclerView = view.findViewById<RecyclerView>(R.id.bookmarkPopupRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = popupBookmarkAdapter
            popupBookmarkAdapter.clickListener = {
                exoPlayer!!.seekTo(it.resumeWindow, it.resumePosition)
            }
            val observer = Observer<List<Bookmark>> {
                if (it != null && it.isNotEmpty()) {
                    popupBookmarkAdapter.list = it
                } else if (it != null && it.isEmpty()) {
                    popupWindow!!.dismiss()
                    Toast.makeText(this, "No bookmark for this video", Toast.LENGTH_SHORT).show()
                }
            }
            videoPlayerViewModel.getVideoBookmarks(path).observe(this, observer)

            popupWindow!!.setOnDismissListener {
                if (videoPlayerViewModel.getVideoBookmarks(path).hasObservers()) {
                    videoPlayerViewModel.getVideoBookmarks(path).removeObserver(observer)
                }
            }
            popupWindow!!.showAtLocation(bookmarkListImageView, Gravity.RIGHT, 0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        }

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


        val dataSource: MediaSource = prepareDataSource(uri)

        playerView.player = exoPlayer
        setPlayPause(true)
        val haveResumePosition: Boolean = resumeWindow != C.INDEX_UNSET
        if (haveResumePosition) {
            exoPlayer!!.seekTo(resumeWindow, resumePosition)
        }
        mediaControllerProgress.requestFocus()
        mediaControllerProgress.progress = 0
        mediaControllerProgress.max = (exoPlayer!!.duration / 1000).toInt()
        mediaControllerProgress.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                            fromUser: Boolean) {
                        if (!fromUser) {
                            return
                        }
                        exoPlayer!!.seekTo((progress * 1000).toLong())
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                })
        exoPlayer!!.prepare(dataSource, !haveResumePosition, false)
    }

    private fun prepareDataSource(uri: Uri): MediaSource {
        val dataSpec = DataSpec(uri)
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSource.FileDataSourceException) {
            e.printStackTrace()
        }

        val factory: DataSource.Factory = DataSource.Factory { fileDataSource }
        return ExtractorMediaSource.Factory(factory).createMediaSource(uri)
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            setPlayPause(false)
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
            when (playbackState) {
                Player.STATE_ENDED -> {
                    setPlayPause(false)
                    exoPlayer!!.seekTo(0)
                }
                Player.STATE_READY -> {
                    setPlayerProgress()
                }
                Player.STATE_BUFFERING -> {
                    Timber.d("Playback buffering")
                }
                Player.STATE_IDLE -> {
                    Timber.d("Playback idle")
                }
            }
        }
    }

    private fun setPlayerProgress() {
        currentTimeTextView.text = exoPlayer!!.contentPosition.toInt().convertToString()

        if (handler == null) handler = Handler()

        handler!!.post(object : Runnable {
            override fun run() {
                if (exoPlayer != null && exoPlayer!!.playWhenReady) {
                    mediaControllerProgress.max = (exoPlayer!!.duration / 1000).toInt()
                    mediaControllerProgress.progress = (exoPlayer!!.contentPosition / 1000).toInt()
                    currentTimeTextView.text = exoPlayer!!.contentPosition.toInt().convertToString()
                    endTimeTextView.text = exoPlayer!!.duration.toInt().convertToString()
                    handler!!.postDelayed(this, 1000)
                }
            }

        })
    }

    private fun setPlayPause(play: Boolean) {
        exoPlayer!!.playWhenReady = play
    }

    override fun onDialogAddClick(bookmarkName: String) {
        setPlayPause(true)
        videoPlayerViewModel.addBookmark(path, bookmarkName, thumbnail, resumeWindow,
                resumePosition)
        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
    }

    override fun onDialogCancelClick() {
        setPlayPause(true)
        Toast.makeText(this, "Bookmark cancelled", Toast.LENGTH_SHORT).show()
    }
}
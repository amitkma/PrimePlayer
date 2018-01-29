package com.github.amitkma.primeplayer.features.videoplayer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.PixelFormat
import android.inputmethodservice.Keyboard
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import com.github.amitkma.calculator.Calculator
import com.github.amitkma.dictionary.Dictionary
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.addbookmarks.AddBookmarkDialog
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.framework.extension.convertToString
import com.github.amitkma.web.WebService
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

    private var shouldPlay: Boolean = true

    private var mBound: Boolean = false
    @Inject
    lateinit var popupBookmarkAdapter: PopupBookmarkAdapter

    private val TAG: String = "VideoPlayerActivity";

    private var mWindowManager: WindowManager? = null

    private lateinit var popupView: View

    private var isShowingPopup: Boolean = false

    private lateinit var params: WindowManager.LayoutParams

    private val CALCULATOR_VIEW = 101
    private val DICTIONARY_VIEW = 102
    private val BOOKMARK_LIST_VIEW = 103
    private val SEARCH_VIEW = 104

    private var CURRENTLY_VISIBLE = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_video_player)
        AndroidInjection.inject(this)
        clearPlayerParams()
        if (intent != null) {
            Timber.d("intent not null")
            path = intent.getStringExtra("video_path")
            videoName = intent.getStringExtra("video_name")
            thumbnail = intent.getStringExtra("video_thumb")
            resumeWindow = intent.getIntExtra("resume_window", C.INDEX_UNSET)
            resumePosition = intent.getLongExtra("resume_position", C.TIME_UNSET)
        }
        videoPlayerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VideoPlayerViewModel::class.java)
        initializeView()
    }

    private fun clearPlayerParams() {
        resumeWindow = C.INDEX_UNSET
        resumePosition = C.TIME_UNSET
        shouldPlay = true
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ");
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ");
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ");
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop: ");
        if (isShowingPopup) {
            removeView()
        }
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ");
    }

    private fun initializeView() {

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
                320, 450,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT)

        //Specify the popupView position
        params.gravity = Gravity.TOP or Gravity.RIGHT   //Initially popupView will be added to top-left corner
        params.x = 105
        params.y = 130
        params.dimAmount = 0.3f

        addBookmarkImageButton.setOnClickListener {
            resumeWindow = exoPlayer!!.currentWindowIndex
            resumePosition = Math.max(0, exoPlayer!!.contentPosition)
            shouldPlay = exoPlayer!!.playWhenReady
            setPlayPause(false)
            val dialogFragment = AddBookmarkDialog.newInstance(
                    videoName + " Bookmark")
            dialogFragment.show(fragmentManager, "bookmark_dialog")
        }

        playerView.setOnTouchListener({ _, e ->
            if (isShowingPopup) {
                removeView()
            } else {
                playerView.onTouchEvent(e)
            }
            true
        })

        calculatorImageView.setOnClickListener {
            val view = Calculator(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != CALCULATOR_VIEW) {
                removeView()
                popupView = view.view
                addView(CALCULATOR_VIEW)
            } else if (CURRENTLY_VISIBLE != CALCULATOR_VIEW) {
                popupView = view.view
                addView(CALCULATOR_VIEW)
            }
        }

        dictionaryImageView.setOnClickListener {
            val view = Dictionary(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != DICTIONARY_VIEW) {
                removeView()
                popupView = view.view
                addView(DICTIONARY_VIEW)
            } else if (CURRENTLY_VISIBLE != DICTIONARY_VIEW) {
                popupView = view.view
                addView(DICTIONARY_VIEW)
            }
        }

        searchImageView.setOnClickListener {
            val view = WebService(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != SEARCH_VIEW) {
                removeView()
                popupView = view.view
                addView(SEARCH_VIEW)
            } else if (CURRENTLY_VISIBLE != SEARCH_VIEW) {
                popupView = view.view
                addView(SEARCH_VIEW)
            }
        }

        bookmarkListImageView.setOnClickListener {
            if (isShowingPopup && CURRENTLY_VISIBLE != BOOKMARK_LIST_VIEW) {
                removeView()
                popupView = createBookmarkPopupWindow()
                addView(BOOKMARK_LIST_VIEW)
            } else if (CURRENTLY_VISIBLE != BOOKMARK_LIST_VIEW) {
                popupView = createBookmarkPopupWindow()
                addView(BOOKMARK_LIST_VIEW)
            }
        }
    }

    private fun createBookmarkPopupWindow(): View {

        val inflater: LayoutInflater = this.getSystemService(
                android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_bookmark, null)

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
                if (CURRENTLY_VISIBLE == BOOKMARK_LIST_VIEW)
                    removeView()
                Toast.makeText(this, "No bookmark for this video", Toast.LENGTH_SHORT).show()
            }
        }
        videoPlayerViewModel.getVideoBookmarks(path).observe(this, observer)
        return view
    }

    private fun removeView() {
        mWindowManager!!.removeView(popupView)
        isShowingPopup = false
        CURRENTLY_VISIBLE = Int.MIN_VALUE
    }

    private fun addView(visibleView: Int) {
        mWindowManager!!.addView(popupView, params)
        isShowingPopup = true
        CURRENTLY_VISIBLE = visibleView
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            setPlayPause(!exoPlayer!!.playWhenReady)
            return true
        }
        else if (keyCode == KeyEvent.KEYCODE_C) {
            val view = Calculator(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != CALCULATOR_VIEW) {
                removeView()
                popupView = view.view
                addView(CALCULATOR_VIEW)
            } else if (CURRENTLY_VISIBLE != CALCULATOR_VIEW) {
                popupView = view.view
                addView(CALCULATOR_VIEW)
            }
            return true
        }
        else if (keyCode == KeyEvent.KEYCODE_D) {
            val view = Dictionary(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != DICTIONARY_VIEW) {
                removeView()
                popupView = view.view
                addView(DICTIONARY_VIEW)
            } else if (CURRENTLY_VISIBLE != DICTIONARY_VIEW) {
                popupView = view.view
                addView(DICTIONARY_VIEW)
            }
            return true
        }
        else if (keyCode == KeyEvent.KEYCODE_G) {
            val view = WebService(this)
            if (isShowingPopup && CURRENTLY_VISIBLE != SEARCH_VIEW) {
                removeView()
                popupView = view.view
                addView(SEARCH_VIEW)
            } else if (CURRENTLY_VISIBLE != SEARCH_VIEW) {
                popupView = view.view
                addView(SEARCH_VIEW)
            }
            return true
        }else
            return super.onKeyUp(keyCode, event)
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, DefaultTrackSelector())
        exoPlayer!!.addListener(eventListener)

        val uri = Uri.fromFile(File(path))
        val dataSource: MediaSource = prepareDataSource(uri)

        playerView.player = exoPlayer
        setPlayPause(shouldPlay)

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
            shouldPlay = exoPlayer!!.playWhenReady
            resumeWindow = exoPlayer!!.currentWindowIndex
            resumePosition = Math.max(0, exoPlayer!!.contentPosition)
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
            setPlayerProgress()
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
                if (exoPlayer != null) {
                    mediaControllerProgress.max = (exoPlayer!!.duration / 1000).toInt()
                    mediaControllerProgress.progress = (exoPlayer!!.contentPosition / 1000).toInt()
                    currentTimeTextView.text = exoPlayer!!.contentPosition.toInt().convertToString()
                    endTimeTextView.text = exoPlayer!!.duration.toInt().convertToString()
                    if (exoPlayer!!.playWhenReady) {
                        handler!!.postDelayed(this, 1000)
                    }
                }
            }

        })
    }

    private fun setPlayPause(play: Boolean) {
        exoPlayer!!.playWhenReady = play
    }

    override fun onDialogAddClick(bookmarkName: String) {
        setPlayPause(shouldPlay)
        videoPlayerViewModel.addBookmark(path, bookmarkName, thumbnail, resumeWindow,
                resumePosition)
        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
    }

    override fun onDialogCancelClick() {
        setPlayPause(shouldPlay)
        Toast.makeText(this, "Bookmark cancelled", Toast.LENGTH_SHORT).show()
    }
}
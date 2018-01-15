package com.github.amitkma.primeplayer.features.videos

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.github.amitkma.primeplayer.R
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.framework.extension.verifyPermissions
import com.github.amitkma.primeplayer.framework.vo.Resource
import com.github.amitkma.primeplayer.framework.vo.ResourceState
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class VideosActivity : AppCompatActivity() {

    /**
     * ViewModelFactory [com.github.amitkma.primeplayer.framework.viewmodel.PrimePlayerViewModelFactory]
     * to provide the ViewModel
     */
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    /**
     * ViewModel to handle view inside this activity.
     */
    private lateinit var videoViewModel: VideoViewModel
    /**
     * Id to identify a storage permission request.
     */
    private val REQUEST_STORAGE = 0

    /**
     * Permissions required to read and write media files from Storage.
     */
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * Log Tag
     */
    private val TAG: String = "VideosActivity"

    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inject this activity using Dagger
        AndroidInjection.inject(this)

        videoViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(VideoViewModel::class.java)

        if (hasPermission()) {
            videoViewModel.getVideos().observe(this, Observer<Resource<List<Video>>> {
                if (it != null) this.handleDataState(it.state, it.data, it.message)
            })
        } else {
            requestPermission()
        }
    }

    /**
     * Handles the state of the Livedata provided by the ViewModel3
     */
    private fun handleDataState(resourceState: ResourceState, data: List<Video>?,
            message: String?) {
        when (resourceState) {
            ResourceState.LOADING -> setupScreenForLoadingState()
            ResourceState.SUCCESS -> setupScreenForSuccess(data)
            ResourceState.ERROR -> setupScreenForError(message)
        }
    }

    private fun setupScreenForLoadingState() {
        Timber.d("LOADING SCREEN")
        // TODO: Show Loading ui.
    }

    private fun setupScreenForSuccess(data: List<Video>?) {
        Timber.d("SUCCESS SCREEN")
        if (data != null && data.isNotEmpty()) {
            Timber.d("data lengths is " + data.size)
            // TODO: Show videos list to view.
        } else {
            Timber.d("data is empty")
            // TODO: Show empty list.
        }
    }

    private fun setupScreenForError(message: String?) {
        Timber.d("ERROR SCREEN")
        // TODO: Show error screen.
    }

    private fun setupViewListeners() {
        // TODO: Set view click listener.
    }

    /*private val emptyListener = object : EmptyListener {
        override fun onCheckAgainClicked() {
            browseBufferoosViewModel.fetchBufferoos()
        }
    }

    private val errorListener = object : ErrorListener {
        override fun onTryAgainClicked() {
            browseBufferoosViewModel.fetchBufferoos()
        }
    }*/

    /**
     * Check whether permissions are granted or not.
     * @return True if permissions are granted.
     */
    private fun hasPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Requests the Storage permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                PERMISSIONS_STORAGE[0]) || ActivityCompat.shouldShowRequestPermissionRationale(
                this, PERMISSIONS_STORAGE[1])) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG, "Displaying storage permission rationale to provide additional context.")

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(view,
                    R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, {
                        ActivityCompat.requestPermissions(this@VideosActivity, PERMISSIONS_STORAGE,
                                REQUEST_STORAGE)
                    }).show()
        } else {
            // Storage permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.verifyPermissions()) {
                videoViewModel.getVideos().observe(this, Observer<Resource<List<Video>>> {
                    if (it != null) this.handleDataState(it.state, it.data, it.message)
                })
            } else {
                Snackbar.make(view, "Storage permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show()
                //TODO: Show permission denied.
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}

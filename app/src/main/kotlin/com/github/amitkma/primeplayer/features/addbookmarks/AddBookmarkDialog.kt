package com.github.amitkma.primeplayer.features.addbookmarks

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import com.github.amitkma.primeplayer.R
import kotlinx.android.synthetic.main.dialog_add_bookmark.view.*

/**
 * Created by falcon on 17/1/18.
 */
class AddBookmarkDialog : DialogFragment() {

    private lateinit var defaultBookmarkName: String
    private lateinit var mListener: AddBookmarkDialogListener

    companion object {

        private val DEFAULT_BOOKMARK_NAME_KEY: String = "default_bookmark_name_key"

        fun newInstance(defaultBookmarkName: String): AddBookmarkDialog {
            val addBookmarkDialog = AddBookmarkDialog()
            val args = Bundle()
            args.putString(
                    DEFAULT_BOOKMARK_NAME_KEY, defaultBookmarkName)
            addBookmarkDialog.arguments = args
            return addBookmarkDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            defaultBookmarkName = arguments.getString(
                    DEFAULT_BOOKMARK_NAME_KEY)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AddBookmarkDialogListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    context.toString() + " must implement AddBookmarkDialog")
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity is AddBookmarkDialogListener) {
                mListener = activity
            } else {
                throw RuntimeException(
                        activity.toString() + " must implement AddBookmarkDialog")
            }
        }
    }


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_add_bookmark, null)
        view.bookmarkNameEditText.setText(defaultBookmarkName)
        builder.setView(view)
                .setPositiveButton(R.string.add, { _, _ ->
                    mListener.onDialogAddClick(view.bookmarkNameEditText.text.toString().trim())
                })
                .setNegativeButton(R.string.cancel, { _, _ ->
                    mListener.onDialogCancelClick()
                })

        return builder.create()
    }

    interface AddBookmarkDialogListener {
        fun onDialogAddClick(bookmarkName: String)
        fun onDialogCancelClick()
    }
}
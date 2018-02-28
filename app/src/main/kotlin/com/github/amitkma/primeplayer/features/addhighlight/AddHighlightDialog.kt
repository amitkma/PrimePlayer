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
import kotlinx.android.synthetic.main.dialog_add_highlight.view.*

/**
 * Created by falcon on 17/1/18.
 */
class AddHighlightDialog : DialogFragment() {

    private lateinit var defaultHighlightName: String
    private lateinit var mListener: AddHighlightListener

    companion object {

        private val DEFAULT_BOOKMARK_NAME_KEY: String = "default_bookmark_name_key"

        fun newInstance(defaultHighlightName: String): AddHighlightDialog {
            val addHighlightDialog = AddHighlightDialog()
            val args = Bundle()
            args.putString(
                    DEFAULT_BOOKMARK_NAME_KEY, defaultHighlightName)
            addHighlightDialog.arguments = args
            return addHighlightDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            defaultHighlightName = arguments.getString(
                    DEFAULT_BOOKMARK_NAME_KEY)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AddHighlightListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    context.toString() + " must implement "+AddHighlightListener::class.qualifiedName)
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity is AddHighlightListener) {
                mListener = activity
            } else {
                throw RuntimeException(
                        activity.toString() + " must implement "+AddHighlightListener::class.qualifiedName)
            }
        }
    }


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_add_highlight, null)
        view.highlightNameEditText.setText(defaultHighlightName)
        builder.setView(view)
                .setPositiveButton(R.string.add, { _, _ ->
                    mListener.onHighlightAddClick(view.highlightNameEditText.text.toString().trim())
                })
                .setNegativeButton(R.string.cancel, { _, _ ->
                    mListener.onHighlightCancelClick()
                })

        return builder.create()
    }

    interface AddHighlightListener {
        fun onHighlightAddClick(bookmarkName: String)
        fun onHighlightCancelClick()
    }
}
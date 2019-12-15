package com.iqbal.app.mikrotikmonitor

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

class ContextProvider : ContentProvider() {
    companion object {
        private val TAG = ContextProvider::class.java.simpleName
    }

    override fun onCreate(): Boolean {
        context?.let {
            Common.setContext(it)
            return true
        }

        return false
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}

object Common {
    @Volatile
    lateinit var appContext: Context

    fun setContext(context: Context) {
        appContext = context
    }
}
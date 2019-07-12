package ru.sccraft.arenacontrol

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

internal class Fe(a: Context) {
    private var fos: FileOutputStream? = null
    var a: ContextWrapper = ContextWrapper(a.applicationContext)

    fun saveFile(name: String, content: String) {

        try {
            fos = a.openFileOutput(name, MODE_PRIVATE)
            fos!!.write(content.toByteArray())
            Log.d(LOG_TAG, "File saved")
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            try {
                if (fos != null)
                    fos!!.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
    }

    companion object {
        private val LOG_TAG = "Fe"
    }
}

package ru.sccraft.arenacontrol

import android.content.Context
import android.content.ContextWrapper
import android.util.Log

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

import android.content.Context.MODE_PRIVATE

/**
 * Created by alexandr on 03.06.17.
 * Операции с файлами
 */

class Fe(a: Context) {
    private var fin: FileInputStream? = null
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

    fun getFile(name: String): String {
        try {
            fin = a.openFileInput(name)
            val bytes = ByteArray(fin!!.available())
            fin!!.read(bytes)
            val text = String(bytes)
            Log.d(LOG_TAG, "Из файла \"$name\" получен текст: $text")
            return text
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "File read error"
        } finally {

            try {
                if (fin != null)
                    fin!!.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
    }

    companion object {
        private const val LOG_TAG = "Fe"
    }
}

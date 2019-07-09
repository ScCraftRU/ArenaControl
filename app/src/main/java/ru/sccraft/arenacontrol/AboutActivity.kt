package ru.sccraft.arenacontrol

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class AboutActivity : AppCompatActivity() {

    internal var номер_сборки = 0 //VersionCode
    internal var название_версии = "" //VersionName
    internal lateinit var vc: TextView
    internal lateinit var vn: TextView
    private var кликни_пять_раз_не_поворачивая_экран: Byte = 0
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupActionBar()
        title = getString(R.string.about)
        toast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT)
        try {
            название_версии = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        try {
            номер_сборки = packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        vc = findViewById(R.id.aboutVersionCode)
        vn = findViewById(R.id.aboutVN)
        vc.text = номер_сборки.toString()
        vn.text = название_версии
        кликни_пять_раз_не_поворачивая_экран = 0
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun rateAPP(view: View) {
        showDialog(1)
    }

    override fun onCreateDialog(id: Int): Dialog {
        val ad: AlertDialog.Builder
        if (id == 1) {
            val title = getString(R.string.rateApp)
            val message = getString(R.string.goToGooglePlayQestion)
            val button1String = getString(R.string.yes)
            val button2String = getString(R.string.no)

            ad = AlertDialog.Builder(this@AboutActivity)
            ad.setTitle(title)  // заголовок
            ad.setMessage(message) // сообщение
            ad.setPositiveButton(button1String) { dialog, arg1 ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=$packageName")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            ad.setNegativeButton(button2String) { dialog, arg1 -> }
            ad.setCancelable(true)
            ad.setOnCancelListener { }
            return ad.create()
        }
        return super.onCreateDialog(id)
    }

    private fun получить_лог_приложения(): String {
        try {
            val процесс = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(InputStreamReader(процесс.inputStream))

            val лог = StringBuilder()
            var линия: String? = ""
            do  {
                линия = bufferedReader.readLine()
                лог.append(линия)
            } while (линия != null)
            return лог.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

    }

    fun getLogcat(view: View) {
        if (кликни_пять_раз_не_поворачивая_экран < 5) {
            кликни_пять_раз_не_поворачивая_экран++
            toast.setText(R.string.onlyForDevelopers)
            toast.duration = Toast.LENGTH_LONG
            toast.show()
            if (кликни_пять_раз_не_поворачивая_экран.toInt() != 5) return
        }
        toast.cancel()
        class Поток : AsyncTask<Void, Void, Intent>() {

            override fun doInBackground(vararg params: Void): Intent {
                val отправить_лог = Intent()
                отправить_лог.action = Intent.ACTION_SEND
                отправить_лог.putExtra(Intent.EXTRA_TEXT, получить_лог_приложения())
                отправить_лог.type = "text/plain"
                return отправить_лог
            }

            override fun onPostExecute(intent: Intent) {
                super.onPostExecute(intent)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

        val поток = Поток()
        поток.execute()

    }

    fun privacy(view: View) {
        val ссылка_на_политику_конфиденциальности = Uri.parse("http://sccraft.ru/android-app/arenacontrol/privacy/")
        val открыть_политику_конфиденциальности_в_браузере = Intent(Intent.ACTION_VIEW, ссылка_на_политику_конфиденциальности)
        startActivity(открыть_политику_конфиденциальности_в_браузере)
    }

    fun viewLicense(view: View) {
        val ссылка_на_лицензию = Uri.parse("https://github.com/sashaqwert/ArenaControl/blob/master/LICENSE")
        val открыть_лицензию_в_браузере = Intent(Intent.ACTION_VIEW, ссылка_на_лицензию)
        startActivity(открыть_лицензию_в_браузере)
    }

    fun viewSourceCode(view: View) {
        val ссылка_на_исходный_код = Uri.parse("https://github.com/sashaqwert/ArenaControl")
        val открыть_исходный_код_в_браузере = Intent(Intent.ACTION_VIEW, ссылка_на_исходный_код)
        startActivity(открыть_исходный_код_в_браузере)
    }
}

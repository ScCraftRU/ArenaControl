package ru.sccraft.arenacontrol

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast

class AddServerActivity : AppCompatActivity() {

    private lateinit var токен: EditText
    private lateinit var имя: EditText
    private lateinit var сервер: Server
    private lateinit var fe: Fe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_server)
        setTitle(R.string.title_activity_server_add)
        токен = findViewById(R.id.addServer_token)
        имя = findViewById(R.id.addServer_name)
        fe = Fe(this)
    }

    fun save(view: View) {
        val токен = this.токен.text.toString()
        if (токен == "" || токен.contains(" ") || токен.contains("&")) {
            Toast.makeText(applicationContext, R.string.incorrectToken, Toast.LENGTH_SHORT).show()
            return
        }
        сервер = Server(токен)
        сервер.имя_сервера = имя.text.toString()
        fe.saveFile("$токен.json", сервер.toJSON())
        finish()
    }

    fun scanQRcode(view: View) {
        try {
            val intent = Intent("com.google.zxing.client.android.SCAN")
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE") // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            val диалог = AlertDialog.Builder(this)
            диалог.setTitle(R.string.addServerActivity_downloadQR_title)
                    .setMessage(R.string.addServerActivity_downloadQR_content)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes) { dialogInterface, i -> скачать_сканнер() }
                    .setNegativeButton(R.string.no) { dialogInterface, i -> }.show()
        }

    }

    private fun скачать_сканнер() {
        try {
            val marketUri = Uri.parse("market://details?id=com.google.zxing.client.android")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Google play is not installed!\nTo scan a QR code please, install pascage com.google.zxing.client.android", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                val contents = data!!.getStringExtra("SCAN_RESULT")
                токен.setText(contents)
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(applicationContext, "Scan canceled!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package ru.sccraft.arenacontrol;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddServerActivity extends AppCompatActivity {

    EditText токен, имя;
    Server сервер;
    Fe fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        setTitle(R.string.title_activity_server_add);
        токен = findViewById(R.id.addServer_token);
        имя = findViewById(R.id.addServer_name);
        fe = new Fe(this);
    }

    public void save(View view) {
        String токен = this.токен.getText().toString();
        if (((токен.equals(""))||(токен.contains(" ")))||(токен.contains("&"))) {
            Toast.makeText(getApplicationContext(), R.string.incorrectToken, Toast.LENGTH_SHORT).show();
            return;
        }
        сервер = new Server(токен);
        сервер.setимя_сервера(имя.getText().toString());
        fe.saveFile(токен + ".json", сервер.toJSON());
        finish();
    }

    public void scanQRcode(View view) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            AlertDialog.Builder диалог = new AlertDialog.Builder(this);
            диалог.setTitle(R.string.addServerActivity_downloadQR_title)
                    .setMessage(R.string.addServerActivity_downloadQR_content)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            скачать_сканнер();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        }
    }

    private void скачать_сканнер() {
        try {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Google play is not installed!\nTo scan a QR code please, install pascage com.google.zxing.client.android", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                токен.setText(contents);
            }
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Scan canceled!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

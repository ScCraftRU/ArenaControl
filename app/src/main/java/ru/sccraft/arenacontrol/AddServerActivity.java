package ru.sccraft.arenacontrol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        сервер = new Server(токен);
        сервер.имя_сервера = имя.getText().toString();
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
            скачать_сканнер();
        }
    }

    private void скачать_сканнер() {
        Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
        startActivity(marketIntent);
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

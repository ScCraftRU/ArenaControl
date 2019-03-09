package ru.sccraft.arenacontrol;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fe fe = new Fe (this);
        fe.saveFile("arenacontrol-ads", "1"); //Отключение рекламы
        fe.saveFile("adid", "1"); //Отключение соглашения на использование рекламного интендификатора

        setContentView(R.layout.activity_main); //Предлагаем обновить приложение с сохранением данных. (Подписи приложений совпадают!!!)
    }

    public void googlePlay(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(get_PlayStore_URL()));
        startActivity(intent);

    }

    public void gitHub(View view) {
        Uri ссылка_на_релизы_GitHub = Uri.parse(getString(R.string.gitHub_url));
        Intent открыть_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_релизы_GitHub);
        startActivity(открыть_в_браузере);
    }

    /**
     * Открывает страницу форума 4PDA.ru
     * @param view передаётся из XML
     */
    public void pda4(View view) {
        Uri ссылка_на_релизы_GitHub = Uri.parse(getString(R.string.pda4));
        Intent открыть_в_браузере = new Intent(Intent.ACTION_VIEW, ссылка_на_релизы_GitHub);
        startActivity(открыть_в_браузере);
    }

    @NonNull
    private String get_PlayStore_URL () {
        return "market://details?id=" + getPackageName();
    }
}

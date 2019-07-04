package ru.sccraft.arenacontrol;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class CommandEditActivity extends AppCompatActivity {

    private Server сервер;
    EditText время_день, время_ночь, время_задать, время_добавить, погода, перезагрузить_плагины;
    SwitchCompat использовать_новые_комманды_переключения_режима_игры;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_edit);
        setTitle(R.string.title_activity_server_command);
        сервер = Server.Companion.fromJSON(getIntent().getStringExtra("server"));

        время_день = findViewById(R.id.editText_command_day);
        время_ночь = findViewById(R.id.editText_command_night);
        время_задать = findViewById(R.id.editText_command_timeSet);
        время_добавить = findViewById(R.id.editText_command_timeAdd);
        погода = findViewById(R.id.editText_weather);
        использовать_новые_комманды_переключения_режима_игры = findViewById(R.id.newGameMode);
        перезагрузить_плагины = findViewById(R.id.editText_reload_command);

        время_день.setText(сервер.getкомманда_день());
        время_ночь.setText(сервер.getкомманда_ночь());
        время_задать.setText(сервер.getкомманда_задать_время());
        время_добавить.setText(сервер.getкомманда_добавить_время());
        погода.setText(сервер.getкомманда_погода());
        использовать_новые_комманды_переключения_режима_игры.setChecked(сервер.getгеймМод_1_13());
        перезагрузить_плагины.setText(сервер.getкомманда_релоад());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server_command, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:
                сохранить();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void сохранить() {
        Fe fe = new Fe(this);
        сервер.setкомманда_день(время_день.getText().toString());
        сервер.setкомманда_ночь(время_ночь.getText().toString());
        сервер.setкомманда_задать_время(время_задать.getText().toString());
        сервер.setкомманда_добавить_время(время_добавить.getText().toString());
        сервер.setкомманда_погода(погода.getText().toString());
        сервер.setгеймМод_1_13(использовать_новые_комманды_переключения_режима_игры.isChecked());
        сервер.setкомманда_релоад(перезагрузить_плагины.getText().toString());
        fe.saveFile(сервер.получить_токен() + ".json", сервер.toJSON());
        finish();
    }
}

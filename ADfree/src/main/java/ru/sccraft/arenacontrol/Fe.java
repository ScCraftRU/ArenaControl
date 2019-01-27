package ru.sccraft.arenacontrol;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

class Fe {
    private static final String LOG_TAG = "Fe";
    private FileInputStream fin;
    private FileOutputStream fos;
    ContextWrapper a;

    Fe(Context a) {
        this.a = new ContextWrapper(a.getApplicationContext());
    }

    void saveFile(String name, String content){

        try {
            fos = a.openFileOutput(name, MODE_PRIVATE);
            fos.write(content.getBytes());
            Log.d(LOG_TAG, "File saved");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}

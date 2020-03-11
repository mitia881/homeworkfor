package com.wau.android.myhomeworkfor;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Servise extends IntentService {

    private final MyServiceBinder mMyServiceBinder;

    public  Servise() {
        super(Servise.class.getName());
        mMyServiceBinder = new MyServiceBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        writeToFile(intent.getStringExtra("action"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void writeToFile(String text) {
        File internalStorage = getFilesDir();
        String filepath = internalStorage.getAbsolutePath() + "/action_log.txt";
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("Exception", "File create failed: " + e.toString());
                e.printStackTrace();
            }
        }

        String textWithDate = getFormatDate() + " " + text + "\n";

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file, true);
            stream.write(textWithDate.getBytes());
            mMyServiceBinder.notifyEndOfWritingFile(filepath);
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFormatDate() {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd '/-/' hh:mm:ss ");
        return formatForDateNow.format(dateNow);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class MyServiceBinder extends Binder {
        interface EndOfWriteListener {
            void onWriteEnd(String filepath);
        }

        private final ArrayList<EndOfWriteListener> listeners = new ArrayList<>();

        void notifyEndOfWritingFile(String filepath) {
            if (!listeners.isEmpty()) {
                for (EndOfWriteListener listener : listeners) {
                    listener.onWriteEnd(filepath);
                }
            }
        }

        public void addListener(@NonNull EndOfWriteListener listener) {
            listeners.add(listener);
        }

        public void removeListener(@NonNull EndOfWriteListener listener) {
            listeners.remove(listener);
        }
    }
}


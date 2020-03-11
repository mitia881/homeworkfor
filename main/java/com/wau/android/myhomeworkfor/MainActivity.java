package com.wau.android.myhomeworkfor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ContextDeclared mContextReceiver;
    private static final String CUSTOM_ACTION = "com.wau.android.Action";
    private Button mButton;
    private  Servise.MyServiceBinder serviceBinder;
    private TextView mTextView;
    Intent intent;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof  Servise.MyServiceBinder) {
                serviceBinder = (Servise.MyServiceBinder) service;
                serviceBinder.addListener(endOfWriteListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (serviceBinder != null) {
                serviceBinder.removeListener(endOfWriteListener);
            }
        }
    };

    private final Servise.MyServiceBinder.EndOfWriteListener endOfWriteListener =
            new Servise.MyServiceBinder.EndOfWriteListener() {

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onWriteEnd(String filepath) {
                    readFile(filepath);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCustomAction();
            }
        });

        intent = new Intent(this, Servise.class);
        mTextView = findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerContextReceiver();
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mContextReceiver != null) {
            unregisterReceiver(mContextReceiver);
        }
        unbindService(mServiceConnection);
    }

    private void registerContextReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(CUSTOM_ACTION);

        mContextReceiver = new ContextDeclared();
        registerReceiver(mContextReceiver, intentFilter);
    }

    private void sendCustomAction() {
        Intent intent = new Intent(CUSTOM_ACTION);
        sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void readFile(String filepath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(filepath);
             Scanner scanner = new Scanner(fileReader)) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine() + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTextView.setText(stringBuilder);
    }

}
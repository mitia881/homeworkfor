package com.wau.android.myhomeworkfor;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ContextDeclared extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();

        String text = intent.getAction();
        Intent intent1 = new Intent(context,  Servise.class);
        intent1.putExtra("action", text);
        context.startService(intent1);
    }
}
package com.wau.android.myhomeworkfor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ManifestDeclared  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getAction();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(context,  Servise.class);
        intent1.putExtra("action", text);
        context.startService(intent1);
    }
}
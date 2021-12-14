package com.example.myapplication;
/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ToastWindow extends BroadcastReceiver {
    //Load toast specific time - 5 minutes before the schedule call
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        Toast.makeText(context, "Calling "+ name + " in 5 minutes", Toast.LENGTH_LONG).show();
    }
}

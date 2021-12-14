package com.example.myapplication;
/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class Call extends BroadcastReceiver{
    @Override
    //Dial at specific time
    public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive");
        String phoneNumber = intent.getStringExtra("phone");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        context.startActivity(callIntent);
    }
}

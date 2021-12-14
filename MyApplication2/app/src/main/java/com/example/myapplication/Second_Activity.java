package com.example.myapplication;
/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Second_Activity extends AppCompatActivity implements OnItemSelectedListener, View.OnClickListener {
    private static final int MAX_CONTACT = 80;
    ArrayList<String> contactList = new ArrayList<>(); //Show contacts name in spinner
    Button btnTimePicker, btnSend, btnAddContact;
    TextView txtTimePicker, etShowContact;
    private int fHour = -1, fMinute = -1, callIndex = 0, i = 0, indexSelect;
    private String select;
    Calendar callCalendar, toastCalendar;

    ArrayList<FutureCall> callArray = new ArrayList<FutureCall>(MAX_CONTACT); //Hold schedule calls from shared preferences
    ArrayList<Contact> contactArray = new ArrayList<Contact>(MAX_CONTACT); //Hold Contact passed from first activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_);

        contactArray = (ArrayList<Contact>) getIntent().getSerializableExtra("contact"); //Get contact array from first activity

        btnTimePicker = (Button) findViewById(R.id.btnTime);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtTimePicker = (TextView) findViewById(R.id.etTime);
        etShowContact = (TextView) findViewById(R.id.etShowContact);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnTimePicker.setOnClickListener(this);
        btnAddContact.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        for (i = 0; i < contactArray.size(); i++) { //Make spinner list of contact names
            if (contactArray.get(i) == null) {
                continue;
            }
            contactList.add(contactArray.get(i).get_Name());
        }

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contactList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        loadData(); // load schedule call from shared preference
        clearCall(); // clear past call from shared preference
    }

    @SuppressLint("ResourceAsColor")
    public void onClick(View v) {
        if (v == btnTimePicker) { // Get wanted ti,e to schedule future call if available
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            fHour = c.get(Calendar.HOUR_OF_DAY);
            fMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            fHour = hourOfDay;
                            fMinute = minute;
                            txtTimePicker.setText(hourOfDay + ":" + minute);
                            setCalendar(minute, hourOfDay);
                            checkAvailable(); //check if the chosen contact available at this time
                        }
                    }, fHour, fMinute, true);
            timePickerDialog.show();
        }
        if (v == btnSend) { //
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){ // check if there is permission to make a phone call
                Toast.makeText(this, "You dont have permission to make a phone call.", Toast.LENGTH_LONG).show();
                return;
             }
            else if (fHour == -1 || fMinute == -1) { //Make sure the user choose time
                Toast.makeText(this, "Enter time", Toast.LENGTH_LONG).show();
            } else {
                if(checkFutureCall()){//check if there is another call at this specific time
                    if(checkAvailable()){//check if the chosen contact available at this time
                        callArray.add(new FutureCall(contactArray.get(indexSelect).get_Name(), contactArray.get(indexSelect).get_Phone_number(), callCalendar)); // set new call at the call array
                        setCall(select, contactArray.get(indexSelect).get_Phone_number()); // schedule call
                        saveData(); // save call array to shred preference
                    }
                    else{
                        etShowContact.setTextColor(R.color.teal_700);
                    }
                }
            }
        }
        if (v == btnAddContact) { //Forward to first activity
            Intent i = new Intent(Second_Activity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @SuppressLint("ResourceAsColor")
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // select item in the spinner
        select = parent.getItemAtPosition(position).toString();
        setIndex();
        etShowContact.setText(contactArray.get(indexSelect).get_Name() + " is available from - " + contactArray.get(indexSelect).get_fHour() + ":" + contactArray.get(indexSelect).get_fMinute() + " Till - " + contactArray.get(indexSelect).get_tHour() + ":" + contactArray.get(indexSelect).get_tMinute());

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void saveData() { // save call array to the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences1", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(callArray);
        editor.putString("call list", json);
        editor.apply();
        txtTimePicker.setText("");
    }

    private void loadData() {// load call array from the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences1", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("call list", null);
        Type type = new TypeToken<ArrayList<FutureCall>>() {
        }.getType();
        callArray = gson.fromJson(json, type);
        if (callArray == null) {
            callArray = new ArrayList<FutureCall>();
        }
    }

    private void setCall(String name, String phone_number) {
        //Set future call
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Call.class);
        intent.putExtra("phone", phone_number);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, callCalendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Call set at " + callCalendar.getTime(), Toast.LENGTH_LONG).show();
        //Set future toast just if the call is set for more then 5 minute from current time
        if((callCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) >= 60000*5) {

            AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(this, ToastWindow.class);
            intent2.putExtra("name", name);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent2, 0);
            alarmManager2.set(AlarmManager.RTC_WAKEUP, toastCalendar.getTimeInMillis(), pendingIntent2);
        }

    }

    private boolean checkAvailable() {
        /*
            * check if the user available at the chosen time, if he does - set send button available and contact details black,
            * else, send button unavailable (fade) and contact details red.
         */
        if (fHour == -1 || fMinute == -1) { //check if the user choose time to set call
            btnSend.setClickable(false);
            btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
            etShowContact.setTextColor(Color.RED);
            return false;
        } else if (contactArray.get(indexSelect).get_fHour() == fHour) {//check if the user available at the chosen time
            if (contactArray.get(indexSelect).get_fMinute() <= fMinute) {
                btnSend.setClickable(true);
                btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                etShowContact.setTextColor(Color.BLACK);
                return true;
            } else {
                btnSend.setClickable(false);
                btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                Toast.makeText(this, "Choose another time", Toast.LENGTH_LONG).show();
                etShowContact.setTextColor(Color.RED);
                return false;
            }
        } else if (contactArray.get(indexSelect).get_fHour() < fHour && contactArray.get(indexSelect).get_tHour() > fHour) {
            btnSend.setClickable(true);
            btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
            etShowContact.setTextColor(Color.BLACK);
            return true;
        } else if (contactArray.get(indexSelect).get_tHour() == fHour) {
            if (contactArray.get(indexSelect).get_tMinute() >= fMinute) {
                btnSend.setClickable(true);
                btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                etShowContact.setTextColor(Color.BLACK);
                return true;
            } else {
                btnSend.setClickable(false);
                btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                Toast.makeText(this, "Choose another time", Toast.LENGTH_LONG).show();
                etShowContact.setTextColor(Color.RED);
                return false;
            }
        } else {
            btnSend.setClickable(false);
            btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
            Toast.makeText(this, "Choose another time", Toast.LENGTH_LONG).show();
            etShowContact.setTextColor(Color.RED);
            return false;
        }
    }

    private void setIndex() { //Set the index of the chosen contact from contact array
        for (int i = 0; i < contactArray.size(); i++) {
            if (contactArray.get(i) == null) {
                continue;
            } else if (contactArray.get(i).get_Name().equals(select)) {
                indexSelect = i;
                break;
            } else {
                continue;
            }
        }
    }

    private boolean checkFutureCall() {
        /*
            Make sure there no other calls schedule the chosen time
         */
        for (int g = 0; g < callArray.size(); g++) {
            if (callArray.get(g) == null) {
                continue;
            } else if(callCalendar.get(Calendar.MINUTE) == callArray.get(g).getCall().get(Calendar.MINUTE) && callCalendar.get(Calendar.HOUR) == callArray.get(g).getCall().get(Calendar.HOUR)){
                btnSend.setClickable(false);
                btnSend.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                Toast.makeText(this, "Already schedule call at this specific time", Toast.LENGTH_LONG).show();
                return false;
            } else {
                continue;
            }
        }
        return true;
    }

    private void setCalendar(int minute, int hour){
        // Set call information

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -1);
        callCalendar = Calendar.getInstance();
        toastCalendar = Calendar.getInstance();
        callCalendar.set(Calendar.HOUR_OF_DAY, hour);
        callCalendar.set(Calendar.MINUTE, minute);
        callCalendar.set(Calendar.SECOND, 0);
        if(callCalendar.before(now)){
            callCalendar.add(Calendar.DAY_OF_MONTH,1);
        }

        toastCalendar.set(Calendar.HOUR_OF_DAY, hour);
        toastCalendar.set(Calendar.MINUTE, minute);
        toastCalendar.set(Calendar.SECOND, 0);
        if(toastCalendar.before(Calendar.getInstance())){
            toastCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
        toastCalendar.add(Calendar.MINUTE, -5);
    }

    private void clearCall(){
        //clear passed call from the call array
        for(int i = 0; i<callArray.size();i++){
            if(callArray.get(i) == null){
                continue;
            } else if(callArray.get(i).getCall().before((Calendar.getInstance()))){
                callArray.remove(i);
            }
        }
    }
}

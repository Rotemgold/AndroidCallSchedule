package com.example.myapplication;

/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.TimePickerDialog;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MAX_CONTACT  = 80;
    Button  btnFromTimePicker, btnTillTimePicker, btnSubmit, btnSecond;
    EditText  etname, etphone;
    TextView txtfTime, txttTime;
    private int  fHour=-1, fMinute=-1, tHour=-1, tMinute=-1;
    // f-from, t-till
    private ArrayList<Contact> contact = new ArrayList<Contact>(); //List of contacts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFromTimePicker=(Button)findViewById(R.id.btn_from_time);
        txtfTime=(TextView)findViewById(R.id.etFrom);

        btnFromTimePicker.setOnClickListener(this);


        btnTillTimePicker=(Button)findViewById(R.id.btn_till_time);
        txttTime=(TextView)findViewById(R.id.etTill);

        btnTillTimePicker.setOnClickListener(this);


        btnSubmit=(Button)findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnSecond=(Button)findViewById(R.id.btnSecond);

        btnSecond.setOnClickListener(this);
        etname = (EditText)findViewById(R.id.etName);
        etname.setText("Enter name");
        etphone = (EditText)findViewById(R.id.etPhone);

        loadData(); //Load Contact information from SharedPreference
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE}, 101); // Request call phone permission from user.
    }

    @Override
    public void onClick(View v) {

        if (v == btnSubmit){ // Add contact detail to the contact list while information match the necessary fields.
            boolean checkNameAndNum = false; //Check name and phone fields
            String name = etname.getText().toString();
            String phone = etphone.getText().toString();
            for(int i = 0; i < contact.size() ; i++) {
                if (contact.get(i) == null) {
                    continue;
                }else if(name.equals("Enter name")){ //Force the user to insert name (can't leave field empty)
                    Toast.makeText(this, "Unavailable name", Toast.LENGTH_LONG).show();
                    checkNameAndNum = true;
                    break;
                } else if (contact.get(i).get_Name().equals(name)) { // if the name inputs already exists in the contact array
                    Toast.makeText(this, "The name " + name + " already exist", Toast.LENGTH_LONG).show();
                    checkNameAndNum = true;
                    break;
                } else {
                    try {
                        int checkPhone = Integer.parseInt(phone);//Make sure that the phone number is legal.
                        // is an integer!
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Unavailable Phone number", Toast.LENGTH_LONG).show();
                        checkNameAndNum = true;
                        break;
                    }
                }
            }
            if(!checkNameAndNum) {
                if(fHour == -1 || fMinute == -1 || tHour == -1 || tMinute == -1){ // force user to insert specific times
                    Toast.makeText(this, "Enter availability time", Toast.LENGTH_LONG).show();
                }
                else if(fHour > tHour){ //legal range of time (hours)
                    Toast.makeText(this, "Unavailable timing", Toast.LENGTH_LONG).show();
                }
                else if((fHour == tHour) && (fMinute > tMinute)){//legal range of time(minutes)
                    Toast.makeText(this, "Unavailable timing", Toast.LENGTH_LONG).show();
                }
                else {
                    //create new contact
                    Contact tmp = new Contact(name, phone, fHour, fMinute, tHour, tMinute);
                    contact.add(tmp);
                    Toast.makeText(this, "Add contact to the list", Toast.LENGTH_SHORT).show();
                    //saved data to shared preference
                    saveData();
                    //Clear fields
                    etname.getText().clear();
                    etphone.getText().clear();
                }
                }

        }
        if (v == btnFromTimePicker) { // Select range of user available time
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
                            txtfTime.setText(hourOfDay + ":" + minute);
                        }
                    }, fHour, fMinute, true);
            timePickerDialog.show();
        }

        if (v == btnTillTimePicker) {// Select range of user available time

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            tHour = c.get(Calendar.HOUR_OF_DAY);
            tMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            tHour = hourOfDay;
                            tMinute = minute;
                            txttTime.setText(hourOfDay + ":" + minute);
                        }
                    }, tHour, tMinute, true);
            timePickerDialog.show();
        }
        if(v == btnSecond){ // Forward to the Second activity (Schedule calls)
            Intent i = new Intent(MainActivity.this, Second_Activity.class);
            //pass contact array
            i.putExtra("contact", this.contact);
            startActivity(i);
        }
    }

    private void saveData() { // Save data to the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contact);
        editor.putString("contact list", json);
        editor.apply();
    }

    private void loadData(){// Load data to from the shared preference
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("contact list",null);
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        contact = gson.fromJson(json, type);
        if(contact == null){
            contact = new ArrayList<Contact>();
        }
    }
}
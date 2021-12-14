package com.example.myapplication;
/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import java.io.Serializable;

public class Contact implements Parcelable {
    /*
    Contact class used in contact array
     */
    private String name;
    private String phone_number;
    private int fHour, fMinute, tHour, tMinute;

    public Contact(String name, String phone, int fHour, int fMinute, int tHour, int tMinute) {
        this.name = name;
        this.phone_number = phone;
        this.fHour = fHour;
        this.fMinute = fMinute;
        this.tHour = tHour;
        this.tMinute = tMinute;
    }


    protected Contact(Parcel in) {
        name = in.readString();
        phone_number = in.readString();
        fHour = in.readInt();
        fMinute = in.readInt();
        tHour = in.readInt();
        tMinute = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public Contact(Contact contact) {
        this.name = contact.get_Name();
        this.phone_number = contact.get_Phone_number();
        this.fHour = contact.get_fHour();
        this.fMinute = contact.get_fMinute();
        this.tHour = contact.get_tHour();
        this.tMinute = contact.get_tMinute();
    }

    public void set_Name(String name) {
        this.name = name;
    }

    public void set_Phone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void set_fHour(int fHour) {
        this.fHour = fHour;
    }

    public void set_fMinute(int fMinute) {
        this.fMinute = fMinute;
    }

    public void set_tHour(int tHour) {
        this.tHour = tHour;
    }

    public void set_tMinute(int tMinute) {
        this.tMinute = tMinute;
    }

    public String get_Name() {
        return name;
    }

    public String get_Phone_number() {
        return phone_number;
    }

    public int get_fHour() {
        return fHour;
    }

    public int get_fMinute() {
        return fMinute;
    }

    public int get_tHour() {
        return tHour;
    }

    public int get_tMinute() {
        return tMinute;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone_number);
        dest.writeInt(fHour);
        dest.writeInt(fMinute);
        dest.writeInt(tHour);
        dest.writeInt(tMinute);
    }
}

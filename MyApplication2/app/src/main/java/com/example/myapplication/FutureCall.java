package com.example.myapplication;
/*
    Assignment 1
    Campus: Ashdod
    Author 1: Dor Hazout 313560328
    Author 2: Rotem Goldshtein Reshef 308577188
 */
import java.util.Calendar;

public class FutureCall {
    /*
        Call class used in call array
     */
    String name;
    String phone;
    Calendar call;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Calendar getCall(){return call;}

    public void setCall(Calendar call){this.call = call;}


    public FutureCall(String name, String phone, Calendar call) {
        this.name = name;
        this.phone = phone;
        this.call = call;
    }
}

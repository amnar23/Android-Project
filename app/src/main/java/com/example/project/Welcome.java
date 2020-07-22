package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(Preferences.getEmail(this)!=null && !Preferences.getEmail(this).equals("")){
            Intent i3=new Intent(this,Home.class);
            startActivity(i3);
            finish();
        }
    }
    public void goToLogin(View v){
       Intent i1=new Intent(this,Login.class);
       startActivity(i1);
       finish();
    }
    public void goToSignup(View v){
        Intent i2=new Intent(this,Signup.class);
        startActivity(i2);
        finish();
    }
}

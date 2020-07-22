package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    Toolbar tb;
    FloatingActionButton fab1, fab2, fab3;
    Preferences utils;
   DatabaseHelper dbHelper;
   User user;
   String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent i=getIntent();
        utils=new Preferences();
        tb = findViewById(R.id.title);
        dbHelper=new DatabaseHelper(this);
        user=new User();
        getUserDetails();
        setSupportActionBar(tb);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPage(viewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        fab1 = findViewById(R.id.item1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, EnterSugar.class);
                startActivity(i);
            }
        });

        fab2 = findViewById(R.id.item2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(Home.this, WeightEntry.class);
                //startActivity(i);
            }
        });

        fab3 = findViewById(R.id.item3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = new Intent(Home.this, MedEntry.class);
               // startActivity(i);
            }
        });

    }
    //take details for profile page
    private void getUserDetails()
    {
        email=utils.getEmail(this);
        user=dbHelper.getUser(email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reminder:
                Intent i3=new Intent(this,Reminder.class);
                startActivity(i3);
                return true;
            case R.id.pdf:
                return true;
            case R.id.delete:
                return true;
            case R.id.logout:
                utils.saveEmail("",this);
                if(utils.getEmail(this)==null||utils.getEmail(this)=="")
                {
                    Intent i2=new Intent(this,Welcome.class);
                    startActivity(i2);
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPage(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment(), "HOME");
        viewPagerAdapter.addFragment(new ProfileFragment(user.getName(),user.getEmail(),user.getDOB(),user.getGender(),user.getPassword()), "MY PROFILE");
        viewPager.setAdapter(viewPagerAdapter);
    }


}
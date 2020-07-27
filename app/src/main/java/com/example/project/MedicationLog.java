package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class MedicationLog extends AppCompatActivity {
    ArrayList<Medication> medEntries = new ArrayList<>();
    SwipeMenuListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    Medication med;
    String id,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_log);
        Intent i = getIntent();
        activity = this;
        listView = findViewById(R.id.medList);
        med=new Medication();
        new MedicationLog.GetMedData().execute();
        utils = new Preferences();
        db = new DatabaseHelper(this);
    }

    public class GetMedData extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return getMedEntries();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            CustomMedicationList customMedList = new CustomMedicationList(activity, medEntries);
            listView.setAdapter(customMedList);
            registerForContextMenu(listView);
            listView.setMenuCreator(creator);
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            makeText(MedicationLog.this, "0 clciked", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(),"You Selected "+medEntries.get(position).getMedication(), Toast.LENGTH_SHORT).show();
                            Intent i2=new Intent(MedicationLog.this,EnterMedication.class);
                            i2.putExtra("med",medEntries.get(position).getMedication());
                            i2.putExtra("dosage",medEntries.get(position).getDosage());
                            i2.putExtra("unit",medEntries.get(position).getUnit());
                            i2.putExtra("date",medEntries.get(position).getDate());
                            i2.putExtra("time",medEntries.get(position).getTime());
                            i2.putExtra("id",medEntries.get(position).getId());
                           startActivity(i2);
                            break;
                        case 1:
                            makeText(MedicationLog.this, "1 clciked", Toast.LENGTH_LONG).show();
                            id=String.valueOf(medEntries.get(position).getId());
                            email=medEntries.get(position).getEmail().trim();
                            boolean flag=db.deleteMedRecord(email,id);
                            if(flag)
                            {
                                Toast.makeText(getApplicationContext(), "Record Deleted ", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                            break;
                    }
                    return false;
                }

           /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(),"You Selected "+medEntries.get(position).getMedication(), Toast.LENGTH_SHORT).show();
                }
            });*/

            });
        }
    }
        protected Void getMedEntries() {
            String email = utils.getEmail(MedicationLog.this);
            Log.d("TAG", email);
            medEntries = db.getMedicEntries(email);
            return null;
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator

    }


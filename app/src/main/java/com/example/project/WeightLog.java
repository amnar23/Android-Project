package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class WeightLog extends AppCompatActivity {
    ArrayList<Weight> weightEntries=new ArrayList<>();
    ListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    FloatingActionButton fab;
    AlertDialog.Builder builder;
    Weight clickedWeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);
        Intent i=getIntent();
        activity=this;
        listView=findViewById(R.id.weightList);
        clickedWeight=new Weight();
        db=new DatabaseHelper(this);
        utils=new Preferences();
        new GetWeightData().execute();
        fab=(FloatingActionButton)findViewById(R.id.weightfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2=new Intent(WeightLog.this,EnterWeight.class);
                startActivity(i2);
            }
        });
    }
    public class GetWeightData extends AsyncTask
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            return getWeightEntries();
        }
        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);

            CustomWeightList customWeightList=new CustomWeightList(activity,weightEntries);
            listView.setAdapter(customWeightList);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(),"You Selected "+weightEntries.get(position).getWeight(), Toast.LENGTH_SHORT).show();
                    clickedWeight.setId(weightEntries.get(position).getId());
                    clickedWeight.setWeight(weightEntries.get(position).getWeight());
                    clickedWeight.setDate(weightEntries.get(position).getDate());
                    clickedWeight.setTime(weightEntries.get(position).getTime());
                    clickedWeight.setEmail(weightEntries.get(position).getEmail());
                }
            });

        }
    }
    protected Void getWeightEntries()
    {
        String email=utils.getEmail(this);
        weightEntries=db.getWeightEntries(email);
        return null;
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logmenu, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                Intent ii=new Intent(this,EnterWeight.class);
                ii.putExtra("id",clickedWeight.getId());
                ii.putExtra("weight",clickedWeight.getWeight());
                ii.putExtra("date",clickedWeight.getDate());
                ii.putExtra("time",clickedWeight.getTime());
                startActivity(ii);

                return true;
            case R.id.delete:
                builder=new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag= db.deleteWeightRecord(clickedWeight.getEmail(),String.valueOf(clickedWeight.getId()));
                        if(flag) {
                            Toast.makeText(getApplicationContext(), "Record Deleted ", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Deletion Unsuccessful",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            default:
                return super.onContextItemSelected(item);
        }
    }
}

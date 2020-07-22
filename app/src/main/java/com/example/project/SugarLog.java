package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SugarLog extends AppCompatActivity {
    ArrayList<Sugar> sugarEntries=new ArrayList<>();
    ListView listView;
    DatabaseHelper db;
    Preferences utils;
    Activity activity;
    FloatingActionButton fab;
    AlertDialog.Builder builder;
    Sugar clickedSugar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar_log);
        Intent i=getIntent();
        activity=this;
        listView=findViewById(R.id.sugarList);
        clickedSugar=new Sugar();
        db=new DatabaseHelper(this);
        utils=new Preferences();
        new GetSugarData().execute();
        fab=(FloatingActionButton)findViewById(R.id.sugarfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2=new Intent(SugarLog.this,EnterSugar.class);
                startActivity(i2);
            }
        });
      //  getSugarEntries();

    }
    public class GetSugarData extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            return getSugarEntries();
        }
        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);

            CustomSugarList customSugarList=new CustomSugarList(activity,sugarEntries);
            listView.setAdapter(customSugarList);
            registerForContextMenu(listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(),"You Selected "+sugarEntries.get(position).getConcentration(), Toast.LENGTH_SHORT).show();
                    clickedSugar.setId(sugarEntries.get(position).getId());
                    clickedSugar.setConcentration(sugarEntries.get(position).getConcentration());
                    clickedSugar.setDate(sugarEntries.get(position).getDate());
                    clickedSugar.setTime(sugarEntries.get(position).getTime());
                    clickedSugar.setEmail(sugarEntries.get(position).getEmail());
                }
            });

        }
    }
    protected Void getSugarEntries()
    {
        String email=utils.getEmail(this);
        sugarEntries=db.getSugarEntries(email);
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
                Intent ii=new Intent(this,EnterSugar.class);
                ii.putExtra("id",clickedSugar.getId());
                ii.putExtra("conc",clickedSugar.getConcentration());
                ii.putExtra("date",clickedSugar.getDate());
                ii.putExtra("time",clickedSugar.getTime());
                ii.putExtra("measured",clickedSugar.getMeasured());
                startActivity(ii);

                return true;
            case R.id.delete:
                builder=new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       boolean flag= db.deleteRecord(clickedSugar.getEmail(),String.valueOf(clickedSugar.getId()));
                       if(flag)
                        Toast.makeText(getApplicationContext(),"Record Deleted ",Toast.LENGTH_SHORT).show();
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

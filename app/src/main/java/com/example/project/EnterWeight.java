package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.Calendar;

public class EnterWeight extends AppCompatActivity {
    DatePickerDialog picker;
    TimePickerDialog timepicker;
    EditText eweight,edate,etime;
    private AwesomeValidation awesomeValidation;
    Weight weight;
    Preferences utils;
    DatabaseHelper dbHelper;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_weight);
        i=getIntent();
        getEditTexts();
        weight=new Weight();
        utils=new Preferences();
        dbHelper=new DatabaseHelper(this);
        if(i.hasExtra("weight"))
        {
            updateWeight();
            ImageView img=findViewById(R.id.image);
            //img.setImageResource(R.drawable.weightentry);
        }
    }
    private void getEditTexts()
    {
        eweight=findViewById(R.id.weight);
        edate=findViewById(R.id.date);
        etime=findViewById(R.id.time);
    }
    //Date Picker
    public void pickDate(View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(EnterWeight.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }
    //Time PIcker
    public void pickTime(View v){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        timepicker = new TimePickerDialog(EnterWeight.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        etime.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes, false);
        timepicker.show();
    }
    private void validate()
    {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        if(String.valueOf(eweight.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.weight,"^[0-9]{1,3}[.]{0,1}[0-9]{0,2}$",R.string.error_weight1);
        }
        else
        {
            awesomeValidation.addValidation(this,R.id.weight,"^[0-9]{1,3}[.]{0,1}[0-9]{0,2}$",R.string.error_weight2);
        }
        if(String.valueOf(edate.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.date,"^[0-9]{1,2}[/][0-9]{1,2}[/][0-9]{4}$",R.string.error_date);
        }
        if(String.valueOf(etime.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.time,"^([0-1][0-9]|[2][0-3]):([0-5][0-9])$",R.string.error_time1);
        }

    }
    public void submitWeight(View v)
    {
        validate();
        if(awesomeValidation.validate())
        {
            setWeight();
           boolean flag;
            if(i.hasExtra("weight"))
            {
                weight.setId(i.getIntExtra("id",0));
                flag=dbHelper.updateWeight(weight);
            }
            else
            {
                flag= dbHelper.addWeight(weight);
            }
           if(flag)
           {
               Intent ii=new Intent(this, WeightLog.class);
               startActivity(ii);
           }
           else
               Toast.makeText(this,"Insertion Failed",Toast.LENGTH_LONG).show();
        }
    }
    private void setWeight()
    {
        weight.setWeight(Double.parseDouble(eweight.getText().toString()));
        weight.setDate(edate.getText().toString());
        weight.setTime(etime.getText().toString());
        weight.setEmail(utils.getEmail(this));
    }
    private void updateWeight()
    {
        eweight.setText(String.valueOf(i.getDoubleExtra("weight",0)));
        edate.setText(i.getStringExtra("date"));
        etime.setText(i.getStringExtra("time"));
    }
}

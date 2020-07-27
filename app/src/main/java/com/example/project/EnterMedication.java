package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.Calendar;

public class EnterMedication extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TimePickerDialog timepicker;
    DatePickerDialog datepicker;
    EditText emedication,edosage,edate,etime;
    Medication med;
    private AwesomeValidation awesomeValidation;
    Preferences utils;
    DatabaseHelper dbHelper;
    Intent i;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_medication);
        i=getIntent();
        spinner = findViewById(R.id.spinner1);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.unit, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        med=new Medication();
        getEditTexts();
        utils=new Preferences();
        dbHelper=new DatabaseHelper(this);
        if(i.hasExtra("med"))
        {
            updateMed();
            ImageView img=findViewById(R.id.image);
            //img.setImageResource(R.drawable.weightentry);
        }
    }
    private void getEditTexts()
    {
        emedication=(EditText)findViewById(R.id.medication);
        edate=(EditText)findViewById(R.id.date);
        etime=(EditText)findViewById(R.id.time);
        edosage=(EditText)findViewById(R.id.dosage);
    }
    //Date Picker
    public void pickDate(View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        datepicker = new DatePickerDialog(EnterMedication.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        datepicker.show();
    }
    //Time PIcker
    public void pickTime(View v){
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        timepicker = new TimePickerDialog(EnterMedication.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        etime.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes, false);
        timepicker.show();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        med.setUnit(parent.getItemAtPosition(position).toString());
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
    private void validate()
    {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        if(String.valueOf(emedication.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.medication,"^[A-Za-z0-9 ]+[\\w .-]*",R.string.error_med);
        }
        if(String.valueOf(edosage.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.dosage,"^[0-9]{1,}$",R.string.error_dos);
        }
        if(String.valueOf(edate.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.date,"^[0-9]{1,2}[/][0-9]{1,2}[/][0-9]{4}$",R.string.error_date);
        }
        else
        {
            awesomeValidation.addValidation(this,R.id.date,"^[0-9]{1,2}[/][0-9]{1,2}[/][0-9]{4}$",R.string.error_date2);
        }
        if(String.valueOf(etime.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.time,"^([0-1][0-9]|[2][0-3]):([0-5][0-9])$",R.string.error_time1);
        }
        else {
            awesomeValidation.addValidation(this,R.id.time,"^([0-1][0-9]|[2][0-3]):([0-5][0-9])$",R.string.error_time2);
        }
    }
    private void setMedication()
    {
        med.setMedication(emedication.getText().toString().trim());
        med.setDate(edate.getText().toString().trim());
        med.setTime(etime.getText().toString().trim());
        med.setEmail(utils.getEmail(this));
        med.setDosage(Double.parseDouble(edosage.getText().toString()));
    }
    public void submitMedication(View v)
    {
        validate();
        if(awesomeValidation.validate())
        {
            setMedication();
            boolean flag;
            if(i.hasExtra("med"))
            {
                med.setId(i.getIntExtra("id",0));
                flag=dbHelper.updateMed(med);
            }
            else
            {
                flag =dbHelper.addMedication(med);
            }

            if(flag)
            {
                Toast.makeText(this,"Insertion successful",Toast.LENGTH_LONG).show();
                Intent ii=new Intent(this,MedicationLog.class);
                startActivity(ii);
                finish();
            }else
            {
                Toast.makeText(this,"Insertion unsuccessful",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void updateMed()
    {
        emedication.setText(i.getStringExtra("med"));
        edosage.setText(String.valueOf(i.getDoubleExtra("dosage",0.0)));
        int position=adapter.getPosition(i.getStringExtra("unit"));
        spinner.setSelection(position);
        edate.setText(i.getStringExtra("date"));
        etime.setText(i.getStringExtra("time"));
    }
}

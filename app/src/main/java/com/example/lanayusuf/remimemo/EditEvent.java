package com.example.lanayusuf.remimemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by LanaYusuf on 10/21/2016.
 */
public class EditEvent extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    //EditEvent class for creating a new event
    //TODO: make EditEvent class for editing an existing class
    private boolean error = true;
    private int day;
    private int month;
    private int year;

    private int hour;
    private int minute;

    private Editable editEventName;
    private Editable editEventDescription;
    private Editable editEventLocation;
    private EditText editTxtDate;
    private EditText editTxtTime;
    private EventRemimemo event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);

        //User clicks button when finished creating event
        Button btnDone = (Button)findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);

        //User clicks button when cancels creating event
        Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        //User clicks button to delete event
        Button btnDelete = (Button)findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        event = new EventRemimemo();

        //User is able to edit the event name
        EditText editTxtEventName = (EditText) findViewById(R.id.editTxt_name);
        //listener to get event name
        editEventName = editTxtEventName.getText();

        //User is able to edit the event description
        EditText editTxtEventDescription = (EditText) findViewById(R.id.editTxt_description);
        //listener to get event description
        editEventDescription = editTxtEventDescription.getText();

        //User is able to add address location of event
        EditText editTxtEventLocation = (EditText) findViewById(R.id.editTxt_location);
        //listener to get event location
        editEventLocation = editTxtEventLocation.getText();

        //User selects priority of event
        Spinner spinner = (Spinner)findViewById(R.id.spinner_priority);
        String[] priority = {"Select", "High", "Low", "None"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priority);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //User clicks on text and calendar pops up
        editTxtDate = (EditText) findViewById(R.id.editTxt_date);
        editTxtDate.setOnClickListener(this);

        //User clicks on text and time pops up
        editTxtTime = (EditText) findViewById(R.id.editTxt_time);
        editTxtTime.setOnClickListener(this);

    }



    @Override
    public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
        year = years;
        month = monthOfYear;
        day = dayOfMonth;
        updateDate();
    }

    // updates the date in the date EditText
    private void updateDate() {
        editTxtDate.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
        hour = hourOfDay;
        minute = minuteOfDay;
        updateTime();
    }

    // updates the time in the time EditText
    private void updateTime() {
        //format correctly
        if(hour == 0){
            if(minute < 10){
                editTxtTime.setText(new StringBuilder().append("12").append(":").append("0").append(minute).append(" AM"));
            }else{
                editTxtTime.setText(new StringBuilder().append("12").append(":").append(minute).append(" AM"));
            }
        }else if(hour == 12){
            if(minute < 10){
                editTxtTime.setText(new StringBuilder().append(hour).append(":").append("0").append(minute).append(" PM"));
            }else{
                editTxtTime.setText(new StringBuilder().append(hour).append(":").append(minute).append(" PM"));
            }
        }else if(hour < 12){
            if(minute < 10){
                editTxtTime.setText(new StringBuilder().append(hour).append(":").append("0").append(minute).append(" AM"));
            }else{
                editTxtTime.setText(new StringBuilder().append(hour).append(":").append(minute).append(" AM"));
            }
        }else{
            if(minute < 10){
                editTxtTime.setText(new StringBuilder().append(hour-12).append(":").append("0").append(minute).append(" PM"));
            }else{
                editTxtTime.setText(new StringBuilder().append(hour-12).append(":").append(minute).append(" PM"));
            }
        }
    }



    @Override
    public void onClick(View v) {

        //If database not initialized, initialize it.
        EventDBHandler.getInstance().initializeDB(v.getContext());

        switch (v.getId())
        {
            case R.id.btn_done:
                //bring to previous Priority page with event updated
                if(!error) {

                    event.setEventName(editEventName.toString());
                    event.setEventDescription(editEventDescription.toString());
                    event.setEventLocation(editEventLocation.toString());
                    event.setEditTxtDate(editTxtDate.getText().toString());
                    event.setEditTxtTime(editTxtTime.getText().toString());

                    EventDBHandler.getInstance().addOrUpdateEvent(event);
                    startActivity(new Intent(this,HighPriority.class));

                }else{
                    displayError();
                }

                break;

            case R.id.btn_cancel:
                startActivity(new Intent(this,HighPriority.class));
                break;

            case R.id.btn_delete:
                //bring to Priority page with event deleted
                break;

            case R.id.editTxt_date:
                //Calendar pop-up
                Calendar calendarDate = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                        calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH),
                        calendarDate.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                break;

            case R.id.editTxt_time:
                //Clock pop-up
                Calendar calendarTime = Calendar.getInstance(TimeZone.getDefault());
                TimePickerDialog timePickerdialog = new TimePickerDialog(this, this,
                        calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(this));
                timePickerdialog.show();
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if(position == 0){
            error = true;
        }else{
            error = false;
            event.setEventPriority(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void displayError(){
        Toast toast = Toast.makeText(this, "ERROR! Priority needs to be chosen!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }



}

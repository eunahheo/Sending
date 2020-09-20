package com.example.first_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.TextView;


import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.awt.font.TextAttribute;
import java.util.Date;

public class EventActivity extends AppCompatActivity {
    TextView title,venue;
    Button  button1,button2,btnClose;
    EditText venuename,name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        title=(TextView)findViewById(R.id.title);
        venue=(TextView)findViewById(R.id.venue);
        btnClose = (Button)findViewById(R.id.btnclose);

        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final View dialogView = (View) View.inflate(EventActivity.this, R.layout.dialog, null);
                AlertDialog.Builder dlg=new AlertDialog.Builder(EventActivity.this);
                dlg.setView(dialogView);
                dlg.show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final View dialogView = (View) View.inflate(EventActivity.this, R.layout.dialog, null);
                AlertDialog.Builder dlg=new AlertDialog.Builder(EventActivity.this);
                dlg.setView(dialogView);
                dlg.show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = (EditText)findViewById(R.id.name);
                venuename=(EditText)findViewById(R.id.venuename);

                title.setText(name.getText().toString());
                venue.setText(venuename.getText().toString());
                SlidingDrawer drawer = (SlidingDrawer)findViewById(R.id.slide);
                drawer.animateClose();
            }
        });
    }
}
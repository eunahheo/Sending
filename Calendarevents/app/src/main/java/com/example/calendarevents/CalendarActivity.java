package com.example.calendarevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quart.job.entity.ScheduleJobBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView ;
import android.widget.Toast;
import java.util.ArrayList;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView compactCalendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());
    private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdf;
    TextView tx_date, tx_today;
    LinearLayout ly_detail;
    LinearLayout ly_left, ly_right;
    Calendar myCalendar;
    ImageView im_back;
    WebService webService;
    ProgressDialog progressDialog;
    String user_type;
    int id;
    Date c;
    SimpleDateFormat df;
    String formattedDate;
    String[] dates = new String[0];
    RecyclerView recyclerView;
    TextView tx_item;
    CalendarAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        init();
        valid();
        calendarlistener();
        Setdate();



        Toast.makeText(getApplicationContext(),""+formattedDate,Toast.LENGTH_LONG).show();
       // new EventListAsy().execute("" + id, formattedDate, user_type);
        new EventListAsy().onPreExecute(""+id,formattedDate,user_type);
        new EventListAsy().onPreExecute(""+id,formattedDate,user_type);
       // new EventViewAsy().execute("" + id, formattedDate, user_type);
        tx_item.setText(formattedDate + " No events available in this day");


        ly_right.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                compactCalendarView.showCalendarWithAnimation();
                compactCalendarView.showNextMonth();
            }
        });

        ly_left.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                compactCalendarView.showCalendarWithAnimation();
                compactCalendarView.showPreviousMonth();
            }
        });

        tx_today.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {

                Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();

            }
        });

        im_back.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View v) {
                finish();
            }
        });


    }

       public void init() {   //variable initialization
    compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
    tx_date = (TextView) findViewById(R.id.text);
    ly_left = (LinearLayout) findViewById(R.id.layout_left);
    ly_right = (LinearLayout) findViewById(R.id.layout_right);
    im_back = (ImageView) findViewById(R.id.image_back);
    tx_today = (TextView) findViewById(R.id.text_today);
    ly_detail = (LinearLayout) findViewById(R.id.layout_detail);

    recyclerView = (RecyclerView) findViewById(R.id.list_recycleView);
    tx_item = (TextView) findViewById(R.id.text_item);
}

   public void valid() {  //set value

        id = getUserID();
                user_type = getUserType();

                progressDialog = new ProgressDialog(CalendarActivity.this, R.style.Theme_AppCompat_Dialog); //원래는 Dialog엿음..
                progressDialog.setCanceledOnTouchOutside(false);

                }

                   public void calendarlistener() {    //calendar method
                compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
@Override            public void onDayClick(Date dateClicked) {

        new EventViewAsy().onPreExecute("" + id, DateFormat.format(dateClicked), user_type);
        tx_item.setText(DateFormat.format(dateClicked) + " No events available in this day");
        }

@Override            public void onMonthScroll(Date firstDayOfNewMonth) {

        compactCalendarView.removeAllEvents();

        tx_date.setText(simpleDateFormat.format(firstDayOfNewMonth));

        new EventListAsy().onPreExecute("" + id, DateFormat.format(firstDayOfNewMonth), user_type);

        new EventViewAsy().onPreExecute("" + id, DateFormat.format(firstDayOfNewMonth), user_type);
        tx_item.setText(DateFormat.format(firstDayOfNewMonth) + " No events available in this day");

        }
        });
        }

          public void Setdate() {      //get current date
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
        }

class EventListAsy extends AsyncTask<String, Void, ArrayList<ScheduleJobBean>> {


    protected void onPreExecute(String s, String formattedDate, String user_type) {
        super.onPreExecute();
        progressDialog.show();
        webService = new WebService();
    }

    @Override
    protected ArrayList<ScheduleJobBean> doInBackground(String... params) {
        return webService.getEventList(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(final ArrayList<ScheduleJobBean> list) {
        super.onPostExecute(list);
        progressDialog.dismiss();

        compactCalendarView.setUseThreeLetterAbbreviation(true);

        sdf = new SimpleDateFormat("MMMM yyyy");

        tx_date.setText(sdf.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        myCalendar = Calendar.getInstance();

        for (int j = 0; j < list.size(); j++) {
            String s1 = list.get(j).getDataitem();
            dates = s1.split("-");

            int mon = Integer.parseInt(dates[1]);
            myCalendar.set(Calendar.YEAR, Integer.parseInt(dates[0]));
            myCalendar.set(Calendar.MONTH, mon - 1);
            myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[2]));

            Event event = new Event(Color.RED, myCalendar.getTimeInMillis(), "test");
            compactCalendarView.addEvent(event);
        }
    }
}

class EventViewAsy extends AsyncTask<String, Void, ArrayList<ScheduleJobBean>> {


    protected void onPreExecute(String s, String format, String user_type) {
        super.onPreExecute();
        progressDialog.show();
        webService = new WebService();
    }

    @Override
    protected ArrayList<ScheduleJobBean> doInBackground(String... params) {
        return webService.viewEvent(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(final ArrayList<ScheduleJobBean> list) {
        super.onPostExecute(list);
        progressDialog.dismiss();

        if (list.size() == 0) {
            tx_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tx_item.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new CalendarAdapter(list, CalendarActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }
}

     private int getUserID() {      //shared preferences get user id method
    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    int user_id = prefs.getInt("User_id", 0);
        return user_id;
                }

                private String getUserType() {    //shared preferences get store type method
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String user_type = prefs.getString("User_type", null);
                return user_type;
                }

                }

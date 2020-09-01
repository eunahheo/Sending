package com.example.calendarevents;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    private List<ScheduleJobBean> jobList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,city,date;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.text_name);
            phone = (TextView) view.findViewById(R.id.text_mobile);
            city=(TextView)view.findViewById(R.id.text_city);
            date=(TextView)view.findViewById(R.id.text_date);

        }
    }


    public CalendarAdapter(List<ScheduleJobBean> moviesList, Activity activity) {
        this.jobList = moviesList;
        this.activity = activity;
    }

    @Override    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_adpter_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.name.setText(jobList.get(position).getName());
        holder.phone.setText(jobList.get(position).getMobile());
        holder.city.setText(jobList.get(position).getCity());
        holder.date.setText(jobList.get(position).getDatetime());
    }


    @Override    public int getItemCount() {
        return jobList.size();
    }
}


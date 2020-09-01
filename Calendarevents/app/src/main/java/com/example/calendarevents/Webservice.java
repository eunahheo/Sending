package com.example.calendarevents;

import android.app.job.JobScheduler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WebService {

    JSONParser jsonParser;

    public WebService() {
        jsonParser = new JSONParser();
    }



    public ArrayList<ScheduleJobBean> getEventList(String id, String date, String user_type) {   //get event date

    JSONObject jsonObject1, jsonObject;
    JSONArray jsonArray;
        ScheduleJobBean bean = new ScheduleJobBean();

    ArrayList<ScheduleJobBean> list = new ArrayList<>();
    String url = "Your URL";
        try {
        JSONObject postDataParams = new JSONObject();

        //enter your json input methed

        //it is sample




        postDataParams.put("user_id", id);
        postDataParams.put("date", date);
        postDataParams.put("user_type", user_type);

        jsonObject = new JSONObject(jsonParser.sendPostRequest(url, postDataParams));

        jsonObject1 = jsonObject.getJSONObject("response_header");

        // Log.i("Input" ,""+id+" "+date+" "+user_type);
        // Log.i("ScheduleOutPut",jsonObject.toString());
        bean.setMessage(jsonObject1.getString("return_msg"));
        int val = jsonObject1.getInt("return_val");

        if (val == 1) {

            jsonArray = jsonObject.getJSONArray("response_data");

            //Log.i("OutPut",jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {

                bean = new ScheduleJobBean();

                JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(i);

                //set value from bean
                bean.setDatetime(jsonArrayJSONObject.getString("appointment_datetime"));

                list.add(bean);

            }
        }

    } catch (
    JSONException e) {
        e.printStackTrace();
    } catch (NullPointerException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }

        return list;
}

       public ArrayList<ScheduleJobBean> viewEvent(String id, String date, String user_type) {   //View Events

    JSONObject jsonObject1, jsonObject;
    JSONArray jsonArray;
    ScheduleJobBean bean = new ScheduleJobBean();

    ArrayList<ScheduleJobBean> list = new ArrayList<>();
    String url = "Your URL";
        try {
                JSONObject postDataParams = new JSONObject();


                //enter your json input methed

                //it is sample

                postDataParams.put("user_id", id);
                postDataParams.put("date", date);
                postDataParams.put("user_type", user_type);

                jsonObject = new JSONObject(jsonParser.sendPostRequest(url, postDataParams));

                jsonObject1 = jsonObject.getJSONObject("response_header");

                // Log.i("Input" ,""+id+" "+date+" "+user_type);
                // Log.i("ScheduleOutPut",jsonObject.toString());
                bean.setMessage(jsonObject1.getString("return_msg"));
                int val = jsonObject1.getInt("return_val");

                if (val == 1) {

                jsonArray = jsonObject.getJSONArray("response_data");

                //Log.i("OutPut",jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {

        bean = new ScheduleJobBean();

        JSONObject jsonArrayJSONObject = jsonArray.getJSONObject(i);

        bean.setName(jsonArrayJSONObject.getString("customer_name"));
        bean.setMobile(jsonArrayJSONObject.getString("customer_phone"));
        bean.setCity(jsonArrayJSONObject.getString("customer_city"));
        bean.setDatetime(jsonArrayJSONObject.getString("appointment_datetime"));

        list.add(bean);

        }
        }

        } catch (JSONException e) {
        e.printStackTrace();
        } catch (NullPointerException e) {
        e.printStackTrace();
        } catch (Exception e) {
        e.printStackTrace();
        }

        return list;
        }


        }
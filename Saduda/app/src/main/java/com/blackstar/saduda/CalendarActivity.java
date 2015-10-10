package com.blackstar.saduda;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 9/24/2015.
 */
public class CalendarActivity extends Activity {

    EventListAdapter listAdapter;
    List<String[]> uList = new ArrayList<String[]>();
    Handler handler= new Handler();
    boolean data2Ready =false, moreMenuOpen = false;
    String username, dateStr, dt="2000/12/31";
    LinearLayout moreLayout;
    Button more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("uname");
        setContentView(R.layout.calender);
        final CalendarView calView = (CalendarView) findViewById(R.id.calendarView);
        final TextView title = (TextView) findViewById(R.id.calTitleText);
        final ListView userList = (ListView) findViewById(R.id.eventList);
        more = (Button) findViewById(R.id.more_tab);
        Button logout = (Button) findViewById(R.id.logout);
        Button create = (Button) findViewById(R.id.createEvent);
        Button create2 = (Button) findViewById(R.id.createEvent2);
        final Button eventHub = (Button) findViewById(R.id.evt_tab);
        moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
        listAdapter = new EventListAdapter(this, R.layout.event_item, uList);
        userList.setAdapter(listAdapter);

        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                dateStr = new ComputeDateTime(year + "-" + (month + 1) + "-" +
                        dayOfMonth + ",0,0,0").getDateString();
                title.setText(dateStr);
                DecimalFormat df = new DecimalFormat("00");
                dateStr = year + "-" + df.format((month + 1)) + "-" + df.format(dayOfMonth) + " 00:00:00";
                dt = year + "/" + (month + 1) + "/" + dayOfMonth;
                GetEvents ge = new GetEvents();
                ge.execute();
                final Runnable loadEvents = new Runnable() {
                    public void run() {
                        if (data2Ready) {
                            listAdapter.notifyDataSetChanged();
                            userList.setSelection(0);
                            data2Ready = false;
                        } else handler.postDelayed(this, 10);
                    }
                };
                handler.postDelayed(loadEvents, 10);
            }
        });

        eventHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), "user_file");
                file.delete();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                intent.putExtra("date", dt);
                startActivity(intent);
                onBackPressed();
            }
        });

        create2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                intent.putExtra("date",dt);
                intent.putExtra("uname", username);
                startActivity(intent);
                onBackPressed();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moreMenuOpen) onBackPressed();
                else {
                    moreLayout.setVisibility(View.VISIBLE);
                    moreMenuOpen = true;
                    more.setBackgroundColor(0xAAFFFFFF);
                }
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(getBaseContext(), EventScreen.class);
                intent.putExtra("ulist", uList.get(pos));
                intent.putExtra("uname", username);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        GetEvents ge = new GetEvents();
        ge.execute();
        final Runnable loadEvents = new Runnable() {
            public void run() {
                if (data2Ready){
                    listAdapter.notifyDataSetChanged();
                    data2Ready=false;
                }
                else handler.postDelayed(this, 10);
            }
        };
        handler.postDelayed(loadEvents, 10);
    }

    @Override
    public void onBackPressed(){
        if(moreMenuOpen){
            moreMenuOpen = false;
            moreLayout.setVisibility(View.GONE);
            more.setBackgroundColor(0x00000000);
        }else{
            super.onBackPressed();
        }
    }

    class GetEvents extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/get_user_event_info.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Checking event table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            if(dateStr!=null) params.add(new BasicNameValuePair("date", dateStr));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {e.printStackTrace();}

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                String result = EntityUtils.toString(response.getEntity());
                Log.d("Http Post Response evt", result);
                uList.clear();
                String[] events = result.split("~~");
                for(String ev : events){
                    if(!ev.isEmpty()) uList.add(ev.split("~!"));
                }
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            data2Ready = true;
            return null;
        }
    }
}

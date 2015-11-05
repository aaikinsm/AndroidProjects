package com.blackstar.saduda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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


public class EventHub extends Activity {

    List<String[]> uList = new ArrayList<String[]>();
    EventListAdapter listAdapter;
    boolean data1Ready=false, data2Ready = false, moreMenuOpen = false;
    String[] data = new String[20], iData = new String[4];
    Bitmap bmp;
    Handler handler= new Handler();
    LinearLayout moreLayout;
    Button more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_hub);

        try {
            FileInputStream fi = openFileInput("user_file");
            Scanner in = new Scanner(fi);
            String line = in.nextLine();
            data=(line.split("~!"));
            iData = data[3].replace("[","").replace("]","").split(",");
            in.close();
        } catch (FileNotFoundException e) {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }

        more = (Button) findViewById(R.id.more_tab);
        Button logout = (Button) findViewById(R.id.logout);
        final Button eventHub = (Button) findViewById(R.id.evt_tab);
        final Button calendar = (Button) findViewById(R.id.cal_tab);
        moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
        TextView username = (TextView) findViewById(R.id.nameView);
        TextView location = (TextView) findViewById(R.id.locationView);
        TextView interests = (TextView) findViewById(R.id.interestsView);
        final ImageView img = (ImageView) findViewById(R.id.imageView);
        final ListView userList = (ListView) findViewById(R.id.eventList);
        listAdapter = new EventListAdapter(this, R.layout.event_item, uList);

        userList.setAdapter(listAdapter);

        try {
            username.setText(data[1]);
            if (iData.length >= 2)
                interests.setText("Interests: " + iData[0] + "," + iData[1] + "...");
            else interests.setText("Interest: " + iData[0]);
            if (!data[9].equals("none")) {
                UpdatePhoto up = new UpdatePhoto();
                up.execute();
            }
        }catch(NullPointerException e){e.printStackTrace();}


       eventHub.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), EventHub.class));
               finish();
           }
       });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CalendarActivity.class);
                intent.putExtra("uname", data[1]);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(),"user_file");
                file.delete();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
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
                intent.putExtra("uname", data[1]);
                startActivity(intent);

            }
        });



        final Runnable loadProfile = new Runnable() {
            public void run() {
                    if (data1Ready){
                        if (bmp != null) img.setImageBitmap(bmp);
                    }
                    else handler.postDelayed(this, 10);
            }
        };
        handler.postDelayed(loadProfile, 10);

    }

    @Override
    protected void onResume(){
        super.onResume();

        GetEvents ge = new GetEvents();
        ge.execute();
        final Runnable loadEvents = new Runnable() {
            public void run() {
                if (data2Ready){
                    listAdapter.notifyDataSetChanged();
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


    class UpdatePhoto extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            URL url = null;
            try {
                url = new URL("http://saduda.com/uploads/profile/small/" + data[9]);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            data1Ready = true;
            return null;
        }
    }

    class GetEvents extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/get_event_info.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Checking event table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", data[1]));

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

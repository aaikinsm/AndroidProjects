package com.blackstar.saduda;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imdc on 10/07/2015.
 */
public class EventScreen extends Activity {

    String[] uList;
    List<String[]> cList = new ArrayList<String[]>();
    boolean imgReady=false, imgOpen = false, chatReady = false;
    Bitmap bmp;
    ImageView imgFull;
    String username;
    Handler handler = new Handler();
    CommentListAdapter listAdapter;
    Runnable loadChat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uList = extras.getStringArray("ulist");
            username = extras.getString("uname");
        }

        setContentView(R.layout.event_details);
        
        TextView title = (TextView) findViewById(R.id.eventTitle);
        final TextView description = (TextView) findViewById(R.id.eventDesc);
        TextView time = (TextView) findViewById(R.id.eventTime);
        TextView location = (TextView) findViewById(R.id.location);
        final ImageView img = (ImageView) findViewById(R.id.eventImg);
        imgFull = (ImageView) findViewById(R.id.fullImage);
        final ImageView fav = (ImageView) findViewById(R.id.eventFav);
        final Switch join = (Switch) findViewById(R.id.eventJoin);
        final LinearLayout chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
        final ScrollView descView = (ScrollView) findViewById(R.id.descriptionView);
        Button send = (Button) findViewById(R.id.send);
        final EditText message = (EditText) findViewById(R.id.commentText);
        final ListView chatList = (ListView) findViewById(R.id.chatListView);
        listAdapter = new CommentListAdapter(this, R.layout.comment_item, cList);

        chatList.setAdapter(listAdapter);


        title.setText(uList[1]);
        time.setText(new ComputeDateTime(uList[9]).getString(true).replace(" | ","\n"));
        description.setText(uList[3]);
        location.setText(uList[5]);
        if (uList[13].equals("true")) join.setChecked(true);
        else join.setChecked(false);
        if (uList[15].equals("true")) fav.setImageResource(R.drawable.favourite1);
        else fav.setImageResource(R.drawable.favourite0);


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateEvent ue;
                if (uList[15].equals("true")) {
                    ue = new UpdateEvent(username,uList[0],"fav","false");
                    uList[15] = "false";
                    fav.setImageResource(R.drawable.favourite0);
                } else {
                    ue = new UpdateEvent(username,uList[0],"fav","true");
                    uList[15] = "true";
                    fav.setImageResource(R.drawable.favourite1);
                }
                ue.execute();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateEvent ue;
                if (uList[13].equals("true")) {
                    ue = new UpdateEvent(username,uList[0],"attend","false");
                    uList[13] = "false";
                    join.setChecked(false);
                } else {
                    ue = new UpdateEvent(username,uList[0],"attend","true");
                    uList[13] = "true";
                    join.setChecked(true);
                }
                ue.execute();
            }
        });

        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imgOpen = true;
                imgReady = false;
                UpdatePhoto up = new UpdatePhoto(uList[17], true);
                up.execute();
                for(int i = 0; i<40; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (imgReady) imgFull.setImageBitmap(bmp);
                    imgFull.setVisibility(View.VISIBLE);
                }
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descView.setVisibility(View.GONE);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().length() > 0) {
                    SendComment sc = new SendComment(username, message.getText().toString(), uList[0]);
                    sc.execute();
                    message.setText("");
                    refreshComments();
                }
            }
        });

        loadChat = new Runnable() {
            public void run() {
                if (chatReady) {
                    listAdapter.notifyDataSetChanged();
                    chatList.setSelection(listAdapter.getCount()-1);
                }else handler.postDelayed(this, 10);
            }
        };

        UpdatePhoto up = new UpdatePhoto(uList[17], false);
        up.execute();
        final Runnable loadImage = new Runnable() {
            public void run() {
                if (imgReady) img.setImageBitmap(bmp);
                else handler.postDelayed(this, 10);
            }
        };
        handler.postDelayed(loadImage, 10);

        final Runnable updateTimer = new Runnable() {
            public void run() {
                refreshComments();
                handler.postDelayed(this, 30000);
            }
        };
        handler.postDelayed(updateTimer, 10);
    }

    public void refreshComments(){
        chatReady = false;
        GetComments gc = new GetComments();
        gc.execute();
        handler.postDelayed(loadChat, 10);
    }

    @Override
    public void onBackPressed(){
        if(imgOpen) {
            imgFull.setVisibility(View.GONE);
            imgOpen = false;
        }
        else {
            super.onBackPressed();
        }
    }

    class UpdatePhoto extends AsyncTask<String, String, String> {
        String path; boolean full;
        public UpdatePhoto(String name, boolean full){
            path = name;
            this.full = full;
        }
        @Override
        protected String doInBackground(String... args) {
            URL url = null;
            try {
                if(full) url = new URL("http://saduda.com/uploads/events/" + path);
                else url = new URL("http://saduda.com/uploads/events/small/" + path);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgReady = true;
            return null;
        }
    }

    class UpdateEvent extends AsyncTask<String, String, String> {
        String uname, event, type, state;
        public UpdateEvent(String name, String evt, String typ, String st){
            uname = name;
            event = evt;
            type = typ;
            state = st;
        }
        private  String url_check_name = "http://saduda.com/sqlphp/event_fav_join.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("event", event));
            params.add(new BasicNameValuePair("state", state));
            params.add(new BasicNameValuePair("type", type));

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
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return null;
        }
    }

    class SendComment extends AsyncTask<String, String, String> {
        String uname, message, eventID;
        public SendComment(String name, String msg, String id){
            uname = name;
            message = msg;
            eventID = id;
        }
        private  String url_check_name = "http://saduda.com/sqlphp/add_comment.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("message", message));
            params.add(new BasicNameValuePair("eventID", eventID));

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
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return null;
        }
    }

    class GetComments extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/get_comments.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Checking event table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("event", uList[0]));

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
                cList.clear();
                String[] comments = result.split("~~");
                for(String ev : comments){
                    cList.add(ev.split("~!"));
                }
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            chatReady = true;
            return null;
        }
    }
}

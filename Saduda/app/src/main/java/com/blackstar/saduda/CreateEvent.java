package com.blackstar.saduda;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewAnimator;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imdc on 17/08/2015.
 */
public class CreateEvent extends Activity {
    ViewAnimator viewAnimator;
    ArrayList<String> interests = new ArrayList<>();
    String list="";
    TextView category;
    int screen;
    String author="default", titleA,descriptionA,dateA,locationA, categoryA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        final Button next = (Button) findViewById(R.id.next);
        final Button social = (Button) findViewById(R.id.social);
        final Button seminar = (Button) findViewById(R.id.seminar);
        final Button sports = (Button) findViewById(R.id.sports);
        final Button seasonal = (Button) findViewById(R.id.seasonal);
        category  = (TextView) findViewById(R.id.category);
        final TextView title  = (TextView) findViewById(R.id.title);
        final TextView location  = (TextView) findViewById(R.id.location);
        final EditText duration  = (EditText) findViewById(R.id.durationInput);
        final EditText date  = (EditText) findViewById(R.id.dateInput);
        final EditText description  = (EditText) findViewById(R.id.descInput);
        viewAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
        final Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        final Animation outAnim = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        viewAnimator.setInAnimation(inAnim);
        viewAnimator.setOutAnimation(outAnim);
        screen=1;

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(screen==1){
                    if(title.getText().length()==0){
                        title.setHint("Title can not be empty");
                        title.setHintTextColor(Color.RED);
                    }
                    else if(list.isEmpty()){
                        category.setText("You must select a category");
                    }
                    else {
                        titleA = title.getText().toString();
                        categoryA = category.getText().toString();
                        viewAnimator.showNext();
                        screen++;
                    }
                }
                else if (screen==2){
                    if(date.getText().toString().isEmpty()){
                        date.setHint("Date has not been entered (D/M/Y)");
                        date.setHintTextColor(Color.RED);
                    }
                    else if(duration.getText().toString().isEmpty()){
                        duration.setHint("Duration has not been entered (Hr:Mn)");
                        duration.setHintTextColor(Color.RED);
                    }
                    else if(description.getText().toString().isEmpty()){
                        description.setHint("Description has not been entered");
                        description.setHintTextColor(Color.RED);
                    }
                    else{
                        dateA = date.getText().toString();
                        descriptionA = description.getText().toString();
                        viewAnimator.showNext();
                        screen++;
                        next.setText("Create");
                    }
                }
                else if(screen==3) {
                    if (location.getText().length() == 0) {
                        location.setHint("Location can not be empty");
                        locationA = location.getText().toString();
                    }else{
                        //Create event, next screen
                        UpdateDatabase ud = new UpdateDatabase();
                        ud.execute();
                        finish();
                    }
                }
            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foundStr = null;
                for (String interest : interests) {
                    if (interest.equals("Social")) foundStr = interest;
                }
                if (foundStr==null){
                    interests.add("Social");
                    social.setTextColor(Color.GRAY);
                }
                else {
                    interests.remove(foundStr);
                    social.setTextColor(Color.BLACK);
                }
                setInterests();
            }
        });

        seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foundStr = null;
                for (String interest : interests) {
                    if (interest.equals("Seminar")) foundStr = interest;
                }
                if (foundStr == null) {
                    interests.add("Seminar");
                    seminar.setTextColor(Color.GRAY);
                } else {
                    interests.remove(foundStr);
                    seminar.setTextColor(Color.BLACK);
                }
                setInterests();
            }
        });


        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foundStr = null;
                for (String interest : interests) {
                    if (interest.equals("Sports")) foundStr = interest;
                }
                if (foundStr == null) {
                    interests.add("Sports");
                    sports.setTextColor(Color.GRAY);
                } else {
                    interests.remove(foundStr);
                    sports.setTextColor(Color.BLACK);
                }
                setInterests();
            }
        });

        seasonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foundStr = null;
                for (String interest : interests) {
                    if (interest.equals("Seasonal")) foundStr = interest;
                }
                if (foundStr==null) {
                    interests.add("Seasonal");
                    seasonal.setTextColor(Color.GRAY);
                }
                else {
                    interests.remove(foundStr);
                    seasonal.setTextColor(Color.BLACK);
                }
                setInterests();
            }
        });
    }

    public void setInterests (){
        list = "";
        for (String interest : interests) {
            list+=(interests.indexOf(interest)+1)+" : "+interest+"\n";
        }
        category.setTextSize(20);
        category.setText(list);
    }

    class UpdateDatabase extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/add_event.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Updating user table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("author", author));
            params.add(new BasicNameValuePair("title", titleA));
            params.add(new BasicNameValuePair("description", descriptionA));
            params.add(new BasicNameValuePair("location", locationA));
            params.add(new BasicNameValuePair("category", categoryA));
            params.add(new BasicNameValuePair("date", dateA));
            params.add(new BasicNameValuePair("image", "none"));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {e.printStackTrace();}

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                String result = EntityUtils.toString(response.getEntity());
                Log.d("Http Post Response", result);
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return null;
        }
    }
}

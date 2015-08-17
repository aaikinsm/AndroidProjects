package com.blackstar.saduda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert on 06/07/2015.
 */
public class Login extends Activity {

    Boolean passMatch = false;
    String name, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button login = (Button) findViewById(R.id.login);
        Button signup = (Button) findViewById(R.id.signup);
        final TextView uname = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final TextView output = (TextView) findViewById(R.id.loginOutput);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
                name = uname.getText().toString().trim();
                try {
                    pass = Crypto.encrypt("Nabeezy-Password", password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(name!=null){
                    CheckDatabase cd = new CheckDatabase();
                    cd.execute();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(passMatch){
                        DownloadMyUserInfo info = new DownloadMyUserInfo();
                        info.execute();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(getApplicationContext(), EventHub.class));
                        finish();
                    }else{
                        output.setText("Invalid User name or Password");
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                finish();
            }
        });
    }

    class CheckDatabase extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/login_authentication.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Checking user table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", name));
            params.add(new BasicNameValuePair("password", pass));

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
                passMatch= result.equals("correct");
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            return null;
        }
    }

    class DownloadMyUserInfo extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/get_user_info.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Checking user table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", name));

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

                try{
                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput("user_file",0));
                    out.write(result);
                    out.close();
                } catch (IOException z) {
                    z.printStackTrace();
                }

            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            return null;
        }


    }
}

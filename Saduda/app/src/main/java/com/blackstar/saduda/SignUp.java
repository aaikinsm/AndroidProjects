package com.blackstar.saduda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert on 30/06/2015.
 */
public class SignUp extends Activity{

    boolean passMatch, nameAvailable = false;
    int screen;
    String dob, list, password, mPhoneNumber, selectedBg, name="no_name", fname;
    int ageLimit =13;
    TextView info2;
    ArrayList<String> interests = new ArrayList<>();
    final int REQUEST_MEDIA = 322;
    ImageButton img;
    EditText username, pass1;
    ViewAnimator viewAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        final Button next = (Button) findViewById(R.id.next);
        final Button social = (Button) findViewById(R.id.social);
        final Button seminar = (Button) findViewById(R.id.seminar);
        final Button sports = (Button) findViewById(R.id.sports);
        final Button seasonal = (Button) findViewById(R.id.seasonal);
        img = (ImageButton) findViewById(R.id.imgButton);
        final TextView info = (TextView) findViewById(R.id.infoText);
        info2 = (TextView) findViewById(R.id.infoText2);
        username = (EditText) findViewById(R.id.username);
        pass1 = (EditText) findViewById(R.id.pass1);
        final EditText pass2 = (EditText) findViewById(R.id.pass2);
        final LinearLayout interestMenu = (LinearLayout) findViewById(R.id.interests);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        viewAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
        datePicker.setMaxDate(System.currentTimeMillis()-(long)(3.156*(Math.pow(10,10))*ageLimit));

        final Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        final Animation outAnim = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        viewAnimator.setInAnimation(inAnim);
        viewAnimator.setOutAnimation(outAnim);

        passMatch = false;
        screen = 1;
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();



        pass1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(pass1.getText().toString().length()>5){
                    pass1.setBackgroundColor(Color.argb(50,100,255,100));
                }else{
                    pass1.setBackgroundColor(Color.argb(50,255, 100, 100));
                }
                pass2.setText("");
                passMatch = false;
                return false;
            }
        });


        pass2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (pass2.getText().toString().equals("" + pass1.getText().toString())) {
                    passMatch = true;
                    pass2.setBackgroundColor(Color.argb(50, 100, 255, 100));
                } else {
                    pass2.setBackgroundColor(Color.argb(50, 255, 100, 100));
                }
                return false;
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_MEDIA);
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
                    if (foundStr==null) {
                        interests.add("Seminar");
                        seminar.setTextColor(Color.GRAY);
                    }
                    else {
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
                    if (foundStr==null) {
                        interests.add("Sports");
                        sports.setTextColor(Color.GRAY);
                    }
                    else {
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


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen == 1) {
                    if (username.getText().toString().trim().length() < 6) {
                        info.setText("Your username must have more than 5 characters");
                    } else if (!passMatch) {
                        info.setText("Invalid password");
                    } else {
                        name = username.getText().toString().trim();
                        CheckDatabase cd = new CheckDatabase();
                        cd.execute();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(nameAvailable) {
                            try {
                                password = Crypto.encrypt("Nabeezy-Password", pass1.getText().toString());
                            } catch (Exception e) { e.printStackTrace(); }
                            viewAnimator.showNext();
                            screen = 2;
                        } else{
                            info.setText("This username is not available \nCreate a new one");
                        }
                    }
                }
                else if(screen == 2){
                    dob = datePicker.getYear()+"-"
                            +new DecimalFormat("00").format(datePicker.getMonth())+"-"
                            +new DecimalFormat("00").format(datePicker.getDayOfMonth());
                    datePicker.setVisibility(View.GONE);
                    interestMenu.setVisibility(View.VISIBLE);
                    info2.setText("SELECT YOUR TOP INTERESTS " +
                            "\n(Yon may receive event recommendations based on your interests)");
                    screen=3;
                }
                else if(screen == 3){
                    next.setText("Finish");
                    viewAnimator.showNext();
                    screen=4;
                }
                else if(screen==4){
                    if(selectedBg!=null) {
                        String filenameArray[] = selectedBg.split("\\.");
                        String extension = filenameArray[filenameArray.length - 1];
                        fname = name + "." + extension;
                        FileUploader fUpload = new FileUploader(selectedBg, fname);
                        fUpload.execute();
                    }

                    UpdateDatabase ud = new UpdateDatabase();
                    ud.execute();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startActivity(new Intent(getApplicationContext(), EventHub.class));
                    finish();

                }
            }
        });
    }

    public void setInterests (){
        list = "";
        for (String interest : interests) {
            list+=(interests.indexOf(interest)+1)+" : "+interest+"\n";
        }
        info2.setTextSize(40);
        info2.setText(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //Assinging on corresponding import
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA && resultCode == RESULT_OK) {
            Uri contentUri = data.getData();
            String[] proj = { MediaStore.MediaColumns.DATA };
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String imagePath =  cursor.getString(column_index);
            selectedBg= imagePath;
            Bitmap myImage = BitmapFactory.decodeFile(imagePath);
            img.setImageBitmap(myImage);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    class CheckDatabase extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/check_name_availability.php";
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
                nameAvailable= result.equals("available");
            }
            catch (ClientProtocolException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            return null;
        }
    }

    class UpdateDatabase extends AsyncTask<String, String, String> {
        private  String url_check_name = "http://saduda.com/sqlphp/update_user.php";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url_check_name);
        /**
         * Updating user table
         * */
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username.getText().toString()));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("interests", interests.toString()));
            params.add(new BasicNameValuePair("number", mPhoneNumber));
            params.add(new BasicNameValuePair("birthday", dob));
            if(fname!=null) params.add(new BasicNameValuePair("image", fname));
            else params.add(new BasicNameValuePair("image", "none"));


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

            DownloadUserInfo di = new DownloadUserInfo();
            di.execute();
            return null;
        }
    }

    class DownloadUserInfo extends AsyncTask<String, String, String> {
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

package com.blackstar.slideflow;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

public class Menu extends Activity{
	int level, highestLevel;
	boolean connection = false;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		final Button challenge = (Button)  findViewById(R.id.buttonChallenge);
		final Button timed = (Button)  findViewById(R.id.buttonTimed);
		//final Button multiplayer = (Button)  findViewById(R.id.buttonMultiplayer);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null) connection = true;

		// configure Flurry
		FlurryAgent.setLogEnabled(false);
		// init Flurry
		FlurryAgent.init(this, "FHXP5J7H49G6NHF4Q72M");
		
		challenge.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		Intent i = new Intent(getApplicationContext(), LevelSelectionActivity.class);
        		startActivity(i);
        		FlurryAgent.logEvent("Challenge");
        	}
		});
		
		timed.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		level=1; highestLevel=1;
        		Intent i = new Intent(getApplicationContext(), SlideActivity.class);
        		i.putExtra("level",level); i.putExtra("highestLevel",highestLevel);
        		i.putExtra("time",200);
        		startActivity(i);
        		FlurryAgent.logEvent("Timed");
        	}
		});

		int fb = (int) (Math.random()*(15)) ;

		if (connection && fb == 1) {
			//Leave email feedback dialog
			final Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialogbox);
			dialog.setCancelable(false);
			TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText("Leave feedback");
			TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
			body.setText("Do you have a comment or suggestion? Send me a message");
			Button dialogButton = (Button) dialog.findViewById(R.id.button1);
			dialogButton.setVisibility(View.VISIBLE);
			dialogButton.setText("Leave feedback");
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("text/plain");
					i.putExtra(Intent.EXTRA_EMAIL, new String[]{"blackstar.feedback@gmail.com"});
					i.putExtra(Intent.EXTRA_SUBJECT, "SlideFlow Feedback");
					i.putExtra(Intent.EXTRA_TEXT, "");
					try {
						startActivity(Intent.createChooser(i, "send email using"));
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(Menu.this, "no email client", Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			});
			Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);
			dialogButton2.setVisibility(View.VISIBLE);
			dialogButton2.setText("perhaps later");
			dialogButton2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "FHXP5J7H49G6NHF4Q72M");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}

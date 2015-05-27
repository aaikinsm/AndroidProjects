package com.blackstar.slideflow;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.blackstar.slideflow.R;
import com.flurry.android.FlurryAgent;

public class Menu extends Activity{
	int level, highestLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		final Button challenge = (Button)  findViewById(R.id.buttonChallenge);
		final Button timed = (Button)  findViewById(R.id.buttonTimed);
		final Button multiplayer = (Button)  findViewById(R.id.buttonMultiplayer);

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

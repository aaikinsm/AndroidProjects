package com.blackstar.slideflow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class LevelSelectionActivity extends Activity{
	int level=1, highestLevel=1;
	DisplayLevels canvas;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levels);
		canvas = (DisplayLevels)  findViewById(R.id.displayLevels1);
		final Button start = (Button)  findViewById(R.id.start);
		try {
			FileInputStream fi = openFileInput("user_file");
			Scanner in = new Scanner(fi);
			level=in.nextInt();
			highestLevel=in.nextInt();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			try{
				OutputStreamWriter out = new OutputStreamWriter(openFileOutput("user_file",0)); 
				out.write("1 1");
				out.close(); 
			} catch (IOException z) {
	    		z.printStackTrace(); 
	    	}
		}
		
		canvas.setLevel(highestLevel);
		
		canvas.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					int out = (canvas.getSelectedLevel(event.getX(),event.getY()));
					if(out!=-1 && out<=highestLevel){
						Intent i = new Intent(getApplicationContext(), SlideActivity.class);
		        		i.putExtra("level",out); i.putExtra("highestLevel",highestLevel);
		        		startActivity(i);
					}
				}
				return false;
			}
		});
		start.setOnClickListener (new View.OnClickListener(){
	    	@Override
			public void onClick (View v){
	    		finish();
	    		Intent i = new Intent(getApplicationContext(), SlideActivity.class);
	    		i.putExtra("level",level); i.putExtra("highestLevel",highestLevel);
	    		startActivity(i);
	    	}
		});
	}	
}

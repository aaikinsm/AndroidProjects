package com.blackstar.slideflow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MultiplayerActivity extends Activity{

	int level =1, highestLevel=1, count, p1Score=0, p2Score =0;
	boolean gameOver;
	Handler mHandler;
	Runnable gameClock;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_slide);
		final SlideView canvas1 = (SlideView)  findViewById(R.id.slideView1);
		final SlideView canvas2 = (SlideView)  findViewById(R.id.slideView2);
		final TextView p1Output = (TextView)  findViewById(R.id.textViewP1);
        final TextView p2Output = (TextView)  findViewById(R.id.textViewP2);
        final SeekBar progress = (SeekBar)  findViewById(R.id.progressBar);
		Context cntx = this;
		mHandler = new Handler();
		gameOver = false;
		
		//read file
		Bundle extras = getIntent().getExtras();
		if (extras != null){  
			level = extras.getInt("level");
			highestLevel = extras.getInt("highestLevel");
			count = extras.getInt("time",-1);
            p1Score = extras.getInt("p1Score");
            p2Score = extras.getInt("p2Score");
            level = (int) (level+(Math.random() * 5));
            p1Output.setText(""+p1Score);
            p2Output.setText(""+p2Score);
		}
		else{
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
		}
		
		
		//Start Time Data for Flurry
		final Map<String, String> flurryParam = new HashMap<String, String>();
	    flurryParam.put("Level", level + "");
		FlurryAgent.logEvent("Level_Time", flurryParam, true);
		
		if (level>100) level=1;

		canvas1.loadData(level);
		canvas1.setOnTouchListener(new OnSwipeTouchListener(cntx) {
            @Override
            public void onSwipeTop() {
                canvas1.setDirection("up");
            }

            @Override
            public void onSwipeRight() {
                canvas1.setDirection("right");
            }

            @Override
            public void onSwipeLeft() {
                canvas1.setDirection("left");
            }

            @Override
            public void onSwipeBottom() {
                canvas1.setDirection("down");
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!gameOver) canvas1.selectBlock(event.getX(), event.getY());
                }
                return gestureDetector.onTouchEvent(event);
            }
        });

		canvas2.invert = true;
        canvas2.loadData(level);
		canvas2.setOnTouchListener(new OnSwipeTouchListener(cntx) {
			@Override
			public void onSwipeTop() {
				canvas2.setDirection("up");
			}
			@Override
			public void onSwipeRight() {
				canvas2.setDirection("right");
			}
			@Override
			public void onSwipeLeft() {
				canvas2.setDirection("left");
			}
			@Override
			public void onSwipeBottom() {
				canvas2.setDirection("down");
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(!gameOver) canvas2.selectBlock(event.getX(), event.getY());
				}
				return gestureDetector.onTouchEvent(event);
			}
		});

		
		gameClock = new Runnable() {   
        	@Override
    		public void run() {
        		if (count<0){
        			gameOver = true;
                    if(p1Score>p2Score){
                        p1Output.setText("You Win");
                        p2Output.setText("You Lose");
                    }else{
                        p2Output.setText("You Win");
                        p1Output.setText("You Lose");
                    }
                    
        		}
        		else if(canvas1.gameOver || canvas2.gameOver){
                    gameOver=true;
                    if(canvas1.gameOver){
                        p1Score++;
                        p1Output.setText(""+p1Score);
                    }
                    else {
                        p2Score++;
                        p2Output.setText(""+p2Score);
                    }

                    Intent i = new Intent(getApplicationContext(), MultiplayerActivity.class);
                    i.putExtra("level",level+ 1);
                    i.putExtra("highestLevel",highestLevel);
                    i.putExtra("p1Score",p1Score);
                    i.putExtra("p2Score",p2Score);
                    i.putExtra("time",count);
                    startActivity(i);
                    finish();
        		}
        		else{
                    count--;
                    progress.setProgress(count/2);
        			mHandler.postDelayed(this, 500);          			
        		}       		
        	}
        };
        mHandler.postDelayed(gameClock, 500);
	}
	
}

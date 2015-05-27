package com.blackstar.slideflow;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BackgroundSlide extends View{

	int width=100, height=100;
	public int [][] data = new int [10][2];
	Paint square = new Paint();

	public BackgroundSlide(Context context){
		super (context);
	}

    public BackgroundSlide(Context context, AttributeSet attrs) {
		super( context, attrs );
	}

	public BackgroundSlide(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
    public void onMeasure(int x, int y){
    	super.onMeasure(x, y);
    	setMeasuredDimension(x, y);
    }
	
	Paint paint = new Paint(), paint2 = new Paint(), paint3 = new Paint();
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		width = getMeasuredWidth();
		height = getMeasuredHeight();

		//Display random flashing squares
		square.setStyle(Paint.Style.FILL);
		int xpos = (int)(Math.random()*width);
		int ypos = (int)(Math.random()*height);
		if(xpos%5==0) canvas.drawRect(xpos, ypos, xpos+100, ypos+100, square); //show at rand times

		//display touch trail
		square.setColor(Color.rgb((int) (Math.random() * 255), 200, (int) (Math.random() * 255)));//random color
		square.setStrokeWidth(6);
		square.setStyle(Paint.Style.STROKE);
		for(int i=0; i<10;i++){
			int size = 10*(10 - i);
			square.setAlpha(100 - (i * 5)); //Diminishing alpha
			canvas.drawRect(data[i][0]-size, data[i][1]-size, data[i][0] + size, data[i][1] + size, square); //Diminishing size
			double rand = 7*Math.random();
			data[i][1]+=(rand-2); //random vertical movement
			data[i][0]+=(rand-3); //random horizontal movement
			if(data[i][0]==0) data[i][0]=10000; //start off screen
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		invalidate();
	}
	

	@Override
	public boolean onTouchEvent( MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

			case MotionEvent.ACTION_MOVE:
				for(int i=9; i>0; i--){
					data[i][0]=data[i-1][0];
					data[i][1]=data[i-1][1];
				}
				data[0][0]=(int)event.getX();
				data[0][1]=(int)event.getY();
				invalidate();
			case MotionEvent.ACTION_UP:

		} return true;
	}

}

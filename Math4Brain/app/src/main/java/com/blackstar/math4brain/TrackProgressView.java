package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TrackProgressView extends View{
	
	Paint textP = new Paint(), pointP = new Paint(), lineP = new Paint();
	long[] dataPoints = new long[]{1500,2344,2344,3000,2636,4000,5097};
	long max = 5098;
	long min = 1500;
	int length = dataPoints.length; 
	
	public TrackProgressView(Context context) {
		super(context);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		pointP.setColor(Color.CYAN);
		lineP.setColor(Color.BLUE);
		textP.setAlpha(50);
		
		int height = getMeasuredHeight()-20;
		int width = getMeasuredWidth();
		
		canvas.drawText(min/100+"",5,height, textP);
		canvas.drawText((min+(max-min)/2)/100+"",5,height/2, textP);
		canvas.drawText(max/100+"",5,20, textP);
		canvas.drawLine(0, height/2, canvas.getWidth(), height/2, textP);
		canvas.drawLine(0, height, canvas.getWidth(), height, textP);
		for (int i = 0; i<length; i++){
			canvas.drawCircle(width/length*(i+1)-10, height+10-(int)((1.0*dataPoints[i]-min)/(max-min)*height), 5, pointP);
			if (i!=0) canvas.drawLine(width/length*(i)-10, height+10-(int)((1.0*dataPoints[i-1]-min)/(max-min)*height), 
					width/length*(i+1)-10, height+10-(int)((1.0*dataPoints[i]-min)/(max-min)*height), lineP);
		}	
	}
	
	public void update(long[][] dataT, int len){
		dataPoints = new long[365];
		length = len;
		int j=1, k=0;
		min = dataT[0][1]; max = dataT[0][1];
		long time, time2;
		while(j<=length){
			long days1 = 150000;//86400000;
			time = dataT[k][0];
			time2 = dataT[k+1][0];
			long elapsed = (time-time2)/days1;
			if(elapsed>30)elapsed=0;
			Log.d("testTrack","k="+dataT[k][0]+" k+1="+dataT[k+1][0]+" times:"+elapsed+" level:"+dataT[k][1]);
//			for(int i=0; i<elapsed; i++){
//				dataPoints[length-j]=dataT[k][1];
//				j++;
//			}
			dataPoints[length-j]=dataT[k][1];
			if (dataPoints[length-j]<min) min = dataPoints[length-j];
			if (dataPoints[length-j]>max) max = dataPoints[length-j];
			j++; k++;
		}
	}
}

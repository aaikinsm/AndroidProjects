package com.blackstar.saduda;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by imdc on 17/07/2015.
 */
public class ComputeDateTime {
    long diffDays, diffHours;
    Date endDate;
    Calendar end;

    public ComputeDateTime(String givenDate){
        givenDate= givenDate.replace(" ",",").replace("-",",").replace(":", ",");
        String data[] = givenDate.split(",");

        Calendar start = Calendar.getInstance();
        end = Calendar.getInstance();
        end.set(Integer.parseInt(data[0]), Integer.parseInt(data[1])-1, Integer.parseInt(data[2]),
                Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
        Date startDate = start.getTime();
        endDate = end.getTime();
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        diffDays = diffTime / (1000 * 60 * 60 * 24);
        diffHours = diffTime / (1000 * 60 * 60);
        //DateFormat dateFormat = DateFormat.getDateInstance();

        System.out.println("The difference between "+(startDate)+" and "+(endDate)+" is "+
                diffDays+" days. & "+diffHours+"hours");
    }

    public String getString(){
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        String result="";
            if (diffDays < 0) {
                result = Math.abs(diffDays - 1) + " day(s) ago";
            } else if (diffDays == 0) {
                if(diffHours>0)
                    result = diffHours + " hour(s) from now";
                else
                    result = diffHours + " hour(s) ago";
            } else if (diffDays < 7) {
                result = (diffDays + 1) + " day(s) from now";
            } else {
                result = dateFormat.format(endDate).toString();
            }
        result+=" | "+timeFormat.format(endDate).toString();
        return result;
    }

    public String getDateTimeString(){
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        String result=dateFormat.format(endDate).toString();
        result+=" | "+timeFormat.format(endDate).toString();
        return result;
    }

    public String getDateString(){
        DateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM");
        return dateFormat.format(endDate).toString();
    }

    public String getDatePlusDuration(String dur){
        String[] duration = dur.split(":");
        int hr = Integer.parseInt(duration[0]);
        end.add(Calendar.HOUR, hr);
        if(duration.length>1) {
            int min = Integer.parseInt(duration[1]);
            end.add(Calendar.MINUTE, min);
        }
        endDate=end.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d H:m:s");
        return dateFormat.format(endDate).toString();
    }
}

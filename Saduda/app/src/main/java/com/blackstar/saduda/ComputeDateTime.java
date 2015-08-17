package com.blackstar.saduda;

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

    public ComputeDateTime(String givenDate){
        givenDate= givenDate.replace(" ",",").replace("-",",").replace(":", ",");
        String data[] = givenDate.split(",");

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
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

    public String getString(Boolean showFullString){
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        String result="";
        if(showFullString) {
            if (diffDays < 0) {
                result = Math.abs(diffDays - 1) + " day(s) ago";
            } else if (diffDays == 0) {
                result = diffHours + " hour(s) from now";
            } else if (diffDays < 7) {
                result = (diffDays + 1) + " day(s) from now";
            } else {
                result = dateFormat.format(endDate).toString();
            }
        } else result = dateFormat.format(endDate).toString();

        result+=" | "+timeFormat.format(endDate).toString();
        return result;
    }
}

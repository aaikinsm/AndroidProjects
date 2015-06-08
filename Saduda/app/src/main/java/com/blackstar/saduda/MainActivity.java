package com.blackstar.saduda;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

    List<String[]> uList = new ArrayList<String[]>();
    EventListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        ListView userList = (ListView) findViewById(R.id.eventList);
        listAdapter = new EventListAdapter(this, R.layout.event_item, uList);

        userList.setAdapter(listAdapter);

        String[] myInf = new String[5];
        myInf[0]=("5/11 10:30PM");
        myInf[1]=("Soca Fete");
        myInf[2]=("Feel the summer time vybz at our 5th annual soca party");
        myInf[3]=("1");
        myInf[4]=("1");
        uList.add(myInf);

        String[] myInf2 = new String[5];
        myInf2[0]=("5/23 11:45AM");
        myInf2[1]=("Job Fair");
        myInf2[2]=("Job fair is being hosted for ryerson graduates. Please bring your resume");
        myInf2[3]=("0");
        myInf2[4]=("0");
        uList.add(myInf2);

        String[] myInf3 = new String[5];
        myInf3[0]=("6/01 5:00PM");
        myInf3[1]=("Canada Day");
        myInf3[2]=("July 1st marks our country's birthday, come and celebrate with your fellow canadians");
        myInf3[3]=("1");
        myInf3[4]=("0");
        uList.add(myInf3);
    }


}

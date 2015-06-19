package com.blackstar.saduda;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends Activity {

    List<String[]> uList = new ArrayList<String[]>();
    EventListAdapter listAdapter;
    boolean submenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        Button eventHub = (Button) findViewById(R.id.evt_tab);
        Button calendar = (Button) findViewById(R.id.cal_tab);
        final ListView userList = (ListView) findViewById(R.id.eventList);
        listAdapter = new EventListAdapter(this, R.layout.event_item, uList);

        userList.setAdapter(listAdapter);

        eventHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });



        String[] myInf = new String[5];
        myInf[0]=("5/11 10:30PM");
        myInf[1]=("4th Annual Island Soca Fete");
        myInf[2]=("Feel the summer time vybz at our 5th annual soca party");
        myInf[3]=("1");
        myInf[4]=("1");
        uList.add(myInf);

        String[] myInf2 = new String[5];
        myInf2[0]=("5/23 11:45AM");
        myInf2[1]=("Job Fair & Expo");
        myInf2[2]=("Ontario's largest and most established recruitment event open to the general public. " +
                "This Job fair is being hosted for Ryerson graduates. Please bring your resume");
        myInf2[3]=("0");
        myInf2[4]=("0");
        uList.add(myInf2);

        String[] myInf3 = new String[5];
        myInf3[0]=("6/01 5:00PM");
        myInf3[1]=("Canada Day");
        myInf3[2]=("July 1st marks our country's birthday, come and celebrate with your fellow canadians, Bring your flags");
        myInf3[3]=("1");
        myInf3[4]=("0");
        uList.add(myInf3);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d("CLickedItem at ", ":" + position);
                setContentView(R.layout.event_details);
                submenu =true;
                TextView title = (TextView) findViewById(R.id.eventTitle);
                final TextView description = (TextView) findViewById(R.id.eventDesc);
                TextView time = (TextView) findViewById(R.id.eventTime);
                ImageView img = (ImageView) findViewById(R.id.eventImg);
                ImageView fav = (ImageView) findViewById(R.id.eventFav);
                Switch join = (Switch) findViewById(R.id.eventJoin);
                Button chat = (Button) findViewById(R.id.chat);

                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        description.setVisibility(View.GONE);
                        userList.setVisibility(View.GONE);
                    }
                });

                time.setText(uList.get(position)[0]);

                title.setText(uList.get(position)[1]);

                description.setText(uList.get(position)[2]);

                if(uList.get(position)[3].equals("1"))
                    join.setChecked(true);
                else
                    join.setChecked(false);

                if(uList.get(position)[4].equals("1"))
                    fav.setImageResource(R.drawable.favourite1);
                else
                    fav.setImageResource(R.drawable.favourite0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(submenu) startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}

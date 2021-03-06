package com.blackstar.saduda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class EventListAdapter extends ArrayAdapter<String[]> {

    // declaring our ArrayList of items
    private List<String[]> objects;
    Bitmap bmp;
    HashMap map = new HashMap<String, Object>();

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public EventListAdapter(Context context, int textViewResourceId, List<String[]> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.event_item, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        String[] i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView title = (TextView) v.findViewById(R.id.eventTitle);
            TextView description = (TextView) v.findViewById(R.id.eventDesc);
            TextView time = (TextView) v.findViewById(R.id.eventTime);
            ImageView img = (ImageView) v.findViewById(R.id.eventImg);
            ImageView join = (ImageView) v.findViewById(R.id.eventJoin);
            img.setImageResource(R.drawable.calendar);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (time != null){
                time.setText(new ComputeDateTime(i[9]).getDateTimeString().replace(" | ","\n"));
            }
            if (title != null){
                if(i[1].length()>25) title.setText(i[1].substring(0,25)+"...");
                else title.setText(i[1]);
            }
            if (description != null){
                if(i[3].length()>100) description.setText(i[3].substring(0, 100) + "...");
                else description.setText(i[3]);
            }
            if (join != null){
                if(i[13].equals("true"))
                    join.setImageResource(R.drawable.switch_on);
                else
                    join.setImageResource(R.drawable.switch_off);
            }

            if(!i[17].equals("none")) {
                UpdatePhoto up = new UpdatePhoto(i[17]);
                if(!map.containsKey(i[17])) up.execute();

                for (int j = 0; j < 50; j++) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(map.containsKey(i[17])){
                        if(map.get(i[17])!=null) img.setImageBitmap((Bitmap) map.get(i[17]));
                        break;
                    }
                }
            }

        }

        // the view must be returned to our activity
        return v;
    }

    class UpdatePhoto extends AsyncTask<String, String, String> {
        String path;
        public UpdatePhoto(String name){
            path = name;
        }
        @Override
        protected String doInBackground(String... args) {
            URL url = null;
            try {
                if(path.contains("://") || path.contains(".com")) url = new URL(path);
                else url = new URL("http://saduda.com/uploads/events/small/" + path);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                map.put(path,bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                map.put(path, null);
            }
            return null;
        }
    }

}

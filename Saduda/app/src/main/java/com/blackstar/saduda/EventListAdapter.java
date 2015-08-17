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
import java.util.List;

public class EventListAdapter extends ArrayAdapter<String[]> {

    // declaring our ArrayList of items
    private List<String[]> objects;
    Bitmap bmp;
    int dataReadyPos=-1;

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
            ImageView fav = (ImageView) v.findViewById(R.id.eventFav);
            Switch join = (Switch) v.findViewById(R.id.eventJoin);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (time != null){
                time.setText(new ComputeDateTime(i[9]).getString(false).replace(" | ","\n"));
            }
            if (title != null){
                if(i[1].length()>15) title.setText(i[1].substring(0,25)+"...");
                else title.setText(i[1]);
            }
            if (description != null){
                if(i[3].length()>100) description.setText(i[3].substring(0, 100) + "...");
                else description.setText(i[3]);
            }
            if (join != null){
                if(i[13].equals("true"))
                    join.setChecked(true);
                else
                    join.setChecked(false);
            }
            if (fav != null){
                if(i[15].equals("true"))
                    fav.setImageResource(R.drawable.favourite1);
                else
                    fav.setImageResource(R.drawable.favourite0);
            }

            if(!i[17].equals("none")) {
                UpdatePhoto up = new UpdatePhoto(i[17], position);
                up.execute();

                for (int j = 0; j < 50; j++) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (dataReadyPos == position) {
                        if (bmp != null) img.setImageBitmap(bmp);
                        break;
                    }
                }
            }

        }

        // the view must be returned to our activity
        return v;
    }

    class UpdatePhoto extends AsyncTask<String, String, String> {
        String path; int position;
        public UpdatePhoto(String name, int pos){
            path = name;
            position = pos;
        }
        @Override
        protected String doInBackground(String... args) {
            URL url = null;
            try {
                url = new URL("http://saduda.com/uploads/events/small/" + path);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataReadyPos=position;
            return null;
        }
    }

}

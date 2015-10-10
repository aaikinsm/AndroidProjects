package com.blackstar.saduda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by albert on 16/07/2015.
 */
public class CommentListAdapter  extends ArrayAdapter<String[]> {
    private List<String[]> objects;
    Bitmap bmp;
    HashMap map = new HashMap<String, Object>();

    public CommentListAdapter(Context context, int textViewResourceId, List<String[]> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.comment_item, null);
        }

        String[] i = objects.get(position);

        if (i != null && i.length>1) {

            TextView name = (TextView) v.findViewById(R.id.author);
            TextView message = (TextView) v.findViewById(R.id.message);
            TextView time = (TextView) v.findViewById(R.id.time);
            ImageView img = (ImageView) v.findViewById(R.id.userImg);
            img.setImageResource(R.drawable.icon_user_default);

            if (time != null){
                time.setText(i[5]);
            }
            if (name != null){
                name.setText(i[1]);
            }
            if (message != null){
                message.setText(i[3]);
            }

            UpdatePhoto up = new UpdatePhoto(i[1]);
            if(!map.containsKey(i[1])) up.execute();

            for (int j = 0; j < 50; j++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(map.containsKey(i[1])){
                    if(map.get(i[1])!=null) img.setImageBitmap((Bitmap) map.get(i[1]));
                    break;
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
                url = new URL("http://saduda.com/uploads/profile/small/" + path+".jpg"); //TODO: Get actual file extention
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

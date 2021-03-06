package me.bowarren.photocal;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bhwarren on 10/3/15.
 */
public class EventsFragment extends android.support.v4.app.ListFragment{

    private static EventHolder eh;
    private Context context;
    private GridViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View retView = inflater.inflate(R.layout.events_view, container, false);
        context = retView.getContext();
        eh = new EventHolder(getContext());
        //eh.addEvent(new PhotoCalEvent("Test Event", new Date(), new Date(), "location", "description", getActivity()));

        adapter = new GridViewAdapter(retView.getContext(), eh.savedEvents);
        setListAdapter(adapter);

        return retView;
    }

    @Override
    public void onResume() {
        //adapter.notifyDataSetChanged();
//        if((FloatingActionButton) this.getView().findViewById(R.id.myFAB) != null)
//            ((FloatingActionButton) this.getView().findViewById(R.id.myFAB)).setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).switchIconsToPreview(false);

        try {
            Thread.sleep(2000);

        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        //update all the events for when edited an event
        ArrayList<HashMap> replaceEvents = new ArrayList<HashMap>();
        while(eh.savedEvents.size() > 0){
            PhotoCalEvent temp = new PhotoCalEvent(eh.savedEvents.remove(0));
            PhotoCalEvent tempRepl = CalendarHelper.getRealInfo(temp.eventId, this.getActivity());
            if(tempRepl != null) {
                tempRepl.image = temp.image;
                replaceEvents.add(tempRepl.toDict());
            }
            else{
                replaceEvents.add(temp.toDict());
            }
        }
        eh.savedEvents = replaceEvents;

        adapter = new GridViewAdapter(getContext(), eh.savedEvents);
        setListAdapter(adapter);
        Log.e("f", eh.savedEvents.size()+"XXXXXXXX");



        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onListItemClick (ListView lv, View clickedView, int index, long row_id){
        //Toast.makeText(context, "removing index: "+String.valueOf(index), Toast.LENGTH_SHORT).show();
        Log.e("f", "item clicked: "+clickedView.getId());
        if(index < eh.savedEvents.size()) {
            Toast.makeText(context, "opening calendar for index: "+String.valueOf(index), Toast.LENGTH_SHORT).show();
            PhotoCalEvent event = new PhotoCalEvent(eh.savedEvents.get(index));

            if(! event.eventName.equals("Not Set")) //if event name is set, open up the calendar to the event
                CalendarHelper.openCalendarEvent(event, getActivity());
            else {
                CalendarHelper.lastIndexSelected = index;
                CalendarHelper.addToCalendar(event, getActivity());
            }

            //clickRemove(index);
        }
    }

    private void clickRemove(int index) {
        eh.removeEvent(eh.savedEvents.get(index));
        adapter.notifyDataSetChanged();
    }



    private class GridViewAdapter extends ArrayAdapter{

        Context context;

        public GridViewAdapter(Context context, ArrayList<HashMap> events) {
            super(context, R.layout.event_item_layout, events);
            this.context = context;
        }

        public View getView(final int index, View rawView, ViewGroup parent){
            Log.e("getView invoked", "");

            if(index >= eh.savedEvents.size() && eh.savedEvents.size() > 1){
                return rawView;
            }

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rawView = inflater.inflate(R.layout.event_item_layout, parent, false);

            GridLayout textHolder = (GridLayout) rawView.findViewById(R.id.event_item_layout);
            GridLayout.LayoutParams textParams = (GridLayout.LayoutParams) textHolder.getLayoutParams();
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            textParams.width = metrics.widthPixels * 3/5;
            textHolder.setLayoutParams(textParams);


            TextView eventName_tv = (TextView) rawView.findViewById(R.id.eventName);
            eventName_tv.setText((String) eh.savedEvents.get(index).get("eventName"));

            TextView begin_tv = (TextView) rawView.findViewById(R.id.begin);
            GregorianCalendar beginTime = (GregorianCalendar) eh.savedEvents.get(index).get("begin");
            if(beginTime != null)
                begin_tv.setText(beginTime.getTime().toString());

            TextView end_tv = (TextView) rawView.findViewById(R.id.end);
            GregorianCalendar endTime = (GregorianCalendar) eh.savedEvents.get(index).get("end");
            if(endTime != null)
                end_tv.setText(endTime.getTime().toString());

            TextView location_tv = (TextView) rawView.findViewById(R.id.location);
            location_tv.setText((String) eh.savedEvents.get(index).get("location"));

            TextView description_tv = (TextView) rawView.findViewById(R.id.description);
            description_tv.setText((String) eh.savedEvents.get(index).get("description"));

            //close button
            ImageView closeButton = (ImageView) rawView.findViewById(R.id.closeButtonView);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoCalEvent eventToDel = new PhotoCalEvent(eh.savedEvents.get(index));
                    CalendarHelper.removeFromCalendar(eventToDel, getActivity());
                    eventToDel.image.delete();
                    clickRemove(index);
                }
            });

            //preview image
            ImageView preview = (ImageView) rawView.findViewById(R.id.preview);
            String path = ((File) eh.savedEvents.get(index).get("image")).getAbsolutePath();
            final String fpath = path;
            Bitmap img = BitmapFactory.decodeFile(path+"_preview.jpg");
            //preview.setImageBitmap(scaleImg(img, 200));
            preview.setImageBitmap(img);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewImageActivity.class);
                    Bundle b = new Bundle();
                    b.putString("imgPath", fpath);
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                }
            });
            //int freeWidth = ((GridLayout) rawView.findViewById(R.id.larger_event_item_layout)).getWidth() - (textParams.width + closeButton.getWidth());
            int freeWidth = (metrics.widthPixels - (textParams.width + metrics.widthPixels/4)); //the 1/6 is for teh close button

            Log.e("size of preview", "Free width: " + freeWidth + " text width: " + textParams.width + " total width: " + ((GridLayout) rawView.findViewById(R.id.larger_event_item_layout)).getWidth());
            preview.setMaxWidth(freeWidth);

            return rawView;
        }

//        private Bitmap scaleImg(Bitmap image, int newHeight){
//            int height = image.getHeight();
//            Double ratio = new Double(height) / newHeight;
//            int newWidth = (int) (image.getWidth() / ratio);
//            return Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
//        }

    }

}


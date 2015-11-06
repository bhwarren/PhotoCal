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
import android.support.v4.app.ListFragment;
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
        adapter.notifyDataSetChanged();
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


            if(index >= eh.savedEvents.size() && eh.savedEvents.size() > 1){
                return rawView;
            }

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rawView = inflater.inflate(R.layout.event_item_layout, parent, false);

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

            ImageView closeButton = (ImageView) rawView.findViewById(R.id.closeButtonView);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarHelper.removeFromCalendar(
                            new PhotoCalEvent(eh.savedEvents.get(index)), getActivity());
                    clickRemove(index);
                }
            });


            ImageView preview = (ImageView) rawView.findViewById(R.id.preview);
            String path = ((File) eh.savedEvents.get(index).get("image")).getAbsolutePath();
            preview.setImageBitmap(BitmapFactory.decodeFile(path));


            return rawView;
        }

    }

}


package me.bowarren.photocal;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
        eh = new EventHolder();
        //eh.addEvent(new PhotoCalEvent("Test Event", new Date(), new Date(), "location", "description"));

        adapter = new GridViewAdapter(retView.getContext(), eh.savedEvents);
        setListAdapter(adapter);

        return retView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onListItemClick (ListView lv, View clicked_view, int index, long row_id){
        Toast.makeText(context, "removing index: "+String.valueOf(index), Toast.LENGTH_SHORT).show();

        if(index >= eh.savedEvents.size())
            return;

        eh.removeEvent(eh.savedEvents.get(index));
        adapter.notifyDataSetChanged();

    }



    private class PhotoCalEvent {

        String eventName;
        Date begin;
        Date end;
        String location;
        String description;

        public PhotoCalEvent(String eventName, Date begin , Date end,
                             String location, String description){
            this.eventName = eventName;
            this.begin = begin;
            this.end = end;
            this.location = location;
            this.description = description;

        }

        public HashMap toDict(){
            HashMap thisDict = new HashMap();
            thisDict.put("eventName", this.eventName);
            thisDict.put("begin", this.begin);
            thisDict.put("end", this.end);
            thisDict.put("location", this.location);
            thisDict.put("description", this.description);
            return thisDict;
        }

    }

    private class EventHolder {
        private File storage;
        private ArrayList<HashMap> savedEvents;


        public EventHolder(){
            storage = new File(getContext().getFilesDir(), "eventsStorage");
            if(!storage.exists()){
                try {
                    storage.createNewFile();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(!storage.canRead()){
                storage.setReadable(true);
            }
            if(!storage.canWrite()){
                storage.setWritable(true);
            }

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storage));
                this.savedEvents = (ArrayList<HashMap>) ois.readObject();
                ois.close();

                return;
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }

            //only hits this if exceptions were caught
            this.savedEvents = new ArrayList<HashMap>();

        }

        //returns a copy of savedEvents
//        public ArrayList<PhotoCalEvent> getEvents(){
//            ArrayList<PhotoCalEvent> retObj = new ArrayList<PhotoCalEvent>();
//            for (HashMap inObj : savedEvents) {
//                PhotoCalEvent event = new PhotoCalEvent(
//                        (String) inObj.get("eventName"),
//                        (Date) inObj.get("begin"),
//                        (Date) inObj.get("end"),
//                        (String) inObj.get("location"),
//                        (String) inObj.get("description"));
//                retObj.add(event);
//            }
//            return retObj;
//
//        }

        public boolean addEvent(PhotoCalEvent event){
            if(savedEvents.add(event.toDict())) {
                writeEvents();
                return true;
            }
            return false;
        }

        public void removeEvent(HashMap event){

            for(int i = 0; i< savedEvents.size(); i++){

                boolean isSame =
                        event.get("eventName").equals( savedEvents.get(i).get("eventName") ) &&
                        event.get("begin").equals(savedEvents.get(i).get("begin")) &&
                        event.get("end").equals(savedEvents.get(i).get("end")) &&
                        event.get("location").equals( savedEvents.get(i).get("location") ) &&
                        event.get("description").equals( savedEvents.get(i).get("description") );

                if(isSame){
                    savedEvents.remove(i);
                    writeEvents();
                    break;
                }
            }
        }

        //write the savedEvents to file
        private void writeEvents(){
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storage));
                oos.writeObject(savedEvents);
                oos.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class GridViewAdapter extends ArrayAdapter{

        Context context;

        public GridViewAdapter(Context context, ArrayList<HashMap> events) {
            super(context, R.layout.event_item_layout, events);
            this.context = context;
        }

        public View getView(int index, View rawView, ViewGroup parent){


            if(index >= eh.savedEvents.size()){
                return rawView;
            }

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rawView = inflater.inflate(R.layout.event_item_layout, parent, false);

            TextView eventName_tv = (TextView) rawView.findViewById(R.id.eventName);
            eventName_tv.setText((String) eh.savedEvents.get(index).get("eventName"));

            TextView begin_tv = (TextView) rawView.findViewById(R.id.begin);
            begin_tv.setText(((Date) eh.savedEvents.get(index).get("begin")).toString());

            TextView end_tv = (TextView) rawView.findViewById(R.id.end);
            end_tv.setText(((Date)eh.savedEvents.get(index).get("end")).toString());

            TextView location_tv = (TextView) rawView.findViewById(R.id.location);
            location_tv.setText((String) eh.savedEvents.get(index).get("location"));

            TextView description_tv = (TextView) rawView.findViewById(R.id.description);
            description_tv.setText((String) eh.savedEvents.get(index).get("description"));

            return rawView;
        }

    }

}
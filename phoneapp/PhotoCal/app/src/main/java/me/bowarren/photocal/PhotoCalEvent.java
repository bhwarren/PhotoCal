package me.bowarren.photocal;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by bhwarren on 10/5/15.
 */
public class PhotoCalEvent {

    String eventName;
    Calendar begin;
    Calendar end;
    String location;
    String description;
    Long eventId;
    Long calendarID;

    //make new eventId for the Event
    public PhotoCalEvent(String eventName, Calendar begin , Calendar end,
                         String location, String description, Activity activity){

        this(eventName, begin, end, location, description,
                getNewId(activity.getContentResolver()));

    }

    //construct object given the existing ID
    public PhotoCalEvent(String eventName, Calendar begin , Calendar end,
                         String location, String description, Long eventId, Long calendarID){
        this(eventName, begin, end, location, description, eventId);
        this.calendarID = calendarID;

    }
    //construct object given the existing ID
    public PhotoCalEvent(String eventName, Calendar begin , Calendar end,
                         String location, String description, Long eventId){
        this.eventName = eventName;
        this.begin = begin;
        this.end = end;
        this.location = location;
        this.description = description;
        this.eventId = eventId;
        Log.e("f", "making new event, adding new id: " + String.valueOf(eventId));

    }

    public PhotoCalEvent(HashMap event){
        this.eventName = (String) event.get("eventName");
        this.begin = (GregorianCalendar) event.get("begin");
        this.end = (GregorianCalendar) event.get("end");
        this.location = (String) event.get("location");
        this.description = (String) event.get("description");
        this.eventId = (Long) event.get("eventId");
        this.calendarID = (Long) event.get("calendarId");
    }

    public HashMap toDict(){
        HashMap thisDict = new HashMap();
        thisDict.put("eventName", this.eventName);
        thisDict.put("begin", this.begin);
        thisDict.put("end", this.end);
        thisDict.put("location", this.location);
        thisDict.put("description", this.description);
        thisDict.put("eventId", this.eventId);
        thisDict.put("calendarId", this.calendarID);
        return thisDict;
    }

    private static Long getNewId(ContentResolver resolver){
        Uri calUri = Uri.parse("content://com.android.calendar/events");
        Cursor cursor = resolver.query(calUri, new String[] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long highestId = cursor.getLong(cursor.getColumnIndex("max_id"));
        return highestId + 1;
    }

    public String toString(){
        return "eventName: "+eventName+"  "+
                "begin: "+begin.toString()+"  "+
                "end: "+end.toString()+"  "+
                "location: "+location+"  "+
                "description: "+description+"  "+
                "eventId: "+String.valueOf(eventId)+"  "+
                "calendarId: "+String.valueOf(calendarID)+"  ";
    }

    public boolean equals(PhotoCalEvent event){
        //if ids match, return true
        if(eventId == event.eventId)
            return true;
        //if one of ids is null, check by fields
        else if(eventId == null || event.eventId == null){

            return eventName.equals(event.eventName) &&
                    begin.equals(event.begin) &&
                    end.equals(event.end) &&
                    location.equals(event.location) &&
                    description.equals(event.description);
        }

        //otherwise the ids definitely don't match
        return false;

    }

}

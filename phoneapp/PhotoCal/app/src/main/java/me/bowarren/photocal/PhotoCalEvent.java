package me.bowarren.photocal;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by bhwarren on 10/5/15.
 */
public class PhotoCalEvent {

    String eventName;
    Date begin;
    Date end;
    String location;
    String description;
    long eventId;

    public PhotoCalEvent(String eventName, Date begin , Date end,
                         String location, String description, Activity activity){
        this.eventName = eventName;
        this.begin = begin;
        this.end = end;
        this.location = location;
        this.description = description;
        this.eventId = getNewId(activity.getContentResolver());
        Log.e("f", "making new event, adding new id: " + String.valueOf(eventId));


    }

    public PhotoCalEvent(HashMap event){
        this.eventName = (String) event.get("eventName");
        this.begin = (Date) event.get("begin");
        this.end = (Date) event.get("end");
        this.location = (String) event.get("location");
        this.description = (String) event.get("description");
        this.eventId = (Long) event.get("eventId");
    }

    public HashMap toDict(){
        HashMap thisDict = new HashMap();
        thisDict.put("eventName", this.eventName);
        thisDict.put("begin", this.begin);
        thisDict.put("end", this.end);
        thisDict.put("location", this.location);
        thisDict.put("description", this.description);
        thisDict.put("eventId", this.eventId);
        return thisDict;
    }

    private static Long getNewId(ContentResolver resolver){
        Uri calUri = Uri.parse("content://com.android.calendar/events");
        Cursor cursor = resolver.query(calUri, new String[] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long highestId = cursor.getLong(cursor.getColumnIndex("max_id"));
        return highestId + 1;
    }

}

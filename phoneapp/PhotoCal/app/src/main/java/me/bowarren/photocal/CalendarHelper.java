package me.bowarren.photocal;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bhwarren on 10/5/15.
 */
public class CalendarHelper {

    public static Long lastEventIdAdded;

    //open up the calendar.  need an activity to start intent
    public static void openCalendar(Activity activity){
        long startMillis = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        activity.startActivity(intent);
    }


    //send the photo to the server and get the stuff back
    public static PhotoCalEvent getEventInfo(File picture, Activity activity){

        //send photo to server

        //get json response

        //return new PhotoCalEvent w/ the response
        PhotoCalEvent event = new PhotoCalEvent("title", new Date(), new Date(), "location", "description", activity);
        return event;

    }

    //add event to the native calendar
    public static void addToCalendar(PhotoCalEvent event, Activity activity){

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.begin.getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end.getTime())
                .putExtra(Events.TITLE, event.eventName)
                .putExtra(Events.DESCRIPTION, event.description)
                .putExtra(Events.EVENT_LOCATION, event.location)
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                .putExtra(Events._ID, event.eventId);
        activity.startActivityForResult(intent, 0);
        //update our event based on what was added
        lastEventIdAdded = event.eventId;

    }

    public static void openCalendarEvent(PhotoCalEvent event, Activity activity){
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.eventId);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(uri);
        activity.startActivity(intent);
    }

    public static void removeFromCalendar(PhotoCalEvent event, Activity activity){
        PhotoCalEvent finalEvent = getRealInfo(event.eventId, activity);

        if(finalEvent == null){
            return;
        }

        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");
        Uri uri = ContentUris.withAppendedId(CALENDAR_URI, finalEvent.eventId);
//
//        if(finalEvent != null) {
//            uri = ContentUris.withAppendedId(uri, event.calendarID);
//        }

//        Intent intent = new Intent(Intent.ACTION_DELETE)
//                .setData(Events.CONTENT_URI)
//                .putExtra(CalendarContract.Calendars._ID, finalEvent.calendarID)
//                .putExtra(Events._ID, finalEvent.eventId);
//        activity.startActivity(intent);

        int rowsDeleted = activity.getContentResolver().delete(uri, null, null);
        //Toast.makeText(activity.getApplicationContext(), "removing this many rows: "+String.valueOf(rowsDeleted), Toast.LENGTH_SHORT).show();
        //Toast.makeText(activity.getApplicationContext(), "removing: "+finalEvent.toString(), Toast.LENGTH_SHORT).show();

    }



    public static PhotoCalEvent getRealInfo(long eventId, Activity activity) {
        Log.e("f","eventid="+String.valueOf(eventId));
        ArrayList<PhotoCalEvent> retList = new ArrayList<PhotoCalEvent>();

        Uri content = Uri.parse("content://com.android.calendar/events");
        String[] vec = new String[] { "title", "dtstart", "dtend", "eventLocation",  "description", "_id", "calendar_id" };

        //question marks correspond to the in-order values of selectionsArgs
        //String selectionClause = "(dtstart >= ? AND dtend <= ?) OR (dtstart >= ? AND allDay = ?)";
        //String[] selectionsArgs = new String[]{"dtstart", "dtend", "dtstart", "1"};
        String selectionClause = "_id = ?";
        String[] selectionsArgs = new String[] { String.valueOf(eventId) };

        Cursor cursor = activity.getApplicationContext().getContentResolver().query(
                        Uri.parse("content://com.android.calendar/events"),
                        vec,
                        selectionClause,
                        selectionsArgs,
                        null);
        cursor.moveToFirst();



        for (int i = 0; i < cursor.getCount(); i++) {

//            Log.e("f", cursor.getString(0)+ " "+
//                    cursor.getString(1)+ " "+
//                    cursor.getString(2)+ " "+
//                    cursor.getString(3)+ " "+
//                    cursor.getString(4)+ " "+
//                    cursor.getString(5));

//            //cursor.getColumnCount()
            PhotoCalEvent tempEvent = new PhotoCalEvent(
                    cursor.getString(0),
                    new Date(Long.parseLong(cursor.getString(1))),
                    new Date(Long.parseLong(cursor.getString(2))),
                    cursor.getString(3),
                    cursor.getString(4),
                    Long.parseLong(cursor.getString(5)),
                    Long.parseLong(cursor.getString(6))
            );

            Log.e("f", "this matches event_id: " + tempEvent.toString());

            retList.add(tempEvent);
            cursor.moveToNext();


        }
        cursor.close();

        if(retList.size() > 1){
            Log.e("f", "more than one event with the same ID, returning first in list");
            Log.e("f", "got real info: "+retList.get(0).toString());
            return retList.get(0);
        }
        else if(retList.size() == 0){
            Log.e("f", "zero events with the same ID");
            return null;
        }
        else{
            return retList.get(0);
        }

    }


}

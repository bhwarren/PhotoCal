package me.bowarren.photocal;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bhwarren on 10/5/15.
 */
public class CalendarHelper {

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
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        activity.startActivity(intent);

        EventHolder eh = new EventHolder(activity.getApplicationContext());
        eh.addEvent(event);

    }

    public static void openCalendarEvent(PhotoCalEvent event, Activity activity){
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.eventId);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(uri);
        activity.startActivity(intent);
    }

    public static void removeFromCalendar(PhotoCalEvent event, Activity activity){
        return;
    }


}

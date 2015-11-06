package me.bowarren.photocal;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bhwarren on 10/5/15.
 */
public class CalendarHelper {
    private static String POST_PIC_URL = "http://www.bowarren.me/photocal";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static Long lastEventIdAdded;
    public static int lastIndexSelected;

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



    public static void addToList(PhotoCalEvent event, final Activity activity){
        new EventHolder(activity).addEvent(event);
    }


    //send the photo to the server and get the stuff back

//    public static void uploadAndAddToCal(File picture, final Activity activity){
//
//        //send photo to server
//        RequestParams params = new RequestParams();
//        try{
//            params.put("filename", picture.getAbsoluteFile());
//            params.put("Content-Disposition", "form-data");
//            params.put("name", "upload");
//            params.put("Content-Type", "image/jpeg");
//        }
//        catch (FileNotFoundException e){
//            Log.e("f","can't find file to upload");
//            return;
//        }
//        client.post(POST_PIC_URL, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // Called if the response is JSONObject
//                Log.e("F", response.toString());
//
//                String eventname = "";
//                GregorianCalendar begin = new GregorianCalendar();
//                GregorianCalendar end = new GregorianCalendar();
//                String location = "";
//                String description = "";
//
//
//                try {
//                    Log.e("f", new Date(response.getLong("begin")).toString());
//                }catch (JSONException e){
//
//                }
//
//                //try getting all of the individual recognized things from the json response
//                try {eventname = response.getString("eventname");}      catch (JSONException e){ Log.e("f",e.toString()); }
//                try {begin.setTimeInMillis(response.getLong("begin"));} catch (JSONException e){Log.e("f",e.toString());}
//                try {end.setTimeInMillis(response.getLong("end"));}     catch(JSONException e){Log.e("f",e.toString());}
//                try {location = response.getString("location");}        catch(JSONException e){Log.e("f",e.toString());}
//                try {description = response.getString("description");}  catch(JSONException e){Log.e("f",e.toString());}
//
//
//                PhotoCalEvent event = new PhotoCalEvent(
//                        eventname,
//                        begin,
//                        end,
//                        location,
//                        description,
//                        activity
//                );
//                addToCalendar(event, activity);
//            }
//
//        });
//
//    }



    //add event to the native calendar
    public static void addToCalendar(PhotoCalEvent event, Activity activity){
        Long id = event.getNewId(activity.getContentResolver());

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                //.putExtra(Events.TITLE, event.eventName)
                .putExtra(Events.DESCRIPTION, event.description)
                .putExtra(Events.EVENT_LOCATION, event.location)
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                .putExtra(Events._ID, id);
        //if no begin time, set it as now
        if(event.begin != null)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.begin.getTimeInMillis());
        else
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, new Date().getTime());

        if(event.end != null)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end.getTimeInMillis());

        activity.startActivityForResult(intent, 0);
        //update our event based on what was added
        lastEventIdAdded = id;
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

            Calendar begin = Calendar.getInstance();
            begin.setTimeInMillis(Long.parseLong(cursor.getString(1)));
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(Long.parseLong(cursor.getString(2)));

            PhotoCalEvent tempEvent = new PhotoCalEvent(
                    cursor.getString(0),
                    begin,
                    end,
                    cursor.getString(3),
                    cursor.getString(4),
                    null, //we'll add the picture later
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

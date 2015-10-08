package me.bowarren.photocal;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bhwarren on 10/6/15.
 */
public class EventHolder {
    private File storage;
    public static ArrayList<HashMap> savedEvents;


    public EventHolder(Context context){
        storage = new File(context.getFilesDir(), "eventsStorage");
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

    public boolean addEvent(PhotoCalEvent event){
        if(savedEvents.add(event.toDict())) {
            Log.e("f", "writing to savedEvents");
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

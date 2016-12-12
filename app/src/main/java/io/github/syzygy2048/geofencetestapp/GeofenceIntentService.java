package io.github.syzygy2048.geofencetestapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by Syzygy on 09.12.16.
 */
public class GeofenceIntentService extends IntentService {

    public GeofenceIntentService() {
        super("GeofenceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        String geofencingTransition = "";
        switch (geofencingEvent.getGeofenceTransition()){
            case Geofence.GEOFENCE_TRANSITION_ENTER: geofencingTransition = "enter";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT: geofencingTransition = "exit";
                break;
            default:
                geofencingTransition = "other";
        }

        final String finaleGeofenceTransition = geofencingTransition;
        final List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        if(triggeringGeofences == null || triggeringGeofences.size() <= 0) {
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GeofenceIntentService.this, "triggering geofences is null wtf", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            for (final Geofence triggeringGeofence : triggeringGeofences) {
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GeofenceIntentService.this, triggeringGeofences.size() + " geofences triggered! " + triggeringGeofence.getRequestId() + " " + finaleGeofenceTransition, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


    }

}

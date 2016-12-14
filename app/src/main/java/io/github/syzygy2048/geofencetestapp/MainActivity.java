package io.github.syzygy2048.geofencetestapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private GoogleApiClient googleApiClient;


    private PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        geofencePendingIntent = null;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Toast.makeText(MainActivity.this, "google api client connected", Toast.LENGTH_SHORT).show();
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                return;
                            } else {
                                getLocation();
                            }

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }

        googleApiClient.connect();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    Location lastKnownLocation;
    Runnable r;


    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           getLocation();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void getLocation(){
        r = new Runnable() {
            @Override
            public void run() {
                lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);     //sometimes doesn't work the first time
                if (lastKnownLocation == null) {
                    Toast.makeText(MainActivity.this, "location null, try again in 5 seconds", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(r, 5000);
                } else {
                    Toast.makeText(MainActivity.this, "location at - lat: " + lastKnownLocation.getLatitude() + ", long" + lastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().getSimpleName(), " lat: " + lastKnownLocation.getLatitude() + ", long" + lastKnownLocation.getLongitude());
                    setGeofenceList(lastKnownLocation);
                }
            }
        };

        new Handler().postDelayed(r, 5000);
    }

    @SuppressWarnings({"MissingPermission"})
    private void setGeofenceList(Location location) {
        geofenceList.add(new Geofence.Builder()
                .setRequestId("near_range")

                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        25
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        geofenceList.add(new Geofence.Builder()
                .setRequestId("near_inverted")

                .setCircularRegion(
                        location.getLongitude(),
                        location.getLatitude(),
                        25
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        geofenceList.add(new Geofence.Builder()
                .setRequestId("middle_range")

                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        50
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        geofenceList.add(new Geofence.Builder()
                .setRequestId("far_range")

                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        75
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

/*
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntentForBroadcast()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(MainActivity.this, "geofences broadcast set - " + status, Toast.LENGTH_SHORT).show();

                //startService(new Intent(MainActivity.this, GeofenceIntentService.class));
            }
        }); */

        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntentForService()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(MainActivity.this, "geofences service set - " + status, Toast.LENGTH_SHORT).show();

                startService(new Intent(MainActivity.this, GeofenceIntentService.class));
            }
        });


    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntentForService() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;       //this never gets set, this is exactly like in the sample code from google https://github.com/googlesamples/android-play-location/blob/master/Geofencing/app/src/main/java/com/google/android/gms/location/sample/geofencing/MainActivity.java#L217
        }
        Intent intent = new Intent(this, GeofenceIntentService.class);
        intent.setAction("io.github.syzygy2048.geofencetestapp.ACTION_RECEIVE_GEOFENCE");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getGeofencePendingIntentForBroadcast() {
     /*   // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        } */
        Intent intent = new Intent(this, GeofenceIntentService.class);
        intent.setAction("io.github.syzygy2048.geofencetestapp.ACTION_RECEIVE_GEOFENCE");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

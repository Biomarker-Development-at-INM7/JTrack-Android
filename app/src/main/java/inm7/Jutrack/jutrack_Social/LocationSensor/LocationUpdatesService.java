
/**
 Copyright © 2020 JuTrack Mobile Framework, JuTrack Platform, JuTrack Social, JuTrack Move, JuTrack EMA

 Licensed under the Apache License, Version 2.0 (the “License”);
 you may not use this file except in compliance with the License.
 you may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an “AS IS” BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License.

 **/
package inm7.Jutrack.jutrack_Social.LocationSensor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.MainActivity;
import inm7.Jutrack.jutrack_Social.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static java.lang.Math.PI;


public class LocationUpdatesService extends Service {


    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = Constants.TEN_MINUTES;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;




    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;
    private Notification mNotification;
    private Notification mNotification2;

    //**
    MyXYZLocation myXYZLocation = new MyXYZLocation();
    LocationSensor myGPSlocation = new LocationSensor();
    public SharedPreferences sharedPref;
    public String username = "";
    public String deviceid = "";
    public String studyId = "";


    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Android O requires a Notification Channel.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startMyOwnForegroundOld();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");


        if(intent != null && intent.getAction() != null)
        {

            if (intent.getAction().equals("STOP")) {
                //your end servce code
                removeLocationUpdates();
                stopForeground(true);
                stopSelf();
            } else
            {
                Toast.makeText(this, getString(R.string.Location_rservice_started), Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startMyOwnForeground();
                } else {
                    startMyOwnForegroundOld();
                }
                requestLocationUpdates();

            }


        } else
        {

            Toast.makeText(this, getString(R.string.Location_rservice_started), Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            } else {
                startMyOwnForegroundOld();
            }
            requestLocationUpdates();


        }


        // get device id if null use username

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");
         deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
         studyId = sharedPref.getString(Constants.studyId, studyId);



        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground_location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
            } else {
                startMyOwnForegroundOld_Location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
            }

        }






        // Tells the system to not try to recreate the service after it has been killed.
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {



        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
       // startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);

        }






    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }



    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                if (mLocation==null)
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startMyOwnForeground_location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
                                    } else {
                                        startMyOwnForegroundOld_Location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
                                    }
                                }
                            } else {
                                Log.w(TAG, "Failed to get location.");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startMyOwnForeground_location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
                                } else {
                                    startMyOwnForegroundOld_Location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
                                }
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground_location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
            } else {
                startMyOwnForegroundOld_Location(getString(R.string.Location_request_head),getString (R.string.Location_request_body));
            }

        }

    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);


        mLocation = location;


        myXYZLocation = GeodeticToEcef(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAltitude());
        // should be negative since left handed used
        //transform to the first assigned value. default is 360 if not assigned so the location will not be transformed.

        myXYZLocation = transformation(myXYZLocation, DegreesToRadians(-1 * (sharedPref.getInt(Constants.transformationid, 360))));

        myGPSlocation = EcefToGeodetic(myXYZLocation.getX(), myXYZLocation.getY(), myXYZLocation.getZ());


        // save to database
        savetodb(mLocation.getProvider(),mLocation.getAccuracy(), myGPSlocation);


        // Notify anyone listening for broadcasts about the new location.
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }




    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }





    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }




    private void startMyOwnForegroundOld() {
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle(Constants.NOTIFICATION_Title)
                .setTicker(Constants.NOTIFICATION_Tiker)
                .setContentText(getText(R.string.mainServiceNotificationText))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                .setGroup(Constants.NOTIFICATION_Title)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSortKey("1")
                .build();

        startForeground(Constants.mainNotificationId, mNotification);

    }

    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.channelName, NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    // this is vita to show notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getText(R.string.mainServiceNotificationText))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                    .setOnlyAlertOnce(true)
                    .build();
            startForeground(Constants.mainNotificationId, notification);
        }
    }


    // my costum functions to hide actual location information :
    // WGS-84 geodetic constants
    public double a = 6378137.0;         // WGS-84 Earth semimajor axis (m)

    public double b = 6356752.314245;     // Derived Earth semiminor axis (m)
    public double f = (a - b) / a;           // Ellipsoid Flatness
    public double f_inv = 1.0 / f;       // Inverse flattening

    //const double f_inv = 298.257223563; // WGS-84 Flattening Factor of the Earth
    //const double b = a - a / f_inv;
    //const double f = 1.0 / f_inv;

    public double a_sq = a * a;
    public double b_sq = b * b;
    public double e_sq = f * (2 - f);    // Square of Eccentricity

    private class MyXYZLocation {

        private double x;
        private double y;
        private double z;

        //getter
        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        //setter

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }

    public MyXYZLocation GeodeticToEcef(double lat, double lon, double h) {

        // Convert to radians in notation consistent with the paper:
        double lambda = DegreesToRadians(lat);
        double phi = DegreesToRadians(lon);
        double s = Math.sin(lambda);
        double N = a / Math.sqrt(1 - e_sq * s * s);

        double sin_lambda = Math.sin(lambda);
        double cos_lambda = Math.cos(lambda);
        double cos_phi = Math.cos((phi));
        double sin_phi = Math.sin(phi);

        double x = (h + N) * cos_lambda * cos_phi;
        double y = (h + N) * cos_lambda * sin_phi;
        double z = (h + (1 - e_sq) * N) * sin_lambda;

        MyXYZLocation myXYZLocation = new MyXYZLocation();
        myXYZLocation.setX(x);
        myXYZLocation.setY(y);
        myXYZLocation.setZ(z);


        return myXYZLocation;


    }

    /**
     * change from X Y Z to GPS location
     *
     * @param x
     * @param y
     * @param z
     * @return
     */

    public LocationSensor EcefToGeodetic(double x, double y, double z) {
        double eps = e_sq / (1.0 - e_sq);
        double p = Math.sqrt(x * x + y * y);
        double q = Math.atan2((z * a), (p * b));
        double sin_q = Math.sin(q);
        double cos_q = Math.cos(q);
        double sin_q_3 = sin_q * sin_q * sin_q;
        double cos_q_3 = cos_q * cos_q * cos_q;
        double phi = Math.atan2((z + eps * b * sin_q_3), (p - e_sq * a * cos_q_3));
        double lambda = Math.atan2(y, x);
        double v = a / Math.sqrt(1.0 - e_sq * Math.sin(phi) * Math.sin(phi));

        double h = (p / Math.cos(phi)) - v;
        double lat = RadiansToDegrees(phi);
        double lon = RadiansToDegrees(lambda);


        LocationSensor myGPSlocation = new LocationSensor();
        myGPSlocation.setAlt(h);
        myGPSlocation.setLat(lat);
        myGPSlocation.setLon(lon);

        return myGPSlocation;


    }

    // rotate along X axis in 3D space. this translation will be only on local client.
    private MyXYZLocation transformation(MyXYZLocation myXYZLocation, double theta) {


        MyXYZLocation newmyXYZLocation = new MyXYZLocation();

        newmyXYZLocation.setX((myXYZLocation.getX() * (Math.round(Math.cos(theta) * 100.0) / 100.0) ) - (myXYZLocation.getY() *  (Math.round(Math.sin(theta) * 100.0) / 100.0)) );
        newmyXYZLocation.setY((myXYZLocation.getX() * (Math.round(Math.sin(theta) * 100.0) / 100.0)  ) + (myXYZLocation.getY() * (Math.round(Math.cos(theta) * 100.0) / 100.0)) );
        newmyXYZLocation.setZ(myXYZLocation.getZ());


        return newmyXYZLocation;
    }

    /**
     * @param degrees
     * @return
     */

    static double DegreesToRadians(double degrees) {
        return PI / 180.0 * degrees;
    }

    static double RadiansToDegrees(double radians) {
        return 180.0 / PI * radians;
    }

    private void savetodb(String provider,double accuracy ,LocationSensor myGPSlocation) {

        LocationSensor sensor = new LocationSensor();
        sensor.setSensorname("location");
        sensor.setTimestamp(System.currentTimeMillis());
        sensor.setDeviceid(deviceid);
        sensor.setUsername(username);
        sensor.setStudyId(studyId);
        sensor.setProvider(provider);
        sensor.setAlt(myGPSlocation.getAlt());
        sensor.setLat(myGPSlocation.getLat());
        sensor.setLon(myGPSlocation.getLon());
        sensor.setAccuracy(accuracy);

        try {
            GetLocationSensorsDataAsyc.populateSensors(LocationSenorsAppDatabase.getAppDatabase(this), sensor);
        } catch (Exception e) {
            throw new NullPointerException();
        }


    }





    /**
     * notification functions
     */
    private void startMyOwnForegroundOld_Location(String title, String body) {
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setTicker(Constants.NOTIFICATION_Tiker)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), PendingIntent.FLAG_NO_CREATE))
                .setGroup(Constants.NOTIFICATION_Title)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOngoing(false)
                .build();

        mNotificationManager.notify(Constants.LocationRequestNotificationId,mNotification);


    }

    private void startMyOwnForeground_location(String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(false)
                    // this is vita to show notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), PendingIntent.FLAG_NO_CREATE))
                    .setAutoCancel(true)
                    .build();
            manager.notify(Constants.LocationRequestNotificationId, notification);


        }
    }

}

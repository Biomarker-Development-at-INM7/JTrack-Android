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
// . for older devices without play sevice, not used in app.
package inm7.Jutrack.jutrack_Social.LocationSensor;

import android.annotation.SuppressLint;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.MainActivity;
import inm7.Jutrack.jutrack_Social.R;


import static java.lang.Math.PI;
public class LocationService extends Service implements LocationListener {

    // private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    double locationAccuracy;// locationAccuracy


    //
    LocationSensor myGPSlocation = new LocationSensor();
    MyXYZLocation myXYZLocation = new MyXYZLocation();


    // Declaring a Location Manager
    protected LocationManager locationManager;
    public Location lastKnownlocation;

    private Notification mNotification;


    public SharedPreferences transformationValue;

    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";


    // restrat automatically.
    @Override
    public void onDestroy() {

        Toast.makeText(this,getString(R.string.Location_rservice_stopepd) , Toast.LENGTH_SHORT).show();
        stopUsingGPS();

        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this,getString(R.string.Location_rservice_started) , Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startMyOwnForegroundOld();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().equals( "STOP")) {
            //your end servce code
            stopForeground(true);
            stopSelf();
        } else
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            } else {
                startMyOwnForegroundOld();
            }
        }


        //**
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // get device id if null use username
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);


//        try {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                Intent i = new Intent(this, MainActivity.class);
//                startActivity(i);
//            }
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                Intent i = new Intent(this, MainActivity.class);
//                startActivity(i);
//            }
//
//            // If any permission above not allowed by user, this condition will
//            // execute every time, else your else part will work
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }





        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                // no network provider is enabled

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);

                //  showSettingsAlert();
                /**
                 *
                 * do error handling here may need to have location in any case
                 */
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Constants.MIN_TIME_BW_UPDATES,
                            Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        lastKnownlocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Constants.MIN_TIME_BW_UPDATES,
                                Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            lastKnownlocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);


                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;

        //return super.onStartCommand(intent, flags, startId);


    }


    private void startMyOwnForegroundOld() {
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle(Constants.NOTIFICATION_Title)
                .setTicker(Constants.NOTIFICATION_Tiker)
                .setContentText(getString(R.string.BackgroundNotificationText))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                .setGroup(Constants.NOTIFICATION_Title)
                .setGroupSummary(true)

                .setOngoing(true).build();


       startForeground(Constants.activityNotificationId, mNotification);

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
                    .setContentTitle(getString(R.string.BackgroundNotificationText))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                    .setOnlyAlertOnce(true)
                    .setOnlyAlertOnce(true)

                    .build();
            startForeground(Constants.activityNotificationId, notification);
        }
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Function to get latitude
     */

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to get location Accuracy
     */
    public double getlocationAccuracy() {
        if (location != null) {
            locationAccuracy = location.getAccuracy();
        }

        // return longitude
        return locationAccuracy;
    }


    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */


    // startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
    @Override
    public void onLocationChanged(Location location) {

        if (isBetterLocation(location, lastKnownlocation)) {
            location = location;

        } else {
            Log.d("location ", "Lastknown : " + "lat : "
                    + location.getLatitude() + "long: " + location.getLongitude() + "Alt: " + location.getAltitude() + "Acc " + location.getAccuracy());
            location = lastKnownlocation;
        }


        Log.d("location ", "onLocationChanged: " + "lat : "
                + location.getLatitude() + "long: " + location.getLongitude() + "Alt: " + location.getAltitude() + "Acc " + location.getAccuracy());


        myXYZLocation = GeodeticToEcef(location.getLatitude(), location.getLongitude(), location.getAltitude());
        // should be negative since left handed used
        //transform to the first assigned value. default is 360 if not assigned so the location will not be transformed.
        transformationValue = PreferenceManager.getDefaultSharedPreferences(this);

        myXYZLocation = transformation(myXYZLocation, DegreesToRadians(-1 * (transformationValue.getInt(Constants.transformationid, 360))));

        myGPSlocation = EcefToGeodetic(myXYZLocation.getX(), myXYZLocation.getY(), myXYZLocation.getZ());


        // save to database
        savetodb(location.getProvider(),location.getAccuracy(), myGPSlocation);


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


    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /**
     * Constant for Location Conversion
     */
    //
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


    /**
     * change From GPS location to X Y Z location
     *
     * @param lat
     * @param lon
     * @param h
     * @return
     */
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


    /**
     *
     *     GPS location Object
     */

   /* public class MyGPSlocation{
        private double longitude;
        private double latitude;
        private double altitude;

        // getter
        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getaltitude() {
            return altitude;
        }

        //setter

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setaltitude(double altitude) {
            this.altitude= altitude;
        }
    }
*/

    /**
     * X Y Z location Object
     */
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


    /**
     * transformation
     *
     * @param myXYZLocation
     * @param theta
     * @return
     */
// rotate along X axis in 3D space. this translation will be only on local client.
    private MyXYZLocation transformation(MyXYZLocation myXYZLocation, double theta) {


        MyXYZLocation newmyXYZLocation = new MyXYZLocation();

        newmyXYZLocation.setX((myXYZLocation.getX() * Math.round(Math.cos(theta))) - (myXYZLocation.getY() * Math.round(Math.sin(theta))));
        newmyXYZLocation.setY((myXYZLocation.getX() * Math.round(Math.sin(theta))) + (myXYZLocation.getY() * Math.round(Math.cos(theta))));
        newmyXYZLocation.setZ(myXYZLocation.getZ());

        return newmyXYZLocation;

    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > Constants.TEN_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -Constants.TEN_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


} // end of function
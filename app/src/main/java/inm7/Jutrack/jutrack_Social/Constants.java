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
package inm7.Jutrack.jutrack_Social;


public final class Constants {


    /**
     * SERVER INFORMATION: ADD your server info here
     * change according to your server and package info
     */

    public final static String serveraddress = "https://jutrack.inm7.de";
    public final static String access_token = "access_token";
    public final static Integer adminPassword = 1234;


    public final static String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public final static String pushNotification_token = "pushNotification_token";
    public final static String UserName_text = "UserName_text";
    public final static String UserName_Password = "UserName_text";
    public final static String studyId = "studyId";
    public final static String isFirstLogin = "firstLogin";

    /**
     * ACRTION types
     */

    public final static String write_data="write_data";
    public final static String add_user="add_user";
    public final static String update_user="update_user";





    /**
     * sync process for sensors
     */
    public final static String Accelerationlastsync = "Accelerationlastsync";
    public final static String Accelerationsynchid = "Accelerationsynchid";

    public final static String Barometerlastsync = "Barometerlastsync";
    public final static String Barometersynchid = "Barometersynchid";


    public final static String Gravitylastsync = "Gravitylastsync";
    public final static String Gravitysynchid = "Gravitysynchid";

    public final static String Gyroscopelastsync = "Gyroscopelastsync";
    public final static String Gyroscopesynchid = "Gyroscopesynchid";

    public final static String LinearAccelerationlastsync = "LinearAccelerationlastsync";
    public final static String LinearAccelerationsynchid = "LinearAccelerationsynchid";


    public final static String RotationVectorlastsync = "RotationVectorlastsync";
    public final static String RotationVectorsynchid = "RotationVectorsynchid";

    public final static String Mageticlastsync = "Mageticlastsync";
    public final static String Magneticsynchid = "Magneticsynchid";

    /**
     * sync process for services
     */

    // location sync
    public final static String locationLastSync = "locationLastSync";
    public final static String locationSyncid = "locationSyncid";

    // activity sync
    public final static String detectedActivityLastSync = "detectedActivityLastSync";
    public final static String detectedActivitySyncid = "detectedActivitySyncid";

    //App usage Stat sync
    public final static String appUsageStatLastSync = "appUsageStatLastSync";
    public final static String appUsageStatSyncid = "appUsageStatSyncid";

    //Acceleration
    public final static String AccelerationLastSync = "AccelerationLastSync";

    //Gyroscope
    public final static String GyroscopeLastSync = "GyroscopeLastSync";

    //Active labeling
    public final static String activeLabelingLastSync = "activeLabelingLastSync";



    /**
     * transformation
     */

    public final static String transformationValue = "transformationValue";
    public final static String transformationid = "transformationid";


    /**
     * Paching
     */


    public final static Integer patchSize = 10*(60*100); // 10 min of data in 100 Hz.
    public final static Integer patchSizeSmall = 100;
    public final static Integer patchSizeXXSmall = 5;


    public final static Integer loopMaxSizePatchSize = 100;


    /**
     * job schedual
     */
    //Sync
    public final static Integer jobPeriod = 15 * 60 * 1000;
    public final static Integer AccelerationSyncJobid = 1001;
    public final static Integer BarometerSyncJobid = 1002;
    public final static Integer GravitySyncJobid = 1003;
    public final static Integer GyroscopeSyncJobid = 1004;
    public final static Integer LinearAccelerationSyncJobid = 1005;
    public final static Integer MagneticSyncJobid = 1006;
    public final static Integer RotationVectorSyncJobid = 1007;

    public final static Integer LocationSyncJobid = 1020;
    public final static Integer DetectedActivitySyncJobid = 1021;
    public final static Integer AppUsageStatSyncJobid = 1022;

    public final static String LocationSyncJobName = "LocationSyncJob";
    public final static String DetectedActivitySyncJobName = "DetectedActivitySyncJob";
    public final static String AppUsageStatSyncJobName = "AppUsageStatSyncJob";
    public final static String MainServiceJobName = "MainServiceJobName";
    public final static String AccelerationJobName = "AccelerationJob";
    public final static String GyroscopeJobName = "GyroscopeJob";
    public final static String ActiveLabelingJobName = "ActiveLabelingJob";


    // AppUsageStat
    public final static Integer AppUsageStatjobPeriod =  60 * 60 * 1000;
    public final static Integer MainWorkerjobPeriod =  120 * 60 * 1000;
    public final static Integer AccelerationJobPeriod =  15 * 60 * 1000;
    public final static Integer GyroscopeJobPeriod =  15 * 60 * 1000;
    public final static Integer ActiveLabelingJobPeriod =  15 * 60 * 1000;

    public final static Integer AppUsageStatJobid = 2002;
    public final static String AppUsageStatJobName = "AppUsageStatJob";



    /**
     * premissions
     */
    public static final int REQUEST_CODE_PERMISSION_FINE = 2001;
    public static final int REQUEST_CODE_PERMISSION_COARSE = 2002;


    /**
     * sensors name and data tables
     */
    public final static String AccelerationSensor_td = "AccelerationSensor_td";
    public final static String BarometerSensor_td = "BarometerSensor_td";
    public final static String GravitySensor_td = "GravitySensor_td";
    public final static String GyroscopeSensor_td = "GyroscopeSensor_td";
    public final static String LinearAccelerationSensor_td = "LinearAccelerationSensor_td";
    public final static String MagneticSensor_td = "MagneticSensor_td";
    public final static String RotationVectorSensor_td = "RotationVectorSensor_td";

    public final static String LocationSensor_td = "LocationSensor_td";

    public final static String AppUsageStatSensor_td = "AppUsageStatSensor_td";
    public final static String DetectedActivitySensor_td = "DetectedActivitySensor_td";
    public final static String ActiveLabelingSensor_td = "ActiveLabelingSensor_td";

    /**
     * Activity detection
     */
    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final int CONFIDENCE = 70;

    /**
     * location servise
     */
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 5; // 5 minute

    public static final int ONE_MINUTES = 1000 * 60 * 1;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final int FIVE_MINUTES = 1000 * 60 * 5;

    public static final int TEN_MINUTES = 1000 * 60 * 10;
    public static final int TWENTY_MINUTES = 1000 * 60 * 20;
    public static final int THIRTY_MINUTES = 1000 * 60 * 30;
    public static final int FORTY_MINUTES = 1000 * 60 * 40;
    public static final int FIFTY_MINUTES = 1000 * 60 * 50;
    public static final int SIXTY_MINUTES = 1000 * 60 * 60;
    public static final int TWO_Hours = 1000 * 60 * 120;
    public static final int One_Hours = 1000 * 60 * 60;
    public static final int One_Day = 1000 * 60 * 60 * 24;
    public static final int Three_Day = 1000 * 60 * 60 * 36;




    /**
     * Notification
     */
    public static final int mainNotificationId = 1;
    public static final int sensorNotificationId = 2;
    public static final int activityNotificationId = 3;
    public static final int locationNotificationId = 4;
    public static final int pushNotificationId = 5;
    public static final int LocationRequestNotificationId = 6;


    public static String NOTIFICATION_CHANNEL_ID = "INM7.Jutrack.jutrack_Social";
    public static String channelName = "My Background Service";


    //old method
    public static String NOTIFICATION_Title = "JuTrack Social";
    public static String NOTIFICATION_Tiker = "tiker";

    public static long versionLimit= 1607460731;

    /**
     * main service
     */
    public static final double motionThreshold = 0.2;
}

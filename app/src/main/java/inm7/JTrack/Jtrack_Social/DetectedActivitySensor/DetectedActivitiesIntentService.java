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
package inm7.JTrack.Jtrack_Social.DetectedActivitySensor;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import inm7.JTrack.Jtrack_Social.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


public class DetectedActivitiesIntentService extends IntentService {
    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";


    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {


        //**
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // get device id if null use username
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);


        super.onCreate();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            //check if meets accuracy threshold

            if (activity.getConfidence() >= Constants.CONFIDENCE) {
                Log.i(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());

                broadcastActivity(activity);
            } else {
                return;
            }

        }
    }


    private void savetodb(DetectedActivity activity) {
        DetectedActivitySensor sensor = new DetectedActivitySensor();


        sensor.setSensorname("activity");
        sensor.setTimestamp(System.currentTimeMillis());
        sensor.setDeviceid(deviceid);
        sensor.setUsername(username);
        sensor.setStudyId(studyId);
        sensor.setActivityType(activity.getType());
        sensor.setConfidence(activity.getConfidence());

        try {
            GetDetectedActivitySensorsDataAsyc.populateSensors(DetectedActivitySenorsAppDatabase.getAppDatabase(this), sensor);
        } catch (Exception e) {
            throw new NullPointerException();

        }


    }


    /**
     * in case needed not used
     *
     * @param activity
     */
    private void broadcastActivity(DetectedActivity activity) {

        savetodb(activity);

        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}

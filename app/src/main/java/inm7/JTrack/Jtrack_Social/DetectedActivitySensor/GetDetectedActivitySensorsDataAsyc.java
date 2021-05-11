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

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetDetectedActivitySensorsDataAsyc {

    private static final String TAG = GetDetectedActivitySensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final DetectedActivitySenorsAppDatabase db, DetectedActivitySensor sensor) {
       PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor);
        task.execute();

       // db.detectedActivitySensorsDao().insertAll(sensor);
        Log.d(TAG, "Detected Activity Rows Count: " + db.detectedActivitySensorsDao().countUsers()+" "+ sensor.getTimestamp());


    }

    public static List<DetectedActivitySensor> getSensors(@NonNull final DetectedActivitySenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<DetectedActivitySensor> sensorsList = null;

        try {
            sensorsList = getSensorsAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sensorsList;
    }

    // get sensors count
    public static Integer getSensorsCount (@NonNull final DetectedActivitySenorsAppDatabase db){

        getSensorCountAsync getSensorCountAsync = new getSensorCountAsync(db);
        Integer sensorsCount = null;

        try {
            sensorsCount = getSensorCountAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sensorsCount;
    }


     //  read all data from database using room for SYNC WITH SERVER
    public static List<DetectedActivitySensor> syncSensorwithServer(@NonNull final DetectedActivitySenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<DetectedActivitySensor> sensorsList = null;

        try {
            sensorsList = getSensorsforServerAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sensorsList;
    }

    //  read sensorID for syc from database using room for SYNC WITH SERVER
    public static Integer getIdtoSync(@NonNull final DetectedActivitySenorsAppDatabase db) {
        getIdforSyncAsync getIdforSyncAsync = new getIdforSyncAsync(db);
        Integer IdToSyc = null;

        try {
            IdToSyc = getIdforSyncAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return IdToSyc;
    }

    //delete data from database using room for SYNC WITH SERVER
    public static boolean DeleteSensorFromLocal(@NonNull final DetectedActivitySenorsAppDatabase db, Integer startindex, Integer endindex) {
        deleteSensorsfromLocalAsync deleteSensorsfromLocalAsync = new deleteSensorsfromLocalAsync(db, startindex, endindex);

        try {
            deleteSensorsfromLocalAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }


    //=====================INTER CLASS FUNCTIONS========================================================
    // read all data from database using room for SYNC WITH SERVER
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<DetectedActivitySensor>> {
        private final DetectedActivitySenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(DetectedActivitySenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<DetectedActivitySensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.detectedActivitySensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<DetectedActivitySensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final DetectedActivitySenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(DetectedActivitySenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.detectedActivitySensorsDao().deleteAllBetween(mstartindex, mendindex);
            }
            catch(Exception e)
            {
                throw new NullPointerException();
            } finally {
                return null;

            }
        }


    }



    // read all sensors Count from database using room
    private static class getSensorCountAsync extends  AsyncTask<Void,Void, Integer> {
        private final DetectedActivitySenorsAppDatabase mDb;

        private getSensorCountAsync(DetectedActivitySenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.detectedActivitySensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final DetectedActivitySenorsAppDatabase mDb;


        private getIdforSyncAsync(DetectedActivitySenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.detectedActivitySensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<DetectedActivitySensor>> {
        private final DetectedActivitySenorsAppDatabase mDb;


        public getSensorsAsync(DetectedActivitySenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<DetectedActivitySensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.detectedActivitySensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<DetectedActivitySensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final DetectedActivitySenorsAppDatabase mDb;
        private final DetectedActivitySensor msensor;

        PopulateSensorsAsync(DetectedActivitySenorsAppDatabase db, DetectedActivitySensor sensor) {
            mDb = db;
            this.msensor = sensor;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDb.detectedActivitySensorsDao().insertAll(msensor);
          //  Log.d(TAG, "Acceleration Rows Count: " + mDb.DetectedActivitySensor().countUsers()+msensor.getTimestamp());
            return null;
        }

    }


}
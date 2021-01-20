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
package inm7.Jutrack.jutrack_Social.ActiveMonitoring;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.DetectedActivitySenorsAppDatabase;
import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.DetectedActivitySensor;

public class GetActiveLabelingSensorsDataAsyc {

    private static final String TAG = GetActiveLabelingSensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final ActiveLabelingSenorsAppDatabase db, ActiveLabelingSensor sensor) {
       PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor);
        task.execute();

       // db.detectedActivitySensorsDao().insertAll(sensor);
        Log.d(TAG, " Active Labeling Count: " + db.activeLabelingSensorsDao().countUsers()+" "+ sensor.getTimestamp());


    }

    public static List<ActiveLabelingSensor> getSensors(@NonNull final ActiveLabelingSenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<ActiveLabelingSensor> sensorsList = null;

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
    public static Integer getSensorsCount (@NonNull final ActiveLabelingSenorsAppDatabase db){

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
    public static List<ActiveLabelingSensor> syncSensorwithServer(@NonNull final ActiveLabelingSenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<ActiveLabelingSensor> sensorsList = null;

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
    public static Integer getIdtoSync(@NonNull final ActiveLabelingSenorsAppDatabase db) {
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
    public static boolean DeleteSensorFromLocal(@NonNull final ActiveLabelingSenorsAppDatabase db, Integer startindex, Integer endindex) {
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
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<ActiveLabelingSensor>> {
        private final ActiveLabelingSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(ActiveLabelingSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<ActiveLabelingSensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.activeLabelingSensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<ActiveLabelingSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final ActiveLabelingSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(ActiveLabelingSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.activeLabelingSensorsDao().deleteAllBetween(mstartindex, mendindex);
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
        private final ActiveLabelingSenorsAppDatabase mDb;

        private getSensorCountAsync(ActiveLabelingSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.activeLabelingSensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final ActiveLabelingSenorsAppDatabase mDb;


        private getIdforSyncAsync(ActiveLabelingSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.activeLabelingSensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<ActiveLabelingSensor>> {
        private final ActiveLabelingSenorsAppDatabase mDb;


        public getSensorsAsync(ActiveLabelingSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<ActiveLabelingSensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.activeLabelingSensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<ActiveLabelingSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final ActiveLabelingSenorsAppDatabase mDb;
        private final ActiveLabelingSensor msensor;

        PopulateSensorsAsync(ActiveLabelingSenorsAppDatabase db, ActiveLabelingSensor sensor) {
            mDb = db;
            this.msensor = sensor;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDb.activeLabelingSensorsDao().insertAll(msensor);
          //  Log.d(TAG, "Acceleration Rows Count: " + mDb.DetectedActivitySensor().countUsers()+msensor.getTimestamp());
            return null;
        }

    }


}
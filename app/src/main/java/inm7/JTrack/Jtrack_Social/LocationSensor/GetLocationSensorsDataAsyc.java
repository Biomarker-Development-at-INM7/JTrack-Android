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
package inm7.JTrack.Jtrack_Social.LocationSensor;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetLocationSensorsDataAsyc {

    private static final String TAG = GetLocationSensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final LocationSenorsAppDatabase db, LocationSensor sensor) {
       PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor);
        task.execute();

      //  db.locationSensorsDao().insertAll(sensor);
        Log.d(TAG, "Location Rows Count: " + db.locationSensorsDao().countUsers()+" "+ sensor.getTimestamp());


    }

    public static List<LocationSensor> getSensors(@NonNull final LocationSenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<LocationSensor> sensorsList = null;

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
    public static Integer getSensorsCount (@NonNull final LocationSenorsAppDatabase db){

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
    public static List<LocationSensor> syncSensorwithServer(@NonNull final LocationSenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<LocationSensor> sensorsList = null;

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
    public static Integer getIdtoSync(@NonNull final LocationSenorsAppDatabase db) {
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
    public static boolean DeleteSensorFromLocal(@NonNull final LocationSenorsAppDatabase db, Integer startindex, Integer endindex) {
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
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<LocationSensor>> {
        private final LocationSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(LocationSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<LocationSensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.locationSensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<LocationSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final LocationSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(LocationSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.locationSensorsDao().deleteAllBetween(mstartindex, mendindex);
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
        private final LocationSenorsAppDatabase mDb;

        private getSensorCountAsync(LocationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.locationSensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final LocationSenorsAppDatabase mDb;


        private getIdforSyncAsync(LocationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.locationSensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<LocationSensor>> {
        private final LocationSenorsAppDatabase mDb;


        public getSensorsAsync(LocationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<LocationSensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.locationSensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<LocationSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final LocationSenorsAppDatabase mDb;
        private final LocationSensor msensor;

        PopulateSensorsAsync(LocationSenorsAppDatabase db, LocationSensor sensor) {
            mDb = db;
            this.msensor = sensor;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDb.locationSensorsDao().insertAll(msensor);
          //  Log.d(TAG, "Location Rows Count: " + mDb.locationSensorsDao().countUsers()+msensor.getTimestamp());
            return null;
        }

    }


}
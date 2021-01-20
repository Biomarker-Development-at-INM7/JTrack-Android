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
package inm7.Jutrack.jutrack_Social.AppUsageStatSensor;

import android.os.AsyncTask;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetAppUsageStatSensorsDataAsyc {

    private static final String TAG = GetAppUsageStatSensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final AppUsageStatSenorsAppDatabase db, AppUsageStatSensor sensor) {
        PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor);
        task.execute();

      //  db.appUsageStatSensorsDao().insertAll(sensor);
     //   Log.d(TAG, "Application Usage Statistic Rows Count: " + db.appUsageStatSensorsDao().countUsers()+" "+ sensor.getTimestamp());


    }

    public static List<AppUsageStatSensor> getSensors(@NonNull final AppUsageStatSenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<AppUsageStatSensor> sensorsList = null;

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
    public static Integer getSensorsCount (@NonNull final AppUsageStatSenorsAppDatabase db){

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
    //  read sensorID for syc from database using room for SYNC WITH SERVER
    public static Integer getIdtoSync(@NonNull final AppUsageStatSenorsAppDatabase db) {
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

     //  read all data from database using room for SYNC WITH SERVER
    public static List<AppUsageStatSensor> syncSensorwithServer(@NonNull final AppUsageStatSenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<AppUsageStatSensor> sensorsList = null;

        try {
            sensorsList = getSensorsforServerAsync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sensorsList;
    }



    //delete data from database using room for SYNC WITH SERVER
    public static boolean DeleteSensorFromLocal(@NonNull final AppUsageStatSenorsAppDatabase db, Integer startindex, Integer endindex) {
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
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<AppUsageStatSensor>> {
        private final AppUsageStatSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(AppUsageStatSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<AppUsageStatSensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.appUsageStatSensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<AppUsageStatSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final AppUsageStatSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(AppUsageStatSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.appUsageStatSensorsDao().deleteAllBetween(mstartindex, mendindex);
            }
            catch(Exception e)
            {
                throw new NullPointerException();
            } finally {
                return null;

            }
        }


    }
    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final AppUsageStatSenorsAppDatabase mDb;


        private getIdforSyncAsync(AppUsageStatSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.appUsageStatSensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors Count from database using room
    private static class getSensorCountAsync extends  AsyncTask<Void,Void, Integer> {
        private final AppUsageStatSenorsAppDatabase mDb;

        private getSensorCountAsync(AppUsageStatSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.appUsageStatSensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }



    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<AppUsageStatSensor>> {
        private final AppUsageStatSenorsAppDatabase mDb;


        public getSensorsAsync(AppUsageStatSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<AppUsageStatSensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.appUsageStatSensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<AppUsageStatSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final AppUsageStatSenorsAppDatabase mDb;
        private final AppUsageStatSensor msensor;

        PopulateSensorsAsync(AppUsageStatSenorsAppDatabase db, AppUsageStatSensor sensor) {
            mDb = db;
            this.msensor = sensor;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDb.appUsageStatSensorsDao().insertAll(msensor);
          //  Log.d(TAG, "Acceleration Rows Count: " + mDb.AccelerationsensorDao().countUsers()+msensor.getTimestamp());
            return null;
        }

    }


}
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
package inm7.Jutrack.jutrack_Social.GyroscopeSensor;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetGyroscopeSensorsDataAsyc {

    private static final String TAG = GetGyroscopeSensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final GyroscopeSenorsAppDatabase db, List<GyroscopeSensor> sensor) {
       PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor.get(0), sensor.get(1), sensor.get(2), sensor.get(3), sensor.get(4),
               sensor.get(5), sensor.get(6), sensor.get(7), sensor.get(8), sensor.get(9), sensor.get(10), sensor.get(11), sensor.get(12),
               sensor.get(13), sensor.get(14),
               sensor.get(15), sensor.get(16), sensor.get(17), sensor.get(18), sensor.get(19));
        task.execute();

      //  db.locationSensorsDao().insertAll(sensor);


    }

    public static List<GyroscopeSensor> getSensors(@NonNull final GyroscopeSenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<GyroscopeSensor> sensorsList = null;

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
    public static Integer getSensorsCount (@NonNull final GyroscopeSenorsAppDatabase db){

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
    public static List<GyroscopeSensor> syncSensorwithServer(@NonNull final GyroscopeSenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<GyroscopeSensor> sensorsList = null;

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
    public static Integer getIdtoSync(@NonNull final GyroscopeSenorsAppDatabase db) {
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
    public static boolean DeleteSensorFromLocal(@NonNull final GyroscopeSenorsAppDatabase db, Integer startindex, Integer endindex) {
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
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<GyroscopeSensor>> {
        private final GyroscopeSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(GyroscopeSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<GyroscopeSensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.GyroscopeSensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<GyroscopeSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final GyroscopeSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(GyroscopeSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.GyroscopeSensorsDao().deleteAllBetween(mstartindex, mendindex);
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
        private final GyroscopeSenorsAppDatabase mDb;

        private getSensorCountAsync(GyroscopeSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.GyroscopeSensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final GyroscopeSenorsAppDatabase mDb;


        private getIdforSyncAsync(GyroscopeSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.GyroscopeSensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<GyroscopeSensor>> {
        private final GyroscopeSenorsAppDatabase mDb;


        public getSensorsAsync(GyroscopeSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<GyroscopeSensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.GyroscopeSensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<GyroscopeSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final GyroscopeSenorsAppDatabase mDb;
        private final GyroscopeSensor msensor0;
        private final GyroscopeSensor msensor1;
        private final GyroscopeSensor msensor2;
        private final GyroscopeSensor msensor3;
        private final GyroscopeSensor msensor4;
        private final GyroscopeSensor msensor5;
        private final GyroscopeSensor msensor6;
        private final GyroscopeSensor msensor7;
        private final GyroscopeSensor msensor8;
        private final GyroscopeSensor msensor9;
        private final GyroscopeSensor msensor10;
        private final GyroscopeSensor msensor11;
        private final GyroscopeSensor msensor12;
        private final GyroscopeSensor msensor13;
        private final GyroscopeSensor msensor14;
        private final GyroscopeSensor msensor15;
        private final GyroscopeSensor msensor16;
        private final GyroscopeSensor msensor17;
        private final GyroscopeSensor msensor18;
        private final GyroscopeSensor msensor19;

        PopulateSensorsAsync(GyroscopeSenorsAppDatabase db, GyroscopeSensor msensor0, GyroscopeSensor msensor1, GyroscopeSensor msensor2, GyroscopeSensor msensor3, GyroscopeSensor msensor4, GyroscopeSensor msensor5, GyroscopeSensor msensor6, GyroscopeSensor msensor7, GyroscopeSensor msensor8, GyroscopeSensor msensor9, GyroscopeSensor msensor10, GyroscopeSensor msensor11, GyroscopeSensor msensor12, GyroscopeSensor msensor13, GyroscopeSensor msensor14, GyroscopeSensor msensor15, GyroscopeSensor msensor16, GyroscopeSensor msensor17, GyroscopeSensor msensor18, GyroscopeSensor msensor19) {
            mDb = db;
            this.msensor0 = msensor0;
            this.msensor1 = msensor1;
            this.msensor2 = msensor2;
            this.msensor3 = msensor3;
            this.msensor4 = msensor4;
            this.msensor5 = msensor5;
            this.msensor6 = msensor6;
            this.msensor7 = msensor7;
            this.msensor8 = msensor8;
            this.msensor9 = msensor9;
            this.msensor10 = msensor10;
            this.msensor11 = msensor11;
            this.msensor12 = msensor12;
            this.msensor13 = msensor13;
            this.msensor14 = msensor14;
            this.msensor15 = msensor15;
            this.msensor16 = msensor16;
            this.msensor17 = msensor17;
            this.msensor18 = msensor18;
            this.msensor19 = msensor19;        }

        @Override
        protected Void doInBackground(final Void... params) {
            try{
            mDb.GyroscopeSensorsDao().insertAll( msensor0, msensor1, msensor2, msensor3, msensor4, msensor5, msensor6, msensor7, msensor8, msensor9,
                    msensor10, msensor11, msensor12, msensor13, msensor14, msensor15, msensor16, msensor17, msensor18, msensor19);
          //  Log.d(TAG, "Location Rows Count: " + mDb.locationSensorsDao().countUsers()+msensor.getTimestamp());
            } catch (Exception e) {
                throw new NullPointerException();

            } finally {

                return null;

            }
        }

    }


}
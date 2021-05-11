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
package inm7.JTrack.Jtrack_Social.AccelerationSensor;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetAccelerationSensorsDataAsyc {

    private static final String TAG = GetAccelerationSensorsDataAsyc.class.getName();

    //=====================PUBLIC CLASS FUNCTIONS========================================================
    public static void populateSensors(@NonNull final AccelerationSenorsAppDatabase db, List<AccelerationSensor> sensor) {
       PopulateSensorsAsync task = new PopulateSensorsAsync(db, sensor.get(0), sensor.get(1), sensor.get(2), sensor.get(3), sensor.get(4),
               sensor.get(5), sensor.get(6), sensor.get(7), sensor.get(8), sensor.get(9), sensor.get(10), sensor.get(11), sensor.get(12),
               sensor.get(13), sensor.get(14),
               sensor.get(15), sensor.get(16), sensor.get(17), sensor.get(18), sensor.get(19));
        task.execute();

      //  db.locationSensorsDao().insertAll(sensor);


    }

    public static List<AccelerationSensor> getSensors(@NonNull final AccelerationSenorsAppDatabase db) {
        getSensorsAsync getSensorsAsync = new getSensorsAsync(db);
        List<AccelerationSensor> sensorsList = null;

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
    public static Integer getSensorsCount (@NonNull final AccelerationSenorsAppDatabase db){

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
    public static List<AccelerationSensor> syncSensorwithServer(@NonNull final AccelerationSenorsAppDatabase db, Integer startindex, Integer endindex) {
        getSensorsforServerAsync getSensorsforServerAsync = new getSensorsforServerAsync(db, startindex, endindex);
        List<AccelerationSensor> sensorsList = null;

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
    public static Integer getIdtoSync(@NonNull final AccelerationSenorsAppDatabase db) {
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
    public static boolean DeleteSensorFromLocal(@NonNull final AccelerationSenorsAppDatabase db, Integer startindex, Integer endindex) {
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
    private static class getSensorsforServerAsync extends AsyncTask<Void, Void, List<AccelerationSensor>> {
        private final AccelerationSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public getSensorsforServerAsync(AccelerationSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected List<AccelerationSensor> doInBackground(Void... voids) {

            // return all sensors data
            return mDb.AccelerationSensorsDao().getAllBetween(mstartindex, mendindex);

        }

        @Override
        protected void onPostExecute(List<AccelerationSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // delete all data from database using room for SYNC WITH SERVER
    private static class deleteSensorsfromLocalAsync extends AsyncTask<Void, Void,Void> {
        private final AccelerationSenorsAppDatabase mDb;
        private final Integer mstartindex;
        private final Integer mendindex;


        public deleteSensorsfromLocalAsync(AccelerationSenorsAppDatabase mDb, Integer mstartindex, Integer mendindex) {
            this.mDb = mDb;
            this.mstartindex = mstartindex;
            this.mendindex = mendindex;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // return all sensors data
            try {
                mDb.AccelerationSensorsDao().deleteAllBetween(mstartindex, mendindex);
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
        private final AccelerationSenorsAppDatabase mDb;

        private getSensorCountAsync(AccelerationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }


        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors list
            return mDb.AccelerationSensorsDao().countUsers();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    // get sensors ID for sync from database using room
    private static class getIdforSyncAsync extends  AsyncTask<Void,Void, Integer> {
        private final AccelerationSenorsAppDatabase mDb;


        private getIdforSyncAsync(AccelerationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            // return sensors id
            return mDb.AccelerationSensorsDao().selectIdforSync();
        }

        @Override
        protected void onPostExecute(Integer IdforSync) {
            super.onPostExecute(IdforSync);
        }
    }

    // read all sensors data from database using room
    private static class getSensorsAsync extends AsyncTask<Void, Void, List<AccelerationSensor>> {
        private final AccelerationSenorsAppDatabase mDb;


        public getSensorsAsync(AccelerationSenorsAppDatabase mDb) {
            this.mDb = mDb;
        }

        @Override
        protected List<AccelerationSensor> doInBackground(Void... voids) {

            // return sensors list
            return mDb.AccelerationSensorsDao().getAll();

        }

        @Override
        protected void onPostExecute(List<AccelerationSensor> sensors) {
            super.onPostExecute(sensors);
        }
    }


    // insert sensor to database using room
    private static class PopulateSensorsAsync extends AsyncTask<Void, Void, Void> {

        private final AccelerationSenorsAppDatabase mDb;
        private final AccelerationSensor msensor0;
        private final AccelerationSensor msensor1;
        private final AccelerationSensor msensor2;
        private final AccelerationSensor msensor3;
        private final AccelerationSensor msensor4;
        private final AccelerationSensor msensor5;
        private final AccelerationSensor msensor6;
        private final AccelerationSensor msensor7;
        private final AccelerationSensor msensor8;
        private final AccelerationSensor msensor9;
        private final AccelerationSensor msensor10;
        private final AccelerationSensor msensor11;
        private final AccelerationSensor msensor12;
        private final AccelerationSensor msensor13;
        private final AccelerationSensor msensor14;
        private final AccelerationSensor msensor15;
        private final AccelerationSensor msensor16;
        private final AccelerationSensor msensor17;
        private final AccelerationSensor msensor18;
        private final AccelerationSensor msensor19;

        PopulateSensorsAsync(AccelerationSenorsAppDatabase db, AccelerationSensor msensor0, AccelerationSensor msensor1, AccelerationSensor msensor2, AccelerationSensor msensor3, AccelerationSensor msensor4, AccelerationSensor msensor5, AccelerationSensor msensor6, AccelerationSensor msensor7, AccelerationSensor msensor8, AccelerationSensor msensor9, AccelerationSensor msensor10, AccelerationSensor msensor11, AccelerationSensor msensor12, AccelerationSensor msensor13, AccelerationSensor msensor14, AccelerationSensor msensor15, AccelerationSensor msensor16, AccelerationSensor msensor17, AccelerationSensor msensor18, AccelerationSensor msensor19) {
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
            mDb.AccelerationSensorsDao().insertAll( msensor0, msensor1, msensor2, msensor3, msensor4, msensor5, msensor6, msensor7, msensor8, msensor9,
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
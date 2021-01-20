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

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import inm7.Jutrack.jutrack_Social.BroadCastReciever.SensorBroadcastReceiver;


public class SyncActiveMonitoringJobService extends Worker {

    private static final String TAG = SyncActiveMonitoringJobService.class.getName();
    private boolean jobCancelled = false;

    SyncActiveMonitoringStatClass syncActiveMonitoringClass = new SyncActiveMonitoringStatClass(getApplicationContext(),1);


    public SyncActiveMonitoringJobService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @Override
    public void onStopped() {
        super.onStopped();
        Intent broadcastIntent = new Intent(getApplicationContext(), SensorBroadcastReceiver.class);
        broadcastIntent.setAction("START");
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            syncActiveMonitoringClass.syncOnBackGround();
            jobCancelled=syncActiveMonitoringClass.jobCancelled;
        } catch(Throwable e) {

            jobCancelled = syncActiveMonitoringClass.jobCancelled;
        }


        if(jobCancelled != false)
        {
            return Result.retry();

        } else {
            return Result.success();

        }



    }
}

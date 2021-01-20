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

import androidx.room.Entity;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.Sensors;


@Entity(tableName = Constants.AppUsageStatSensor_td)
public class AppUsageStatSensor extends Sensors {

    private long beginTimeStamp;
    private long endTimeStamp;
    private long lastTimeUsed;
    private long totalTimeinForeground;
    private String appName;



    //constructor



    //setters

    public void setBeginTimeStamp(long beginTimeStamp) {
        this.beginTimeStamp = beginTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public void setTotalTimeinForeground(long totalTimeinForeground) {
        this.totalTimeinForeground = totalTimeinForeground;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
//getters


    public long getBeginTimeStamp() {
        return beginTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public long getTotalTimeinForeground() {
        return totalTimeinForeground;
    }

    public String getAppName() {
        return appName;
    }
}

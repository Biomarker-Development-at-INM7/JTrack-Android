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

import androidx.room.Entity;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.Sensors;


@Entity(tableName = Constants.ActiveLabelingSensor_td)
public class ActiveLabelingSensor extends Sensors {



    private String taskName;
    private long startTimestamp;
    private long endTimestamp;
    private long duration;
    private String voiceId;



    //constructor



    //setters


    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


//getters


    public String getVoiceId() {
        return voiceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public long getDuration() {
        return duration;
    }
}

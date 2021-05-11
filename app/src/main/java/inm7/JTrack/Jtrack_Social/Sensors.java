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
package inm7.JTrack.Jtrack_Social;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AccelerationSensor_td")
public class Sensors {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String sensorname;
    private long timestamp;
    private String deviceid;
    private String username;
    private String studyId;




    //getter
    public Long getId() {
        return id;
    }

    public String getSensorname() {
        return sensorname;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public String getUsername() {
        return username;
    }

    public String getStudyId() {
        return studyId;
    }

    //setter
    public void setId(Long id) {
        this.id = id;
    }

    public void setSensorname(String sensorname) {
        this.sensorname = sensorname;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }
}

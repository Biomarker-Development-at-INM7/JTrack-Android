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

import java.util.List;

@Entity(tableName = Constants.BarometerSensor_td)
public class Patinet {

    private Integer status;
    private String deviceid;
    private String pushNotification_token;
    private String username;
    private String studyId;
    private long time_joined;
    private long time_left;
    private String study_duration;

    private String deviceModel;
    private String deviceBrand;
    private String osVersion;

    private String applicationType;
    private String appVersion;


    private List<String> sensors = null;
    private  String freq;
    private int active_labeling;



    //constructor


    //setters
    public void setapplicationType(String applicationType) {
        this.applicationType = applicationType;
    }
    public void setappVersion(String appVersion_ema) {
        this.appVersion = appVersion;
    }


    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public void setTime_joined(long time_joined) {
        this.time_joined = time_joined;
    }

    public void setTime_left(long time_left) {
        this.time_left = time_left;
    }

    public void setPushNotification_token(String pushNotification_token) {
        this.pushNotification_token = pushNotification_token;
    }

    public void setStudy_duration(String study_duration) {
        this.study_duration = study_duration;
    }


    public void setSensors(List<String>  sensors) {
        this.sensors = sensors;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public void setActive_labeling(int active_labeling) {
        this.active_labeling = active_labeling;
    }
    //getters


    public String getapplicationType() {
        return applicationType;
    }
    public String getappVersion() {
        return appVersion;
    }


    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public Integer getStatus() {
        return status;
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

    public long getTime_joined() {
        return time_joined;
    }

    public long getTime_left() {
        return time_left;
    }

    public String getPushNotification_token() {
        return pushNotification_token;
    }

    public String getStudy_duration() {
        return study_duration;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public String getFreq() {
        return freq;
    }

    public int getActive_labeling() {
        return active_labeling;
    }
}

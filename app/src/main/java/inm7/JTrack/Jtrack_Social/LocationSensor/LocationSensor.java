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

import androidx.room.Entity;

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.Sensors;


@Entity(tableName = Constants.LocationSensor_td)
public class LocationSensor extends Sensors {

    private double lat;
    private double lon;
    private double alt;
    private double accuracy;
    private String provider;


    //constructor



    //setters

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

//getters


    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getProvider() {
        return provider;
    }
}

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
package inm7.Jutrack.jutrack_Social.DetectedActivitySensor;

import androidx.room.Entity;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.Sensors;


@Entity(tableName = Constants.DetectedActivitySensor_td)
public class DetectedActivitySensor extends Sensors {

    //depending on version some activities are not supported!
    /*

    IN_VEHICLE  0
    ON_BICYCLE  1
    ON_FOOT     2
    STILL       3
    UNKNOWN     4
    TILTING     5
    WALKING     7
    RUNNING     8

     */

    private int activityType;
    private double confidence;



    //constructor



    //setters


    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

//getters


    public int getActivityType() {
        return activityType;
    }

    public double getConfidence() {
        return confidence;
    }
}

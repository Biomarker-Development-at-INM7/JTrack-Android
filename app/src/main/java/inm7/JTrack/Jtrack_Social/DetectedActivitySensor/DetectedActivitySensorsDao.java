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
package inm7.JTrack.Jtrack_Social.DetectedActivitySensor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DetectedActivitySensorsDao {


    @Query("SELECT  sensorname,timestamp,deviceid,username,studyId,activityType,confidence FROM DetectedActivitySensor_td")
    List<DetectedActivitySensor> getAll();

    @Query("SELECT sensorname,timestamp,deviceid,username,studyId,activityType,confidence FROM DetectedActivitySensor_td WHERE id BETWEEN :startindex AND :endindex")
    List<DetectedActivitySensor> getAllBetween(Integer startindex, Integer endindex);

    @Query("SELECT * FROM DetectedActivitySensor_td where sensorname LIKE  :firstName ")
    DetectedActivitySensor findByName(String firstName);

    @Query("SELECT COUNT(*) from DetectedActivitySensor_td")
    int countUsers();

    @Query("SELECT id from DetectedActivitySensor_td ORDER BY timestamp ASC LIMIT  1")
    int selectIdforSync();

    @Insert
    void insertAll(DetectedActivitySensor... sensor);

    @Delete
    void delete(DetectedActivitySensor sensor);

    @Query("DELETE  FROM detectedactivitysensor_td WHERE id BETWEEN :startindex AND :endindex")
    void deleteAllBetween(Integer startindex, Integer endindex);
}

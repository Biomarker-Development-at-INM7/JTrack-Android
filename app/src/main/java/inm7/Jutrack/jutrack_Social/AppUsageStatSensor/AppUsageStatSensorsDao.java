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

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppUsageStatSensorsDao {


    @Query("SELECT sensorname,timestamp,deviceid,username,studyId,appName,beginTimeStamp,endTimeStamp,lastTimeUsed,totalTimeinForeground FROM AppUsageStatSensor_td")
    List<AppUsageStatSensor> getAll();

    @Query("SELECT sensorname,timestamp,deviceid,username,studyId,appName,beginTimeStamp,endTimeStamp,lastTimeUsed,totalTimeinForeground FROM AppUsageStatSensor_td WHERE id BETWEEN :startindex AND :endindex")
    List<AppUsageStatSensor> getAllBetween(Integer startindex, Integer endindex);

 //   @Query("SELECT * FROM AppUsageStatSensor_td where sensorname LIKE  :firstName ")
   // AppUsageStatSensor findByName(String firstName);

    @Query("SELECT id from AppUsageStatSensor_td ORDER BY timestamp ASC LIMIT  1")
    int selectIdforSync();

    @Query("SELECT COUNT(*) from AppUsageStatSensor_td")
    int countUsers();

    @Insert
    void insertAll(AppUsageStatSensor... sensor);

    @Delete
    void delete(AppUsageStatSensor sensor);

    @Query("DELETE  FROM AppUsageStatSensor_td WHERE id BETWEEN :startindex AND :endindex")
    void deleteAllBetween(Integer startindex, Integer endindex);
}

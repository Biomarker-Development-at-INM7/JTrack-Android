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

package inm7.Jutrack.jutrack_Social.ActiveLabeling;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActiveLabelingSensorsDao {


    @Query("SELECT  sensorname,timestamp,deviceid,username,studyId,startTimestamp,endTimestamp,taskName,voiceId,duration FROM ActiveLabelingSensor_td")
    List<ActiveLabelingSensor> getAll();

    @Query("SELECT sensorname,timestamp,deviceid,username,studyId,startTimestamp,endTimestamp,taskName,voiceId,duration  FROM ActiveLabelingSensor_td WHERE id BETWEEN :startindex AND :endindex")
    List<ActiveLabelingSensor> getAllBetween(Integer startindex, Integer endindex);

    @Query("SELECT * FROM ActiveLabelingSensor_td where sensorname LIKE  :firstName ")
    ActiveLabelingSensor findByName(String firstName);

    @Query("SELECT COUNT(*) from ActiveLabelingSensor_td")
    int countUsers();

    @Query("SELECT id from ActiveLabelingSensor_td ORDER BY timestamp ASC LIMIT  1")
    int selectIdforSync();

    @Insert
    void insertAll(ActiveLabelingSensor... sensor);

    @Delete
    void delete(ActiveLabelingSensor sensor);

    @Query("DELETE  FROM ActiveLabelingSensor_td WHERE id BETWEEN :startindex AND :endindex")
    void deleteAllBetween(Integer startindex, Integer endindex);
}

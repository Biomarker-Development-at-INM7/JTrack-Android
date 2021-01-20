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
package inm7.Jutrack.jutrack_Social.GyroscopeSensor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GyroscopeSensorsDao {


    @Query("SELECT  sensorname,timestamp,deviceid,username,x,y,z FROM GyroscopeSensor_td")
    List<GyroscopeSensor> getAll();

    @Query("SELECT sensorname,timestamp,deviceid,username,studyId,x,y,z  FROM GyroscopeSensor_td WHERE id BETWEEN :startindex AND :endindex")
    List<GyroscopeSensor> getAllBetween(Integer startindex, Integer endindex);


    @Query("SELECT COUNT(*) from GyroscopeSensor_td")
    int countUsers();

    @Query("SELECT id from GyroscopeSensor_td ORDER BY timestamp ASC LIMIT  1")
    int selectIdforSync();

    @Insert
    void insertAll(GyroscopeSensor... sensor);

    @Delete
    void delete(GyroscopeSensor sensor);


    @Query("DELETE  FROM GyroscopeSensor_td WHERE id BETWEEN :startindex AND :endindex")
    void deleteAllBetween(Integer startindex, Integer endindex);
}

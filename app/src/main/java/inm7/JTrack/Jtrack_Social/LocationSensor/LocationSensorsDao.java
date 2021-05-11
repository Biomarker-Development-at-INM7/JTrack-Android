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

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationSensorsDao {


    @Query("SELECT  sensorname,timestamp,deviceid,username,provider,studyId,alt,lat,lon,accuracy FROM LocationSensor_td")
    List<LocationSensor> getAll();

    @Query("SELECT sensorname,timestamp,deviceid,username,provider,studyId,alt,lat,lon,accuracy FROM LocationSensor_td WHERE id BETWEEN :startindex AND :endindex")
    List<LocationSensor> getAllBetween(Integer startindex, Integer endindex);


    @Query("SELECT COUNT(*) from LocationSensor_td")
    int countUsers();

    @Query("SELECT id from LocationSensor_td ORDER BY timestamp ASC LIMIT  1")
    int selectIdforSync();

    @Insert
    void insertAll(LocationSensor... sensor);

    @Delete
    void delete(LocationSensor sensor);


    @Query("DELETE  FROM LocationSensor_td WHERE id BETWEEN :startindex AND :endindex")
    void deleteAllBetween(Integer startindex, Integer endindex);
}

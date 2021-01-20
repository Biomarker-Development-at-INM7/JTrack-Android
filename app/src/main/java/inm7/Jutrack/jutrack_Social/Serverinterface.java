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
package inm7.Jutrack.jutrack_Social;

import inm7.Jutrack.jutrack_Social.AccelerationSensor.AccelerationSensor;
import inm7.Jutrack.jutrack_Social.ActiveMonitoring.ActiveLabelingSensor;
import inm7.Jutrack.jutrack_Social.AppUsageStatSensor.AppUsageStatSensor;
import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.DetectedActivitySensor;
import inm7.Jutrack.jutrack_Social.GyroscopeSensor.GyroscopeSensor;
import inm7.Jutrack.jutrack_Social.LocationSensor.LocationSensor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Serverinterface {


    // App usage stat detection service

    @POST("api")
    Call<Patinet> createPatient(@Header("charset") String charset,@Header("ACTION") String action_value,@Header("md5") String md5_value,@Body Patinet post);

    @POST("api")
    Call<Patinet> loginPatient(@Header("ACTION") String action_value,@Header("md5") String md5_value,@Body Patinet post);



    // location service
    @GET("api")
    Call<LocationSensor> getLocationSensor();

    @POST("api")
    Call<LocationSensor> createLocationSensor(@Header("charset") String charset,@Header("ACTION") String action_value,@Header("md5") String Md5,
                                                    @Body List<LocationSensor> post);

    // Activity detection service
    @GET("api")
    Call<DetectedActivitySensor> getDetectedActivitySensor();

    @POST("api")
    Call<DetectedActivitySensor> createDetectedActivitySensor(@Header("charset") String charset,@Header("ACTION") String action_value,@Header("md5") String Md5,
                                                                    @Body List<DetectedActivitySensor> post);

    // App usage stat detection service
    @GET("api")
    Call<AppUsageStatSensor> getAppUsageStatSensor();

    @POST("api")
    Call<AppUsageStatSensor> createAppUsageStatSensor(@Header("charset") String charset, @Header("ACTION") String action_value,@Header("md5") String Md5,
                                                            @Body List<AppUsageStatSensor> post);

    @POST("api")
    Call<AccelerationSensor> createAccelerationSensor(@Header("charset") String charset, @Header("ACTION") String action_value, @Header("md5") String Md5,
                                                      @Body List<AccelerationSensor> post);
    @POST("api")
    Call<GyroscopeSensor> createGyroscopeSensor(@Header("charset") String charset, @Header("ACTION") String action_value, @Header("md5") String Md5,
                                                @Body List<GyroscopeSensor> post);


    // Active Labeling service
    @GET("api")
    Call<ActiveLabelingSensor> getActiveLabelingSensor();

    @POST("api")
    Call<ActiveLabelingSensor> createActiveLabelingSensor(@Header("charset") String charset, @Header("ACTION") String action_value,@Header("md5") String Md5,
                                                      @Body List<ActiveLabelingSensor> post);

}

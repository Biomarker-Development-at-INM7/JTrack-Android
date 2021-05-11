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

import android.content.ContentValues;

public class Data {
    private long time = 0;
    private String sensor = null;
    private float x = 0f;
    private float y = 0f;
    private float z = 0f;
    private int activity_ID = 0;


    public long getTime() {
        return time;
    }

    public String getSensor() {
        return sensor;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getActivity_ID(){return activity_ID;}


    Data(long time, String sensor, float x, float y, float z,int activity_ID) {
        this.time = time;
        this.sensor = sensor;
        this.x = x;
        this.y = y;
        this.z = z;
        this.activity_ID = activity_ID;
    }

    ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("timeStamp", time);
        contentValues.put("sensorName", sensor);
        contentValues.put("x", x);
        contentValues.put("y", y);
        contentValues.put("z", z);
        contentValues.put("activity_ID",activity_ID);
        return contentValues;
    }

    public String toString() {
        return "{\n" +
                "\ttimeStamp : " + time + "\n" +
                "\tsensorName : " + sensor + "\n" +
                "\tx : " + x + "\n" +
                "\ty : " + y + "\n" +
                "\tz : " + z + "\n" +
                "\tactivity_ID : "+activity_ID + "\n" +
                "}";

    }
}

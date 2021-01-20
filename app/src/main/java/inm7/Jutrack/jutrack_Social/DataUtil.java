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

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.*;
import java.util.ArrayList;


@SuppressWarnings("unused")
public class DataUtil extends SQLiteOpenHelper {
    //put devise id
    private static final String DB_NAME = "sensor.db";
    private SQLiteDatabase db;
    private Context context = null;

    DataUtil(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
    }

    public ArrayList<String> queryTable() {
        db = getWritableDatabase();
        ArrayList<String> as = new ArrayList<>();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            if(cursor.getString(0).equals("android_metadata"))continue;
            as.add(cursor.getString(0));
        }
        cursor.close();
        return as;
    }

    //convert to json
    public JSONArray readTable() {
        JSONArray resulinJSON = new JSONArray();
        db = getWritableDatabase();
        ArrayList<String> as = new ArrayList<>();
        for (String table : queryTable()) {
            Cursor cursor = db.rawQuery("select * from " + table + " order by id", null);
            while (cursor.moveToNext()) {
                if(cursor.getString(0).equals("android_metadata"))continue;
                //as.add(cursor.getString(0));
                resulinJSON.put(cursor.getString(0));
            }
            cursor.close();
        }


        return resulinJSON;
    }


    private String TB_NAME = null;

    /**
     * create table
     */
    public void create(String table) {
        db = getWritableDatabase();
        db.execSQL("CREATE TABLE " + table +
                "(id integer primary key, timeStamp long, sensorName text, x float, y float, z float, activity_ID int);");
        this.TB_NAME = table;
        Toast.makeText(context, "create " + table + " success", Toast.LENGTH_SHORT).show();

    }

    /**
     * writing data into sqlite.
     */
    public void recode(Data data) {
        db = getWritableDatabase();
        db.insert(TB_NAME, null, data.toContentValues());
    }

    /**
     * export data from sqlite to /sdcard/Android/data/[package name]/files/[motion].csv
     */
    public void export() {
        db = getWritableDatabase();
        try {
            StringBuilder sb = new StringBuilder("export [ ");
            for (String table : queryTable()) {
                File file = new File(context.getExternalFilesDir("") + "/" + table + ".csv");
                if (file == null) throw new Resources.NotFoundException(table);
                FileWriter fw = new FileWriter(file);
                Cursor cursor = db.rawQuery("select * from " + table + " order by id", null);
                while (cursor.moveToNext()) {
                    String tmp = cursor.getInt(0) + "," +
                            cursor.getLong(1) + "," +
                            cursor.getString(2) + "," +
                            cursor.getFloat(3) + "," +
                            cursor.getFloat(4) + "," +
                            cursor.getFloat(5) + "," +
                            cursor.getInt(6) + "\n";
                    fw.write(tmp);
                }
                cursor.close();
                sb.append(table);
                sb.append(" ");
            }
            sb.append("] finish");
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * clear all tables
     */
    public void reset() {
        db = getWritableDatabase();
        StringBuilder sb = new StringBuilder("drop [ ");
        for (String table : queryTable()) {
            db.execSQL("DROP TABLE " + table + ";");
            sb.append(table);
            sb.append(" ");
        }
        sb.append("] success");
        Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
    }
	/*
	public Map<String, Data> avg(String table) {
		db = getWritableDatabase();
		Cursor cursor;
		cursor = db.rawQuery("SELECT sensor,COUNT(*),SUM(x),SUM(y),SUM(z) FROM " + table + " GROUP BY sensor;",null);
		Map<String, Data> map = new HashMap<>();
		while (cursor.moveToNext()){
			Data data = new Data(cursor.getInt(1), cursor.getString(0), cursor.getFloat(2),
				cursor.getFloat(3), cursor.getFloat(4));
			map.put(cursor.getString(0), data);
		}
		cursor.close();
		return map;
	}

	*/

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}

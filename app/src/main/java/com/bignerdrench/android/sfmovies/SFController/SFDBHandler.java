package com.bignerdrench.android.sfmovies.SFController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdrench.android.sfmovies.SFModel.SFLocation;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by abhijeetgaur16 on 18/02/17.
 */

public class SFDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";
    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LOCATION = "locations";
    public static final String COLUMN_FUNFACT = "fun_fact";
    public static final String COLUMN_RELEASE_YEAR = "release_year";
    public static final String COLUMN_DISTRIBUTOR = "distributor";
    public static final String COLUMN_DIRECTOR = "director";
    public static final String COLUMN_WRITER = "writer";
    public static final String COLUMN_ACTOR1 = "actor_1";
    public static final String COLUMN_ACTOR2 = "actor_2";
    public static final String COLUMN_ACTOR3 = "actor_3";
    public static final String COLUMN_PRODUCTION = "production_company";
    public static final String COLUMN_LATITUDE = "lat";
    public static final String COLUMN_LONGITUDE = "lng";

    public SFDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+TABLE_MOVIES+ "("+
                        COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        COLUMN_TITLE + " TEXT, "+
                        COLUMN_RELEASE_YEAR + " INTEGER, "+
                        COLUMN_LOCATION + " TEXT, " +
                        COLUMN_FUNFACT + " TEXT, "+
                        COLUMN_DISTRIBUTOR + " TEXT, "+
                        COLUMN_DIRECTOR + " TEXT, "+
                        COLUMN_WRITER + " TEXT, "+
                        COLUMN_ACTOR1+ " TEXT, "+
                        COLUMN_ACTOR2 + " TEXT, "+
                        COLUMN_ACTOR3 + " TEXT, "+
                        COLUMN_PRODUCTION + " TEXT " +
                        COLUMN_LATITUDE + " REAL, "+
                        COLUMN_LONGITUDE + " REAL "+
                        ");";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_MOVIES);
        onCreate(db);
    }

    public void addAddressData(SFLocation place)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE,place.getmTitle());
        values.put(COLUMN_LOCATION,place.getLocations());
        values.put(COLUMN_FUNFACT,place.getmFunFacts());
        values.put(COLUMN_DIRECTOR,place.getmDirector());
        values.put(COLUMN_RELEASE_YEAR,place.getmReleaseYear());
        values.put(COLUMN_DISTRIBUTOR,place.getmDistributor());
        values.put(COLUMN_PRODUCTION,place.getmProductionCompany());
        values.put(COLUMN_WRITER,place.getmWriter());
        values.put(COLUMN_ACTOR1,place.getmActor1());
        values.put(COLUMN_ACTOR2,place.getmActor2());
        values.put(COLUMN_ACTOR3,place.getmActor3());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MOVIES,null,values);
        db.close();
    }

    public void updateLocationData(SFLocation place, Double latitude, Double longitude){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE,latitude);
        values.put(COLUMN_LONGITUDE,longitude);
        if(place.getLocations() != null)
        {
            SQLiteDatabase db = getWritableDatabase();
            db.update(TABLE_MOVIES,values,COLUMN_LOCATION+"="+place.getLocations(),null);
        }
    }

    public LatLng getDataForAddress(String address){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOVIES+" WHERE "+ "locations =\""+address+"\"";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

            double lat = c.getDouble(c.getColumnIndex("lat"));
            double lng = c.getDouble(c.getColumnIndex("lng"));
            if(lat!=0 && lng!=0)
            {
                return new LatLng(lat,lng);
            }
        else return null;
    }

}

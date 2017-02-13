package com.bignerdrench.android.sfmovies.SFController;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bignerdrench.android.sfmovies.R;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Geometry;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Location;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Result;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.SFGoogleResponse;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhijeetgaur16 on 11/02/17.
 */

public class SFGetLocationIntentService extends IntentService {

    public static final String BROADCAST_ACTION = "com.abhijeet.SFMovies.geoData";
    private final Handler handler = new Handler();
    ArrayList<SFLocation> locationRecvd;
    SFLocation searchResponse;
    String TAG = "SFGetLocationIntent";
    int counter = 0;


    public SFGetLocationIntentService(){
        super("LocationService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        locationRecvd = intent.getParcelableArrayListExtra("locationList");
        SFGeocodeGoogle geo;
        int counter = 0;
        LatLng temp = null;
        Controller cm = new Controller(getResources().getString(R.string.MAP_URL));
        for(final SFLocation l:locationRecvd)
        {
            if(l!= null && l.getLocations() != null && !l.getLocations().isEmpty())
            {
                final SFLocation obj = l;
                cm.plotOnMap(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if(response.isSuccessful())
                        {
                            Log.v(TAG,"Response successful ");
                            try{
                                SFGoogleResponse geoResponse = (SFGoogleResponse)response.body();
                                List<Result> results = geoResponse.getResults();
                                Result result = results.get(0);
                                Geometry gm = result.getGeometry();
                                Location location = gm.getLocation();

                                double Latitude = location.getLat();
                                double Longitude = location.getLng();
                                Log.v(TAG,"lat : "+Latitude);
                                Log.v(TAG,"lng : "+Longitude);

                                Intent mIntent = new Intent(BROADCAST_ACTION);
                                mIntent.putExtra("isFromService",true);
                                mIntent.putExtra("lat",Latitude);
                                mIntent.putExtra("lng",Longitude);
                                mIntent.putExtra("SFObject",obj);
                                LocalBroadcastManager.getInstance(SFGetLocationIntentService.this).sendBroadcast(mIntent);
                            }
                            catch (Exception e){
                                Log.v(TAG,"Entity not found");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.v(TAG,"Response failed");
                    }
                }, l.getLocations() + ", San Francisco, CA", getString(R.string.google_maps_key));
            }
            counter++;
            Log.v(TAG,"counter : "+counter);
        }
    }
}

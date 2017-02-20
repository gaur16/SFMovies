package com.bignerdrench.android.sfmovies.SFController;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bignerdrench.android.sfmovies.R;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Geometry;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Location;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.Result;
import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.SFGoogleResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abhijeetgaur16 on 12/02/17.
 */

public class SFGeocodeGoogle implements Callback<SFGoogleResponse> {


    private Context context;
    String TAG= "SFGeocodegoogle";

    public SFGeocodeGoogle(Context context, String address, String key) {
        this.context = context;
        Controller cm = new Controller(context.getResources().getString(R.string.MAP_URL));
        cm.fetchLocationForAddress(this,address,key);
    }

    @Override
    public void onFailure(Call<SFGoogleResponse> call, Throwable t) {
        Log.v(TAG,"No data received");
    }

    @Override
    public void onResponse(Call<SFGoogleResponse> call, Response<SFGoogleResponse> response) {
        if(response.isSuccessful())
        {
            Log.v(TAG,"Response successful ");
            try{
                SFGoogleResponse geoResponse = response.body();
                List<Result> results = geoResponse.getResults();
                Result result = results.get(0);
                Geometry gm = result.getGeometry();
                Location location = gm.getLocation();

                double Latitude = location.getLat();
                double Longitude = location.getLng();
                Log.v(TAG,"lat : "+Latitude);
                Log.v(TAG,"lng : "+Longitude);

                final Intent mIntent = new Intent(SFGetLocationIntentService.BROADCAST_ACTION);
                mIntent.putExtra("isSingleQuery",true);
                mIntent.putExtra("lat",Latitude);
                mIntent.putExtra("lng",Longitude);
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);
            }
            catch (Exception e){
                Log.v(TAG,"Entity not found");
            }
        }
    }
}

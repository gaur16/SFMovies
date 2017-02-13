package com.bignerdrench.android.sfmovies.SFController;

import android.util.Log;

import java.util.List;


/**
 * Created by abhijeetgaur16 on 10/02/17.
 */

import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.SFGoogleResponse;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;
import com.bignerdrench.android.sfmovies.SFView.SFMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Controller {
    private String TAG = "Controller";
    private SFApi gerritAPI;

    public Controller(String URL) {
         Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClientBuilder.build())
                .build();

        gerritAPI = retrofit.create(SFApi.class);
    }

    public void start(SFMap fragment, String key) {
        Call<List<SFLocation>> call = gerritAPI.getLocations(key,350);
        call.enqueue(fragment);
    }

    public void fetchForKeyword(Callback callback, String keyword,int limit, String key){
        keyword = keyword.toUpperCase();
        Log.v(TAG,"keyword: "+keyword);
        Call<List<SFLocation>> call = gerritAPI.getSearchResponse(key,keyword,limit);
        call.enqueue(callback);
    }

    public void fetchLocationForAddress(SFGeocodeGoogle geo, String address, String key){
        Call<SFGoogleResponse> call = gerritAPI.getGeoLocationDataGoogle(address+", San Francisco, CA",key);
        call.enqueue(geo);
    }

    public void plotOnMap(Callback sf, String address, String key){
        Call<SFGoogleResponse> call = gerritAPI.getGeoLocationDataGoogle(address+", San Francisco, CA",key);
        call.enqueue(sf);
    }
}

package com.bignerdrench.android.sfmovies.SFController;

/**
 * Created by abhijeetgaur16 on 10/02/17.
 */

import com.bignerdrench.android.sfmovies.SFModel.SFGoogleResponse.SFGoogleResponse;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface SFApi {

    @GET("resource/wwmu-gmzc.json")
    Call<List<SFLocation>> getLocations(@Query("$$app_token") String apiToken,
                                        @Query("$limit") int limit);

    @GET("resource/wwmu-gmzc.json")
    Call<List<SFLocation>> getSearchResponse(@Query("$$app_token") String apiToken,
                                             @Query("$q") String keyword,
                                             @Query("$limit") int limit);

    @GET("/maps/api/geocode/json")
    Call<SFGoogleResponse> getGeoLocationDataGoogle(@Query("address") String address,
                                              @Query("key") String key);

}

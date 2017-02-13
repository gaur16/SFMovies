package com.bignerdrench.android.sfmovies.SFModel;

import com.google.android.gms.drive.realtime.internal.event.ObjectChangedDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by abhijeetgaur16 on 13/02/17.
 */

public class SFMarker implements ClusterItem{

    private final LatLng mPosition;
    private String mTitle="";
    private String mSnippet="";
    private Object mTag;

    public SFMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public SFMarker(double lat, double lng, String title, String snippet, Object tag) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mTag = tag;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public Object getmTag() {
        return mTag;
    }

    public String getmSnippet() {
        return mSnippet;
    }

    public String getmTitle() {
        return mTitle;
    }
}


package com.bignerdrench.android.sfmovies.SFView;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdrench.android.sfmovies.R;
import com.bignerdrench.android.sfmovies.SFController.SFGeocodeGoogle;
import com.bignerdrench.android.sfmovies.SFController.SFGetLocationIntentService;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;
import com.bignerdrench.android.sfmovies.SFController.Controller;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SFMap extends Fragment implements OnMapReadyCallback, Callback<List<SFLocation>> {

    String TAG = "SFMap";
    protected MapView mMapView;
    GoogleMap mMap;
    Intent intent;
    private SFLocation searchResult;
    private View mView;
    private BottomSheetBehavior mBottomSheetBehavior;

    private TextView mTitle;
    private TextView mReleaseYear;
    private TextView mLocation;
    private TextView mFunFact;
    private TextView mDirector ;
    private TextView mWriter ;
    private TextView mProduction;

    private LinearLayout title_layout ;
    private LinearLayout release_year_layout;
    private LinearLayout location_layout ;
    private LinearLayout funFact_layout ;
    private LinearLayout director_layout ;
    private LinearLayout writer_layout;
    private LinearLayout production_layout;

    Controller cm;
    private ArrayList<SFLocation> mRecvdList;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.sf_map_view, container, false);

        mMapView = (MapView) mView.findViewById(R.id.fragment_embedded_map_view_mapview);
        View bottomSheet = mView.findViewById(R.id.bottom_sheet);
        mTitle = (TextView) mView.findViewById(R.id.title_text);
        mReleaseYear = (TextView) mView.findViewById(R.id.release_year_text);
        mLocation = (TextView) mView.findViewById(R.id.location_text);
        mFunFact = (TextView) mView.findViewById(R.id.fun_fact_text);
        mDirector = (TextView) mView.findViewById(R.id.director_text);
        mWriter = (TextView) mView.findViewById(R.id.writer_text);
        mProduction = (TextView) mView.findViewById(R.id.production_house_text);

        title_layout = (LinearLayout) mView.findViewById(R.id.title);
        release_year_layout = (LinearLayout) mView.findViewById(R.id.release_year);
        location_layout = (LinearLayout) mView.findViewById(R.id.location);
        funFact_layout = (LinearLayout) mView.findViewById(R.id.fun_fact);
        director_layout = (LinearLayout) mView.findViewById(R.id.director);
        writer_layout = (LinearLayout) mView.findViewById(R.id.writer);
        production_layout = (LinearLayout) mView.findViewById(R.id.production_house);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        cm = new Controller(getResources().getString(R.string.BASE_URL));

        final SFAutoComplete autoComplete = (SFAutoComplete) mView.findViewById(R.id.sf_auto_complete);
        autoComplete.setThreshold(4);
        autoComplete.setAdapter(new SearchCompleteAdapter(getActivity()));
        autoComplete.setLoadingIndicator((android.widget.ProgressBar) mView.findViewById(R.id.loading));
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SFLocation place = (SFLocation) adapterView.getItemAtPosition(position);
                autoComplete.setText(place.getLocations());
                SFGeocodeGoogle sfg = new SFGeocodeGoogle(getContext(),place.getLocations(),getString(R.string.google_maps_key));
                searchResult = place;
            }
        });

        intent = new Intent(getActivity(), SFGetLocationIntentService.class);
        return mView;
    }

    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    //Unregistering broadcast receiver here
    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        getActivity().stopService(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    //Called when Map is displaying
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Map Ready: Starting Background service to fetch location data
        cm.start(this, getResources().getString(R.string.db_api_token));
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7749,-122.4194),15.0f));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.v(TAG,"Info window clicked");
                SFLocation sf = (SFLocation) marker.getTag();
                setBottomSheetText(sf);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void onFailure(Call<List<SFLocation>> call, Throwable t) {

    }

    //Response for fetching the result of AutoCompleteSearch
    @Override
    public void onResponse(Call<List<SFLocation>> call, Response<List<SFLocation>> response) {
        if(response.isSuccessful()) {
            List<SFLocation> recvdLocations = response.body();
            mRecvdList = new ArrayList<SFLocation>(new HashSet<SFLocation>(recvdLocations));        //removes repeated Locations
            intent.putParcelableArrayListExtra("locationList",mRecvdList);
            getActivity().startService(intent);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(SFGetLocationIntentService.BROADCAST_ACTION));
        } else {
            Log.v(TAG,response.errorBody().toString());
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("isFromService",true))
                updateUI(intent);
            else
                updateSingleMarker(intent);
        }
    };

    //Called when Map receives intent from Background service
    private void updateUI(Intent intent) {
        if(mMap!=null)
        {
            double latitude = intent.getDoubleExtra("lat", 0);
            double longitude = intent.getDoubleExtra("lng", 0);
            SFLocation place = intent.getParcelableExtra("SFObject");
            if (latitude != 0 && longitude != 0 && place != null)
            {
                Marker zuck = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .title(place.getmTitle() + "("+place.getmReleaseYear()+")")
                        .snippet(place.getLocations()));
                zuck.setTag(place);
            }
            Log.v(TAG, "counter : " + intent.getIntExtra("counter", 0));
        }
    }

    //Called when a single location is searched
    private void updateSingleMarker(Intent intent){
        if(mMap != null){
            double latitude = intent.getDoubleExtra("lat",0);
            double longitude = intent.getDoubleExtra("lng",0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));

            Marker zuck =  mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker())
                    .title(searchResult.getmTitle()+"("+searchResult.getmReleaseYear()+")")
                    .snippet(searchResult.getLocations()));
            zuck.setTag(searchResult);
            zuck.showInfoWindow();
            hideSoftInputKeyboard();
        }
    }

    private void hideSoftInputKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setBottomSheetText(SFLocation sf){
        if(sf.getmTitle()!=null && !sf.getmTitle().isEmpty())
        {
            mTitle.setText(sf.getmTitle());
        }
        else title_layout.setVisibility(View.GONE);
        if(sf.getLocations()!=null && !sf.getLocations().isEmpty())
        {
            mLocation.setText(sf.getLocations());
        }
        else location_layout.setVisibility(View.GONE);
        if(sf.getmReleaseYear()!=0 )
        {
            mReleaseYear.setText(String.valueOf(sf.getmReleaseYear()));
        }
        else release_year_layout.setVisibility(View.GONE);
        if(sf.getmFunFacts()!=null && !sf.getmFunFacts().isEmpty())
        {
            mFunFact.setText(sf.getmFunFacts());
        }
        else funFact_layout.setVisibility(View.GONE);
        if(sf.getmDirector()!=null && !sf.getmDirector().isEmpty())
        {
            mDirector.setText(sf.getmDirector());
        }
        else director_layout.setVisibility(View.GONE);
        if(sf.getmWriter()!=null && !sf.getmWriter().isEmpty())
        {
            mWriter.setText(sf.getmWriter());
        }
        else writer_layout.setVisibility(View.GONE);
        if(sf.getmProductionCompany()!=null && !sf.getmProductionCompany().isEmpty())
        {
            mProduction.setText(sf.getmProductionCompany());
        }
        else production_layout.setVisibility(View.GONE);
    }
}


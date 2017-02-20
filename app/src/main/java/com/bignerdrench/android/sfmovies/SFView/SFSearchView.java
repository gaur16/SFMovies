package com.bignerdrench.android.sfmovies.SFView;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bignerdrench.android.sfmovies.R;
import com.bignerdrench.android.sfmovies.SFController.SFGeocodeGoogle;
import com.bignerdrench.android.sfmovies.SFController.SFGetLocationIntentService;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;

/**
 * Created by abhijeetgaur16 on 18/02/17.
 */

public class SFSearchView extends Fragment {

    private View mView;
    private ProgressBar mProgressBar;
    public static final String BROADCAST_SEARCH = "com.abhijeet.SFMovies.searchAction";
    SFAutoComplete mAutoComplete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.sf_search_view, container, false);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.loading);
        final SFAutoComplete autoComplete = (SFAutoComplete) mView.findViewById(R.id.sf_auto_complete);
        if(autoComplete!=null){
        mAutoComplete = autoComplete;
        autoComplete.setThreshold(3);
        autoComplete.setAdapter(new SearchCompleteAdapter(getActivity()));
        autoComplete.setLoadingIndicator(mProgressBar);
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SFLocation place = (SFLocation) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(SFGetLocationIntentService.BROADCAST_ACTION);
                intent.putExtra("place_from_search",place);
                intent.putExtra("isFromSearch",true);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                getActivity().finish();
            }
        });
        }

        return mView;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isFilteringStarted = intent.getBooleanExtra("filteringStarted",false);
            Boolean isFilteringDone = intent.getBooleanExtra("filteringDone",false);
            Boolean isFilteringFailed = intent.getBooleanExtra("filteringFailed",false);
            Boolean success = intent.getBooleanExtra("success",false);

            if(isFilteringStarted){
                mProgressBar.setVisibility(View.VISIBLE);
                mAutoComplete.setEnabled(false);
            }

            if(isFilteringDone){
                mProgressBar.setVisibility(View.GONE);
                mAutoComplete.setEnabled(true);
                if(!success){
                    Toast.makeText(getContext(),"Sorry could not fetch results",Toast.LENGTH_SHORT).show();
                }
            }

            if(isFilteringFailed){
                mProgressBar.setVisibility(View.GONE);
                mAutoComplete.setEnabled(true);
                Toast.makeText(getContext(),"Server did not return a valid response. Check your internet connection.",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_SEARCH));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}

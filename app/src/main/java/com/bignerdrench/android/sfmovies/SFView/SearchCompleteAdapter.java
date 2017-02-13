package com.bignerdrench.android.sfmovies.SFView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bignerdrench.android.sfmovies.R;
import com.bignerdrench.android.sfmovies.SFController.Controller;
import com.bignerdrench.android.sfmovies.SFModel.SFLocation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 25;
    private Context mContext;
    private List<SFLocation> resultList = new ArrayList<SFLocation>();
    String TAG = "SearchCompleteAdapter";

    public SearchCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public SFLocation getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drop_down_list, parent, false);
        }
        Log.v(TAG,"loc : " + getItem(position).getLocations());
        ((TextView) convertView.findViewById(R.id.movie_location)).setText(getItem(position).getLocations());
        Log.v(TAG,"loc : " + getItem(position).getmTitle());
        ((TextView) convertView.findViewById(R.id.movie_title)).setText(getItem(position).getmTitle());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    Controller cm = new Controller(mContext.getResources().getString(R.string.BASE_URL));
                    cm.fetchForKeyword(new Callback<List<SFLocation>>() {
                        @Override
                        public void onResponse(Call<List<SFLocation>> call, Response<List<SFLocation>> response) {
                            // Assign the data to the FilterResults
                            if(response.isSuccessful())
                            {
                                List<SFLocation> recvd = response.body();

                                filterResults.values = recvd;
                                filterResults.count = recvd.size();
                                for(SFLocation r:recvd){
                                    Log.v(TAG,"recvd : " + r.getmTitle()+
                                    " "+r.getmReleaseYear()+
                                    " "+r.getLocations());
                                }
                                publishResults(constraint,filterResults);
                            }
                            else System.out.println(response.errorBody());
                        }
                        @Override
                        public void onFailure(Call<List<SFLocation>> call, Throwable t) {
                            Log.v(TAG,"Failed To Fetch data from server");
                        }
                    },constraint.toString(),MAX_RESULTS,mContext.getResources().getString(R.string.db_api_token));
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.v(TAG,"Publish results called");
                if (results != null && results.count > 0) {
                    Log.v(TAG,"Publish results called : " + results.values);
                    resultList = (List<SFLocation>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}

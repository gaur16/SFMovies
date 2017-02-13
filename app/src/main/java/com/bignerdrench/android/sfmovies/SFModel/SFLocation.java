package com.bignerdrench.android.sfmovies.SFModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abhijeetgaur16 on 10/02/17.
 */

public class SFLocation implements Parcelable {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("release_year")
    private int mReleaseYear;

    @SerializedName("locations")
    private String locations;

    @SerializedName("fun_facts")
    private String mFunFacts;

    @SerializedName("production_company")
    private String mProductionCompany;

    @SerializedName("distributor")
    private String mDistributor;

    @SerializedName("director")
    private String mDirector;

    @SerializedName("writer")
    private String mWriter;

    @SerializedName("actor_1")
    private String mActor1;

    @SerializedName("actor_2")
    private String mActor2;

    @SerializedName("actor_3")
    private String mActor3;

    public String getLocations() {
        return locations;
    }

    public String getmActor1() {
        return mActor1;
    }

    public String getmActor2() {
        return mActor2;
    }

    public String getmActor3() {
        return mActor3;
    }

    public String getmDirector() {
        return mDirector;
    }

    public String getmDistributor() {
        return mDistributor;
    }

    public String getmFunFacts() {
        return mFunFacts;
    }

    public String getmProductionCompany() {
        return mProductionCompany;
    }

    public int getmReleaseYear() {
        return mReleaseYear;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmWriter() {
        return mWriter;
    }

    public void setLocation(String locations) {
        this.locations = locations;
    }

    public void setmActor1(String mActor1) {
        this.mActor1 = mActor1;
    }

    public void setmActor2(String mActor2) {
        this.mActor2 = mActor2;
    }

    public void setmActor3(String mActor3) {
        this.mActor3 = mActor3;
    }

    public void setmDirector(String mDirector) {
        this.mDirector = mDirector;
    }

    public void setmDistributor(String mDistributor) {
        this.mDistributor = mDistributor;
    }

    public void setmFunFacts(String mFunFacts) {
        this.mFunFacts = mFunFacts;
    }

    public void setmProductionCompany(String mProductionCompany) {
        this.mProductionCompany = mProductionCompany;
    }

    public void setmReleaseYear(int mReleaseYear) {
        this.mReleaseYear = mReleaseYear;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmWriter(String mWriter) {
        this.mWriter = mWriter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SFLocation)) {
            return false;
        }
        SFLocation other = (SFLocation) obj;
        if(this.locations != null)
            return this.locations.equals(other.locations);
        return false;
    }

    @Override
    public int hashCode() {
        if(locations!=null)
            return locations.hashCode();
        else return 0;
    }


    protected SFLocation(Parcel in) {
        mTitle = in.readString();
        mReleaseYear = in.readInt();
        locations = in.readString();
        mFunFacts = in.readString();
        mProductionCompany = in.readString();
        mDistributor = in.readString();
        mDirector = in.readString();
        mWriter = in.readString();
        mActor1 = in.readString();
        mActor2 = in.readString();
        mActor3 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mReleaseYear);
        dest.writeString(locations);
        dest.writeString(mFunFacts);
        dest.writeString(mProductionCompany);
        dest.writeString(mDistributor);
        dest.writeString(mDirector);
        dest.writeString(mWriter);
        dest.writeString(mActor1);
        dest.writeString(mActor2);
        dest.writeString(mActor3);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SFLocation> CREATOR = new Parcelable.Creator<SFLocation>() {
        @Override
        public SFLocation createFromParcel(Parcel in) {
            return new SFLocation(in);
        }

        @Override
        public SFLocation[] newArray(int size) {
            return new SFLocation[size];
        }
    };
}

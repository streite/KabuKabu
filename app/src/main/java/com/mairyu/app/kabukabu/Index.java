package com.mairyu.app.kabukabu;

import android.os.Parcel;
import android.os.Parcelable;

//==================================================================================================
//===   Index
//==================================================================================================
public class Index implements Parcelable {

    private int id;
    private String Name;
    private String Country;
    private float Price;
    private float Change;
    private float PercChange;
    private String Trend;

    public Index() {

        id = 0;
        Name = "";
        Country = "";
        Price = 0;
        Change = 0;
        PercChange = 0;
        Trend = "none";
    }

    public Index(Parcel source) {

        id = source.readInt();
        Name = source.readString();
        Country = source.readString();
        Price = source.readFloat();
        Change = source.readFloat();
        PercChange = source.readFloat();
        Trend = source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public float getChange() {
        return Change;
    }

    public void setChange(float change) {
        Change = change;
    }

    public float getPercChange() {
        return PercChange;
    }

    public void setPercChange(float percChange) {
        PercChange = percChange;
    }

    public String getTrend() {
        return Trend;
    }

    public void setTrend(String trend) {
        Trend = trend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(Name);
        dest.writeString(Country);
        dest.writeFloat(Price);
        dest.writeFloat(Change);
        dest.writeFloat(PercChange);
        dest.writeString(Trend);
    }

    public static final Creator<Index> CREATOR = new Creator<Index>() {
        @Override
        public Index[] newArray(int size) {
            return new Index[size];
        }

        @Override
        public Index createFromParcel(Parcel source) {
            return new Index(source);
        }
    };
}
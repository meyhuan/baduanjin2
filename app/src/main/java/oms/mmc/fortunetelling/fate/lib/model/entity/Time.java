package oms.mmc.fortunetelling.fate.lib.model.entity;

import android.os.Parcel;

import com.smartydroid.android.starter.kit.model.entity.Entity;

/**
 * Author: meyu
 * Date:   16/4/1
 * Email:  627655140@qq.com
 */
public class Time extends Entity{

    public String time;
    public String timestring;
    public String top;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeString(this.top);
        dest.writeString(this.timestring);
    }

    public Time() {
    }

    protected Time(Parcel in) {
        this.time = in.readString();
        this.top = in.readString();
        this.timestring = in.readString();
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        @Override
        public Time createFromParcel(Parcel source) {
            return new Time(source);
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };
}

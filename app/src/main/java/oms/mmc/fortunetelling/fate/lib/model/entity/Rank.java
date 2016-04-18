package oms.mmc.fortunetelling.fate.lib.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: meyu
 * Date:   16/3/7
 * Email:  627655140@qq.com
 */
public class Rank implements Parcelable {

    public String id;
    public String user_id;
    public String totaltime;
    public String posttime;
    public String username;
    public String avatar;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_id);
        dest.writeString(this.totaltime);
        dest.writeString(this.posttime);
        dest.writeString(this.username);
        dest.writeString(this.avatar);
    }

    public Rank() {
    }

    protected Rank(Parcel in) {
        this.id = in.readString();
        this.user_id = in.readString();
        this.totaltime = in.readString();
        this.posttime = in.readString();
        this.username = in.readString();
        this.avatar = in.readString();
    }

    public static final Creator<Rank> CREATOR = new Creator<Rank>() {
        @Override
        public Rank createFromParcel(Parcel source) {
            return new Rank(source);
        }

        @Override
        public Rank[] newArray(int size) {
            return new Rank[size];
        }
    };


    @Override
    public String toString() {
        return "Rank{" +
                "avatar='" + avatar + '\'' +
                ", id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", totaltime='" + totaltime + '\'' +
                ", posttime='" + posttime + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

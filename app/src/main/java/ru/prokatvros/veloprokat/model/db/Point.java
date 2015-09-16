package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Point")
public class Point extends Model implements Parcelable {

    @Column(name = "Title")
    public String title;

    @Column(name = "Address")
    public String address;





    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {

        public Point createFromParcel(Parcel in) {

            return Point.load(Point.class, in.readLong());
        }

        public Point[] newArray(int size) {
            return new Point[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
    }

}
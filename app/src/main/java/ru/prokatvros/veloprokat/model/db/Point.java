package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Point")
public class Point extends Model implements Parcelable {


    @Expose
    @Column(name="ServerId")
    public String serverId;


    @Expose
    @Column(name = "Title")
    public String title;

    @Expose
    @Column(name = "Address", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String address;

    public static List<Point> getAll() {

        return new Select()
                .from(Point.class)
                .execute();
    }

    public static Point getByAddress(String address) {
        return new Select()
                .from(Point.class)
                .where("Address = ?", address)
                .executeSingle();
    }

    public static Point getByServerId(String serverId) {
        return new Select()
                .from(Point.class)
                .where("ServerId = ?", serverId)
                .executeSingle();
    }

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

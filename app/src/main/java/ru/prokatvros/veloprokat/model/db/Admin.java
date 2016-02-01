package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Admin")
public class Admin extends Model implements Parcelable {

    @Expose
    @Column(name = "ServerId")
    public long serverId;

    @Expose
    @Column(name = "Name")
    public String name;

    public static final Parcelable.Creator<Admin> CREATOR = new Parcelable.Creator<Admin>() {

        public Admin createFromParcel(Parcel in) {

            return Admin.load(Admin.class, in.readLong());
        }

        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    public String getAvatarUrl() {
        return "http://prokatvros.temp.swtest.ru/api/files/images/cat.jpg";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
    }

}

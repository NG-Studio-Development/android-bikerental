package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Client")
public class Client extends Model implements Parcelable {

    public static String TAG = "CLIENT";

    @Expose
    @Column(name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE )
    public int serverId;

    @Expose
    @Column(name = "Name")
    public String name;

    @Expose
    @Column(name = "Surname")
    public String surname;

    @Expose
    @Column(name = "Phone", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    public String phone;

    @Expose
    @Column(name = "Avatar")
    public String avatar;

    @Expose
    @Column(name = "VipNumber")
    public String vipNumber = "N/A";

    @Column(name = "CountRents", onDelete = Column.ForeignKeyAction.CASCADE)
    public int countRents = -1;

    @Column(name = "Summ")
    public int summ = -1;

    @Expose
    @Column(name = "BlackList")
    public int blackList = 0;

    public boolean hasVipNumber() {
        return !vipNumber.equals("N/A");
    }

    public static List<Client> getAll() {
        return new Select()
                .from(Client.class)
                .execute();
    }

    public static List<Client> getAllMain() {
        return new Select()
                .from(Client.class)
                .where("VipNumber IS ?", "N/A")
                .where("BlackList = ?", 0)
                .execute();
    }

    public static List<Client> getAllVip() {
        return new Select()
                .from(Client.class)
                .where("VipNumber IS NOT ?", "N/A")
                .execute();
    }

    public static List<Client> getAllBlackList() {
        return new Select()
                .from(Client.class)
                .where("BlackList = ?", 1)
                .execute();
    }

    public static List<Client> getBySubNumber(String subNumber) {
        return new Select()
                .from(Client.class)
                .where("Phone LIKE ?", "%" + subNumber + "%")
                .execute();
    }

    public static Client getByNumber(String number) {
        return new Select()
                .from(Client.class)
                .where("Phone = ?", number)
                .executeSingle();
    }



    public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {

        public Client createFromParcel(Parcel in) {

            return Client.load(Client.class, in.readLong());
        }

        public Client[] newArray(int size) {
            return new Client[size];
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

package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Client")
public class Client extends Model implements Parcelable {

    public static String TAG = "CLIENT";

    @Expose
    @Column(name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    public String serverId;

    @Expose
    @Column(name = "Name")
    public String name;

    @Expose
    @Column(name = "Surname")
    public String surname;

    @Expose
    @Column(name = "Phone")
    public String phone;

    @Expose
    @Column(name = "Avatar")
    public String avatar;

    @Expose
    @Column(name = "VipNumber")
    public String vipNumber = "N/A";

    @Column(name = "CountRents")
    public int countRents = 0;

    @Expose
    @Column(name = "Summ")
    public int summ = 0;

    @Expose
    @Column(name = "Blacklist")
    public int blackList = 0;

    public boolean hasVipNumber() {
        return !vipNumber.equals("N/A");
    }


    public interface LoadListener {
        void onLoad();
    }

    public static void parse(final List<Client> list/*, final LoadListener listener*/) {

        //new Thread(new Runnable() {
            //@Override
            //public void run() {

                ActiveAndroid.beginTransaction();

                try {

                    for (Client client : list) {
                        client.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }finally {
                    ActiveAndroid.endTransaction();
                }
    }

    public static List<Client> getAll() {
        return new Select()
                .from(Client.class)
                .execute();
    }

    public static From getAllMain() {
        return new Select()
                .from(Client.class)
                .where("VipNumber IS ?", "N/A")
                .where("Blacklist = ?", 0)
                .limit("1000");
                //.execute();
    }

    public static From getAllVip() {
        return new Select()
                .from(Client.class)
                .where("VipNumber IS NOT ?", "N/A")
                .where("Blacklist = ?", 0);
                //.execute();
}

    public static From getAllBlackList() {
        return new Select()
                .from(Client.class)
                .where("BlackList = ?", 1);
                //.execute();
    }


    public static From getFromBySubNumber(String subNumber) {
        return new Select()
                .from(Client.class)
                .where("Phone LIKE ?", "%" + subNumber + "%");

    }

    public static List<Client> getBySubNumber(String subNumber) {
        return getFromBySubNumber(subNumber).execute();
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

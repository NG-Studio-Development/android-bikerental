package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(name = "Rent")
public class Rent extends Model implements Parcelable {

    private final static Map<String, Rent> poolOfRents = new HashMap<>();

    int completed;

    @Column(name = "Client")
    public Client client;

    @Column(name = "Inventory")
    public Inventory inventory;

    @Column(name = "Breakdown")
    public Breakdown breakdown;

    public static Rent createRentInPool(String key) {
        Rent rent = new Rent();
        poolOfRents.put(key, rent);
        return rent;
    }

    public static void removeRentInPool(String key) {
        poolOfRents.remove(key);
    }

    public static Rent getRentFromPool(String key) {
        return poolOfRents.get(key);
    }

    public static List<Rent> getAll() {
        return new Select()
                .from(Rent.class)
                .execute();
    }

    public static void removeEmpty() {
        List<Rent> rentList = getAll();

        for (Rent rent : rentList) {
            if (rent.client == null && rent.inventory == null)
                rent.delete();

        }
    }


    public static final Parcelable.Creator<Rent> CREATOR = new Parcelable.Creator<Rent>() {

        public Rent createFromParcel(Parcel in) {

            return Rent.load(Rent.class, in.readLong());
        }

        public Rent[] newArray(int size) {
            return new Rent[size];
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

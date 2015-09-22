package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prokatvros.veloprokat.BikerentalApplication;

@Table(name = "Rent")
public class Rent extends Model implements Parcelable {

    private final static Map<String, Rent> poolOfRents = new HashMap<>();

    @Expose
    @Column(name = "ServerId", onDelete = Column.ForeignKeyAction.CASCADE)
    private int serverId;

    @Expose
    @Column(name = "Token", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    private String token;

    @Expose
    @Column(name = "IsCompleted", onDelete = Column.ForeignKeyAction.CASCADE)
    public int isCompleted;

    @Expose
    @Column(name = "Client", onDelete = Column.ForeignKeyAction.CASCADE, index = true)
    public Client client;

    @Expose
    @Column(name = "Inventory", onDelete = Column.ForeignKeyAction.CASCADE)
    public Inventory inventory;

    @Expose
    @Column(name = "Breakdown", onDelete = Column.ForeignKeyAction.CASCADE)
    public Breakdown breakdown;

    @Expose
    @Column(name = "EndTime")
    public long endTime;

    public void setCompleteds(boolean completeds) {
        this.isCompleted = completeds ? 1:0;
    }

    public boolean isCompleted() {
        return isCompleted == 1;
    }


    public void puckFields() {
        Client clientField = null;

        if (client != null ) {
            clientField = Client.getByNumber(client.phone);
            if (clientField != null)
                client = clientField;
            else
                client.save();
        }
        inventory.save();

    }

    public Rent() {
        super();
        token = BikerentalApplication.getInstance().getUUID() +
                Calendar.getInstance().getTimeInMillis();
    }


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

    public static List<Rent> getAllByCompleted(boolean completed) {
        int completedInInteger = completed ? 1 : 0;

        return new Select()
                .from(Rent.class)
                .where("IsCompleted = ?", completedInInteger)
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

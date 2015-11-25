package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prokatvros.veloprokat.BikerentalApplication;

@Table(name = "Rental")
public class Rent extends Model {

    private final static Map<String, Rent> poolOfRents = new HashMap<>();

    @Expose
    @Column( name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    public String serverId;

    @Expose
    @Column( name = "Token" )
    private String token;

    @Expose
    @Column( name = "IsCompleted" )
    public Integer isCompleted = 0;

    @Expose
    @Column( name = "StartTime" )
    public long startTime;

    @Expose
    @Column(name = "Client", onDelete = Column.ForeignKeyAction.CASCADE)
    public Client client;

    @Expose
    @Column(name = "Inventory", onDelete = Column.ForeignKeyAction.CASCADE)
    public Inventory inventory;

    @Expose
    @Column(name = "InventoryAddition")
    public Inventory inventoryAddition;

    @Expose
    @Column(name = "Breakdown", onDelete = Column.ForeignKeyAction.CASCADE)
    public Breakdown breakdown;

    @Expose
    @Column(name = "EndTime")
    public Long endTime;

    @Expose
    @Column(name = "PaidFine")
    public int paidFine;

    @Expose
    @Column(name = "Surcharge")
    public int surcharge;

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

        Inventory inventory = Inventory.getByNumber(this.inventory.number);

        if (inventory == null)
            this.inventory.save();
        else
            this.inventory = inventory;


        if (inventoryAddition != null) {
            inventory = Inventory.getByNumber(this.inventoryAddition.number);

            if (inventory == null)
                this.inventoryAddition.save();
            else
                inventoryAddition = inventory;
        }

        if (this.breakdown != null) {
            Breakdown breakdown = Breakdown.getByCode(this.breakdown.code);
            if (breakdown == null) {
                this.breakdown.save();
            } else {
                this.breakdown = breakdown;
            }
        }

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

    public static void parse(List<Rent> list) {

        ActiveAndroid.beginTransaction();

        try {
            for (Rent rent : list) {

                rent.fullSave();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }



    public int getCost() {
        int cost = 0;

        if ( inventory == null ) {
            throw new NullPointerException("inventory can not be NULL !!!");
        } else if(endTime == null) {
            throw new NullPointerException("endTime can not be NULL !!!");
        }

        DateTime startTime = new DateTime(this.startTime);
        DateTime endTime = new DateTime(this.endTime);

        Period p = new Period(startTime, endTime);

        int hours = p.getHours();
        int days = p.getDays();

        if ( days > 0 && days < 3 )
            cost = inventory.tarif.sumDay * days;
        else if (days == 3)
            cost = inventory.tarif.sumTsHour;
        else if (hours > 0)
            cost = inventory.tarif.sumHour * hours;

        return cost;

    }

    public Long fullSave() {
        if (this.inventory != null)
            this.inventory.save();

        if (this.client != null)
            this.client.save();

        return save();
    }

}

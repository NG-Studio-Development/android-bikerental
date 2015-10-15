package ru.prokatvros.veloprokat.model.db;

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

@Table(name = "Rental")
public class Rent extends Model /*implements Parcelable*/ {

    private final static Map<String, Rent> poolOfRents = new HashMap<>();

    @Expose
    @Column(name = "serverId")
    private Integer serverId;

    @Expose
    @Column(name = "Token"/*, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE*/ )
    private String token;

    @Expose
    @Column(name = "IsCompleted")
    public Integer isCompleted = 0;

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

        //=


        //=


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

    }

    /*public void saveCrud() {
        List<Point> pointsAll = Point.getAll();
        Point points = Point.getByAddress(this.points.address);
        if ( points == null )
            this.points.save();
        else
            this.points = points;

    }*/

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

    public int getCost() {
        if ( inventory != null && endTime != null )
            return 99;
        else
            return 0;
    }

}

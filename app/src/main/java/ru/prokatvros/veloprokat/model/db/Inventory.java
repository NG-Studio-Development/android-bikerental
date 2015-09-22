package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

@Table(name = "Inventory")
public class Inventory extends Model implements Serializable {

    @Expose
    @Column(name = "ServerId", onDelete = Column.ForeignKeyAction.CASCADE)
    public String serverId;

    @Expose
    @Column(name = "Model", onDelete = Column.ForeignKeyAction.CASCADE)
    public String model;

    @Expose
    @Column(name = "Number", onDelete = Column.ForeignKeyAction.CASCADE)
    public String number;

    @Expose
    @Column(name = "CountRents", onDelete = Column.ForeignKeyAction.CASCADE)
    public int countRents = -1;

    /*@Expose
    @Column(name = "Cost")
    public int cost = -1; */

    @Expose
    @Column(name = "Tarif", onDelete = Column.ForeignKeyAction.CASCADE)
    public Tarif tarif;

    @Expose
    @Column(name = "Point", onDelete = Column.ForeignKeyAction.CASCADE)
    public Point points;
    //public List<Point> pointList;


    public static List<Inventory> getAll() {

        return new Select()
                .from(Inventory.class)
                .execute();
    }

    public static List<Inventory> getByName(String model) {
        return new Select()
                .from(Inventory.class)
                .where("Model = ?", model)
                .execute();
    }

    public static List<Inventory> getBySubName(String subName) {
        return new Select()
                .from(Inventory.class)
                .where("Model LIKE ?", "%" + subName +"%")
                .execute();
    }
}

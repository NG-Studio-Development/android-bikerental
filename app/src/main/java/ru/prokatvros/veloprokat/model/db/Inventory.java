package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

@Table(name = "Inventory")
public class Inventory extends Model implements Serializable {

    @Column(name = "Model")
    public String model;

    @Column(name = "Number")
    public String number;

    @Column(name = "CountRents")
    public int countRents;

    @Column(name = "Cost")
    public int cost;

    @Column(name = "Tarif")
    public Tarif tarif;

    @Column(name = "Point")
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

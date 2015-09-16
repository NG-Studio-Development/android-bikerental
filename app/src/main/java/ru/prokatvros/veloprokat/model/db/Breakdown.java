package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Breakdown")
public class Breakdown extends Model implements Serializable {

    @Column(name = "Code")
    public String code;

    @Column(name = "Description")
    public String description;

    @Column(name = "Cost")
    public int cost;


    public static List<Breakdown> initListForDEBUG() {
        List<Breakdown> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Breakdown breakdown = new Breakdown();
            breakdown.description = "Breakdown "+(i+1);
            breakdown.save();
            list.add(breakdown);
        }

        return list;
    }

    public static List<Breakdown> getAll() {
        return new Select()
                .from(Breakdown.class)
                .execute();
    }

    /*@Override
    public String toString() {
        return description;
    }*/
}
package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Breakdown")
public class Breakdown extends Model implements Serializable {

    public static String CODE_WITHOUT_BREAKDOWN = "code_without_breakdown";

    @Expose
    @Column(name = "Code")
    public String code;

    @Expose
    @Column(name = "Description")
    public String description;

    @Expose
    @Column(name = "Summ")
    public int summ;

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

    public static Breakdown getByCode( String code ) {
        return new Select()
                .from(Breakdown.class)
                .where("Code = ?", code)
                .executeSingle();
    }

    public static Breakdown createWithoutBreakdown(String description) {
        Breakdown breakdown = new Breakdown();
        breakdown.code = CODE_WITHOUT_BREAKDOWN;
        breakdown.description = description;
        return breakdown;
    }

    /*@Override
    public String toString() {
        return description;
    }*/
}

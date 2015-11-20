package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Tarif")
public class Tarif extends Model {

    @Expose
    @Column(name = "Name")
    public String name;

    @Expose
    @Column(name = "SumHour")
    public int sumHour;

    @Expose
    @Column(name = "SumDay")
    public int sumDay;

    @Expose
    @Column(name = "SumTsHour")
    public int sumTsHour;

}

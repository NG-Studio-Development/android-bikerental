package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Rent")
public class Message extends Model {

    @Expose
    @Column(name = "Message")
    public String message;

    @Expose
    @Column(name = "Admin")
    public Admin admin;
}

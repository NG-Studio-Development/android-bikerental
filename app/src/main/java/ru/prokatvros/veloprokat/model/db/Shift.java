package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.Date;

@Table(name = "Shift")
public class Shift extends Model {

    public static int STATE_IN_PROGRESS = 0;
    public static int STATE_DELIVERED = 1;
    public static int STATE_APPROVED = 2;

    @Expose
    @Column( name = "serverId" )
    public Long serverId;

    @Expose
    @Column( name = "Admin" )
    public Admin admin;

    @Expose
    @Column( name = "Point", onDelete = Column.ForeignKeyAction.CASCADE)
    public Point point;

    @Expose
    @Column( name = "Date" )
    public Date date;

    @Expose
    @Column( name = "State" )
    public int state;

}

package ru.prokatvros.veloprokat.model.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

@Table(name = "Inventory")
public class Inventory extends Model implements Parcelable {

    public static final Long MAIN_GROUP = 1l;
    public static final Long ADDITIONAL_GROUP = 2l;

    @Expose
    @Column(name = "ServerId")
    public String serverId;

    @Expose
    @Column(name = "Model")
    public String model;

    @Expose
    @Column( name = "Number", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    public String number;

    @Expose
    @Column(name = "CountRents")
    public int countRents = -1;

    /*@Expose
    @Column(name = "Cost")
    public int cost = -1; */

    @Expose
    @Column(name = "Tarif", onDelete = Column.ForeignKeyAction.CASCADE)
    public Tarif tarif;

    @Expose
    @Column(name = "Point" /*, onDelete = Column.ForeignKeyAction.CASCADE*/ )
    private Point points;

    @Expose
    @Column(name = "IdGroup")
    private Long idGroup;


    public void saveCrud() {
        List<Point> pointsAll = Point.getAll();
        Point points = Point.getByAddress(this.points.address);
        if ( points == null )
            this.points.save();
        else
            this.points = points;

    }

    public static List<Inventory> getAll() {

        return new Select()
                .from(Inventory.class)
                .execute();
    }

    public static Inventory getByNumber(String number) {
        return new Select()
                .from(Inventory.class)
                .where("Number = ?", number)
                .executeSingle();
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
                .where("Model LIKE ?", "%" + subName + "%")
                .execute();
    }

    public static List<Inventory> getBySubNumber(String subNumber) {
        return new Select()
                .from(Inventory.class)
                .where("Number LIKE ?", "%" + subNumber + "%")
                .execute();
    }

    public static List<Inventory> getBySubNumber( String subNumber, Long idGroup ) {
        return new Select()
                .from(Inventory.class)
                .where("Number LIKE ?", "%" + subNumber + "%")
                .where("IdGroup = ?", String.valueOf(idGroup))
                .execute();
    }

    public static List<Inventory> getByPoint( Point point ) {
        List<Inventory> allInventory = getAll();
        List<Point> allPoint = Point.getAll();

        List<Inventory> byPoint = new Select()
                .from(Inventory.class)
                .where( "Point = ?", point.getId() )
                .execute();

        return byPoint;
    }

    /*public static List<Inventory> getByGroup(Long idGroup) {
        return new Select()
                .from( Inventory.class )
                .where( "IdGroup = ?", idGroup )
                .execute();
    } */

    public static final Parcelable.Creator<Inventory> CREATOR = new Parcelable.Creator<Inventory>() {

        public Inventory createFromParcel(Parcel in) {

            return Inventory.load(Inventory.class, in.readLong());
        }

        public Inventory[] newArray(int size) {
            return new Inventory[size];
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

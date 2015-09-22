package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

@Table(name = "Client")
public class Client extends Model implements Serializable {

    @Expose
    @Column(name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE )
    public int serverId = -1;

    @Expose
    @Column(name = "Name")
    public String name;

    @Expose
    @Column(name = "Surname")
    public String surname;

    @Expose
    @Column(name = "Phone")
    public String phone;

    public static List<Client> getAll() {
        return new Select()
                .from(Client.class)
                .execute();
    }

    public static List<Client> getBySubNumber(String subNumber) {
        return new Select()
                .from(Client.class)
                .where("Phone LIKE ?", "%" + subNumber + "%")
                .execute();
    }

    public static Client getByNumber(String number) {
        return new Select()
                .from(Client.class)
                .where("Phone = ?", number)
                .executeSingle();
    }

}

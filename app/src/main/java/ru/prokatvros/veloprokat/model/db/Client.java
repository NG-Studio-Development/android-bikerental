package ru.prokatvros.veloprokat.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

@Table(name = "Client")
public class Client extends Model implements Serializable {

    @Column(name = "Name")
    public String name;

    @Column(name = "Surname")
    public String surname;

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
                .where("Phone LIKE ?", "%" + subNumber +"%")
                .execute();
    }

}

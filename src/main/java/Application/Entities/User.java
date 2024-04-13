package Application.Entities;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password")
    private String password;

//    @Column(name = "permissions")
//    private String permissions;
    public User(){

    }

    public User(String name, String password){
        this.name = name;
        this.password = password;
//        this.permissions = permissions;
    }


    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(String permissions) {
//        this.permissions = permissions;
//    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
//                ", permissions='" + permissions + '\'' +
                '}';
    }
}

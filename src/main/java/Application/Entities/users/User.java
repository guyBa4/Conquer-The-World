package Application.Entities.users;
import Application.Events.EventRecipient;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements EventRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password")
    private String password;

    public User() {
    }
    
    public User(String name, String password){
        this.name = name;
        this.password = password;
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

package Application.Entities.users;
import Application.Events.EventRecipient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.tomcat.util.digester.ArrayStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mobile_users")
public class MobileUser {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "mobile_user_id")
    @JsonIgnore
    private List<MobilePlayer> mobilePlayers;
    public MobileUser() {
    }
    public MobileUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.mobilePlayers = new LinkedList<>();
    }

    public UUID getId() {
        return id;
    }

    public MobileUser setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public MobileUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MobileUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<MobilePlayer> getMobilePlayers() {
        return mobilePlayers;
    }

    public MobileUser setMobilePlayers(List<MobilePlayer> mobilePlayers) {
        this.mobilePlayers = mobilePlayers;
        return this;
    }
}

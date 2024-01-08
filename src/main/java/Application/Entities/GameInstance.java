package Application.Entities;
import jakarta.persistence.*;

@Entity
@Table(name = "game_instances")
public class GameInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private Long Id;


    public GameInstance(){

    }
}

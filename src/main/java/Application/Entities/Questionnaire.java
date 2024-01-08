package Application.Entities;
import jakarta.persistence.*;

@Entity
@Table(name = "questionnaire")
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private Long Id;


    public Questionnaire(){

    }
}

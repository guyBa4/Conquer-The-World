package Application.Entities;
import javax.persistence.*;

@Entity
@Table
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private Long Id;


    public Questionnaire(){

    }
}

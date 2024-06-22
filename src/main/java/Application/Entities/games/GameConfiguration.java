//package Application.Entities;
//
//import jakarta.persistence.*;
//
//import java.util.UUID;
//
//@Entity
//public class GameConfiguration {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
//    @Column(name = "id", nullable = false, unique = true)
//    private UUID id;
//
//    @OneToOne
//    @JoinColumn(name = "game_instance_id", referencedColumnName = "id")
//    private GameInstance gameInstance;
//
//    @Column(name = "can_reconquer_tiles")
//    private boolean canReconquerTiles;              // If true, groups can conquer each other tiles
//
//    @Column(name = "multiple_questions_per_tile")
//    private boolean multipleQuestionsPerTile;       // If true then tiles with difficulty 1-2 will have to be answered
//                                                    // with one question, tiles with difficulty 3-4 will have to be
//                                                    // answered with 2 questions and tiles with difficulty 5 will have
//                                                    // to be answered with 3 questions
//
//    @Column(name = "simultaneous_conquering")
//    private boolean simultaneousConquering;         // If true, players from different groups will be able to attack tiles
//                                                    // at the same time
//}

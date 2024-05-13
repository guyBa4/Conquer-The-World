package Application.Repositories;

import Application.Entities.AssignedQuestion;
import Application.Entities.MobilePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssignedQuestionRepository  extends JpaRepository<AssignedQuestion, UUID> {
}

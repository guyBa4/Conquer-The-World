package Application.Repositories;

import Application.Entities.Answer;
import Application.Entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository  extends JpaRepository<Question, UUID> {

}

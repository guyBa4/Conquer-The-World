package Application.Repositories;

import Application.Entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository  extends JpaRepository<Question, Long> {
}

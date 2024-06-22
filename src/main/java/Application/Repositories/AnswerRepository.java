package Application.Repositories;

import Application.Entities.questions.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    public List<Answer> findByQuestionId(UUID questionId);
}

package Application.DataAccessLayer.Repositories;

import Application.Entities.questions.AssignedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignedQuestionRepository  extends JpaRepository<AssignedQuestion, UUID> {
    @Query("SELECT aq FROM AssignedQuestion aq WHERE aq.questionnaire.id = :questionnaireId AND aq.difficultyLevel = :difficultyLevel")
    List<AssignedQuestion> findByQuestionnaireIdAndDifficultyLevel(@Param("questionnaireId") UUID questionnaireId, @Param("difficultyLevel") int difficultyLevel);

    public List<AssignedQuestion> findByQuestionId(UUID questionId);
    public List<AssignedQuestion> findByQuestionnaireId(UUID questionnaireId);
}

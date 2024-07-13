package Application.DataAccessLayer.Repositories;

import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.QuestionsQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionsQueueRepository extends JpaRepository<QuestionsQueue, UUID> {
    List<QuestionsQueue> findByQuestionsQueueContains(AssignedQuestion assignedQuestion);
}

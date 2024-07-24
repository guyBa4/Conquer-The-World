package UnitTests;

import Application.Entities.games.*;
import Application.Entities.users.*;
import Application.Entities.questions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionsQueueTest {

    @Mock
    private AssignedQuestion mockAssignedQuestion;

    private QuestionsQueue questionsQueue;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        questionsQueue = new QuestionsQueue();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(questionsQueue.getQuestionsQueue());
        assertTrue(questionsQueue.getQuestionsQueue().isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        List<AssignedQuestion> questionList = new LinkedList<>();
        questionList.add(mockAssignedQuestion);

        questionsQueue = new QuestionsQueue(5, questionList);

        assertEquals(5, questionsQueue.getDifficulty());
        assertEquals(1, questionsQueue.getQuestionsQueue().size());
        assertEquals(mockAssignedQuestion, questionsQueue.getQuestionsQueue().get(0));
    }

    @Test
    public void testSetId() {
        UUID uuid = UUID.randomUUID();
        questionsQueue.setId(uuid);
        assertEquals(uuid, questionsQueue.getId());
    }

    @Test
    public void testSetQuestionsQueue() {
        List<AssignedQuestion> questionList = new LinkedList<>();
        questionList.add(mockAssignedQuestion);

        questionsQueue.setQuestionsQueue(questionList);
        assertEquals(1, questionsQueue.getQuestionsQueue().size());
        assertEquals(mockAssignedQuestion, questionsQueue.getQuestionsQueue().get(0));
    }

    @Test
    public void testSetDifficulty() {
        questionsQueue.setDifficulty(10);
        assertEquals(10, questionsQueue.getDifficulty());
    }

    @Test
    public void testGenerateQuestionFromQueue() {
        AssignedQuestion question1 = mock(AssignedQuestion.class);
        AssignedQuestion question2 = mock(AssignedQuestion.class);
        List<AssignedQuestion> questionList = new LinkedList<>();
        questionList.add(question1);
        questionList.add(question2);

        questionsQueue.setQuestionsQueue(questionList);

        AssignedQuestion generatedQuestion = questionsQueue.generateQuestionFromQueue();
        assertTrue(generatedQuestion == question1 || generatedQuestion == question2);
        assertEquals(1, questionsQueue.getQuestionsQueue().size());
        assertFalse(questionsQueue.getQuestionsQueue().contains(generatedQuestion));
    }

    @Test
    public void testShuffleList() {
        List<AssignedQuestion> questionList = new LinkedList<>();
        questionList.add(mockAssignedQuestion);
        List<AssignedQuestion> shuffledList = QuestionsQueue.shuffleList(questionList);
        assertEquals(1, shuffledList.size());
        assertEquals(mockAssignedQuestion, shuffledList.get(0));
    }
}

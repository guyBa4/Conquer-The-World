//package UnitTests;
//
//import Application.Entities.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.sql.Time;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class QuestionnaireTest {
//
//    @Mock
//    private User mockUser;
//
//    @Mock
//    private AssignedQuestion mockAssignedQuestion;
//
//    private Questionnaire questionnaire;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        questionnaire = new Questionnaire();
//    }
//
//    @Test
//    public void testDefaultConstructor() {
//        assertNotNull(questionnaire.getQuestions());
//        assertTrue(questionnaire.getQuestions().isEmpty());
//    }
//
//    @Test
//    public void testParameterizedConstructor() {
//        List<AssignedQuestion> questionList = new LinkedList<>();
//        questionList.add(mockAssignedQuestion);
//
//        questionnaire = new Questionnaire("Sample Questionnaire", questionList, mockUser);
//
//        assertEquals("Sample Questionnaire", questionnaire.getName());
//        assertEquals(1, questionnaire.getQuestions().size());
//        assertEquals(mockUser, questionnaire.getCreator());
//        assertNotNull(questionnaire.getTimeCreated());
//        assertNotNull(questionnaire.getLastUpdated());
//    }
//
//    @Test
//    public void testSetName() {
//        questionnaire.setName("New Name");
//        assertEquals("New Name", questionnaire.getName());
//    }
//
//    @Test
//    public void testSetQuestions() {
//        List<AssignedQuestion> questionList = new LinkedList<>();
//        questionList.add(mockAssignedQuestion);
//
//        questionnaire.setQuestions(questionList);
//        assertEquals(1, questionnaire.getQuestions().size());
//        assertEquals(mockAssignedQuestion, questionnaire.getQuestions().get(0));
//    }
//
//    @Test
//    public void testAddAssignedQuestion() {
//        questionnaire.addAssignedQuestion(mockAssignedQuestion);
//        assertEquals(1, questionnaire.getQuestions().size());
//        assertEquals(mockAssignedQuestion, questionnaire.getQuestions().get(0));
//    }
//
//    @Test
//    public void testAddAssignedQuestionWithParameters() {
//        Question mockQuestion = mock(Question.class);
//        questionnaire.addAssignedQuestion(mockQuestion, 5);
//
//        assertEquals(1, questionnaire.getQuestions().size());
//        AssignedQuestion addedQuestion = questionnaire.getQuestions().get(0);
//        assertEquals(mockQuestion, addedQuestion.getQuestion());
//        assertEquals(5, addedQuestion.getDifficultyLevel());
//    }
//}

package UnitTests;

import Application.Entities.*;
import Application.Repositories.*;
import Application.Response;
import Application.ServiceLayer.QuestionService;
import Application.DataAccessLayer.DALController;
import Application.APILayer.JsonToInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class QuestionServiceTest {

    @Spy
    private RepositoryFactory repositoryFactory = new RepositoryFactory(
            mock(UserRepository.class),
            mock(GameInstanceRepository.class),
            mock(MapRepository.class),
            mock(QuestionRepository.class),
            mock(QuestionnaireRepository.class),
            mock(RunningGameInstanceRepository.class),
            mock(MobilePlayerRepository.class),
            mock(AnswerRepository.class),
            mock(AssignedQuestionRepository.class)
    );

    @Mock
    private DALController dalController;

    @Mock
    private JsonToInstance jsonToInstance;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        questionService.init(repositoryFactory);
        questionService.setDalController(dalController);
    }

    @Test
    public void testAddQuestion() {
        Response<Question> response = questionService.addQuestion("Sample question", true, "Correct Answer",
                Arrays.asList("Wrong Answer 1", "Wrong Answer 2"), Arrays.asList("Tag1", "Tag2"), 5, null);

        assertTrue(response.isSuccessful(), "Expected response to be successful");
        assertNotNull(response.getValue(), "Expected response value to be non-null");
        verify(repositoryFactory.questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    public void testAddQuestionWithIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Invalid Argument")).when(repositoryFactory.questionRepository).save(any(Question.class));

        Response<Question> response = questionService.addQuestion("Sample question", true, "Correct Answer",
                Arrays.asList("Wrong Answer 1", "Wrong Answer 2"), Arrays.asList("Tag1", "Tag2"), 5, null);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(403, response.getStatus(), "Expected status to be 403");
        assertEquals("Invalid Argument", response.getMessage(), "Expected message to be 'Invalid Argument'");
    }


    @Test
    public void testGetQuestionsByQuestionnaireId() {
        UUID questionnaireId = UUID.randomUUID();
        Questionnaire mockQuestionnaire = mock(Questionnaire.class);
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();

        when(dalController.getQuestionnaire(questionnaireId)).thenReturn(mockQuestionnaire);
        when(mockQuestionnaire.getQuestions()).thenReturn(assignedQuestions);

        Response<List<AssignedQuestion>> response = questionService.getQuestionsByQuestionnaireId(questionnaireId);

        assertTrue(response.isSuccessful(), "Expected response to be successful");
        assertEquals(assignedQuestions, response.getValue(), "Expected response value to be the assigned questions list");
    }

    @Test
    public void testGetQuestionsByQuestionnaireIdWithException() {
        UUID questionnaireId = UUID.randomUUID();

        when(dalController.getQuestionnaire(questionnaireId)).thenThrow(new IllegalArgumentException("Invalid ID"));

        Response<List<AssignedQuestion>> response = questionService.getQuestionsByQuestionnaireId(questionnaireId);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(403, response.getStatus(), "Expected status to be 403");
        assertEquals("Invalid ID", response.getMessage(), "Expected message to be 'Invalid ID'");
    }

    @Test
    public void testAddQuestionnaire() {
        Map<String, Integer> questionsIdsToDifficulty = new HashMap<>();
        UUID questionUuid = UUID.randomUUID();
        questionsIdsToDifficulty.put(questionUuid.toString(), 1);

        User mockUser = mock(User.class);
        when(dalController.getUser(any(UUID.class))).thenReturn(mockUser);

        Question mockQuestion = mock(Question.class);
        when(dalController.getQuestion(questionUuid)).thenReturn(mockQuestion);

        Response<Questionnaire> response = questionService.addQuestionnaire("Sample Questionnaire", UUID.randomUUID().toString(), questionsIdsToDifficulty);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertNotNull(response.getValue(), "Expected response value to be non-null");
        verify(repositoryFactory.questionnaireRepository, times(1)).save(any(Questionnaire.class));
    }

    @Test
    public void testFilterQuestions() {
        Page<Question> mockPage = mock(Page.class);
        when(repositoryFactory.questionRepository.findByFilters(anyString(), anyInt(), any(PageRequest.class))).thenReturn(mockPage);

        Response<Page<Question>> response = questionService.filterQuestions(0, 10, "sample", Arrays.asList("Tag1"), 5);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockPage, response.getValue(), "Expected response value to be the mock page");
    }

    @Test
    public void testFilterQuestionsWithDifferentParameters() {
        Page<Question> mockPage = mock(Page.class);
        when(repositoryFactory.questionRepository.findByFilters(anyString(), isNull(), any(PageRequest.class))).thenReturn(mockPage);

        Response<Page<Question>> response = questionService.filterQuestions(0, 10, "sample", Arrays.asList("Tag1"), null);

        assertTrue(response.isSuccessful(), "Expected response to be successful");
        assertEquals(mockPage, response.getValue(), "Expected response value to be the mock page");
    }

    @Test
    public void testDeleteQuestion() {
        UUID questionId = UUID.randomUUID();
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();

        when(repositoryFactory.assignedQuestionRepository.findByQuestionId(questionId)).thenReturn(assignedQuestions);

        Response<Boolean> response = questionService.deleteQuestion(questionId);

        assertTrue(response.isSuccessful(), "Expected response to be successful");
        assertTrue(response.getValue(), "Expected response value to be true");
        verify(repositoryFactory.assignedQuestionRepository, times(1)).deleteAll(assignedQuestions);
        verify(repositoryFactory.questionRepository, times(1)).deleteById(questionId);
    }

    @Test
    public void testDeleteQuestionWithInvalidId() {
        UUID questionId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Invalid ID")).when(repositoryFactory.questionRepository).deleteById(questionId);

        Response<Boolean> response = questionService.deleteQuestion(questionId);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful with invalid ID");
        assertEquals(403, response.getStatus(), "Expected status to be 403 with invalid ID");
    }

    @Test
    public void testDeleteQuestionnaire() {
        UUID questionnaireId = UUID.randomUUID();
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();
        List<GameInstance> gameInstances = new LinkedList<>();

        when(repositoryFactory.assignedQuestionRepository.findByQuestionnaireId(questionnaireId)).thenReturn(assignedQuestions);
        when(repositoryFactory.gameInstanceRepository.findByQuestionnaireId(questionnaireId)).thenReturn(gameInstances);

        Response<Boolean> response = questionService.deleteQuestionnaire(questionnaireId);

        assertTrue(response.isSuccessful(), "Expected response to be successful");
        assertTrue(response.getValue(), "Expected response value to be true");
        verify(repositoryFactory.assignedQuestionRepository, times(1)).deleteAll(assignedQuestions);
        verify(repositoryFactory.questionnaireRepository, times(1)).deleteById(questionnaireId);
    }

    @Test
    public void testDeleteQuestionnaireWithInvalidId() {
        UUID questionnaireId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Invalid ID")).when(repositoryFactory.questionnaireRepository).deleteById(questionnaireId);

        Response<Boolean> response = questionService.deleteQuestionnaire(questionnaireId);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful with invalid ID");
        assertEquals(403, response.getStatus(), "Expected status to be 403 with invalid ID");
    }
}

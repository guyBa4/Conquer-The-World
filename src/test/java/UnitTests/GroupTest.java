package UnitTests;

import Application.Entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupTest {

    @Mock
    private RunningGameInstance mockRunningGameInstance;

    @Mock
    private MobilePlayer mockMobilePlayer;

    @Mock
    private QuestionsQueue mockQuestionsQueue;

    private Group group;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new Group(1, mockRunningGameInstance);
    }

    @Test
    public void testDefaultConstructor() {
        Group defaultGroup = new Group();
        assertNull(defaultGroup.getId());
        assertNull(defaultGroup.getRunningGameInstance());
        assertNull(defaultGroup.getMobilePlayers());
        assertNull(defaultGroup.getQuestionsQueues());
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, group.getNumber());
        assertEquals(0, group.getScore());
        assertNotNull(group.getMobilePlayers());
        assertEquals(mockRunningGameInstance, group.getRunningGameInstance());
        assertNotNull(group.getQuestionsQueues());
    }

    @Test
    public void testSetId() {
        UUID uuid = UUID.randomUUID();
        group.setId(uuid);
        assertEquals(uuid, group.getId());
    }

    @Test
    public void testSetAndGetRunningGameInstance() {
        group.setRunningGameInstance(mockRunningGameInstance);
        assertEquals(mockRunningGameInstance, group.getRunningGameInstance());
    }

    @Test
    public void testSetAndGetMobilePlayers() {
        List<MobilePlayer> mobilePlayers = new LinkedList<>();
        mobilePlayers.add(mockMobilePlayer);
        group.setMobilePlayers(mobilePlayers);
        assertEquals(1, group.getMobilePlayers().size());
        assertEquals(mockMobilePlayer, group.getMobilePlayers().get(0));
    }

    @Test
    public void testAddMobilePlayer() {
        group.addMobilePlayer(mockMobilePlayer);
        assertEquals(1, group.getMobilePlayers().size());
        assertEquals(mockMobilePlayer, group.getMobilePlayers().get(0));
    }

    @Test
    public void testAddMobilePlayerWithException() {
        Group zeroGroup = new Group(0, mockRunningGameInstance);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            zeroGroup.addMobilePlayer(mockMobilePlayer);
        });
        assertEquals("group number 0 cabbot conatain mobile users!", exception.getMessage());
    }

    @Test
    public void testSetAndGetNumber() {
        group.setNumber(2);
        assertEquals(2, group.getNumber());
    }

    @Test
    public void testSetAndGetScore() {
        group.setScore(10);
        assertEquals(10, group.getScore());
    }

    @Test
    public void testAddScore() {
        group.addScore(5);
        assertEquals(5, group.getScore());
        group.addScore(10);
        assertEquals(15, group.getScore());
    }

    @Test
    public void testSetAndGetQuestionsQueues() {
        List<QuestionsQueue> questionsQueues = new LinkedList<>();
        questionsQueues.add(mockQuestionsQueue);
        group.setQuestionsQueues(questionsQueues);
        assertEquals(1, group.getQuestionsQueues().size());
        assertEquals(mockQuestionsQueue, group.getQuestionsQueues().get(0));
    }

    @Test
    public void testAddQuestionQueue() {
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();
        group.addQuestionQueue(5, assignedQuestions);
        assertEquals(1, group.getQuestionsQueues().size());
        assertEquals(5, group.getQuestionsQueues().get(0).getDifficulty());
    }

    @Test
    public void testGetSize() {
        assertEquals(0, group.getSize());
        group.addMobilePlayer(mockMobilePlayer);
        assertEquals(1, group.getSize());
    }

    @Test
    public void testGenerateQuestionFromQueue() {
        AssignedQuestion mockAssignedQuestion = mock(AssignedQuestion.class);
        when(mockQuestionsQueue.getDifficulty()).thenReturn(5);
        when(mockQuestionsQueue.generateQuestionFromQueue()).thenReturn(mockAssignedQuestion);
        group.addQuestionQueue(5, new LinkedList<>());
        group.getQuestionsQueues().set(0, mockQuestionsQueue);

        AssignedQuestion result = group.generateQuestionFromQueue(5);
        assertEquals(mockAssignedQuestion, result);
    }

    @Test
    public void testGenerateQuestionFromQueueWithNoMatchingDifficulty() {
        AssignedQuestion result = group.generateQuestionFromQueue(10);
        assertNull(result);
    }
}

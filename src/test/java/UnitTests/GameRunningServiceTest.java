package UnitTests;

import Application.DataAccessLayer.DALController;
import Application.Entities.*;
import Application.Enums.GameStatus;
import Application.Events.*;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.RunningGameInstanceRepository;
import Application.Repositories.*;
import Application.Response;
import Application.ServiceLayer.EventService;
import Application.ServiceLayer.GameRunningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameRunningServiceTest {

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
    private EventService eventService;

    @InjectMocks
    private GameRunningService gameRunningService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameRunningService.setDalController(dalController);
    }

    @Test
    public void testOpenWaitingRoom() {
        UUID gameId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();
        GameInstance mockGameInstance = mock(GameInstance.class);

        when(dalController.getGameInstance(gameId)).thenReturn(mockGameInstance);

        Response<RunningGameInstance> response = gameRunningService.OpenWaitingRoom(gameId, hostId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertNotNull(response.getValue(), "Expected response value to be non-null");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(any(RunningGameInstance.class));
        verify(eventService, times(1)).addEmitter(hostId);
    }

    @Test
    public void testOpenWaitingRoomWithInvalidGameId() {
        UUID gameId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();

        when(dalController.getGameInstance(gameId)).thenThrow(new IllegalArgumentException("Invalid Game ID"));

        Response<RunningGameInstance> response = gameRunningService.OpenWaitingRoom(gameId, hostId);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(403, response.getStatus(), "Expected status to be 403");
        assertEquals("Invalid Game ID", response.getMessage(), "Expected message to be 'Invalid Game ID'");
    }

    @Test
    public void testEnterGameWithCode() {
        String gameCode = "testCode";
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(repositoryFactory.runningGameInstanceRepository.findByCode(gameCode)).thenReturn(Collections.singletonList(mockRunningGameInstance));

        Response<MobilePlayer> response = gameRunningService.enterGameWithCode(gameCode);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertNotNull(response.getValue(), "Expected response value to be non-null");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(mockRunningGameInstance);
    }

    @Test
    public void testEnterGameWithInvalidCode() {
        String gameCode = "invalidCode";

        when(repositoryFactory.runningGameInstanceRepository.findByCode(gameCode)).thenReturn(Collections.emptyList());

        Response<MobilePlayer> response = gameRunningService.enterGameWithCode(gameCode);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals("game code not valid", response.getMessage(), "Expected message to be 'game code not valid'");
    }

    @Test
    public void testAddMobileDetails() {
        UUID mobileId = UUID.randomUUID();
        String name = "Player";
        MobilePlayer mockMobilePlayer = mock(MobilePlayer.class);
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(dalController.getMobilePlayer(mobileId)).thenReturn(mockMobilePlayer);
        when(mockMobilePlayer.getRunningGameInstance()).thenReturn(mockRunningGameInstance);

        Response<RunningGameInstance> response = gameRunningService.addMobileDetails(mobileId, name);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockRunningGameInstance, response.getValue(), "Expected response value to be the running game instance");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(mockRunningGameInstance);
    }

    @Test
    public void testAddMobileDetailsWithInvalidName() {
        UUID mobileId = UUID.randomUUID();
        String name = "P";

        Response<RunningGameInstance> response = gameRunningService.addMobileDetails(mobileId, name);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(403, response.getStatus(), "Expected status to be 403");
        assertEquals("Mobile player name must be longer than 2 characters.", response.getMessage(), "Expected message to be 'Mobile player name must be longer than 2 characters.'");
    }

    @Test
    public void testGetWaitingRoomDetails() {
        UUID runningGameId = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);

        Response<RunningGameInstance> response = gameRunningService.getWaitingRoomDetails(runningGameId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockRunningGameInstance, response.getValue(), "Expected response value to be the running game instance");
    }

    @Test
    public void testStartGame() {
        UUID runningGameId = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);

        Response<RunningGameInstance> response = gameRunningService.startGame(runningGameId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockRunningGameInstance, response.getValue(), "Expected response value to be the running game instance");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(mockRunningGameInstance);
    }

    @Test
    public void testStartGameWithInvalidId() {
        UUID runningGameId = UUID.randomUUID();

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(null);

        Response<RunningGameInstance> response = gameRunningService.startGame(runningGameId);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(400, response.getStatus(), "Expected status to be 400");
        assertEquals("Game not found", response.getMessage(), "Expected message to be 'Game not found'");
    }

    @Test
    public void testGetRunningGame() {
        UUID runningGameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(repositoryFactory.runningGameInstanceRepository.findByRunningIdAndMobilePlayers_id(runningGameId, userId)).thenReturn(Collections.singletonList(mockRunningGameInstance));

        Response<RunningGameInstance> response = gameRunningService.getRunningGame(runningGameId, userId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockRunningGameInstance, response.getValue(), "Expected response value to be the running game instance");
    }

    @Test
    public void testGetRunningTiles() {
        UUID runningGameId = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);
        List<RunningTile> mockTiles = Arrays.asList(mock(RunningTile.class), mock(RunningTile.class));

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);
        when(mockRunningGameInstance.getTiles()).thenReturn(mockTiles);

        Response<List<RunningTile>> response = gameRunningService.getRunningTiles(runningGameId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockTiles, response.getValue(), "Expected response value to be the list of running tiles");
    }

    @Test
    public void testGetQuestion() {
        UUID runningTileId = UUID.randomUUID();
        int group = 1;
        UUID runningGameId = UUID.randomUUID();
        UUID authorizationToken = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);
        MobilePlayer mockMobilePlayer = mock(MobilePlayer.class);
        Group mockGroup = mock(Group.class);
        AssignedQuestion mockAssignedQuestion = mock(AssignedQuestion.class);

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);
        when(mockRunningGameInstance.getPlayer(authorizationToken)).thenReturn(mockMobilePlayer);
        when(mockMobilePlayer.getGroup()).thenReturn(mockGroup);
        when(mockGroup.getNumber()).thenReturn(group);
        when(mockRunningGameInstance.getGroupByNumber(group)).thenReturn(mockGroup);
        when(mockRunningGameInstance.getQuestion(runningTileId, group, mockMobilePlayer)).thenReturn(mockAssignedQuestion);

        Response<AssignedQuestion> response = gameRunningService.getQuestion(runningTileId, group, runningGameId, authorizationToken);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockAssignedQuestion, response.getValue(), "Expected response value to be the assigned question");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(mockRunningGameInstance);
    }

    @Test
    public void testCheckAnswer() throws IOException {
        UUID runningGameId = UUID.randomUUID();
        UUID tileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String answer = "testAnswer";
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);
        MobilePlayer mockMobilePlayer = mock(MobilePlayer.class);

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);
        when(mockRunningGameInstance.getPlayer(userId)).thenReturn(mockMobilePlayer);
        when(mockRunningGameInstance.checkAnswer(tileId, mockMobilePlayer, questionId, answer, repositoryFactory.answerRepository)).thenReturn(true);

        Response<Boolean> response = gameRunningService.checkAnswer(runningGameId, tileId, userId, questionId, answer);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertTrue(response.getValue(), "Expected response value to be true");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).save(mockRunningGameInstance);
        verify(gameRunningService, times(1)).publishEvent(any(), any(), any());
    }

    @Test
    public void testEndRunningGame() {
        UUID runningGameId = UUID.randomUUID();
        RunningGameInstance mockRunningGameInstance = mock(RunningGameInstance.class);

        when(dalController.getRunningGameInstance(runningGameId)).thenReturn(mockRunningGameInstance);

        Response<Boolean> response = gameRunningService.endRunningGame(runningGameId);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertTrue(response.getValue(), "Expected response value to be true");
        verify(repositoryFactory.runningGameInstanceRepository, times(1)).delete(mockRunningGameInstance);
        verify(gameRunningService, times(1)).publishEvent(any(), any(), any());
    }
}

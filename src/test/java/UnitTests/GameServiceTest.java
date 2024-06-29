//package UnitTests;
//
//import Application.Entities.*;
//import Application.DataAccessLayer.Repositories.*;
//import Application.Response;
//import Application.ServiceLayer.GameService;
//import Application.ServiceLayer.GameRunningService;
//import Application.DataAccessLayer.DALController;
//import Application.APILayer.JsonToInstance;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class GameServiceTest {
//
//    @Spy
//    private RepositoryFactory repositoryFactory = new RepositoryFactory(
//            mock(UserRepository.class),
//            mock(GameInstanceRepository.class),
//            mock(MapRepository.class),
//            mock(QuestionRepository.class),
//            mock(QuestionnaireRepository.class),
//            mock(RunningGameInstanceRepository.class),
//            mock(MobilePlayerRepository.class),
//            mock(AnswerRepository.class),
//            mock(AssignedQuestionRepository.class)
//    );
//
//    @Mock
//    private GameRunningService gameRunningService;
//
//    @Mock
//    private DALController dalController;
//
//    @Mock
//    private JsonToInstance jsonToInstance;
//
//    @InjectMocks
//    private GameService gameService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        gameService.init(repositoryFactory, gameRunningService);
//        gameService.setDalController(dalController);
//    }
//
//    @Test
//    public void testAddGameInstance() {
//        UUID questionnaireUuid = UUID.randomUUID();
//        UUID mapUuid = UUID.randomUUID();
//        UUID creatorUuid = UUID.randomUUID();
//
//        Questionnaire mockQuestionnaire = mock(Questionnaire.class);
//        GameMap mockGameMap = mock(GameMap.class);
//        User mockUser = mock(User.class);
//
//        when(dalController.getQuestionnaire(questionnaireUuid)).thenReturn(mockQuestionnaire);
//        when(dalController.getMap(mapUuid)).thenReturn(mockGameMap);
//        when(dalController.getUser(creatorUuid)).thenReturn(mockUser);
//
//        Response<GameInstance> response = gameService.addGameInstance("Test Game", "Test Description", questionnaireUuid, mapUuid, creatorUuid, 2, 60, true, 30, startingPositions);
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertNotNull(response.getValue(), "Expected response value to be non-null");
//        verify(repositoryFactory.gameInstanceRepository, times(1)).save(any(GameInstance.class));
//    }
//
//    @Test
//    public void testAddGameInstanceWithIllegalArgumentException() {
//        UUID questionnaireUuid = UUID.randomUUID();
//        UUID mapUuid = UUID.randomUUID();
//        UUID creatorUuid = UUID.randomUUID();
//
//        when(dalController.getQuestionnaire(questionnaireUuid)).thenThrow(new IllegalArgumentException("Invalid Questionnaire"));
//
//        Response<GameInstance> response = gameService.addGameInstance("Test Game", "Test Description", questionnaireUuid, mapUuid, creatorUuid, 2, 60, true, 30, startingPositions);
//
//        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
//        assertEquals(403, response.getStatus(), "Expected status to be 403");
//        assertEquals("Invalid Questionnaire", response.getMessage(), "Expected message to be 'Invalid Questionnaire'");
//    }
//
//    @Test
//    public void testGetGameInstance() {
//        UUID gameInstanceUuid = UUID.randomUUID();
//        GameInstance mockGameInstance = mock(GameInstance.class);
//
//        when(dalController.getGameInstance(gameInstanceUuid)).thenReturn(mockGameInstance);
//
//        Response<GameInstance> response = gameService.getGameInstance(gameInstanceUuid);
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertEquals(mockGameInstance, response.getValue(), "Expected response value to be the mock game instance");
//    }
//
//    @Test
//    public void testGetAllGameInstances() {
//        List<GameInstance> mockGameInstances = Arrays.asList(mock(GameInstance.class), mock(GameInstance.class));
//
//        when(repositoryFactory.gameInstanceRepository.findAll()).thenReturn(mockGameInstances);
//
//        Response<List<GameInstance>> response = gameService.getAllGameInstance();
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertEquals(mockGameInstances, response.getValue(), "Expected response value to be the list of mock game instances");
//    }
//
//    @Test
//    public void testGetMaps() {
//        Page<GameMap> mockPage = mock(Page.class);
//        when(repositoryFactory.mapRepository.findBy(any(PageRequest.class))).thenReturn(mockPage);
//
//        Response<Page<GameMap>> response = gameService.getMaps(0, 10, null);
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertEquals(mockPage, response.getValue(), "Expected response value to be the mock page");
//    }
//
//    @Test
//    public void testGetMapsWithName() {
//        Page<GameMap> mockPage = mock(Page.class);
//        when(repositoryFactory.mapRepository.findByName(anyString(), any(PageRequest.class))).thenReturn(mockPage);
//
//        Response<Page<GameMap>> response = gameService.getMaps(0, 10, "Test Map");
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertEquals(mockPage, response.getValue(), "Expected response value to be the mock page");
//    }
//
//    @Test
//    public void testDeleteGame() {
//        UUID gameId = UUID.randomUUID();
//        List<RunningGameInstance> runningGameInstances = Arrays.asList(mock(RunningGameInstance.class), mock(RunningGameInstance.class));
//
//        when(repositoryFactory.runningGameInstanceRepository.findByGameInstance_Id(gameId)).thenReturn(runningGameInstances);
//
//        Response<Boolean> response = gameService.deleteGame(gameId);
//
//        assertTrue(response.isSuccessful(), response.getMessage());
//        assertTrue(response.getValue(), "Expected response value to be true");
//        verify(repositoryFactory.runningGameInstanceRepository, times(1)).deleteAll(runningGameInstances);
//        verify(repositoryFactory.gameInstanceRepository, times(1)).deleteById(gameId);
//    }
//
//    @Test
//    public void testDeleteGameWithInvalidId() {
//        UUID gameId = UUID.randomUUID();
//
//        doThrow(new IllegalArgumentException("Invalid ID")).when(repositoryFactory.gameInstanceRepository).deleteById(gameId);
//
//        Response<Boolean> response = gameService.deleteGame(gameId);
//
//        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful with invalid ID");
//        assertEquals(403, response.getStatus(), "Expected status to be 403 with invalid ID");
//    }
//}

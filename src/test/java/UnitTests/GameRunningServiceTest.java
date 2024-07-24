package UnitTests;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Application.DataAccessLayer.DALController;
import Application.DataAccessLayer.Repositories.*;
import Application.Entities.games.*;
import Application.Entities.users.*;
import Application.Entities.questions.*;
import Application.Enums.GameStatus;
import Application.Response;
import Application.ServiceLayer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class GameRunningServiceTest {

    @InjectMocks
    private GameRunningService gameRunningService;

    @Mock
    private RepositoryFactory repositoryFactory;

    @Mock
    private RunningGameInstanceRepository runningGameInstanceRepository;

    @Mock
    private MobilePlayerRepository mobilePlayerRepository;

    @Mock
    private MobileUserRepository mobileUserRepository;

    @Mock
    private DALController dalController;

    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameRunningService.setDalController(dalController);
    }


    @Test
    void testOpenWaitingRoomFailure() {
        UUID gameId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();

        when(dalController.getGameInstance(gameId)).thenThrow(new IllegalArgumentException("Game instance not found"));

        Response<RunningGameInstance> response = gameRunningService.OpenWaitingRoom(gameId, hostId);

        assertFalse(response.isSuccessful());
        assertEquals(403, response.getStatus());
        assertEquals("Game instance not found", response.getMessage());
    }

    @Test
    void testEnterGameWithCodeSuccess() {
        String gameCode = "testCode";
        String mobileUserId = UUID.randomUUID().toString();
        RunningGameInstance runningGameInstance = mock(RunningGameInstance.class);
        MobileUser mobileUser = mock(MobileUser.class);
        MobilePlayer mobilePlayer = mock(MobilePlayer.class);

        when(runningGameInstanceRepository.findByCode(gameCode)).thenReturn(Collections.singletonList(runningGameInstance));
        when(mobileUserRepository.findById(UUID.fromString(mobileUserId))).thenReturn(Optional.of(mobileUser));
//        when(runningGameInstance.addMobilePlayer(any(MobilePlayer.class))).thenReturn(void);

        Response<MobilePlayer> response = gameRunningService.enterGameWithCode(gameCode, mobileUserId);
    }

    @Test
    void testEnterGameWithCodeFailure() {
        String gameCode = "testCode";
        String mobileUserId = UUID.randomUUID().toString();

        when(runningGameInstanceRepository.findByCode(gameCode)).thenReturn(Collections.emptyList());

        Response<MobilePlayer> response = gameRunningService.enterGameWithCode(gameCode, mobileUserId);

        assertFalse(response.isSuccessful());
        verify(mobileUserRepository, never()).findById(UUID.fromString(mobileUserId));
    }

    @Test
    void testAddMobileDetailsSuccess() {
        UUID mobileId = UUID.randomUUID();
        String name = "PlayerName";
        MobilePlayer mobilePlayer = mock(MobilePlayer.class);
        RunningGameInstance runningGameInstance = mock(RunningGameInstance.class);

        when(dalController.getMobilePlayer(mobileId)).thenReturn(mobilePlayer);
        when(mobilePlayer.getRunningGameInstance()).thenReturn(runningGameInstance);
        when(runningGameInstanceRepository.save(runningGameInstance)).thenReturn(runningGameInstance);

        Response<RunningGameInstance> response = gameRunningService.addMobileDetails(mobileId, name);

    }

    @Test
    void testAddMobileDetailsFailure() {
        UUID mobileId = UUID.randomUUID();
        String name = "P";

        Response<RunningGameInstance> response = gameRunningService.addMobileDetails(mobileId, name);

        assertFalse(response.isSuccessful());
        assertEquals("Mobile player name must be longer than 2 characters.", response.getMessage());
        verify(dalController, never()).getMobilePlayer(mobileId);
        verify(runningGameInstanceRepository, never()).save(any(RunningGameInstance.class));
    }
}

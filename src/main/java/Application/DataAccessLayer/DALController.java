package Application.DataAccessLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.games.GameInstance;
import Application.Entities.games.GameMap;
import Application.Entities.games.RunningGameInstance;
import Application.Entities.games.RunningTile;
import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.Question;
import Application.Entities.questions.Questionnaire;
import Application.Entities.users.MobilePlayer;
import Application.Entities.users.MobileUser;
import Application.Entities.users.PlayerStatistic;
import Application.Entities.users.User;
import Application.DataAccessLayer.Repositories.RepositoryFactory;

import java.util.Optional;
import java.util.UUID;

public class DALController {
    private static DALController instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;


    private DALController() {
    }

    public static DALController getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new DALController();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
    }

    public Questionnaire getQuestionnaire(UUID uuid){
        Optional<Questionnaire> optional = repositoryFactory.questionnaireRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no questionnaire with this UUID : " + uuid);
        Questionnaire object = optional.get();
        return object;
    }

    public Question getQuestion(UUID uuid){
        Optional<Question> optional = repositoryFactory.questionRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no question with this UUID : " + uuid);
        Question object = optional.get();
        return object;
    }

    public User getUser(UUID uuid){
        Optional<User> optional = repositoryFactory.userRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no user with this UUID : " + uuid);
        User object = optional.get();
        return object;
    }

    public MobileUser getMobileUser(UUID uuid){
        Optional<MobileUser> optional = repositoryFactory.mobileUserRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no MobileUser with this UUID : " + uuid);
        MobileUser object = optional.get();
        return object;
    }

    public GameInstance getGameInstance(UUID uuid){
        Optional<GameInstance> optional = repositoryFactory.gameInstanceRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no GameInstance with this UUID : " + uuid);
        GameInstance object = optional.get();
        return object;
    }

    public RunningGameInstance getRunningGameInstance(UUID uuid){
        Optional<RunningGameInstance> optional = repositoryFactory.runningGameInstanceRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no running game instance with this UUID : " + uuid);
        RunningGameInstance object = optional.get();
        return object;
    }

    public MobilePlayer getMobilePlayer(UUID uuid){
        Optional<MobilePlayer> optional = repositoryFactory.mobilePlayerRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no MobilePlayer with this UUID : " + uuid);
        MobilePlayer object = optional.get();
        return object;
    }

    public AssignedQuestion getAssignedQuestion(UUID uuid){
        Optional<AssignedQuestion> optional = repositoryFactory.assignedQuestionRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no AssignedQuestion with this UUID : " + uuid);
        AssignedQuestion object = optional.get();
        return object;
    }

    public GameMap getMap(UUID uuid){
        Optional<GameMap> optional = repositoryFactory.mapRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no Map with this UUID : " + uuid);
        GameMap object = optional.get();
        return object;
    }

    public PlayerStatistic getPlayerStatistic(UUID uuid){
        Optional<PlayerStatistic> optional = repositoryFactory.playerStatisticRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no PlayerStatistic with this UUID : " + uuid);
        PlayerStatistic object = optional.get();
        return object;
    }

    public RunningTile getRunningTile(UUID uuid) {
        Optional<RunningTile> optional = repositoryFactory.runningTileRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no RunningTile with this UUID : " + uuid);
        RunningTile object = optional.get();
        return object;
    }

    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    public DALController setRepositoryFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        return this;
    }


    public boolean needToInitiate() {
        return repositoryFactory == null;
    }

}

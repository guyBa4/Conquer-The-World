package Application.DataAccessLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Repositories.QuestionRepository;
import Application.Repositories.QuestionnaireRepository;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.QuestionService;

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

    public Map getMap(UUID uuid){
        Optional<Map> optional = repositoryFactory.mapRepository.findById(uuid);
        if (optional.isEmpty())
            throw new IllegalArgumentException("there is no Map with this UUID : " + uuid);
        Map object = optional.get();
        return object;
    }




}

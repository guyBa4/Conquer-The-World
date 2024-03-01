package Application.APILayer;

import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenHandler {
    private static TokenHandler instance = null;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;
    Map<UUID, UUID> mobileUserToGame;

    private TokenHandler() {
    }

    public static TokenHandler getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new TokenHandler();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        mobileUserToGame = new HashMap<>();
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        userRepository = repositoryFactory.userRepository;
    }

    public void verifyWebUserToken(String token) throws IllegalArgumentException{
        UUID uuid = UUID.fromString(token);
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("UUID not exists");
        }
    }

    public UUID verifyMobileUserToken(String token) throws IllegalArgumentException{
        UUID uuid = UUID.fromString(token);
        UUID game =mobileUserToGame.get(uuid);
        if (game != null)
            return game;
        throw new IllegalArgumentException("UUID not exists");
    }

    public void addMobileUserToken(String mobileToken, String gameToken) throws IllegalArgumentException{
        UUID mobileUUID = UUID.fromString(mobileToken);
        UUID gameUUID = UUID.fromString(gameToken);
        mobileUserToGame.putIfAbsent(mobileUUID, gameUUID);
    }
}

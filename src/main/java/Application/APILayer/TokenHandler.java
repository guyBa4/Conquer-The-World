package Application.APILayer;

import Application.DataAccessLayer.Repositories.MobilePlayerRepository;
import Application.DataAccessLayer.Repositories.RepositoryFactory;
import Application.DataAccessLayer.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenHandler {
    private static TokenHandler instance = null;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;
    private MobilePlayerRepository mobilePlayerRepository;

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
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.userRepository = repositoryFactory.userRepository;
        this.mobilePlayerRepository = repositoryFactory.mobilePlayerRepository;
    }

    public void verifyWebUserToken(String token) throws IllegalArgumentException{
        UUID uuid = UUID.fromString(token);
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("UUID not exists");
        }
    }
    public void verifyAnyToken(String token) throws IllegalArgumentException{
        UUID uuid = UUID.fromString(token);
        if (userRepository.existsById(uuid)) {
            return;
        }
        if (mobilePlayerRepository.existsById(uuid)) {
            return;
        }
        throw new IllegalArgumentException("UUID not exists");
    }

}

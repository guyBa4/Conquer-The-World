package Application.APILayer.controllers;

import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenHandler {
    private static TokenHandler instance = null;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;

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
        userRepository = repositoryFactory.userRepository;
    }

    public void verifyToken(String token){
        UUID uuid = UUID.fromString(token);
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("UUID not exists");
        }    }
}

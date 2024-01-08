package Application.Repositories;

import Application.Entities.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class RepositoryFactory {
    public final UserRepository userRepository;
    public final GameInstanceRepository gameInstanceRepository;
    public final MapRepository mapRepository;
    public final QuestionRepository questionRepository;
    public final QuestionnaireRepository questionnaireRepository;
    public final TileRepository tileRepository;


    @Autowired
    public RepositoryFactory(UserRepository userRepository, GameInstanceRepository gameInstanceRepository, MapRepository mapRepository, QuestionRepository questionRepository, QuestionnaireRepository questionnaireRepository, TileRepository tileRepository){
        this.gameInstanceRepository = gameInstanceRepository;
        this.tileRepository = tileRepository;
        this.userRepository = userRepository;
        this.mapRepository = mapRepository;
        this.questionRepository = questionRepository;
        this.questionnaireRepository = questionnaireRepository;

    }
}


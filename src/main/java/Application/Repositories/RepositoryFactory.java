package Application.Repositories;

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
    public final RunningGameInstanceRepository runningGameInstanceRepository;
    public final MobilePlayerRepository mobilePlayerRepository;
    public final AnswerRepository answerRepository;
    public final AssignedQuestionRepository assignedQuestionRepository;
    public final PlayerStatisticRepository playerStatisticRepository;
    public final GameStatisticRepository gameStatisticRepository;
    public final RunningTileRepository runningTileRepository;


    @Autowired
    public RepositoryFactory(UserRepository userRepository,
                             GameInstanceRepository gameInstanceRepository,
                             MapRepository mapRepository,
                             QuestionRepository questionRepository,
                             QuestionnaireRepository questionnaireRepository,
                             RunningGameInstanceRepository runningGameInstanceRepository,
                             MobilePlayerRepository mobilePlayerRepository,
                             AnswerRepository answerRepository,
                             AssignedQuestionRepository assignedQuestionRepository,
                             PlayerStatisticRepository playerStatisticRepository, GameStatisticRepository gameStatisticRepository, RunningTileRepository runningTileRepository){
        this.gameInstanceRepository = gameInstanceRepository;
        this.userRepository = userRepository;
        this.mapRepository = mapRepository;
        this.questionRepository = questionRepository;
        this.questionnaireRepository = questionnaireRepository;
        this.runningGameInstanceRepository = runningGameInstanceRepository;
        this.mobilePlayerRepository = mobilePlayerRepository;
        this.answerRepository = answerRepository;
        this.assignedQuestionRepository = assignedQuestionRepository;
        this.playerStatisticRepository = playerStatisticRepository;
        this.gameStatisticRepository = gameStatisticRepository;
        this.runningTileRepository = runningTileRepository;
    }
}


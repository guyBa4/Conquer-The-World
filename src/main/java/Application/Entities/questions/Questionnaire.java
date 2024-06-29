package Application.Entities.questions;
import Application.Entities.users.User;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "questionnaires")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User creator;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id")
    private List<AssignedQuestion> questions;

    @Column(name = "time_created")
    private Time timeCreated;

    @Column(name = "last_updated")
    private Time lastUpdated;

    @Column(name = "shared")
    private boolean shared;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "questionnaire_tags", joinColumns = @JoinColumn(name = "questionnaire_id"))
    @Column(name = "tag")
    private Set<String> tags;

    public Questionnaire(){
        questions = new LinkedList<>();
    }

    public Questionnaire(String name, List<AssignedQuestion> questionList, User creator) {
        this.name = name;
        this.questions = questionList;
        this.timeCreated = new Time(new Date().getTime());
        this.lastUpdated = new Time(new Date().getTime());
        this.creator = creator;
    }
    public Questionnaire(String name, User creator) {
        this.name = name;
        this.timeCreated = new Time(new Date().getTime());
        this.lastUpdated = new Time(new Date().getTime());
        this.creator = creator;
    }

    public Questionnaire(String name, List<AssignedQuestion> questionList, User creator, Set<String> stringSet) {
        this.name = name;
        this.questions = questionList;
        this.timeCreated = new Time(new Date().getTime());
        this.lastUpdated = new Time(new Date().getTime());
        this.creator = creator;
        this.tags = stringSet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssignedQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssignedQuestion> questions) {
        this.questions = questions;
    }
    public void addAssignedQuestion(AssignedQuestion assignedQuestion) {
        this.questions.add(assignedQuestion);
    }
    public void addAssignedQuestion(Question question, int difficultyLevel) {
        AssignedQuestion assignedQuestion = new AssignedQuestion(question, difficultyLevel);
        this.questions.add(assignedQuestion);
    }

    public User getCreator() {
        return creator;
    }

    public Questionnaire setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public Time getTimeCreated() {
        return timeCreated;
    }

    public Questionnaire setTimeCreated(Time timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }

    public Time getLastUpdated() {
        return lastUpdated;
    }

    public Questionnaire setLastUpdated(Time lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public boolean isShared() {
        return shared;
    }

    public Questionnaire setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void addTags(String tag) {
        this.tags.add(tag);
    }
}

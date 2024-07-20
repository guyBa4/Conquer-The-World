package Application.Entities.questions;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    
    @Column(name = "multiple_choice")
    private boolean multipleChoice;
    
    @Column
    private String question;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Answer> answers;

    @Column
    private Integer difficulty;
    
    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image; // Holds image data

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    private Set<String> tags;

    @Column(name = "shared")
    private boolean shared;
    
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    public Question(){
    }


    public Question(boolean multipleChoice, String question, List<Answer> answers, Integer difficulty) {
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.answers = answers;
        this.difficulty = difficulty;
        this.shared = true;
    }
    
    public Question(boolean multipleChoice, String question, Integer difficulty, byte[] image, boolean shared) {
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.difficulty = difficulty;
        this.image = image;
        this.shared = shared;
    }

    public Question(boolean multipleChoice, String question, int difficulty, byte[] image, List<String> tags, boolean shared) {
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.difficulty = difficulty;
        this.image = image;
        this.tags = new HashSet<>(tags);
        this.shared = shared;
    }
    // Copy constructor
    public Question(Question questionToCopy) {
        this.multipleChoice = questionToCopy.multipleChoice;
        this.question = questionToCopy.question;
        this.answers = questionToCopy.answers != null ? new ArrayList<>(questionToCopy.answers.size()) : null;
        if (questionToCopy.answers != null) {
            for (Answer answer : questionToCopy.answers) {
                Answer copiedAnswer = new Answer(answer);
                copiedAnswer.setQuestion(this); // set the question reference to the new copy
                this.answers.add(copiedAnswer);
            }
        }
        this.difficulty = questionToCopy.difficulty;
        this.image = questionToCopy.image != null ? questionToCopy.image.clone() : null;
        this.tags = questionToCopy.tags != null ? new HashSet<>(questionToCopy.tags) : null;
        this.shared = false;
        this.creatorId = questionToCopy.creatorId;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
    public Answer getAnswer(String answerInput) {
        for(Answer ans : answers){
            if (ans.getAnswerText().equals(answerInput))
                return ans;
        }
        return null;
    }
    public boolean isAnswerCorrect(String answerInput) {
        for(Answer ans : answers){
            if (ans.getAnswerText().equals(answerInput))
                return ans.getCorrect();
        }
        return false;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public Question setImage(byte[] image) {
        this.image = image;
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
    
    public boolean getShared() {
        return shared;
    }
    
    public Question setShared(boolean shared) {
        this.shared = shared;
        return this;
    }
    
    public UUID getCreatorId() {
        return creatorId;
    }
    
    public Question setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
        return this;
    }
}

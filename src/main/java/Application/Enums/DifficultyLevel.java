package Application.Enums;

public enum DifficultyLevel {
    MIN_DIFFICULTY(1),
    MAX_DIFFICULTY(5);

    private final int value;

    DifficultyLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

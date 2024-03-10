package Application.Entities;

import Application.Enums.TileType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunningGameInstance extends GameInstance{


    private UUID runningId;
    private Map<UUID, MobilePlayer> mobilePlayers;
    private String code;
    private Map<String, Tile> tiles;

    public RunningGameInstance(GameInstance gameInstance) {
        super(gameInstance); //copy contractor
        runningId = UUID.randomUUID();
//        this.code = String.valueOf(Math.round(Math.random()*1000000));
        this.code = "666666";
        this.mobilePlayers = new HashMap<>();
        this.tiles = new HashMap<>();
        tiles.put("TX", new Tile("TX", TileType.FREE, 0, 2));
        tiles.put("MT", new Tile("MT", TileType.FREE, 0, 2));
        tiles.put("ND", new Tile("ND", TileType.FREE, 0, 2));
        tiles.put("ID", new Tile("ID", TileType.FREE, 0, 1));
        tiles.put("WA", new Tile("WA", TileType.FREE, 0, 2));
        tiles.put("AZ", new Tile("AZ", TileType.FREE, 0, 5));
        tiles.put("CA", new Tile("CA", TileType.FREE, 0, 4));
        tiles.put("CO", new Tile("CO", TileType.FREE, 0, 1));
        tiles.put("NV", new Tile("NV", TileType.FREE, 0, 2));
        tiles.put("NM", new Tile("NM", TileType.FREE, 0, 4));
        tiles.put("OR", new Tile("OR", TileType.FREE, 0, 2));
        tiles.put("UT", new Tile("UT", TileType.FREE, 0, 3));
        tiles.put("WY", new Tile("WY", TileType.FREE, 0, 1));
        tiles.put("KS", new Tile("KS", TileType.FREE, 0, 5));
        tiles.put("NE", new Tile("NE", TileType.FREE, 0, 2));
        tiles.put("OK", new Tile("OK", TileType.FREE, 0, 4));
        tiles.put("SD", new Tile("SD", TileType.FREE, 0, 3));
    }

    public UUID getRunningId() {
        return runningId;
    }

    public void setRunningId(UUID runningId) {
        this.runningId = runningId;
    }
    public Map<UUID, MobilePlayer> getMobilePlayers() {
        return mobilePlayers;
    }

    public void setMobilePlayers(Map<UUID, MobilePlayer> mobilePlayers) {
        this.mobilePlayers = mobilePlayers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Map<String, Tile> tiles) {
        this.tiles = tiles;
    }

    public void addMobilePlayer(MobilePlayer mobilePlayer){
        UUID id = mobilePlayer.getUuid();
        mobilePlayers.put(id, mobilePlayer);
    }

    public void getMobilePlayers(MobilePlayer mobilePlayer){
        UUID id = mobilePlayer.getUuid();
        mobilePlayers.put(id, mobilePlayer);
    }

    public Question getQuestion(int difficulty) {
        List<Question> questionList = this.getQuestionnaire().getQuestions().stream().filter((question) -> (question.getDifficulty() == difficulty)).toList();
        int i = (int) (Math.random()*questionList.size());
        return questionList.get(i);
    }

    public boolean checkAnswer(UUID questionId, String answer) {
        List<Question> questionList = this.getQuestionnaire().getQuestions().stream().filter((question) -> (question.getId() == questionId)).toList();
        return questionList.get(0).getAnswer().equals(answer);
    }
}

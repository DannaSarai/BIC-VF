package logic.levels;

import java.io.Serializable;

public class Level implements Serializable {

    protected int score;
    protected int[][] map;
    protected int levelIndex;
    protected int enemyCount;
    protected int currentScore;

    /**
     * Initializes the game state with the provided map, current score, and level
     * index.
     *
     * @param map          an integer type of map. The map representing the game
     *                     layout.
     * @param currentScore currentScore an integer representing the current score of
     *                     the player.
     * @param levelIndex   an integer representing the index of the current level.
     */
    public void init(int[][] map, int currentScore, int levelIndex) {
        this.score = currentScore;
        this.currentScore = currentScore;
        this.levelIndex = levelIndex;
        this.setMap(map);
    }

    /**
     * Retrieves the total score accumulated in the level.
     *
     * @return the total score accumulated in the level
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Retrieves the current score of the player.
     *
     * @return the current score of the player
     */
    public int getCurrentScore() {
        return this.currentScore;
    }

    /**
     * Retrieves the index of the current level.
     *
     * @return the index of the current level
     */
    public int getLevelIndex() {
        return this.levelIndex;
    }

    /**
     * Gets the number of rows in the map.
     *
     * @return The number of rows in the map, or 0 if the map is null.
     */
    public int getNumberRows() {
        return getMap()[0].length;
    }

    /**
     * Gets the number of columns in the map.
     *
     * @return The number of columns in the map, or 0 if the map is null or has no
     * rows.
     */
    public int getNumberCols() {
        return getMap().length;
    }

    public int[][] getMap() {
        return this.map;
    }

    /**
     * Sets the map for the game, and calculates scores and enemy count based on the
     * map.
     *
     * @param map an integer type of map. The map representing the game layout.
     */
    private void setMap(int[][] map) {
        this.map = map;
        if (map != null) {
            processMapValues(map);
        }
    }

    private void processMapValues(int[][] map) {
        for (int[] row : map) {
            for (int tileValue : row) {
                updateScore(tileValue);
                updateEnemyCount(tileValue);
            }
        }
    }

    private void updateScore(int value) {
        if (value == 2 || value == 3) {
            this.score++;
        }
    }

    private void updateEnemyCount(int value) {
        if (value == 4 || value == 5) {
            this.enemyCount++;
        }
    }

}

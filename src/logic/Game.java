package logic;

import logic.entities.Entity;
import logic.entities.IceCream;
import logic.generators.EntityGenerator;
import logic.levels.Level;
import logic.levels.LevelManager;
import logic.results.Death;
import logic.results.Points;

import java.util.ArrayList;
import java.awt.event.KeyEvent;

public class Game {

    private int score;
    private int totalScore;
    private boolean running;
    private Level currentLevel;
    private int currentLevelIndex;
    private LevelManager levelManager;
    private ArrayList<Entity> entities;
    private boolean lastLevelCompleted;
    private boolean alive;

    /**
     * Constructs a new `Game` object, initializing its state.
     */
    public Game() {
        this.totalScore = 0;
        this.running = false;
        this.currentLevelIndex = 1;
        this.levelManager = new LevelManager();
        this.lastLevelCompleted = false;
        this.alive = true;
    }

    /**
     * Gets the total score of the game.
     *
     * @return The total score of the game.
     */
    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * Gets the status of the last level completion.
     *
     * @return True if the last level was completed, otherwise false.
     */
    public boolean getLastLevelCompleted() {
        return this.lastLevelCompleted;
    }

    /**
     * Gets the status of the player's life.
     *
     * @return True if the player is alive, otherwise false.
     */
    public boolean getAlive() {
        return this.alive;
    }

    /**
     * Sets the status of the player's life.
     *
     * @param val The value to set for the player's life status.
     * @return The new value of the player's life status.
     */
    private boolean setAlive(boolean val) {
        this.alive = val;
        return val;
    }

    /**
     * Sets the status of the last level completion.
     *
     * @param val The value to set for the last level completion status.
     * @return The new value of the last level completion status.
     */
    private boolean setLastLevelCompleted(boolean val) {
        this.lastLevelCompleted = !val;
        return val;
    }

    /**
     * Gets the current score of the game.
     *
     * @return The current score of the game.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Gets the index of the current level.
     *
     * @return The index of the current level.
     */
    public int getCurrentLevelIndex() {
        return this.currentLevelIndex;
    }

    /**
     * Gets the list of entities in the game.
     *
     * @return The list of entities in the game.
     */
    public ArrayList<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Checks if the game is currently running.
     *
     * @return True if the game is running, otherwise false.
     */
    public boolean getRunning() {
        return this.running;
    }

    /**
     * Resets the game to level 1 with a total score of 0.
     */
    public void resetToLevel1() {
        this.currentLevelIndex = 1;
        this.totalScore = 0;
    }

    /**
     * Resets the current level and score.
     */
    public void resetLevel() {
        currentLevelIndex -= currentLevelIndex > levelManager.getMaxLevel() ? 1 : 0;
        totalScore -= score;
    }

    /**
     * Moves all entities in the game.
     *
     * @return The result of moving the entities.
     */
    public int moveEntities() {
        int moveResult = 0;
        for (Entity entity : this.entities) {
            moveResult = entity != null ? entity.move(this.entities) : 1;
        }
        return moveResult;
    }

    /**
     * Starts the game by setting it to running and generating entities for the current level.
     */
    public void startGame() {
        this.running = true;
        this.generateEntities(this.levelManager.getLevel(this.currentLevelIndex), this.totalScore);
    }

    /**
     * Restarts the game by resetting entities and starting it again.
     *
     * @return Always returns 0.
     */
    public int restartGame() {
        this.entities = new ArrayList<Entity>();
        this.startGame();
        return 0;
    }

    /**
     * Handles collision with food entities, updating score and removing the food entity.
     *
     * @param foodIndex The index of the food entity.
     * @return Always returns 0.
     */
    private int handleFoodCollition(int foodIndex) {
        this.score++;
        this.totalScore++;
        this.entities.set(foodIndex, null);
        return 0;
    }

    /**
     * Sets the running state of the game.
     *
     * @param val The value to set running state to.
     */
    private void setRunning(boolean val) {
        this.running = val;
    }

    /**
     * Handles collisions between the player entity and other entities.
     *
     * @param entity The player entity.
     * @return The result of handling player collisions.
     */
    private int handlePlayerCollitions(Entity entity) {
        int handleResult = 0;
        boolean isRunning = this.running;
        for (int i = 0; i < this.entities.size() && isRunning; i++) {
            handleResult = isCollectingFood(entity, i)
                    ? this.handleFoodCollition(i)
                    : 1;
            isRunning = isDeathCollition(entity, i)
                    ? this.setAlive(false)
                    : true;
        }
        this.setRunning(isRunning);
        return handleResult;
    }

    /**
     * Checks if there is a death collision between the player entity and other entities.
     *
     * @param entity The player entity.
     * @param i      The index of the entity to check collision with.
     * @return True if there is a death collision, otherwise false.
     */
    private boolean isDeathCollition(Entity entity, int i) {
        return entity.handleCoalitions(this.entities.get(i)) instanceof Death;
    }

    /**
     * Checks if the player entity is collecting food.
     *
     * @param entity The player entity.
     * @param i      The index of the entity to check for food collection.
     * @return True if the player is collecting food, otherwise false.
     */
    private boolean isCollectingFood(Entity entity, int i) {
        return entity.handleCoalitions(this.entities.get(i)) instanceof Points;
    }

    /**
     * Adjusts the level after completing a level.
     *
     * @return The next level.
     */
    private int adjustLevel() {
        if (this.currentLevelIndex > this.levelManager.getMaxLevel()) {
            this.currentLevelIndex = this.levelManager.getMaxLevel() + 1;
        }

        return this.currentLevelIndex <= this.levelManager.getMaxLevel() ? this.restartGame() : 1;
    }

    /**
     * Checks the score of the game and adjusts the level accordingly.
     *
     * @return the score.
     */
    public int checkScore() {
        int scoreAdjusted = this.score == this.currentLevel.getScore() ? this.adjustLevel() : 1;
        boolean isRunning = this.getRunning();

        if (isRunning && this.currentLevelIndex > this.levelManager.getMaxLevel()) {
            this.setLastLevelCompleted(false);
            isRunning = false;
        }
        this.setRunning(isRunning);
        return scoreAdjusted;
    }

    /**
     * Checks collisions of entities in the game, particularly focusing on the main Player.
     *
     * @return The result is the number of succesful collisions handled or 1 if there were no
     * Player entities to handle collisions for. Always returns 0.
     */
    public int checkCollitions() {
        int collisionsChecked = 0;
        for (Entity entity : this.entities) {
            collisionsChecked = isTheMainPlayer(entity) ? this.handlePlayerCollitions(entity) : 1;
        }
        return collisionsChecked;
    }

    /**
     * Checks if the entity is the player, particularly the focus on the IceCream entity
     *
     * @param entity Entity that it's going to be checked
     * @return True if the entity es an IceCream, otherwise it's not a player entity
     */
    private static boolean isTheMainPlayer(Entity entity) {
        return entity != null && entity instanceof IceCream;
    }

    /**
     * Generates entities for the current level of the game.
     *
     * @param level The current level of the game.
     * @param score The score of the game.
     */
    public void generateEntities(Level level, int score) {
        this.currentLevel = level;
        this.currentLevelIndex = this.currentLevel.getLevelIndex();
        this.score = this.currentLevel.getCurrentScore();
        this.totalScore = this.score + score;
        this.entities = EntityGenerator.generate(this.currentLevel);
    }

    /**
     * Handles the actions to be taken when the game is running.
     *
     * @return Always returns 0.
     */
    private int handleRunningAction() {
        this.checkCollitions();
        this.moveEntities();
        this.checkScore();
        return 0;
    }

    /**
     * Performs an action in the game, such as handling collisions and moving entities.
     *
     * @return The action performed.
     */
    public int actionPerformed() {
        return this.getRunning() ? this.handleRunningAction() : 0;
    }

    /**
     * Handles keyboard input for the game.
     *
     * @param e The KeyEvent representing the key pressed.
     * @return The result of handling the key press.
     */
    public int handleGameKeys(KeyEvent e) {
        int keyHandlingResult = 0;
        for (Entity entity : entities) {
            keyHandlingResult = entity != null ? entity.handleKeyEvent(e, entities) : 1;
        }
        return keyHandlingResult;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}

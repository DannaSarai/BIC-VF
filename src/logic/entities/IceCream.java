package logic.entities;

import java.util.ArrayList; // Necesario para instanciar (new ArrayList)
import java.util.List;      // Necesario para la interfaz (List)
import java.awt.event.KeyEvent;

import common.Direction;
import logic.results.Death;
import logic.results.GameResult;
import logic.results.None;
import logic.results.Points;

/**
 * It allows the player to move around the board, throw ice and destroy it, and handle collisions with other entities
 */
public class IceCream extends Entity {

    public IceCream(int id, int x, int y) {
        super("IceCream", id, x, y);
    }

    // CAMBIO: ArrayList -> List
    @Override
    public int handleKeyEvent(KeyEvent e, List<Entity> entities) {
        this.move(e, entities);
        this.setSpell(e);
        this.castDestroySpell(e, entities);
        this.castCreateSpell(e, entities);
        return 0;
    }

    @Override
    public void setID(int iD) {
        super.setID(iD);
    }

    @Override
    public int getLevelId() {
        return this.iD == 0 ? 6 : 7;
    }

    // CAMBIO: ArrayList -> List
    @Override
    protected boolean canMove(int x, int y, List<Entity> entities) {
        entities = entities == null ? new ArrayList<>() : entities;
        boolean foundIce = false;
        for (Entity entity : entities) {
            if (entity instanceof IceBlock && entity.getPositionX() == x && entity.getPositionY() == y) {
                foundIce = true;
                break;
            }
        }
        return this.withinBounds(x, y) && !foundIce;
    }

    // CAMBIO: Corrección del error "new List<>()"
    @Override
    protected boolean canMoveIndestructible(int x, int y, List<Entity> entities) {
        // CORREGIDO: Se instancia ArrayList, aunque la variable sea List
        entities = entities == null ? new ArrayList<>() : entities;
        boolean foundIndestructible = false;
        for (Entity entity : entities) {
            if (entity instanceof IndestructibleBlock && isSamePosition(x, y, entity)) {
                foundIndestructible = true;
                break;
            }
        }
        return this.withinBounds(x, y) && !foundIndestructible;
    }

    @Override
    public GameResult handleCoalitions(Entity entity) {
        return entity != null && isSamePosition(this.getPositionX(), this.getPositionY(), entity)
                ? entity instanceof Enemy ? new Death() : entity instanceof Fruit ? new Points() : new None()
                : new None();
    }

    // CAMBIO: ArrayList -> List en métodos privados
    private int getNewX(Direction direction, int x, int y, List<Entity> entities) {
        int stepSize = direction == Direction.LEFT ? -1 : 1;
        return (canMove(x + this.groundUsed * stepSize, y, entities)
                && canMoveIndestructible(x + this.groundUsed * stepSize, y, entities))
                && direction != Direction.NONE
                ? x + this.groundUsed * stepSize
                : x;
    }

    private int getNewY(Direction direction, int x, int y, List<Entity> entities) {
        int stepSize = direction == Direction.UP ? -1 : 1;
        return (canMove(x, y + this.groundUsed * stepSize, entities)
                && canMoveIndestructible(x, y + this.groundUsed * stepSize, entities))
                && direction != Direction.NONE
                ? y + this.groundUsed * stepSize
                : y;
    }

    private void move(KeyEvent e, List<Entity> entities) {
        int x = this.getPositionX();
        int y = this.getPositionY();
        int code = e.getKeyCode();
        y = getY(entities, code, x, y);
        x = getX(entities, code, x, y);
        this.setPositionY(y);
        this.setPositionX(x);
    }

    private int getX(List<Entity> entities, int code, int x, int y) {
        return code == KeyEvent.VK_LEFT ? getNewX(Direction.LEFT, x, y, entities)
                : code == KeyEvent.VK_RIGHT ? getNewX(Direction.RIGHT, x, y, entities) : x;
    }

    private int getY(List<Entity> entities, int code, int x, int y) {
        return code == KeyEvent.VK_UP ? getNewY(Direction.UP, x, y, entities)
                : code == KeyEvent.VK_DOWN ? getNewY(Direction.DOWN, x, y, entities) : y;
    }

    private void setSpell(KeyEvent e) {
        this.setID(e.getKeyCode() == KeyEvent.VK_SPACE ? this.iD == 0 ? 1 : 0 : this.iD);
    }

    private boolean addIce(int index, int x, int y, List<Entity> entities) {
        boolean addedIce = this.iD == 0 && entities.get(index) == null;
        entities.set(index, addedIce ? new IceBlock(0, x, y) : entities.get(index));
        return addedIce;
    }

    private boolean removeIce(int index, int x, int y, List<Entity> entities) {
        boolean iceRemoved = this.iD == 1 && entities.get(index) != null
                && y == entities.get(index).getPositionY() && x == entities.get(index).getPositionX();
        entities.set(index, iceRemoved ? null : entities.get(index));
        return iceRemoved;
    }

    private Direction getKeyXDirection(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_A ? Direction.LEFT
                : e.getKeyCode() == KeyEvent.VK_D ? Direction.RIGHT : Direction.NONE;
    }

    private Direction getKeyYDirection(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_S ? Direction.DOWN
                : e.getKeyCode() == KeyEvent.VK_W ? Direction.UP : Direction.NONE;
    }

    private boolean castSpell(int id, List<Entity> entities, int i, int x, int y) {
        return id == 0 ? this.addIce(i, x, y, entities) : this.removeIce(i, x, y, entities);
    }

    private boolean handleSpellCasting(List<Entity> entities, int i, KeyEvent e) {
        int y = this.getNewY(getKeyYDirection(e), this.getPositionX(), this.getPositionY(), null);
        int x = this.getNewX(getKeyXDirection(e), this.getPositionX(), this.getPositionY(), null);
        return isValidMove(x, y) && this.castSpell(this.iD, entities, i, x, y);
    }

    private boolean isValidMove(int x, int y) {
        return x != this.getPositionX() || y != this.getPositionY();
    }

    private void castCreateSpell(KeyEvent e, List<Entity> entities) {
        boolean spellCasted = false;
        for (int i = 0; i < entities.size() && !spellCasted; i++) {
            spellCasted = entities.get(i) == null && this.handleSpellCasting(entities, i, e);
        }
    }

    private void castDestroySpell(KeyEvent e, List<Entity> entities) {
        boolean spellCasted = false;
        for (int i = 0; i < entities.size() && !spellCasted; i++) {
            spellCasted = isIceEntity(entities, i) && handleSpellCasting(entities, i, e);
        }
    }

    private static boolean isIceEntity(List<Entity> entities, int index) {
        return entities.get(index) != null && entities.get(index) instanceof IceBlock;
    }
}
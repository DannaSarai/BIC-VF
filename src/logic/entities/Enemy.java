package logic.entities;

import java.util.List; // <--- CAMBIO IMPORTANTE: Usamos List, no ArrayList
import common.Direction;

/**
 * Represents the enemy in the game...
 */
public class Enemy extends Entity {
    private int forward;
    private Direction direction;

    public Enemy(int id, int x, int y) {
        super("Enemy", id, x, y);
        this.direction = id == 0 ? Direction.VERTICAL : Direction.HORIZONTAL;
        this.forward = 1;
    }

    @Override
    public int getLevelId() {
        return this.iD == 0 ? 4 : 5;
    }

    private boolean checkBlock(int x, int y, Entity entity, Class<?> type) {
        boolean found = type.isInstance(entity) && isSamePosition(x, y, entity);
        this.forward *= found ? -1 : 1;
        return found;
    }

    private boolean checkIceBlock(int x, int y, Entity entity) {
        return checkBlock(x, y, entity, IceBlock.class);
    }

    private boolean checkIndestructibleBlock(int x, int y, Entity entity) {
        return checkBlock(x, y, entity, IndestructibleBlock.class);
    }

    // --- AQUÍ EMPIEZAN LOS CAMBIOS A LIST ---

    @Override
    protected boolean canMove(int x, int y, List<Entity> entities) { // <--- CAMBIADO A List
        boolean ice = false;
        for (int i = 0; i < entities.size() && !ice; i++) {
            ice = this.checkIceBlock(x, y, entities.get(i));
        }
        return this.withinBounds(x, y) && !ice;
    }

    @Override
    protected boolean canMoveIndestructible(int x, int y, List<Entity> entities) { // <--- CAMBIADO A List
        boolean indestructible = false;
        for (int i = 0; i < entities.size() && !indestructible; i++) {
            indestructible = this.checkIndestructibleBlock(x, y, entities.get(i));
        }
        return this.withinBounds(x, y) && !indestructible;
    }

    @Override
    protected boolean withinBounds(int x, int y) {
        boolean isWithinEdgeBounds = x < this.groundUsed || y < this.groundUsed;
        boolean isOutsideEdgeBounds = x > this.mapLimitWidth - this.groundUsed * 2
                || y > this.mapLimitHeight - this.groundUsed * 2;
        if (isWithinEdgeBounds) {
            this.forward = -1;
        } else if (isOutsideEdgeBounds) {
            this.forward = 1;
        }
        return !(isWithinEdgeBounds || isOutsideEdgeBounds);
    }

    @Override
    public int move(List<Entity> entities) { // <--- CAMBIADO A List
        int x = this.getPositionX();
        int y = this.getPositionY();
        x = calculateNewX(x, y, entities);
        y = calculateNewY(x, y, entities);
        this.setPositionX(x);
        this.setPositionY(y);
        return 0;
    }

    // Métodos auxiliares privados también actualizados
    private int calculateNewX(int x, int y, List<Entity> entities) { // <--- CAMBIADO A List
        if (this.direction == Direction.HORIZONTAL) {
            int targetX = x - this.groundUsed * 2 * this.forward;
            if (canMove(targetX, y, entities) && canMoveIndestructible(targetX, y, entities)) {
                return x - this.groundUsed * this.forward;
            } else {
                return x + this.groundUsed * this.forward;
            }
        }
        return x;
    }

    private int calculateNewY(int x, int y, List<Entity> entities) { // <--- CAMBIADO A List
        if (this.direction == Direction.VERTICAL) {
            int targetY = y - this.groundUsed * 2 * this.forward;
            if (canMove(x, targetY, entities) && canMoveIndestructible(x, targetY, entities)) {
                return y - this.groundUsed * this.forward;
            } else {
                return y + this.groundUsed * this.forward;
            }
        }
        return y;
    }
}
package simulation.entity;

import simulation.world.Coordinates;
import simulation.world.WorldMap;

/** Травоядное: ищет траву, идёт к ней и съедает. */
public class Herbivore extends Creature {

    public Herbivore(int speed, int hp) {
        super(speed, hp);
    }

    @Override
    protected Class<? extends Entity> getTargetType() {
        return Grass.class;
    }

    @Override
    protected void interactWithTarget(Coordinates targetPosition, WorldMap map) {
        map.removeEntity(targetPosition); // съели траву
    }
}
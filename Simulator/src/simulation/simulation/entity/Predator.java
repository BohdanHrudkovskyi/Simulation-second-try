package simulation.entity;

import simulation.world.Coordinates;
import simulation.world.WorldMap;

/** Хищник: ищет травоядное, идёт к нему и атакует. */
public class Predator extends Creature {
    private final int attackPower;

    public Predator(int speed, int hp, int attackPower) {
        super(speed, hp);
        if (attackPower <= 0) {
            throw new IllegalArgumentException("Сила атаки должна быть положительной");
        }
        this.attackPower = attackPower;
    }

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    protected Class<? extends Entity> getTargetType() {
        return Herbivore.class;
    }

    @Override
    protected void interactWithTarget(Coordinates targetPosition, WorldMap map) {
        map.getEntity(targetPosition).ifPresent(entity -> {
            Herbivore prey = (Herbivore) entity;
            prey.takeDamage(attackPower);
            if (prey.isDead()) {
                map.removeEntity(targetPosition);
            }
        });
    }
}
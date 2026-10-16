package simulation.entity;

import simulation.pathfinding.PathFinder;
import simulation.world.Coordinates;
import simulation.world.WorldMap;

import java.util.List;
import java.util.Optional;

/**
 * Существо: имеет скорость (сколько клеток проходит за ход) и здоровье.
 * Общая логика хода (найти цель, дойти, взаимодействовать) написана здесь один раз.
 * Наследники лишь говорят, ЧТО они ищут и ЧТО делают, когда дошли.
 */
public abstract class Creature extends Entity {
    private final PathFinder pathFinder = new PathFinder();
    private final int speed;
    private int hp;

    protected Creature(int speed, int hp) {
        if (speed <= 0 || hp <= 0) {
            throw new IllegalArgumentException("Скорость и HP должны быть положительными");
        }
        this.speed = speed;
        this.hp = hp;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
    }

    public boolean isDead() {
        return hp == 0;
    }

    /** Тип цели, которую ищет существо (травоядное ищет Grass, хищник ищет Herbivore). */
    protected abstract Class<? extends Entity> getTargetType();

    /** Что делать, когда цель стоит в соседней клетке (съесть, атаковать). */
    protected abstract void interactWithTarget(Coordinates targetPosition, WorldMap map);

    /**
     * Сделать ход: либо приблизиться к цели (не более speed клеток),
     * либо, если цель рядом, взаимодействовать с ней. Не то и другое сразу.
     * Существо не знает своих координат (их хранит карта), поэтому позиция передаётся параметром.
     */
    public void makeMove(Coordinates position, WorldMap map) {
        Optional<List<Coordinates>> foundPath = pathFinder.findPath(map, position, getTargetType());
        if (foundPath.isEmpty()) {
            return; // цели нет или она недостижима, стоим на месте
        }

        List<Coordinates> path = foundPath.get(); // последняя клетка пути это цель
        Coordinates targetPosition = path.get(path.size() - 1);

        if (path.size() == 1) {
            interactWithTarget(targetPosition, map);
            return;
        }

        int stepsToWalk = Math.min(speed, path.size() - 1); // в клетку цели не заходим
        Coordinates current = position;
        for (int i = 0; i < stepsToWalk; i++) {
            Coordinates next = path.get(i);
            map.moveEntity(current, next);
            current = next;
        }
    }
}
package simulation.action;

import simulation.entity.Entity;
import simulation.world.WorldMap;

import java.util.function.Supplier;

/**
 * Следит за численностью: если объектов заданного типа стало меньше минимума,
 * добавляет недостающих на случайные свободные клетки.
 * Пример: new ReplenishEntitiesAction(Grass.class, Grass::new, 8)
 */
public class Replenishentitiesaction implements Action {
    private final Class<? extends Entity> type;
    private final Supplier<? extends Entity> entityFactory;
    private final int minCount;

    public Replenishentitiesaction(Class<? extends Entity> type, Supplier<? extends Entity> entityFactory, int minCount) {
        this.type = type;
        this.entityFactory = entityFactory;
        this.minCount = minCount;
    }

    @Override
    public void execute(WorldMap map) {
        int currentCount = map.getEntitiesOfType(type).size();
        if (currentCount < minCount) {
            new SpawnEntitiesAction(entityFactory, minCount - currentCount).execute(map);
        }
    }
}
package simulation.action;

import simulation.entity.Entity;
import simulation.world.Coordinates;
import simulation.world.WorldMap;
 
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
 
/**
 * Размещает на случайных свободных клетках заданное количество новых объектов.
 * Один класс подходит для любых типов (камни, деревья, трава, существа),
 * поэтому отдельные классы под каждый тип не нужны.
 *
 * Пример: new SpawnEntitiesAction(Rock::new, 5)
 */

//<? extends Entity>: It can create Entity or any subclass (Wolf, Herb, etc.).
public class SpawnEntitiesAction implements Action {
    private final Supplier<? extends Entity> entityFactory;
    private final int count;
    
    public SpawnEntitiesAction(Supplier<? extends Entity> entityFactory, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        this.entityFactory = entityFactory;
        this.count = count;
    }
 
    @Override
    public void execute(WorldMap map) {
        List<Coordinates> emptyCells = map.getEmptyCells();
        Collections.shuffle(emptyCells);
 
        int amountToPlace = Math.min(count, emptyCells.size());
        for (int i = 0; i < amountToPlace; i++) {
            map.addEntity(emptyCells.get(i), entityFactory.get());
        }
    }
}
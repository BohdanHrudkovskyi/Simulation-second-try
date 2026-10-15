package simulation.action;

import simulation.entity.Creature;
import simulation.world.Coordinates;
import simulation.world.WorldMap;

import java.util.Map;

/** Даёт сделать ход каждому существу на карте. */
public class Moveallcreaturesaction implements Action {

    @Override
    public void execute(WorldMap map) {
        // getEntitiesOfType возвращает копию, поэтому карту можно менять во время обхода
        for (Map.Entry<Coordinates, Creature> entry : map.getEntitiesOfType(Creature.class).entrySet()) {
            Coordinates position = entry.getKey();
            Creature creature = entry.getValue();

            // Пока шёл обход, существо могли съесть: проверяем, что оно всё ещё на своём месте
            boolean stillThere = map.getEntity(position).filter(entity -> entity == creature).isPresent();
            if (stillThere) {
                creature.makeMove(position, map);
            }
        }
    }
}
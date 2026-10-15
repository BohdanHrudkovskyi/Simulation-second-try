package simulation.action;

import simulation.world.WorldMap;
 
/**
 * Действие, совершаемое над миром: расставить объекты, сходить существами,
 * добавить траву и т.д. Каждое действие описывается отдельным классом.
 */
public interface Action {
    void execute(WorldMap map);
}

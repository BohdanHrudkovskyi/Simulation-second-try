package simulation;

import simulation.entity.Grass;
import simulation.entity.Rock;
import simulation.entity.Tree;
import simulation.render.ConsoleRenderer;
import simulation.render.Renderer;
import simulation.world.Coordinates;
import simulation.world.WorldMap;
 
public class Main {
    public static void main(String[] args) {
        WorldMap map = new WorldMap(6, 10);
        map.addEntity(new Coordinates(0, 0), new Rock());
        map.addEntity(new Coordinates(1, 4), new Tree());
        map.addEntity(new Coordinates(2, 3), new Grass());
        map.addEntity(new Coordinates(4, 7), new Grass());
 
        Renderer renderer = new ConsoleRenderer();
        renderer.render(map);
    }
}
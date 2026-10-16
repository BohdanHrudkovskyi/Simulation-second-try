package simulation;

import simulation.action.Action;
import simulation.action.Moveallcreaturesaction;
import simulation.action.Replenishentitiesaction;
import simulation.action.SpawnEntitiesAction;
import simulation.entity.Grass;
import simulation.entity.Herbivore;
import simulation.entity.Predator;
import simulation.entity.Rock;
import simulation.entity.Tree;
import simulation.render.ConsoleRenderer;
import simulation.world.WorldMap;

import java.util.List;
import java.util.Scanner;

public class Main {
    // Параметры мира: меняй их, чтобы найти самый интересный баланс
    private static final int ROWS = 10;
    private static final int COLUMNS = 20;
    private static final long TURN_DELAY_MILLIS = 700;

    private static final int ROCKS = 12;
    private static final int TREES = 12;

    private static final int START_GRASS = 15;
    private static final int MIN_GRASS = 8;

    private static final int START_HERBIVORES = 6;
    private static final int MIN_HERBIVORES = 3;
    private static final int HERBIVORE_SPEED = 2;
    private static final int HERBIVORE_HP = 8;

    private static final int PREDATORS = 2;
    private static final int PREDATOR_SPEED = 2;
    private static final int PREDATOR_HP = 10;
    private static final int PREDATOR_ATTACK = 4;

    public static void main(String[] args) {
        WorldMap map = new WorldMap(ROWS, COLUMNS);

        List<Action> initActions = List.of(
                new SpawnEntitiesAction(Rock::new, ROCKS),
                new SpawnEntitiesAction(Tree::new, TREES),
                new SpawnEntitiesAction(Grass::new, START_GRASS),
                new SpawnEntitiesAction(() -> new Herbivore(HERBIVORE_SPEED, HERBIVORE_HP), START_HERBIVORES),
                new SpawnEntitiesAction(() -> new Predator(PREDATOR_SPEED, PREDATOR_HP, PREDATOR_ATTACK), PREDATORS)
        );
        List<Action> turnActions = List.of(
                new Moveallcreaturesaction(),
                new Replenishentitiesaction(Grass.class, Grass::new, MIN_GRASS),
                new Replenishentitiesaction(Herbivore.class,
                        () -> new Herbivore(HERBIVORE_SPEED, HERBIVORE_HP), MIN_HERBIVORES)
        );

        Simulation simulation = new Simulation(map, new ConsoleRenderer(), initActions, turnActions, TURN_DELAY_MILLIS);

        System.out.println("Enter - пауза/продолжить, q + Enter - выход");
        simulation.initialize();
        simulation.startSimulation();

        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            if (scanner.nextLine().trim().equalsIgnoreCase("q")) {
                break;
            }
            if (running) {
                simulation.pauseSimulation();
            } else {
                simulation.startSimulation();
            }
            running = !running;
        }
        simulation.pauseSimulation();
    }
}
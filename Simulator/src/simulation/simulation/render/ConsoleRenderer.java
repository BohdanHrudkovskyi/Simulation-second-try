package simulation.render;

import simulation.entity.Entity;
import simulation.entity.Grass;
import simulation.entity.Herbivore;
import simulation.entity.Predator;
import simulation.entity.Rock;
import simulation.entity.Tree;
import simulation.world.Coordinates;
import simulation.world.WorldMap;

/** Рисует карту в консоли: одна клетка = один символ-эмодзи. */
public class ConsoleRenderer implements Renderer {
    private static final String EMPTY_SPRITE = "🟩";
    private static final String UNKNOWN_SPRITE = "❓";

    @Override
    public void render(WorldMap map, int turnCount) {
        StringBuilder frame = new StringBuilder();
        frame.append("Ход: ").append(turnCount).append(System.lineSeparator());

        for (int row = 0; row < map.getRows(); row++) {
            for (int column = 0; column < map.getColumns(); column++) {
                frame.append(getSprite(map, new Coordinates(row, column)));
            }
            frame.append(System.lineSeparator());
        }

        frame.append(System.lineSeparator());
        System.out.print(frame);
    }

    private String getSprite(WorldMap map, Coordinates coordinates) {
        return map.getEntity(coordinates)
                .map(this::getEntitySprite)
                .orElse(EMPTY_SPRITE);
    }

    private String getEntitySprite(Entity entity) {
        if (entity instanceof Grass) {
            return "🌱";
        }
        if (entity instanceof Rock) {
            return "🪨";
        }
        if (entity instanceof Tree) {
            return "🌳";
        }
        if (entity instanceof Herbivore) {
            return "🐇";
        }
        if (entity instanceof Predator) {
            return "🐺";
        }
        return UNKNOWN_SPRITE;
    }
}
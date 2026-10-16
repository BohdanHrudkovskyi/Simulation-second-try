package simulation.pathfinding;

import simulation.entity.Entity;
import simulation.world.Coordinates;
import simulation.world.WorldMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * Поиск кратчайшего пути поиском в ширину (BFS).
 * Не зависит от типа существа: искомый тип цели передаётся параметром,
 * поэтому один и тот же класс используют и травоядные (цель: Grass),
 * и хищники (цель: Herbivore).
 */
public class PathFinder {
    // Шаги вверх, вниз, влево, вправо: {изменение строки, изменение столбца}
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Ищет кратчайший путь от start до ближайшего объекта типа targetType.
     * Другие объекты считаются препятствиями.
     *
     * @return клетки пути по порядку, от первого шага до клетки с целью включительно
     *         (стартовая клетка в путь не входит); пустой Optional, если цель недостижима
     */
    public Optional<List<Coordinates>> findPath(WorldMap map, Coordinates start, Class<? extends Entity> targetType) {
        Queue<Coordinates> queue = new ArrayDeque<>();
        // Для каждой посещённой клетки запоминаем, из какой пришли. Заодно это список "посещённых".
        Map<Coordinates, Coordinates> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Coordinates current = queue.poll();

            for (Coordinates neighbor : getNeighbors(current)) {
                if (!map.isInside(neighbor) || cameFrom.containsKey(neighbor)) {
                    continue;
                }

                Optional<Entity> entity = map.getEntity(neighbor);

                if (entity.isPresent()) {
                    if (targetType.isInstance(entity.get())) {
                        cameFrom.put(neighbor, current);
                        return Optional.of(buildPath(cameFrom, neighbor));
                    }
                    continue; // чужой объект: препятствие, обходим
                }

                cameFrom.put(neighbor, current);
                queue.add(neighbor);
            }
        }

        return Optional.empty();
    }

    private List<Coordinates> getNeighbors(Coordinates coordinates) {
        List<Coordinates> neighbors = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            neighbors.add(new Coordinates(
                    coordinates.row() + direction[0],
                    coordinates.column() + direction[1]));
        }
        return neighbors;
    }

    /** Идём от цели назад по cameFrom до старта, затем разворачиваем список. */
    private List<Coordinates> buildPath(Map<Coordinates, Coordinates> cameFrom, Coordinates target) {
        List<Coordinates> path = new ArrayList<>();
        for (Coordinates step = target; cameFrom.get(step) != null; step = cameFrom.get(step)) {
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }
}
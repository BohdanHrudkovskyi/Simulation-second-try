package simulation.world;

import simulation.entity.Entity;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WorldMap {
    private final int rows;
    private final int columns;
    private final Map<Coordinates, Entity> entities = new HashMap<>();

    public WorldMap(int rows, int coulumns) {
        if(rows <= 0 || coulumns <= 0){
            throw new IllegalArgumentException("Размер карты должны быть положительными");
        }
        this.rows = rows;
        this.columns = coulumns;
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }
    //клетка в пределах карты? 
    public boolean isInside(Coordinates coordinates) {
        return coordinates.row() >= 0 && coordinates.row() < rows
                && coordinates.column() >= 0 && coordinates.column() < columns;
    }
    //можно ли сюда встать
    public boolean isEmpty(Coordinates coordinates) {
        return isInside(coordinates) && !entities.containsKey(coordinates);
    }
    /*Метод возвращает Optional<Entity>, то есть «коробку», которая либо содержит объект, либо пуста. Так мы не возвращаем null и не получаем неожиданный NullPointerException. Сразу видно по типу, что объекта в клетке может не быть. Рендерер использует это так: .map(...).orElse(EMPTY_SPRITE).

Методы изменения

addEntity(coordinates, entity) ставит объект на клетку. Метод защищается от двух ошибок:
Клетка за пределами карты → IllegalArgumentException.
Клетка уже занята → IllegalStateException.*/
    public Optional<Entity> getEntity(Coordinates coordinates) {
        return Optional.ofNullable(entities.get(coordinates));
    }
 
    public void addEntity(Coordinates coordinates, Entity entity) {
        if (!isInside(coordinates)) {
            throw new IllegalArgumentException("Координаты за пределами карты: " + coordinates);
        }
        if (entities.containsKey(coordinates)) {
            throw new IllegalStateException("Клетка уже занята: " + coordinates);
        }
        entities.put(coordinates, entity);
    }
    
    public void removeEntity(Coordinates coordinates) {
        entities.remove(coordinates);
    }
    /*removeEntity(coordinates) убирает объект с клетки (например, съеденную траву). Если клетка и так пуста, ничего не произойдёт. */
    
    public void moveEntity(Coordinates from, Coordinates to) {
        Entity entity = entities.get(from);
        if (entity == null) {
            throw new IllegalStateException("В клетке нет объекта: " + from);
        }
        if (!isEmpty(to)) {
            throw new IllegalStateException("Целевая клетка недоступна: " + to);
        }
        entities.remove(from);
        entities.put(to, entity);
    }
 
     /** Все сущности заданного типа вместе с их координатами (копия, безопасно для изменения карты). */
     public <T extends Entity> Map<Coordinates, T> getEntitiesOfType(Class<T> type) {
        Map<Coordinates, T> result = new HashMap<>();
        for (Map.Entry<Coordinates, Entity> entry : entities.entrySet()) {
            if (type.isInstance(entry.getValue())) {
                result.put(entry.getKey(), type.cast(entry.getValue()));
            }
        }
        return result;
    }

    /*Здесь работают дженерики: <T extends Entity> означает «какой-то подтип Entity», и метод возвращает словарь именно этого типа. Нам не нужны отдельные списки для травы, травоядных и хищников: одна карта и один универсальный метод. Это решает пункт чеклиста «параллельные коллекции для разных типов».

Внутри type.isInstance(...) проверяет, подходит ли объект под тип, а type.cast(...) приводит его к нужному типу. Метод возвращает копию, поэтому можно спокойно перебирать результат и одновременно менять карту.

getEmptyCells() перебирает все клетки по строкам и столбцам и возвращает список свободных. Он пригодится при расстановке объектов и при добавлении травы.

Почему так устроено
Скрытая внутренность. Снаружи никто не знает, что внутри HashMap. Если захотите заменить его на что-то другое, остальной код менять не придётся.
Правила внутри карты. Проверки «в границах» и «клетка свободна» находятся в одном месте, и остальным классам не нужно их повторять.
Быстрый доступ. Поиск по ключу в HashMap в среднем занимает константное время. Найти объект по координатам можно за один шаг, а не перебором списка.
Нет координат внутри Entity. Расположение хранит только карта, поэтому данные не могут разойтись. */
    
/** Свободные клетки карты (пригодится при расстановке объектов). */
    public List<Coordinates> getEmptyCells() {
        List<Coordinates> result = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Coordinates coordinates = new Coordinates(row, column);
                if (!entities.containsKey(coordinates)) {
                    result.add(coordinates);
                }
            }
        }
        return result;
    }
}

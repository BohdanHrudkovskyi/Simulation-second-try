package simulation.render;

import simulation.world.WorldMap;

/**
 * Умеет отобразить состояние карты.
 * Симуляция зависит от этого интерфейса, а не от конкретной реализации,
 * поэтому консоль можно заменить на графику, не трогая остальной код.
 */
public interface Renderer {
    void render(WorldMap map, int turnCount);
}
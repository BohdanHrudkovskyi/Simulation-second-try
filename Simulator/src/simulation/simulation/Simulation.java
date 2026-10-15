package simulation;

import simulation.action.Action;
import simulation.render.Renderer;
import simulation.world.WorldMap;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Главный класс приложения: хранит карту, счётчик ходов, рендерер
 * и два списка действий (до старта и на каждый ход).
 */
public class Simulation {
    private final WorldMap map;
    private final Renderer renderer;
    private final List<Action> initActions;
    private final List<Action> turnActions;
    private final long turnDelayMillis;

    // Фоновый поток-демон: не мешает программе завершиться
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "simulation-loop");
        thread.setDaemon(true);
        return thread;
    });
    private ScheduledFuture<?> loopTask;
    private int turnCount = 0;

    public Simulation(WorldMap map, Renderer renderer, List<Action> initActions,
                      List<Action> turnActions, long turnDelayMillis) {
        this.map = map;
        this.renderer = renderer;
        this.initActions = List.copyOf(initActions);
        this.turnActions = List.copyOf(turnActions);
        this.turnDelayMillis = turnDelayMillis;
    }

    /** Выполняет действия перед стартом (расстановка объектов) и рисует начальное состояние. */
    public synchronized void initialize() {
        for (Action action : initActions) {
            action.execute(map);
        }
        renderer.render(map, turnCount);
    }

    /** Просимулировать и отрендерить один ход. */
    public synchronized void nextTurn() {
        for (Action action : turnActions) {
            action.execute(map);
        }
        turnCount++;
        renderer.render(map, turnCount);
    }

    /** Запускает бесконечный цикл: ход, пауза, ход, пауза... Повторный вызов ничего не меняет. */
    public synchronized void startSimulation() {
        if (loopTask != null) {
            return;
        }
        loopTask = scheduler.scheduleWithFixedDelay(this::runTurnSafely, 0, turnDelayMillis, TimeUnit.MILLISECONDS);
    }

    /** Приостанавливает цикл. Текущий ход доигрывается до конца. */
    public synchronized void pauseSimulation() {
        if (loopTask != null) {
            loopTask.cancel(false);
            loopTask = null;
        }
    }

    public synchronized int getTurnCount() {
        return turnCount;
    }

    // Если в запланированной задаче вылетит исключение, планировщик молча её отменит.
    // Поэтому ловим и печатаем сами.
    private void runTurnSafely() {
        try {
            nextTurn();
        } catch (RuntimeException e) {
            e.printStackTrace();
            pauseSimulation();
        }
    }
}
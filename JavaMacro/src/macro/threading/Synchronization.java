package macro.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Synchronization {

    private final List<Future<?>> scriptFutures;
    private final Queue<Integer> keyPressed;
    private final Queue<Integer> mouseClicked;

    public Synchronization() {
        keyPressed = new LinkedBlockingQueue<>();
        mouseClicked = new LinkedBlockingQueue<>();
        scriptFutures = new ArrayList<>();
    }

    public int getKeyPresses() {
        return keyPressed.size();
    }

    public int getMouseClicks() {
        return mouseClicked.size();
    }

    public Integer getNextKeyPress() {
        return keyPressed.poll();
    }

    public Integer getNextMouseClicked() {
        return mouseClicked.poll();
    }

    public void addKeyPress(Integer value) {
        keyPressed.add(value);
        synchronized (this) {
            this.notify();
        }
    }

    public void addMouseClicked(Integer value) {
        mouseClicked.add(value);
        synchronized (this) {
            this.notify();
        }
    }

    public synchronized void addScriptFuture(Future<?> future) {
        scriptFutures.add(future);
    }

    public synchronized List<Future<?>> getScriptFutures() {
        return scriptFutures;
    }

    public synchronized void removeScriptFuture(Future future) {
        scriptFutures.remove(future);
    }

    public synchronized void clearScriptFutures() {
        scriptFutures.clear();
    }
}
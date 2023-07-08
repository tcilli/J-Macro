package macro.threading;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Synchronization {

    private final Queue<Future<?>> scriptFutures;
    private final Queue<Integer> keyPressed;
    private final Queue<Integer> mouseClicked;
    private final Queue<String> keysInUse; 

    public Synchronization() {
        keyPressed = new LinkedBlockingQueue<>();
        mouseClicked = new LinkedBlockingQueue<>();
        scriptFutures = new LinkedBlockingQueue<>();
        keysInUse = new LinkedBlockingQueue<>();
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

    public void addKey(String key) {
        keysInUse.add(key);
    }
    public void removeKey(String key) {
        keysInUse.remove(key);
    }
    public boolean containsKey(String key)
    {
        return keysInUse.contains(key);
    }
    public void clearKeys() {
        keysInUse.clear();
    }

    public void addScriptFuture(Future<?> future) {
        scriptFutures.add(future);
    }

    public void removeScriptFuture(Future<?> future) {
        scriptFutures.remove(future);
    }

    public void clearScriptFutures() {
        scriptFutures.clear();
    }

    public Queue<Future<?>> getScriptFutures() {
        return scriptFutures;
    }
}
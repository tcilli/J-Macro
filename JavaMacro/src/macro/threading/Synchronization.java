package macro.threading;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class to synchronize any data shared between threads.
 */
public final class Synchronization {

    private final Queue<Integer> keyPressed;
    private final Queue<Integer> keyReleased;
    private final Queue<Integer> mouseClicked;

    public Synchronization() {
        keyPressed = new LinkedBlockingQueue<>();
        keyReleased = new LinkedBlockingQueue<>();
        mouseClicked = new LinkedBlockingQueue<>();
    }

    public int getKeyPresses() {
        return keyPressed.size();
    }
    public int getKeyReleases() {
        return keyReleased.size();
    }
    public int getMouseClicks() {
        return mouseClicked.size();
    }

    public Integer getNextKeyPress() {
        return keyPressed.poll();
    }
    public Integer getNextKeyRelease() {
        return keyReleased.poll();
    }
    public Integer getNextMouseClicked() {
        return mouseClicked.poll();
    }

    public void addKeyPress(final Integer value) {
        keyPressed.add(value);
        synchronized (this) {
            this.notify();
        }
    }
    public void addKeyReleased(final Integer value) {
        keyReleased.add(value);
        synchronized (this) {
            this.notify();
        }
    }
    public void addMouseClicked(final Integer value) {
        mouseClicked.add(value);
        synchronized (this) {
            this.notify();
        }
    }

    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
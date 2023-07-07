package macro;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Synchronization {

    private String lock = "";

    public synchronized void setKeyLock(String key) {
        lock = key;
    }
    public synchronized String getKeyLock() {
        return lock;
    }
    public synchronized void releaseKeyLock() { lock = ""; }

    private Queue<Integer> keyPressed;
    private Queue<Integer> mouseClicked;

    public Synchronization() {
        keyPressed = new LinkedBlockingQueue<>();
        mouseClicked = new LinkedBlockingQueue<>();
    }

    public int getKeyPresses() { return keyPressed.size(); }
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

    public AtomicBoolean stopScript = new AtomicBoolean(false);

    public long lastrun = System.currentTimeMillis();

}

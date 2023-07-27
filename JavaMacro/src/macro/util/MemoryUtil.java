package macro.util;

import macro.Main;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class MemoryUtil {

    private static MemoryUsage getHeapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    }

    private static MemoryUsage getNonHeapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
    }

    public static void printHeapMemoryUsage() {
        Main.getConsoleBuffer().append("Memory Heap: ").append(getHeapMemoryUsage()).append("\n")
                    .append("Memory Non-Heap: ").append(getNonHeapMemoryUsage());
        Main.pushConsoleMessage();
    }
}
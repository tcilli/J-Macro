package macro;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class MemoryUtil {

    public static MemoryUsage getHeapMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    }
}
package btools.server;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class IpAccessMonitor {
    private static long lastCleanup;
    private static Object sync = new Object();
    private static Map<String, Long> ipAccess = new HashMap();
    private static long MAX_IDLE = 900000;
    private static long CLEANUP_INTERVAL = 10000;

    public static boolean touchIpAccess(String ip) {
        boolean z;
        long t = System.currentTimeMillis();
        synchronized (sync) {
            Long lastTime = ipAccess.get(ip);
            ipAccess.put(ip, Long.valueOf(t));
            z = lastTime == null || t - lastTime.longValue() > MAX_IDLE;
        }
        return z;
    }

    public static int getSessionCount() {
        int size;
        long t = System.currentTimeMillis();
        synchronized (sync) {
            if (t - lastCleanup > CLEANUP_INTERVAL) {
                cleanup(t);
                lastCleanup = t;
            }
            size = ipAccess.size();
        }
        return size;
    }

    private static void cleanup(long t) {
        Map<String, Long> newMap = new HashMap<>(ipAccess.size());
        for (Map.Entry<String, Long> e : ipAccess.entrySet()) {
            if (t - e.getValue().longValue() <= MAX_IDLE) {
                newMap.put(e.getKey(), e.getValue());
            }
        }
        ipAccess = newMap;
    }
}

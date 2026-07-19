package btools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class StackSampler extends Thread {
    private BufferedWriter bw;
    private int interval;
    private volatile boolean stopped;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", new Locale.Builder().setLanguage("en").setRegion("US").build());
    private Random rand = new Random();
    private int flushCnt = 0;

    public StackSampler(File logfile, int interval) {
        this.interval = interval;
        try {
            this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logfile, true)));
        } catch (Exception e) {
            printError("StackSampler: " + e.getMessage());
        }
    }

    protected void printError(String msg) {
        System.out.println(msg);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.stopped) {
            dumpThreads();
        }
        if (this.bw != null) {
            try {
                this.bw.close();
            } catch (Exception e) {
            }
        }
    }

    public void dumpThreads() {
        try {
            int wait1 = this.rand.nextInt(this.interval);
            int wait2 = this.interval - wait1;
            sleep(wait1);
            StringBuilder sb = new StringBuilder(this.df.format(new Date()) + " THREADDUMP\n");
            Map<Thread, StackTraceElement[]> allThreads = getAllStackTraces();
            Iterator<Map.Entry<Thread, StackTraceElement[]>> it = allThreads.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<Thread, StackTraceElement[]> e = it.next();
                Thread t = e.getKey();
                if (t != currentThread()) {
                    StackTraceElement[] stack = e.getValue();
                    if (matchesFilter(stack)) {
                        sb.append(" (ID=").append(t.getId()).append(" \"").append(t.getName()).append("\" ").append(t.getState()).append("\n");
                        for (StackTraceElement line : stack) {
                            sb.append("    ").append(line.toString()).append("\n");
                        }
                        sb.append("\n");
                    }
                }
            }
            this.bw.write(sb.toString());
            int i = this.flushCnt;
            this.flushCnt = i + 1;
            if (i >= 0) {
                this.flushCnt = 0;
                this.bw.flush();
            }
            sleep(wait2);
        } catch (Exception e2) {
        }
    }

    public void close() {
        this.stopped = true;
        interrupt();
    }

    private boolean matchesFilter(StackTraceElement[] stack) {
        boolean positiveMatch = false;
        for (StackTraceElement e : stack) {
            String s = e.toString();
            if (s.indexOf("btools") >= 0) {
                positiveMatch = true;
            }
            if (s.indexOf("Thread.sleep") >= 0 || s.indexOf("PlainSocketImpl.socketAccept") >= 0) {
                return false;
            }
        }
        return positiveMatch;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("StackSampler...");
        Class<?> clazz = Class.forName(args[0]);
        String[] args2 = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            args2[i - 1] = args[i];
        }
        StackSampler t = new StackSampler(new File("stacks.log"), 1000);
        t.start();
        try {
            clazz.getMethod("main", String[].class).invoke(null, args2);
        } finally {
            t.close();
        }
    }
}

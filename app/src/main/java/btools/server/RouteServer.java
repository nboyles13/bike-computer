package btools.server;

import btools.router.OsmNodeNamed;
import btools.router.OsmTrack;
import btools.router.ProfileCache;
import btools.router.RoutingContext;
import btools.router.RoutingEngine;
import btools.router.RoutingParamCollector;
import btools.server.request.ProfileUploadHandler;
import btools.server.request.RequestHandler;
import btools.server.request.ServerHandler;
import btools.util.StackSampler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class RouteServer extends Thread implements Comparable<RouteServer> {
    static final String HTTP_STATUS_BAD_REQUEST = "400 Bad Request";
    static final String HTTP_STATUS_FORBIDDEN = "403 Forbidden";
    static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500 Internal Server Error";
    static final String HTTP_STATUS_NOT_FOUND = "404 Not Found";
    static final String HTTP_STATUS_OK = "200 OK";
    public static final String PROFILE_UPLOAD_URL = "/brouter/profile";
    private Socket clientSocket = null;
    private RoutingEngine cr = null;
    public ServiceContext serviceContext;
    private long starttime;
    private volatile boolean terminated;
    private static Object threadPoolSync = new Object();
    private static boolean debug = Boolean.getBoolean("debugThreadPool");
    private static DateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", new Locale("en", "US"));

    public void stopRouter() {
        RoutingEngine e = this.cr;
        if (e != null) {
            e.terminate();
        }
    }

    private static String formattedTimeStamp(long t) {
        String str;
        synchronized (tsFormat) {
            str = tsFormat.format(new Date(System.currentTimeMillis()));
        }
        return str;
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:167:0x03df  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03e1  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x041c  */
    @Override // java.lang.Thread, java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void run() throws Throwable {
        String encodings;
        String xff;
        String xff2;
        String headers;
        String s;
        BufferedReader br = null;
        BufferedWriter bw = null;
        String getline = null;
        RoutingEngine routingEngine = null;
        boolean z = true;
        try {
            br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream(), "UTF-8"));
            String xff3 = null;
            String xff4 = null;
            String getline2 = null;
            String xff5 = null;
            while (true) {
                try {
                    String line = br.readLine();
                    if (line == null) {
                        writeHttpHeader(bw, HTTP_STATUS_BAD_REQUEST);
                        bw.flush();
                        this.cr = routingEngine;
                        try {
                            br.close();
                        } catch (Exception e) {
                        }
                        try {
                            bw.close();
                        } catch (Exception e2) {
                        }
                        if (this.clientSocket != null) {
                            try {
                                this.clientSocket.close();
                            } catch (Exception e3) {
                            }
                        }
                        this.terminated = z;
                        synchronized (threadPoolSync) {
                            threadPoolSync.notifyAll();
                        }
                        long t = System.currentTimeMillis();
                        long ms = t - this.starttime;
                        System.out.println(formattedTimeStamp(t) + ((String) null) + " ip=" + ((String) null) + " ms=" + ms + " -> " + getline2);
                        return;
                    }
                    if (line.length() == 0) {
                        InetAddress ip = this.clientSocket.getInetAddress();
                        String sIp = xff5 == null ? ip == null ? "null" : ip.toString() : xff5;
                        boolean newSession = IpAccessMonitor.touchIpAccess(sIp);
                        String sessionInfo = " new";
                        if (!newSession) {
                            int sessionCount = IpAccessMonitor.getSessionCount();
                            String sessionInfo2 = "    " + Math.min(sessionCount, 999);
                            sessionInfo = sessionInfo2.substring(sessionInfo2.length() - 4);
                        }
                        String excludedAgents = System.getProperty("excludedAgents");
                        if (xff5 != null && excludedAgents != null) {
                            StringTokenizer tk = new StringTokenizer(excludedAgents, ",");
                            while (tk.hasMoreTokens()) {
                                if (xff5.indexOf(tk.nextToken()) >= 0) {
                                    writeHttpHeader(bw, HTTP_STATUS_FORBIDDEN);
                                    bw.write("Bad agent: " + xff5);
                                    bw.flush();
                                    this.cr = null;
                                    try {
                                        br.close();
                                    } catch (Exception e4) {
                                    }
                                    try {
                                        bw.close();
                                    } catch (Exception e5) {
                                    }
                                    if (this.clientSocket != null) {
                                        try {
                                            this.clientSocket.close();
                                        } catch (Exception e6) {
                                        }
                                    }
                                    this.terminated = true;
                                    synchronized (threadPoolSync) {
                                        try {
                                            threadPoolSync.notifyAll();
                                        } finally {
                                            th = th;
                                            encodings = xff5;
                                            while (true) {
                                                try {
                                                } catch (Throwable th) {
                                                    th = th;
                                                }
                                            }
                                        }
                                    }
                                    long t2 = System.currentTimeMillis();
                                    long ms2 = t2 - this.starttime;
                                    PrintStream printStream = System.out;
                                    String agent = formattedTimeStamp(t2);
                                    printStream.println(agent + sessionInfo + " ip=" + sIp + " ms=" + ms2 + " -> " + getline2);
                                    return;
                                }
                            }
                        }
                        if (getline2.startsWith("GET /favicon.ico")) {
                            writeHttpHeader(bw, HTTP_STATUS_NOT_FOUND);
                            bw.flush();
                            this.cr = null;
                            try {
                                br.close();
                            } catch (Exception e7) {
                            }
                            try {
                                bw.close();
                            } catch (Exception e8) {
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e9) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                threadPoolSync.notifyAll();
                            }
                            long t3 = System.currentTimeMillis();
                            long ms3 = t3 - this.starttime;
                            System.out.println(formattedTimeStamp(t3) + sessionInfo + " ip=" + sIp + " ms=" + ms3 + " -> " + getline2);
                            return;
                        }
                        if (getline2.startsWith("GET /robots.txt")) {
                            writeHttpHeader(bw, HTTP_STATUS_OK);
                            bw.write("User-agent: *\n");
                            bw.write("Disallow: /\n");
                            bw.flush();
                            this.cr = null;
                            try {
                                br.close();
                            } catch (Exception e10) {
                            }
                            try {
                                bw.close();
                            } catch (Exception e11) {
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e12) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                threadPoolSync.notifyAll();
                            }
                            long t4 = System.currentTimeMillis();
                            long ms4 = t4 - this.starttime;
                            System.out.println(formattedTimeStamp(t4) + sessionInfo + " ip=" + sIp + " ms=" + ms4 + " -> " + getline2);
                            return;
                        }
                        String url = getline2.split(" ")[1];
                        RoutingParamCollector routingParamCollector = new RoutingParamCollector();
                        Map<String, String> params = routingParamCollector.getUrlParams(url);
                        long maxRunningTime = getMaxRunningTime();
                        if (params.containsKey("lonlats") && params.containsKey("profile")) {
                            RequestHandler handler = new ServerHandler(this.serviceContext, params);
                            RoutingContext rc = handler.readRoutingContext();
                            List<OsmNodeNamed> wplist = routingParamCollector.getWayPointList(params.get("lonlats"));
                            if (wplist.size() < 10) {
                                SuspectManager.nearRecentWps.add(wplist);
                            }
                            if (params.containsKey("profile")) {
                                params.remove("profile");
                            }
                            int engineMode = params.containsKey("engineMode") ? Integer.parseInt(params.get("engineMode")) : 0;
                            routingParamCollector.setParams(rc, wplist, params);
                            this.cr = new RoutingEngine(null, null, this.serviceContext.segmentDir, wplist, rc, engineMode);
                            this.cr.quite = true;
                            this.cr.doRun(maxRunningTime);
                            if (this.cr.getErrorMessage() != null) {
                                writeHttpHeader(bw, HTTP_STATUS_BAD_REQUEST);
                                bw.write(this.cr.getErrorMessage());
                                bw.write("\n");
                            } else {
                                OsmTrack track = this.cr.getFoundTrack();
                                String encodings2 = engineMode == 2 ? null : xff2;
                                if (encodings2 == null || encodings2.indexOf("gzip") < 0) {
                                    headers = null;
                                    writeHttpHeader(bw, handler.getMimeType(), handler.getFileName(), headers, HTTP_STATUS_OK);
                                    if (engineMode != 0 || engineMode == 4) {
                                        if (track != null) {
                                            if (headers != null) {
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                Writer w = new OutputStreamWriter(new GZIPOutputStream(baos), "UTF-8");
                                                w.write(handler.formatTrack(track));
                                                w.close();
                                                bw.flush();
                                                this.clientSocket.getOutputStream().write(baos.toByteArray());
                                            } else {
                                                bw.write(handler.formatTrack(track));
                                            }
                                        }
                                    } else if ((engineMode == 2 || engineMode == 3) && (s = this.cr.getFoundInfo()) != null) {
                                        bw.write(s);
                                    }
                                } else {
                                    headers = "Content-Encoding: gzip\r\n";
                                    writeHttpHeader(bw, handler.getMimeType(), handler.getFileName(), headers, HTTP_STATUS_OK);
                                    if (engineMode != 0) {
                                        if (track != null) {
                                        }
                                    }
                                }
                            }
                            bw.flush();
                            this.cr = null;
                            try {
                                br.close();
                            } catch (Exception e13) {
                            }
                            try {
                                bw.close();
                            } catch (Exception e14) {
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e15) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                threadPoolSync.notifyAll();
                            }
                            long t5 = System.currentTimeMillis();
                            long ms5 = t5 - this.starttime;
                            System.out.println(formattedTimeStamp(t5) + sessionInfo + " ip=" + sIp + " ms=" + ms5 + " -> " + getline2);
                            return;
                        }
                        if (!url.startsWith(PROFILE_UPLOAD_URL)) {
                            if (url.startsWith("/brouter/suspects")) {
                                writeHttpHeader(bw, url.endsWith(".json") ? "application/json" : "text/html", HTTP_STATUS_OK);
                                SuspectManager.process(url, bw);
                                this.cr = null;
                                try {
                                    br.close();
                                } catch (Exception e16) {
                                }
                                try {
                                    bw.close();
                                } catch (Exception e17) {
                                }
                                if (this.clientSocket != null) {
                                    try {
                                        this.clientSocket.close();
                                    } catch (Exception e18) {
                                    }
                                }
                                this.terminated = true;
                                synchronized (threadPoolSync) {
                                    threadPoolSync.notifyAll();
                                }
                                long t6 = System.currentTimeMillis();
                                long ms6 = t6 - this.starttime;
                                System.out.println(formattedTimeStamp(t6) + sessionInfo + " ip=" + sIp + " ms=" + ms6 + " -> " + getline2);
                                return;
                            }
                            writeHttpHeader(bw, HTTP_STATUS_NOT_FOUND);
                            bw.flush();
                            this.cr = null;
                            try {
                                br.close();
                            } catch (Exception e19) {
                            }
                            try {
                                bw.close();
                            } catch (Exception e20) {
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e21) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                threadPoolSync.notifyAll();
                            }
                            long t7 = System.currentTimeMillis();
                            long ms7 = t7 - this.starttime;
                            System.out.println(formattedTimeStamp(t7) + sessionInfo + " ip=" + sIp + " ms=" + ms7 + " -> " + getline2);
                            return;
                        }
                        if (getline2.startsWith("OPTIONS")) {
                            writeHttpHeader(bw, "text/plain", null, "Access-Control-Allow-Methods: GET, POST\r\nAccess-Control-Allow-Headers: Content-Type\r\n", HTTP_STATUS_OK);
                            bw.flush();
                            this.cr = null;
                            try {
                                br.close();
                            } catch (Exception e22) {
                            }
                            try {
                                bw.close();
                            } catch (Exception e23) {
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e24) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                try {
                                    threadPoolSync.notifyAll();
                                } catch (Throwable th2) {
                                    th = th2;
                                    while (true) {
                                        try {
                                            throw th;
                                        } catch (Throwable th3) {
                                            th = th3;
                                        }
                                    }
                                }
                            }
                            long t8 = System.currentTimeMillis();
                            long ms8 = t8 - this.starttime;
                            System.out.println(formattedTimeStamp(t8) + sessionInfo + " ip=" + sIp + " ms=" + ms8 + " -> " + getline2);
                            return;
                        }
                        writeHttpHeader(bw, "application/json", HTTP_STATUS_OK);
                        String profileId = url.length() > PROFILE_UPLOAD_URL.length() + 1 ? url.substring(PROFILE_UPLOAD_URL.length() + 1) : null;
                        ProfileUploadHandler uploadHandler = new ProfileUploadHandler(this.serviceContext);
                        uploadHandler.handlePostRequest(profileId, br, bw);
                        bw.flush();
                        this.cr = null;
                        try {
                            br.close();
                        } catch (Exception e25) {
                        }
                        try {
                            bw.close();
                        } catch (Exception e26) {
                        }
                        if (this.clientSocket != null) {
                            try {
                                this.clientSocket.close();
                            } catch (Exception e27) {
                            }
                        }
                        this.terminated = true;
                        synchronized (threadPoolSync) {
                            try {
                                threadPoolSync.notifyAll();
                            } catch (Throwable th4) {
                                th = th4;
                                while (true) {
                                    try {
                                        throw th;
                                    } catch (Throwable th5) {
                                        th = th5;
                                    }
                                }
                            }
                        }
                        long t9 = System.currentTimeMillis();
                        long ms9 = t9 - this.starttime;
                        PrintStream printStream2 = System.out;
                        String profileId2 = formattedTimeStamp(t9);
                        printStream2.println(profileId2 + sessionInfo + " ip=" + sIp + " ms=" + ms9 + " -> " + getline2);
                        return;
                    }
                    String agent2 = xff5;
                    if (getline2 == null) {
                        getline2 = line;
                    }
                    String line2 = line.toLowerCase();
                    xff5 = line2.startsWith("user-agent: ") ? line2.substring("user-agent: ".length()) : agent2;
                    xff4 = line2.startsWith("accept-encoding: ") ? line2.substring("accept-encoding: ".length()) : encodings;
                    xff3 = line2.startsWith("x-forwarded-for: ") ? line2.substring("x-forwarded-for: ".length()) : xff;
                    routingEngine = null;
                    z = true;
                } catch (Throwable th6) {
                    th = th6;
                    getline = getline2;
                    Throwable e28 = th;
                    try {
                        try {
                            writeHttpHeader(bw, HTTP_STATUS_INTERNAL_SERVER_ERROR);
                            bw.flush();
                        } catch (Throwable th7) {
                            this.cr = null;
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (Exception e29) {
                                }
                            }
                            if (bw != null) {
                                try {
                                    bw.close();
                                } catch (Exception e30) {
                                }
                            }
                            if (this.clientSocket != null) {
                                try {
                                    this.clientSocket.close();
                                } catch (Exception e31) {
                                }
                            }
                            this.terminated = true;
                            synchronized (threadPoolSync) {
                                threadPoolSync.notifyAll();
                                long t10 = System.currentTimeMillis();
                                long ms10 = t10 - this.starttime;
                                System.out.println(formattedTimeStamp(t10) + ((String) null) + " ip=" + ((String) null) + " ms=" + ms10 + " -> " + getline);
                                throw th7;
                            }
                        }
                    } catch (IOException e32) {
                    }
                    System.out.println("RouteServer got exception (will continue): " + String.valueOf(e28));
                    e28.printStackTrace();
                    this.cr = null;
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception e33) {
                        }
                    }
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (Exception e34) {
                        }
                    }
                    if (this.clientSocket != null) {
                        try {
                            this.clientSocket.close();
                        } catch (Exception e35) {
                        }
                    }
                    this.terminated = true;
                    synchronized (threadPoolSync) {
                        threadPoolSync.notifyAll();
                    }
                    long t11 = System.currentTimeMillis();
                    long ms11 = t11 - this.starttime;
                    System.out.println(formattedTimeStamp(t11) + ((String) null) + " ip=" + ((String) null) + " ms=" + ms11 + " -> " + getline);
                    return;
                }
            }
        } catch (Throwable th8) {
            th = th8;
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:76:? -> B:68:0x0231). Please report as a decompilation issue!!! */
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket;
        ServiceContext serviceContext;
        String dirs;
        File stackLog;
        System.out.println("BRouter 1.7.9 / 22042026");
        if (args.length != 5 && args.length != 6) {
            System.out.println("serve BRouter protocol");
            System.out.println("usage: java RouteServer <segmentdir> <profiledir> <customprofiledir> <port> <maxthreads> [bindaddress]");
            return;
        }
        ServiceContext serviceContext2 = new ServiceContext();
        serviceContext2.segmentDir = new File(args[0]);
        serviceContext2.profileDir = args[1];
        System.setProperty("profileBaseDir", serviceContext2.profileDir);
        String dirs2 = args[2];
        StringTokenizer tk = new StringTokenizer(dirs2, ",");
        serviceContext2.customProfileDir = tk.nextToken();
        serviceContext2.sharedProfileDir = tk.hasMoreTokens() ? tk.nextToken() : serviceContext2.customProfileDir;
        int maxthreads = Integer.parseInt(args[4]);
        ProfileCache.setSize(maxthreads * 2);
        Queue<RouteServer> threadQueue = new PriorityQueue<>();
        ServerSocket serverSocket2 = args.length > 5 ? new ServerSocket(Integer.parseInt(args[3]), 100, InetAddress.getByName(args[5])) : new ServerSocket(Integer.parseInt(args[3]));
        File stackLog2 = new File("stacks.txt");
        if (stackLog2.exists()) {
            StackSampler stackSampler = new StackSampler(stackLog2, 1000);
            stackSampler.start();
            System.out.println("*** sampling stacks into stacks.txt *** ");
        }
        while (true) {
            Socket clientSocket = serverSocket2.accept();
            RouteServer server = new RouteServer();
            server.serviceContext = serviceContext2;
            server.clientSocket = clientSocket;
            server.starttime = System.currentTimeMillis();
            cleanupThreadQueue(threadQueue);
            if (debug) {
                System.out.println("threadQueue.size()=" + threadQueue.size());
            }
            if (threadQueue.size() >= maxthreads) {
                synchronized (threadPoolSync) {
                    try {
                        long maxage = server.starttime - threadQueue.peek().starttime;
                        long maxWaitTime = 2000 - maxage;
                        if (debug) {
                            try {
                                serverSocket = serverSocket2;
                            } catch (Throwable th) {
                                th = th;
                            }
                            try {
                                System.out.println("maxage=" + maxage + " maxWaitTime=" + maxWaitTime);
                            } catch (Throwable th2) {
                                th = th2;
                                throw th;
                            }
                        } else {
                            serverSocket = serverSocket2;
                        }
                        try {
                            if (debug) {
                                Iterator<RouteServer> it = threadQueue.iterator();
                                while (it.hasNext()) {
                                    ServiceContext serviceContext3 = serviceContext2;
                                    String dirs3 = dirs2;
                                    try {
                                        File stackLog3 = stackLog2;
                                        Socket clientSocket2 = clientSocket;
                                        try {
                                            System.out.println("age=" + (server.starttime - it.next().starttime));
                                            serviceContext2 = serviceContext3;
                                            dirs2 = dirs3;
                                            stackLog2 = stackLog3;
                                            clientSocket = clientSocket2;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            throw th;
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                        throw th;
                                    }
                                }
                                serviceContext = serviceContext2;
                                dirs = dirs2;
                                stackLog = stackLog2;
                            } else {
                                serviceContext = serviceContext2;
                                dirs = dirs2;
                                stackLog = stackLog2;
                            }
                            if (maxWaitTime > 0) {
                                threadPoolSync.wait(maxWaitTime);
                            }
                            long t = System.currentTimeMillis();
                            System.out.println(formattedTimeStamp(t) + " contention! ms waited " + (t - server.starttime));
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                    }
                }
                cleanupThreadQueue(threadQueue);
                if (threadQueue.size() >= maxthreads) {
                    if (debug) {
                        System.out.println("stopping oldest thread...");
                    }
                    RouteServer oldest = threadQueue.poll();
                    oldest.stopRouter();
                    long t2 = System.currentTimeMillis();
                    System.out.println(formattedTimeStamp(t2) + " contention! ms killed " + (t2 - oldest.starttime));
                }
            } else {
                serverSocket = serverSocket2;
                serviceContext = serviceContext2;
                dirs = dirs2;
                stackLog = stackLog2;
            }
            threadQueue.add(server);
            server.start();
            if (debug) {
                System.out.println("thread started...");
            }
            serverSocket2 = serverSocket;
            serviceContext2 = serviceContext;
            dirs2 = dirs;
            stackLog2 = stackLog;
        }
    }

    private static Map<String, String> getUrlParams(String url) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String decoded = URLDecoder.decode(url, "UTF-8");
        StringTokenizer tk = new StringTokenizer(decoded, "?&");
        while (tk.hasMoreTokens()) {
            String t = tk.nextToken();
            StringTokenizer tk2 = new StringTokenizer(t, "=");
            if (tk2.hasMoreTokens()) {
                String key = tk2.nextToken();
                if (tk2.hasMoreTokens()) {
                    String value = tk2.nextToken();
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    private static long getMaxRunningTime() {
        String sMaxRunningTime = System.getProperty("maxRunningTime");
        if (sMaxRunningTime == null) {
            return 60000L;
        }
        long maxRunningTime = Integer.parseInt(sMaxRunningTime) * 1000;
        return maxRunningTime;
    }

    private static void writeHttpHeader(BufferedWriter bw, String status) throws IOException {
        writeHttpHeader(bw, "text/plain", status);
    }

    private static void writeHttpHeader(BufferedWriter bw, String mimeType, String status) throws IOException {
        writeHttpHeader(bw, mimeType, null, status);
    }

    private static void writeHttpHeader(BufferedWriter bw, String mimeType, String fileName, String status) throws IOException {
        writeHttpHeader(bw, mimeType, fileName, null, status);
    }

    private static void writeHttpHeader(BufferedWriter bw, String mimeType, String fileName, String headers, String status) throws IOException {
        bw.write(String.format("HTTP/1.1 %s\r\n", status));
        bw.write("Connection: close\r\n");
        bw.write("Content-Type: " + mimeType + "; charset=utf-8\r\n");
        if (fileName != null) {
            bw.write("Content-Disposition: attachment; filename=\"" + fileName + "\"\r\n");
        }
        bw.write("Access-Control-Allow-Origin: *\r\n");
        if (headers != null) {
            bw.write(headers);
        }
        bw.write("\r\n");
    }

    private static void cleanupThreadQueue(Queue<RouteServer> threadQueue) {
        boolean removedItem;
        do {
            removedItem = false;
            Iterator<RouteServer> it = threadQueue.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                RouteServer t = it.next();
                if (t.terminated) {
                    threadQueue.remove(t);
                    removedItem = true;
                    break;
                }
            }
        } while (removedItem);
    }

    @Override // java.lang.Comparable
    public int compareTo(RouteServer t) {
        if (this.starttime < t.starttime) {
            return -1;
        }
        return this.starttime > t.starttime ? 1 : 0;
    }
}

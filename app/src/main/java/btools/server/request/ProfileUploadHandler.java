package btools.server.request;

import btools.router.RoutingContext;
import btools.router.RoutingEngine;
import btools.server.ServiceContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ProfileUploadHandler {
    public static final String CUSTOM_PREFIX = "custom_";
    private static final int MAX_LENGTH = 100000;
    public static final String SHARED_PREFIX = "shared_";
    private ServiceContext serviceContext;

    public ProfileUploadHandler(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public void handlePostRequest(String profileId, BufferedReader br, BufferedWriter response) throws IOException {
        String id;
        BufferedWriter fileWriter = null;
        try {
            if (profileId != null) {
                id = profileId.substring(CUSTOM_PREFIX.length());
            } else {
                id = new StringBuilder().append(System.currentTimeMillis()).toString();
            }
            File file = new File(getOrCreateCustomProfileDir(), id + ".brf");
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            readPostData(br, fileWriter, id);
            fileWriter.flush();
            Map<String, String> responseData = new HashMap<>();
            responseData.put("profileid", CUSTOM_PREFIX + id);
            validateProfile(id, responseData);
            response.write(toJSON(responseData));
            try {
                fileWriter.close();
            } catch (Exception e) {
            }
        } catch (Throwable th) {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e2) {
                }
            }
            throw th;
        }
    }

    private File getOrCreateCustomProfileDir() {
        File customProfileDir = new File(this.serviceContext.profileDir, this.serviceContext.customProfileDir);
        if (!customProfileDir.exists()) {
            customProfileDir.mkdir();
        }
        return customProfileDir;
    }

    private static void readPostData(BufferedReader ir, BufferedWriter bw, String id) throws IOException {
        int numChars = 0;
        do {
            if (!ir.ready()) {
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                }
                if (!ir.ready()) {
                    return;
                }
            }
            int c = ir.read();
            if (c != -1) {
                bw.write(c);
                numChars++;
            } else {
                return;
            }
        } while (numChars <= MAX_LENGTH);
        throw new IOException("Maximum number of characters exceeded (100000, " + id + ")");
    }

    private String toJSON(Map<String, String> data) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            sb.append(first ? "\n" : ",\n");
            sb.append("  \"");
            sb.append(entry.getKey());
            sb.append("\": \"");
            sb.append(entry.getValue());
            sb.append("\"");
            first = false;
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    public void validateProfile(String id, Map<String, String> responseData) {
        try {
            RoutingContext rc = new RoutingContext();
            rc.localFunction = this.serviceContext.customProfileDir + "/" + id;
            new RoutingEngine(null, null, null, null, rc);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "";
            } else if (msg.indexOf("does not contain expressions for context") >= 0) {
                msg = msg.substring(msg.indexOf("does not contain expressions for context"));
            }
            responseData.put("error", "Profile error: " + msg);
        }
    }
}

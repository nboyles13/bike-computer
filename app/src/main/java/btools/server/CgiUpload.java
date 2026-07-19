package btools.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;

/* JADX INFO: loaded from: classes.dex */
public class CgiUpload {
    public static void main(String[] args) {
        try {
            _main(args);
        } catch (Exception e) {
            System.out.println("unexpected exception: " + String.valueOf(e));
        }
    }

    private static void _main(String[] args) throws Exception {
        String line;
        int coordsEnd;
        String htmlTemplate = args[0];
        String customeProfileDir = args[1];
        String id = new StringBuilder().append(System.currentTimeMillis()).toString();
        System.out.println("Content-type: text/html");
        System.out.println();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(customeProfileDir + "/" + id + ".brf")));
        BufferedReader ir = new BufferedReader(new InputStreamReader(System.in));
        String postData = ir.readLine();
        String[] coordValues = new String[4];
        if (postData != null) {
            int coordsIdx = postData.indexOf("coords=");
            if (coordsIdx >= 0 && (coordsEnd = postData.indexOf(38)) >= 0) {
                String coordsString = postData.substring(coordsIdx + 7, coordsEnd);
                postData = postData.substring(coordsEnd + 1);
                int pos = 0;
                for (int idx = 0; idx < 4; idx++) {
                    int p = coordsString.indexOf(95, pos);
                    coordValues[idx] = coordsString.substring(pos, p);
                    pos = p + 1;
                }
            }
            int sepIdx = postData.indexOf(61);
            if (sepIdx >= 0) {
                postData = postData.substring(sepIdx + 1);
            }
            postData = URLDecoder.decode(postData, "ISO-8859-1");
            bw.write(postData);
        }
        bw.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(htmlTemplate)));
        while (true) {
            String line2 = br.readLine();
            if (line2 != null) {
                if (line2.indexOf("<!-- sample profiles -->") >= 0) {
                    line = "    <option value=\"../customprofiles/" + id + "\">custom</option>";
                } else if (line2.indexOf("paste your profile here") >= 0) {
                    System.out.println("<textarea type=\"text\" name=\"profile\" rows=30 cols=100>");
                    System.out.println(postData);
                    line = "</textarea>";
                } else {
                    line = replaceCoord(replaceCoord(replaceCoord(replaceCoord(line2, "lonfrom", coordValues[0]), "latfrom", coordValues[1]), "lonto", coordValues[2]), "latto", coordValues[3]);
                }
                System.out.println(line);
            } else {
                br.close();
                return;
            }
        }
    }

    private static String replaceCoord(String line, String name, String value) {
        String inputTag = "<td><input type=\"text\" name=\"" + name + "\"";
        if (line.indexOf(inputTag) >= 0) {
            return inputTag + " value=\"" + value + "\"></td>";
        }
        return line;
    }
}

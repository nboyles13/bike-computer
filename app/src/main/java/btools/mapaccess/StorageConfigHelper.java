package btools.mapaccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/* JADX INFO: loaded from: classes.dex */
public class StorageConfigHelper {
    public static File getSecondarySegmentDir(File segmentDir) {
        return getStorageLocation(segmentDir, "secondary_segment_dir=");
    }

    public static File getAdditionalMaptoolDir(File segmentDir) {
        return getStorageLocation(segmentDir, "additional_maptool_dir=");
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0030, code lost:
    
        r4 = r4.substring(r7.length()).trim();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0042, code lost:
    
        if (r4.startsWith("/") == false) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0044, code lost:
    
        r5 = new java.io.File(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x004a, code lost:
    
        r5 = new java.io.File(r6, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x004f, code lost:
    
        r0 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0054, code lost:
    
        if (r0.exists() != false) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0056, code lost:
    
        r0 = null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static File getStorageLocation(File segmentDir, String tag) {
        File res = null;
        BufferedReader br = null;
        File configFile = new File(segmentDir, "storageconfig.txt");
        try {
            try {
                br = new BufferedReader(new FileReader(configFile));
                while (true) {
                    String line = br.readLine();
                    if (line != null) {
                        String line2 = line.trim();
                        if (!line2.startsWith("#") && line2.startsWith(tag)) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                br.close();
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            if (br != null) {
                br.close();
            }
        } catch (Throwable th) {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e3) {
                }
            }
            throw th;
        }
        return res;
    }
}

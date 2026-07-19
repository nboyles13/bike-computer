package btools.router;

import btools.mapaccess.StorageConfigHelper;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public final class RoutingHelper {
    public static File getAdditionalMaptoolDir(File segmentDir) {
        return StorageConfigHelper.getAdditionalMaptoolDir(segmentDir);
    }

    public static File getSecondarySegmentDir(File segmentDir) {
        return StorageConfigHelper.getSecondarySegmentDir(segmentDir);
    }

    public static boolean hasDirectoryAnyDatafiles(File segmentDir) {
        if (hasAnyDatafiles(segmentDir)) {
            return true;
        }
        File secondary = StorageConfigHelper.getSecondarySegmentDir(segmentDir);
        if (secondary != null) {
            return hasAnyDatafiles(secondary);
        }
        return false;
    }

    private static boolean hasAnyDatafiles(File dir) {
        String[] fileNames = dir.list();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".rd5")) {
                return true;
            }
        }
        return false;
    }
}

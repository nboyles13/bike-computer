package btools.router;

import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import btools.expressions.BExpressionMetaData;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public final class ProfileCache {
    private static ProfileCache[] apc = new ProfileCache[1];
    private static boolean debug = Boolean.getBoolean("debugProfileCache");
    private static File lastLookupFile;
    private static long lastLookupTimestamp;
    private BExpressionContextNode expctxNode;
    private BExpressionContextWay expctxWay;
    private File lastProfileFile;
    private long lastProfileTimestamp;
    private long lastUseTime;
    private boolean profilesBusy;

    public static synchronized void setSize(int size) {
        apc = new ProfileCache[size];
    }

    public static synchronized boolean parseProfile(RoutingContext rc) {
        File profileDir;
        File profileFile;
        String profileBaseDir = System.getProperty("profileBaseDir");
        if (profileBaseDir == null) {
            profileDir = new File(rc.localFunction).getParentFile();
            profileFile = new File(rc.localFunction);
        } else {
            profileDir = new File(profileBaseDir);
            profileFile = new File(profileDir, rc.localFunction + ".brf");
        }
        rc.profileTimestamp = (profileFile.lastModified() + rc.getKeyValueChecksum()) << 24;
        File lookupFile = new File(profileDir, "lookups.dat");
        if (!lookupFile.equals(lastLookupFile) || lookupFile.lastModified() != lastLookupTimestamp) {
            if (lastLookupFile != null) {
                System.out.println("******** invalidating profile-cache after lookup-file update ******** ");
            }
            apc = new ProfileCache[apc.length];
            lastLookupFile = lookupFile;
            lastLookupTimestamp = lookupFile.lastModified();
        }
        ProfileCache lru = null;
        int unusedSlot = -1;
        int i = 0;
        while (true) {
            if (i >= apc.length) {
                break;
            }
            ProfileCache pc = apc[i];
            if (pc != null) {
                if (pc.profilesBusy || !profileFile.equals(pc.lastProfileFile)) {
                    if (lru == null || lru.lastUseTime > pc.lastUseTime) {
                        lru = pc;
                    }
                } else {
                    if (rc.profileTimestamp == pc.lastProfileTimestamp) {
                        rc.expctxWay = pc.expctxWay;
                        rc.expctxNode = pc.expctxNode;
                        rc.readGlobalConfig();
                        pc.profilesBusy = true;
                        return true;
                    }
                    lru = pc;
                    unusedSlot = -1;
                }
            } else if (unusedSlot < 0) {
                unusedSlot = i;
            }
            i++;
        }
        BExpressionMetaData meta = new BExpressionMetaData();
        rc.expctxWay = new BExpressionContextWay(rc.memoryclass * 512, meta);
        rc.expctxNode = new BExpressionContextNode(0, meta);
        rc.expctxNode.setForeignContext(rc.expctxWay);
        meta.readMetaData(new File(profileDir, "lookups.dat"));
        rc.expctxWay.parseFile(profileFile, "global", rc.keyValues);
        rc.expctxNode.parseFile(profileFile, "global", rc.keyValues);
        rc.readGlobalConfig();
        if (rc.processUnusedTags) {
            rc.expctxWay.setAllTagsUsed();
        }
        if (lru == null || unusedSlot >= 0) {
            lru = new ProfileCache();
            if (unusedSlot >= 0) {
                apc[unusedSlot] = lru;
                if (debug) {
                    System.out.println("******* adding new profile at idx=" + unusedSlot + " for " + String.valueOf(profileFile));
                }
            }
        }
        if (lru.lastProfileFile != null && debug) {
            System.out.println("******* replacing profile of age " + ((System.currentTimeMillis() - lru.lastUseTime) / 1000) + " sec " + String.valueOf(lru.lastProfileFile) + "->" + String.valueOf(profileFile));
        }
        lru.lastProfileTimestamp = rc.profileTimestamp;
        lru.lastProfileFile = profileFile;
        lru.expctxWay = rc.expctxWay;
        lru.expctxNode = rc.expctxNode;
        lru.profilesBusy = true;
        lru.lastUseTime = System.currentTimeMillis();
        return false;
    }

    public static synchronized void releaseProfile(RoutingContext rc) {
        int i = 0;
        while (true) {
            if (i < apc.length) {
                ProfileCache pc = apc[i];
                if (pc == null || rc.expctxWay != pc.expctxWay || rc.expctxNode != pc.expctxNode) {
                    i++;
                } else {
                    pc.profilesBusy = false;
                    break;
                }
            } else {
                break;
            }
        }
        rc.expctxWay = null;
        rc.expctxNode = null;
    }
}

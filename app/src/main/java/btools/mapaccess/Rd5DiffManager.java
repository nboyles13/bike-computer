package btools.mapaccess;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public final class Rd5DiffManager {
    public static void main(String[] args) throws Exception {
        calcDiffs(new File(args[0]), new File(args[1]));
    }

    public static void calcDiffs(File oldDir, File newDir) throws Exception {
        File newDiffDir;
        File[] filesNew;
        File oldDiffDir;
        int i;
        File file = oldDir;
        File oldDiffDir2 = new File(file, "diff");
        File newDiffDir2 = new File(newDir, "diff");
        File[] filesNew2 = newDir.listFiles();
        int length = filesNew2.length;
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            File fn = filesNew2[i3];
            String name = fn.getName();
            if (!name.endsWith(".rd5")) {
                oldDiffDir = oldDiffDir2;
                newDiffDir = newDiffDir2;
                filesNew = filesNew2;
            } else if (fn.length() < 1048576) {
                oldDiffDir = oldDiffDir2;
                newDiffDir = newDiffDir2;
                filesNew = filesNew2;
            } else {
                String basename = name.substring(i2, name.length() - 4);
                File fo = new File(file, name);
                if (!fo.isFile()) {
                    oldDiffDir = oldDiffDir2;
                    newDiffDir = newDiffDir2;
                    filesNew = filesNew2;
                } else {
                    String md5 = getMD5(fo);
                    String md5New = getMD5(fn);
                    System.out.println("name=" + name + " md5=" + md5);
                    File specificNewDiffs = new File(newDiffDir2, basename);
                    specificNewDiffs.mkdirs();
                    String diffFileName = md5 + ".df5";
                    File diffFile = new File(specificNewDiffs, diffFileName);
                    newDiffDir = newDiffDir2;
                    String dummyDiffFileName = md5New + ".df5";
                    filesNew = filesNew2;
                    File dummyDiffFile = new File(specificNewDiffs, dummyDiffFileName);
                    dummyDiffFile.createNewFile();
                    Rd5DiffTool.diff2files(fo, fn, diffFile);
                    File specificOldDiffs = new File(oldDiffDir2, basename);
                    if (!specificOldDiffs.isDirectory()) {
                        oldDiffDir = oldDiffDir2;
                    } else {
                        oldDiffDir = oldDiffDir2;
                        File[] oldDiffs = specificOldDiffs.listFiles();
                        int length2 = oldDiffs.length;
                        int i4 = 0;
                        while (i4 < length2) {
                            int i5 = length2;
                            File od = oldDiffs[i4];
                            File[] oldDiffs2 = oldDiffs;
                            if (!od.getName().endsWith(".df5")) {
                                i = length;
                            } else if (System.currentTimeMillis() - od.lastModified() > 777600000) {
                                i = length;
                            } else {
                                File updatedDiff = new File(specificNewDiffs, od.getName());
                                if (updatedDiff.exists()) {
                                    i = length;
                                } else {
                                    Rd5DiffTool.addDeltas(od, diffFile, updatedDiff);
                                    i = length;
                                    updatedDiff.setLastModified(od.lastModified());
                                }
                            }
                            i4++;
                            length2 = i5;
                            oldDiffs = oldDiffs2;
                            length = i;
                        }
                    }
                }
            }
            i3++;
            file = oldDir;
            newDiffDir2 = newDiffDir;
            filesNew2 = filesNew;
            oldDiffDir2 = oldDiffDir;
            length = length;
            i2 = 0;
        }
    }

    public static String getMD5(File f) throws IOException {
        int len;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            DigestInputStream dis = new DigestInputStream(bis, md);
            byte[] buf = new byte[8192];
            do {
                len = dis.read(buf);
            } while (len > 0);
            dis.close();
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int v = b & UByte.MAX_VALUE;
                sb.append(hexChar(v >>> 4)).append(hexChar(v & 15));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5 algorithm not available", e);
        }
    }

    private static char hexChar(int v) {
        return (char) (v > 9 ? (v - 10) + 97 : v + 48);
    }
}

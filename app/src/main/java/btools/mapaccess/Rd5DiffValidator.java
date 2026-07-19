package btools.mapaccess;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public final class Rd5DiffValidator {
    public static void main(String[] args) throws Exception {
        validateDiffs(new File(args[0]), new File(args[1]));
    }

    public static void validateDiffs(File oldDir, File newDir) throws Exception {
        File oldDiffDir;
        File newDiffDir;
        File[] filesNew;
        File oldDiffDir2 = new File(oldDir, "diff");
        File newDiffDir2 = new File(newDir, "diff");
        File[] filesNew2 = newDir.listFiles();
        int length = filesNew2.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            File fn = filesNew2[i2];
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
                String basename = name.substring(i, name.length() - 4);
                File fo = new File(oldDir, name);
                if (!fo.isFile()) {
                    oldDiffDir = oldDiffDir2;
                    newDiffDir = newDiffDir2;
                    filesNew = filesNew2;
                } else {
                    String md5 = Rd5DiffManager.getMD5(fo);
                    String md5New = Rd5DiffManager.getMD5(fn);
                    System.out.println("name=" + name + " md5=" + md5);
                    File specificNewDiffs = new File(newDiffDir2, basename);
                    String diffFileName = md5 + ".df5";
                    File diffFile = new File(specificNewDiffs, diffFileName);
                    oldDiffDir = oldDiffDir2;
                    newDiffDir = newDiffDir2;
                    filesNew = filesNew2;
                    File fcmp = new File(oldDir, name + "_tmp");
                    Rd5DiffTool.recoverFromDelta(fo, diffFile, fcmp, new Rd5DiffTool());
                    String md5Cmp = Rd5DiffManager.getMD5(fcmp);
                    if (!md5Cmp.equals(md5New)) {
                        throw new RuntimeException("**************** md5 mismatch!! *****************");
                    }
                }
            }
            i2++;
            oldDiffDir2 = oldDiffDir;
            newDiffDir2 = newDiffDir;
            filesNew2 = filesNew;
            i = 0;
        }
    }
}

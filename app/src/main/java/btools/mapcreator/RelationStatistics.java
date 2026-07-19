package btools.mapcreator;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class RelationStatistics extends MapCreatorBase {
    public static void main(String[] args) throws Exception {
        System.out.println("*** RelationStatistics: count relation networks");
        if (args.length != 1) {
            System.out.println("usage: java WayCutter <relation-file>");
        } else {
            new RelationStatistics().process(new File(args[0]));
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:6:0x001c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void process(File relationFileIn) throws Exception {
        Map<String, long[]> relstats = new HashMap<>();
        DataInputStream dis = createInStream(relationFileIn);
        while (true) {
            try {
                readId(dis);
                String network = dis.readUTF();
                int waycount = 0;
                while (wid != -1) {
                    waycount++;
                }
                long[] stat = relstats.get(network);
                if (stat == null) {
                    stat = new long[2];
                    relstats.put(network, stat);
                }
                stat[0] = stat[0] + 1;
                stat[1] = stat[1] + ((long) waycount);
            } catch (EOFException e) {
                dis.close();
                for (String network2 : relstats.keySet()) {
                    long[] stat2 = relstats.get(network2);
                    System.out.println("network: " + network2 + " has " + stat2[0] + " relations with " + stat2[1] + " ways");
                }
                return;
            }
        }
    }
}

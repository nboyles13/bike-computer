package btools.mapcreator;

import btools.expressions.BExpressionContextWay;
import btools.expressions.BExpressionMetaData;
import btools.util.CompactLongSet;
import btools.util.FrozenLongSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class RelationMerger extends MapCreatorBase {
    private BExpressionContextWay expctxCheck;
    private BExpressionContextWay expctxReport;
    private CompactLongSet routesetall;
    private Map<String, CompactLongSet> routesets;
    private DataOutputStream wayOutStream;

    public static void main(String[] args) throws Exception {
        System.out.println("*** RelationMerger: merge relations into ways");
        if (args.length != 6) {
            System.out.println("usage: java RelationMerger <way-file-in> <way-file-out> <relation-file> <lookup-file> <report-profile> <check-profile>");
        } else {
            new RelationMerger().process(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), new File(args[4]), new File(args[5]));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00fd A[LOOP:2: B:31:0x00f7->B:33:0x00fd, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void init(File relationFileIn, File lookupFile, File reportProfile, File checkProfile) throws Exception {
        String str;
        BExpressionMetaData metaReport;
        String str2 = "_";
        BExpressionMetaData metaReport2 = new BExpressionMetaData();
        this.expctxReport = new BExpressionContextWay(metaReport2);
        metaReport2.readMetaData(lookupFile);
        BExpressionMetaData metaCheck = new BExpressionMetaData();
        this.expctxCheck = new BExpressionContextWay(metaCheck);
        metaCheck.readMetaData(lookupFile);
        this.expctxReport.parseFile(reportProfile, "global");
        this.expctxCheck.parseFile(checkProfile, "global");
        this.routesets = new HashMap();
        this.routesetall = new CompactLongSet();
        DataInputStream dis = createInStream(relationFileIn);
        while (true) {
            try {
                readId(dis);
                String route = dis.readUTF();
                String network = dis.readUTF();
                String state = dis.readUTF();
                int value = "proposed".equals(state) ? 3 : 2;
                String tagname = "route_" + route + str2 + network;
                CompactLongSet routeset = null;
                if (this.expctxCheck.getLookupNameIdx(tagname) < 0) {
                    str = str2;
                } else {
                    try {
                        String key = tagname + str2 + value;
                        str = str2;
                        routeset = this.routesets.get(key);
                        if (routeset == null) {
                            routeset = new CompactLongSet();
                            this.routesets.put(key, routeset);
                        }
                    } catch (EOFException e) {
                        dis.close();
                        for (String key2 : this.routesets.keySet()) {
                            CompactLongSet routeset2 = new FrozenLongSet(this.routesets.get(key2));
                            this.routesets.put(key2, routeset2);
                            System.out.println("marked " + routeset2.size() + " routes for key: " + key2);
                        }
                        return;
                    }
                }
                while (true) {
                    long wid = readId(dis);
                    metaReport = metaReport2;
                    if (wid == -1) {
                        break;
                    }
                    if (routeset != null) {
                        try {
                            if (!routeset.contains(wid)) {
                                routeset.add(wid);
                                this.routesetall.add(wid);
                            }
                        } catch (EOFException e2) {
                            dis.close();
                            while (r0.hasNext()) {
                            }
                            return;
                        }
                    }
                    metaReport2 = metaReport;
                }
                str2 = str;
                metaReport2 = metaReport;
            } catch (EOFException e3) {
            }
        }
    }

    public void process(File wayFileIn, File wayFileOut, File relationFileIn, File lookupFile, File reportProfile, File checkProfile) throws Exception {
        init(relationFileIn, lookupFile, reportProfile, checkProfile);
        this.wayOutStream = createOutStream(wayFileOut);
        new WayIterator(this, true).processFile(wayFileIn);
        this.wayOutStream.close();
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData data) throws Exception {
        if (this.routesetall.contains(data.wid)) {
            boolean ok = true;
            this.expctxReport.evaluate(false, data.description);
            boolean warn = ((double) this.expctxReport.getCostfactor()) >= 10000.0d;
            if (warn) {
                this.expctxCheck.evaluate(false, data.description);
                ok = ((double) this.expctxCheck.getCostfactor()) < 10000.0d;
                System.out.println("** relation access conflict for wid = " + data.wid + " tags:" + this.expctxReport.getKeyValueDescription(false, data.description) + " (ok=" + ok + ")");
            }
            if (ok) {
                this.expctxReport.decode(data.description);
                for (String key : this.routesets.keySet()) {
                    CompactLongSet routeset = this.routesets.get(key);
                    if (routeset.contains(data.wid)) {
                        int sepIdx = key.lastIndexOf(95);
                        String tagname = key.substring(0, sepIdx);
                        int val = Integer.parseInt(key.substring(sepIdx + 1));
                        this.expctxReport.addSmallestLookupValue(tagname, val);
                    }
                }
                data.description = this.expctxReport.encode();
            }
        }
        if (this.wayOutStream != null) {
            data.writeTo(this.wayOutStream);
        }
    }
}

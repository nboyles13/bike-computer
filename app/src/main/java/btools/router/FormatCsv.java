package btools.router;

import java.io.BufferedWriter;
import java.io.StringWriter;

/* JADX INFO: loaded from: classes.dex */
public class FormatCsv extends Formatter {
    public FormatCsv(RoutingContext rc) {
        super(rc);
    }

    @Override // btools.router.Formatter
    public String format(OsmTrack t) {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            writeMessages(bw, t);
            return sw.toString();
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public void writeMessages(BufferedWriter bw, OsmTrack t) throws Exception {
        dumpLine(bw, "Longitude\tLatitude\tElevation\tDistance\tCostPerKm\tElevCost\tTurnCost\tNodeCost\tInitialCost\tWayTags\tNodeTags\tTime\tEnergy");
        for (String m : t.aggregateMessages()) {
            dumpLine(bw, m);
        }
        if (bw != null) {
            bw.close();
        }
    }

    private void dumpLine(BufferedWriter bw, String s) throws Exception {
        if (bw == null) {
            System.out.println(s);
        } else {
            bw.write(s);
            bw.write("\n");
        }
    }
}

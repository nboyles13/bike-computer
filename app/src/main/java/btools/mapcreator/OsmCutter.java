package btools.mapcreator;

import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import btools.expressions.BExpressionMetaData;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OsmCutter extends MapCreatorBase {
    private BExpressionContextNode _expctxNode;
    private BExpressionContextWay _expctxWay;
    private long changesetsParsed;
    private DataOutputStream cyclewayDos;
    private DatabasePseudoTagProvider dbPseudoTagProvider;
    public NodeFilter nodeFilter;
    private long nodesParsed;
    private long recordCnt;
    private long relsParsed;
    public RestrictionCutter restrictionCutter;
    private DataOutputStream restrictionsDos;
    public WayCutter wayCutter;
    private DataOutputStream wayDos;
    private long waysParsed;

    public static void main(String[] args) throws Exception {
        System.out.println("*** OsmCutter: cut an osm map in node-tiles + a way file");
        if (args.length != 6 && args.length != 7) {
            System.out.println("usage: bzip2 -dc <map> | java OsmCutter <lookup-file> <out-tile-dir> <out-way-file> <out-rel-file> <out-res-file> <filter-profile>");
            System.out.println("or   : java OsmCutter <lookup-file> <out-tile-dir> <out-way-file> <out-rel-file> <out-res-file> <filter-profile> <inputfile> ");
        } else {
            new OsmCutter().process(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), new File(args[4]), new File(args[5]), args.length > 6 ? new File(args[6]) : null);
        }
    }

    public void process(File lookupFile, File outTileDir, File wayFile, File relFile, File resFile, File profileFile, File mapFile) throws Exception {
        if (!lookupFile.exists()) {
            throw new IllegalArgumentException("lookup-file: " + String.valueOf(lookupFile) + " does not exist");
        }
        BExpressionMetaData meta = new BExpressionMetaData();
        this._expctxWay = new BExpressionContextWay(meta);
        this._expctxNode = new BExpressionContextNode(meta);
        meta.readMetaData(lookupFile);
        this._expctxWay.parseFile(profileFile, "global");
        this.outTileDir = outTileDir;
        if (!outTileDir.isDirectory()) {
            throw new RuntimeException("out tile directory " + String.valueOf(outTileDir) + " does not exist");
        }
        this.wayDos = wayFile == null ? null : new DataOutputStream(new BufferedOutputStream(new FileOutputStream(wayFile)));
        this.cyclewayDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(relFile)));
        if (resFile != null) {
            this.restrictionsDos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(resFile)));
        }
        long t0 = System.currentTimeMillis();
        new OsmParser().readMap(mapFile, this, this, this);
        long t1 = System.currentTimeMillis();
        System.out.println("parsing time (ms) =" + (t1 - t0));
        closeTileOutStreams();
        if (this.wayDos != null) {
            this.wayDos.close();
        }
        this.cyclewayDos.close();
        if (this.restrictionsDos != null) {
            this.restrictionsDos.close();
        }
        System.out.println(statsLine());
    }

    private void checkStats() {
        long j = this.recordCnt + 1;
        this.recordCnt = j;
        if (j % 1000000 == 0) {
            System.out.println(statsLine());
        }
    }

    private String statsLine() {
        return "records read: " + this.recordCnt + " nodes=" + this.nodesParsed + " ways=" + this.waysParsed + " rels=" + this.relsParsed + " changesets=" + this.changesetsParsed;
    }

    public void setDbTagFilename(String filename) {
        this.dbPseudoTagProvider = new DatabasePseudoTagProvider(filename, null);
    }

    public void setDbTagDatabase(String jdbcurl) {
        this.dbPseudoTagProvider = new DatabasePseudoTagProvider(null, jdbcurl);
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        this.nodesParsed++;
        checkStats();
        if (this.dbPseudoTagProvider != null) {
            this.dbPseudoTagProvider.addNodeTags(n);
        }
        if (n.getTagsOrNull() != null) {
            int[] lookupData = this._expctxNode.createNewLookupData();
            for (Map.Entry<String, String> e : n.getTagsOrNull().entrySet()) {
                this._expctxNode.addLookupValue(e.getKey(), e.getValue(), lookupData);
            }
            n.description = this._expctxNode.encode(lookupData);
        }
        int tileIndex = getTileIndex(n.ilon, n.ilat);
        if (tileIndex >= 0) {
            n.writeTo(getOutStreamForTile(tileIndex));
            if (this.wayCutter != null) {
                this.wayCutter.nextNode(n);
            }
        }
    }

    private void generatePseudoTags(Map<String, String> map) {
        String concrete = null;
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = e.getKey();
            if ("concrete".equals(key)) {
                return;
            }
            if ("surface".equals(key)) {
                String value = e.getValue();
                if (value.startsWith("concrete:")) {
                    concrete = value.substring("concrete:".length());
                }
            }
        }
        if (concrete != null) {
            map.put("concrete", concrete);
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData w) throws Exception {
        this.waysParsed++;
        checkStats();
        if (w.getTagsOrNull() == null) {
            return;
        }
        if (this.dbPseudoTagProvider != null) {
            this.dbPseudoTagProvider.addWayTags(w.wid, w.getTagsOrNull());
        }
        generatePseudoTags(w.getTagsOrNull());
        int[] lookupData = this._expctxWay.createNewLookupData();
        for (String key : w.getTagsOrNull().keySet()) {
            String value = w.getTag(key);
            this._expctxWay.addLookupValue(key, value.replace(' ', '_'), lookupData);
        }
        w.description = this._expctxWay.encode(lookupData);
        if (w.description == null) {
            return;
        }
        this._expctxWay.evaluate(false, w.description);
        boolean ok = ((double) this._expctxWay.getCostfactor()) < 10000.0d;
        this._expctxWay.evaluate(true, w.description);
        if (!ok && !(((double) this._expctxWay.getCostfactor()) < 10000.0d)) {
            return;
        }
        if (this.wayDos != null) {
            w.writeTo(this.wayDos);
        }
        if (this.wayCutter != null) {
            this.wayCutter.nextWay(w);
        }
        if (this.nodeFilter != null) {
            this.nodeFilter.nextWay(w);
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.RelationListener
    public void nextRelation(RelationData r) throws IOException {
        this.relsParsed++;
        checkStats();
        String route = r.getTag("route");
        if (route == null) {
            return;
        }
        String network = r.getTag("network");
        if (network == null) {
            network = "";
        }
        String state = r.getTag("state");
        if (state == null) {
            state = "";
        }
        writeId(this.cyclewayDos, r.rid);
        this.cyclewayDos.writeUTF(route);
        this.cyclewayDos.writeUTF(network);
        this.cyclewayDos.writeUTF(state);
        for (int i = 0; i < r.ways.size(); i++) {
            long wid = r.ways.get(i);
            writeId(this.cyclewayDos, wid);
        }
        writeId(this.cyclewayDos, -1L);
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.RelationListener
    public void nextRestriction(RelationData r, long fromWid, long toWid, long viaNid) throws Exception {
        RelationData relationData = r;
        String type = relationData.getTag("type");
        if (type != null && "restriction".equals(type)) {
            short exceptions = 0;
            String except = relationData.getTag("except");
            if (except != null) {
                short exceptions2 = (short) (toBit("bicycle", 0, except) | 0);
                exceptions = (short) (toBit("hgv", 4, except) | ((short) (toBit("psv", 3, except) | ((short) (toBit("forestry", 2, except) | ((short) (toBit("agricultural", 2, except) | ((short) (toBit("motorcar", 1, except) | exceptions2)))))))));
            }
            for (String restrictionKey : r.getTagsOrNull().keySet()) {
                if (restrictionKey.equals("restriction") || restrictionKey.startsWith("restriction:")) {
                    String restriction = relationData.getTag(restrictionKey);
                    RestrictionData res = new RestrictionData();
                    res.restrictionKey = restrictionKey;
                    res.restriction = restriction;
                    res.exceptions = exceptions;
                    res.fromWid = fromWid;
                    res.toWid = toWid;
                    res.viaNid = viaNid;
                    if (this.restrictionsDos != null) {
                        res.writeTo(this.restrictionsDos);
                    }
                    if (this.restrictionCutter != null) {
                        this.restrictionCutter.nextRestriction(res);
                    }
                    relationData = r;
                }
            }
        }
    }

    private static short toBit(String tag, int bitpos, String s) {
        return (short) (s.indexOf(tag) < 0 ? 0 : 1 << bitpos);
    }

    private int getTileIndex(int ilon, int ilat) {
        int lon = ilon / 45000000;
        int lat = ilat / 30000000;
        if (lon < 0 || lon > 7 || lat < 0 || lat > 5) {
            System.out.println("warning: ignoring illegal pos: " + ilon + "," + ilat);
            return -1;
        }
        return (lon * 6) + lat;
    }

    @Override // btools.mapcreator.MapCreatorBase
    protected String getNameForTile(int tileIndex) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        int lon = ((tileIndex / 6) * 45) - 180;
        int lat = ((tileIndex % 6) * 30) - 90;
        if (lon < 0) {
            sbAppend = new StringBuilder().append("W").append(-lon);
        } else {
            sbAppend = new StringBuilder().append("E").append(lon);
        }
        String slon = sbAppend.toString();
        if (lat < 0) {
            sbAppend2 = new StringBuilder().append("S").append(-lat);
        } else {
            sbAppend2 = new StringBuilder().append("N").append(lat);
        }
        String slat = sbAppend2.toString();
        return slon + "_" + slat + ".ntl";
    }
}

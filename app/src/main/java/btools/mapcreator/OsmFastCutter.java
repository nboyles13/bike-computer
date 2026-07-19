package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class OsmFastCutter extends MapCreatorBase {
    public static void main(String[] args) throws Exception {
        System.out.println("*** OsmFastCutter: cut an osm map in node-tiles + way-tiles");
        if (args.length != 11 && args.length != 12 && args.length != 13) {
            System.out.println("usage: bzip2 -dc <map> | java OsmFastCutter <lookup-file> <node-dir> <way-dir> <node55-dir> <way55-dir> <border-file> <out-rel-file> <out-res-file> <filter-profile> <report-profile> <check-profile> <map-file> [db-tag-filename | db-tag-jdbcurl]");
            System.out.println("or   : java OsmFastCutter <lookup-file> <node-dir> <way-dir> <node55-dir> <way55-dir> <border-file> <out-rel-file> <out-res-file> <filter-profile> <report-profile> <check-profile> <map-file> [db-tag-filename | db-tag-jdbcurl] <inputfile> ");
        } else {
            doCut(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), new File(args[4]), new File(args[5]), new File(args[6]), new File(args[7]), new File(args[8]), new File(args[9]), new File(args[10]), args.length > 11 ? new File(args[11]) : null, args.length > 12 ? args[12] : null);
        }
    }

    public static void doCut(File lookupFile, File nodeDir, File wayDir, File node55Dir, File way55Dir, File borderFile, File relFile, File resFile, File profileAll, File profileReport, File profileCheck, File mapFile, String dbTagInfo) throws Exception {
        OsmCutter cutter = new OsmCutter();
        if (dbTagInfo != null) {
            if (dbTagInfo.toLowerCase().startsWith("jdbc")) {
                cutter.setDbTagDatabase(dbTagInfo);
            } else {
                cutter.setDbTagFilename(dbTagInfo);
            }
        }
        cutter.wayCutter = new WayCutter();
        cutter.wayCutter.init(wayDir);
        cutter.restrictionCutter = new RestrictionCutter();
        cutter.restrictionCutter.init(new File(nodeDir.getParentFile(), "restrictions"), cutter.wayCutter);
        NodeFilter nodeFilter = new NodeFilter();
        nodeFilter.init();
        cutter.nodeFilter = nodeFilter;
        cutter.process(lookupFile, nodeDir, null, relFile, null, profileAll, mapFile);
        cutter.wayCutter.finish();
        cutter.restrictionCutter.finish();
        WayCutter5 wayCut5 = new WayCutter5();
        wayCut5.relMerger = new RelationMerger();
        wayCut5.relMerger.init(relFile, lookupFile, profileReport, profileCheck);
        wayCut5.restrictionCutter5 = new RestrictionCutter5();
        wayCut5.restrictionCutter5.init(new File(nodeDir.getParentFile(), "restrictions55"), wayCut5);
        wayCut5.nodeFilter = nodeFilter;
        wayCut5.nodeCutter = new NodeCutter();
        wayCut5.nodeCutter.init(node55Dir);
        wayCut5.process(nodeDir, wayDir, way55Dir, borderFile);
    }
}

package btools.mapcreator;

import btools.util.CompactLongMap;
import btools.util.FrozenLongMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class DatabasePseudoTagProvider {
    static final int IMPORT_TYPE_NODES = 1;
    static final int IMPORT_TYPE_NONE = 0;
    static final int IMPORT_TYPE_START = -1;
    static final int IMPORT_TYPE_WAYS = 2;
    FrozenLongMap<Map<String, String>> dbNodeData;
    FrozenLongMap<Map<String, String>> dbWayData;
    private long cntOsmWays = 0;
    private long cntWayModified = 0;
    private long cntOsmNodes = 0;
    private long cntNodesModified = 0;
    private Map<String, Long> pseudoTagsFound = new HashMap();

    /* JADX WARN: Removed duplicated region for block: B:119:? A[Catch: Exception -> 0x0177, SQLException -> 0x0179, SYNTHETIC, TRY_LEAVE, TryCatch #16 {SQLException -> 0x0179, Exception -> 0x0177, blocks: (B:33:0x012d, B:75:0x0176, B:74:0x0173, B:70:0x016d), top: B:110:0x0006, inners: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x016d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void main(String[] args) throws Throwable {
        Throwable th;
        OutputStream gZIPOutputStream;
        Throwable th2;
        Throwable th3;
        String jdbcurl = args[0];
        String filename = args[1];
        try {
            try {
                Connection conn = DriverManager.getConnection(jdbcurl);
                try {
                    if (filename.endsWith(".gz")) {
                        try {
                            gZIPOutputStream = new GZIPOutputStream(new FileOutputStream(filename));
                        } catch (Throwable th4) {
                            th = th4;
                            if (conn != null) {
                                throw th;
                            }
                            try {
                                conn.close();
                                throw th;
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                                throw th;
                            }
                        }
                    } else {
                        gZIPOutputStream = new FileOutputStream(filename);
                    }
                    try {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(gZIPOutputStream));
                        try {
                            conn.setAutoCommit(false);
                            System.out.println("DatabasePseudoTagProvider dumping data from database to file " + filename);
                            PreparedStatement psAllTags = conn.prepareStatement("select node_id, crossing_class from crossing_tags");
                            try {
                                try {
                                    bw.write("#####nodetags#####\n");
                                    bw.write("node_id;crossing_class\n");
                                    psAllTags.setFetchSize(100);
                                    ResultSet rs = psAllTags.executeQuery();
                                    long dbRows = 0;
                                    while (rs.next()) {
                                        StringBuilder line = new StringBuilder();
                                        String filename2 = filename;
                                        try {
                                            line.append(rs.getLong("node_id"));
                                            appendDBTag(line, rs, "crossing_class");
                                            line.append('\n');
                                            bw.write(line.toString());
                                            dbRows++;
                                            filename = filename2;
                                        } catch (Throwable th6) {
                                            th3 = th6;
                                            if (psAllTags == null) {
                                                throw th3;
                                            }
                                            try {
                                                psAllTags.close();
                                                throw th3;
                                            } catch (Throwable th7) {
                                                th3.addSuppressed(th7);
                                                throw th3;
                                            }
                                        }
                                    }
                                    System.out.println(".. from database: node tag rows = " + dbRows);
                                    if (psAllTags != null) {
                                        psAllTags.close();
                                    }
                                    PreparedStatement psAllTags2 = conn.prepareStatement("SELECT * from all_tags");
                                    try {
                                        bw.write("#####waytags#####\n");
                                        bw.write("losmid;noise_class;river_class;forest_class;town_class;traffic_class\n");
                                        psAllTags2.setFetchSize(100);
                                        ResultSet rs2 = psAllTags2.executeQuery();
                                        long dbRows2 = 0;
                                        while (rs2.next()) {
                                            StringBuilder line2 = new StringBuilder();
                                            line2.append(rs2.getLong("losmid"));
                                            appendDBTag(line2, rs2, "noise_class");
                                            appendDBTag(line2, rs2, "river_class");
                                            appendDBTag(line2, rs2, "forest_class");
                                            appendDBTag(line2, rs2, "town_class");
                                            appendDBTag(line2, rs2, "traffic_class");
                                            line2.append('\n');
                                            bw.write(line2.toString());
                                            dbRows2++;
                                        }
                                        System.out.println(".. from database: way tag rows = " + dbRows2);
                                        if (psAllTags2 != null) {
                                            psAllTags2.close();
                                        }
                                        bw.close();
                                        if (conn != null) {
                                            conn.close();
                                        }
                                    } finally {
                                    }
                                } catch (Throwable th8) {
                                    th3 = th8;
                                }
                            } catch (Throwable th9) {
                                th2 = th9;
                                try {
                                    bw.close();
                                    throw th2;
                                } catch (Throwable th10) {
                                    th2.addSuppressed(th10);
                                    throw th2;
                                }
                            }
                        } catch (Throwable th11) {
                            th2 = th11;
                        }
                    } catch (Throwable th12) {
                        th = th12;
                        if (conn != null) {
                        }
                    }
                } catch (Throwable th13) {
                    th = th13;
                }
            } catch (SQLException e) {
                g = e;
                System.err.format("DatabasePseudoTagProvider execute sql .. SQL State: %s\n%s\n", g.getSQLState(), g.getMessage());
                System.exit(1);
            } catch (Exception e2) {
                f = e2;
                f.printStackTrace();
                System.exit(1);
            }
        } catch (SQLException e3) {
            g = e3;
            System.err.format("DatabasePseudoTagProvider execute sql .. SQL State: %s\n%s\n", g.getSQLState(), g.getMessage());
            System.exit(1);
        } catch (Exception e4) {
            f = e4;
            f.printStackTrace();
            System.exit(1);
        }
    }

    private static void appendDBTag(StringBuilder sb, ResultSet rs, String name) throws SQLException {
        sb.append(';');
        String v = rs.getString(name);
        if (v != null) {
            sb.append(v);
        }
    }

    public DatabasePseudoTagProvider(String filename, String jdbcurl) throws Throwable {
        if (filename != null) {
            doFileImport(filename);
        }
        if (jdbcurl != null) {
            doDatabaseImport(jdbcurl);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:108:? A[Catch: all -> 0x0168, SYNTHETIC, TRY_LEAVE, TryCatch #6 {all -> 0x0168, blocks: (B:4:0x0008, B:66:0x0167, B:65:0x0164, B:30:0x00c6, B:41:0x0139, B:53:0x014f, B:52:0x014c, B:31:0x00ca, B:32:0x00db, B:34:0x00e1, B:38:0x00fe, B:37:0x00fb, B:39:0x0106, B:61:0x015e, B:48:0x0146), top: B:92:0x0008, inners: #1, #2, #7 }] */
    /* JADX WARN: Removed duplicated region for block: B:110:? A[Catch: Exception -> 0x0176, SQLException -> 0x017f, SYNTHETIC, TRY_LEAVE, TryCatch #12 {SQLException -> 0x017f, Exception -> 0x0176, blocks: (B:3:0x0004, B:43:0x013e, B:75:0x0175, B:74:0x0172), top: B:101:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x016c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x015e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void doDatabaseImport(String jdbcurl) throws Throwable {
        Throwable th;
        Throwable th2;
        try {
            Connection conn = DriverManager.getConnection(jdbcurl);
            try {
                System.out.println("DatabasePseudoTagProvider reading from database");
                conn.setAutoCommit(false);
                Map<Map<String, String>, Map<String, String>> mapUnifier = new HashMap<>();
                PreparedStatement psAllTags = conn.prepareStatement("SELECT * from all_tags");
                try {
                    psAllTags.setFetchSize(100);
                    CompactLongMap<Map<String, String>> data = new CompactLongMap<>();
                    ResultSet rs = psAllTags.executeQuery();
                    long dbRows = 0;
                    while (rs.next()) {
                        try {
                            long osm_id = rs.getLong("losmid");
                            Map<String, String> row = new HashMap<>(5);
                            addDBTag(row, rs, "noise_class");
                            addDBTag(row, rs, "river_class");
                            addDBTag(row, rs, "forest_class");
                            addDBTag(row, rs, "town_class");
                            addDBTag(row, rs, "traffic_class");
                            Map<String, String> knownRow = mapUnifier.get(row);
                            if (knownRow != null) {
                                row = knownRow;
                            } else {
                                mapUnifier.put(row, row);
                            }
                            Connection conn2 = conn;
                            try {
                                data.fastPut(osm_id, row);
                                dbRows++;
                                conn = conn2;
                            } catch (Throwable th3) {
                                th2 = th3;
                                conn = conn2;
                                if (psAllTags != null) {
                                }
                            }
                        } catch (Throwable th4) {
                            th2 = th4;
                        }
                    }
                    Connection conn3 = conn;
                    try {
                        this.dbWayData = new FrozenLongMap<>(data);
                        System.out.println("read from database: way rows =" + this.dbWayData.size() + " unique rows=" + mapUnifier.size());
                        if (psAllTags != null) {
                            try {
                                psAllTags.close();
                            } catch (Throwable th5) {
                                th = th5;
                                conn = conn3;
                                if (conn != null) {
                                }
                            }
                        }
                        conn = conn3;
                        PreparedStatement psAllTags2 = conn.prepareStatement("SELECT node_id, crossing_class from crossing_tags");
                        try {
                            psAllTags2.setFetchSize(100);
                            mapUnifier.clear();
                            CompactLongMap<Map<String, String>> data2 = new CompactLongMap<>();
                            ResultSet rs2 = psAllTags2.executeQuery();
                            long dbRows2 = 0;
                            while (rs2.next()) {
                                long osm_id2 = rs2.getLong("node_id");
                                Map<String, String> row2 = new HashMap<>();
                                addDBTag(row2, rs2, "crossing_class");
                                Map<String, String> knownRow2 = mapUnifier.get(row2);
                                if (knownRow2 != null) {
                                    row2 = knownRow2;
                                } else {
                                    mapUnifier.put(row2, row2);
                                }
                                data2.fastPut(osm_id2, row2);
                                dbRows2++;
                            }
                            this.dbNodeData = new FrozenLongMap<>(data2);
                            System.out.println("read from database: node rows =" + this.dbNodeData.size() + " unique rows=" + mapUnifier.size());
                            if (psAllTags2 != null) {
                                psAllTags2.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } finally {
                        }
                    } catch (Throwable th6) {
                        conn = conn3;
                        th2 = th6;
                        if (psAllTags != null) {
                            throw th2;
                        }
                        try {
                            psAllTags.close();
                            throw th2;
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                            throw th2;
                        }
                    }
                } catch (Throwable th8) {
                    th2 = th8;
                }
            } catch (Throwable th9) {
                th = th9;
                if (conn != null) {
                    throw th;
                }
                try {
                    conn.close();
                    throw th;
                } catch (Throwable th10) {
                    th.addSuppressed(th10);
                    throw th;
                }
            }
        } catch (SQLException g) {
            System.err.format("DatabasePseudoTagProvider execute sql .. SQL State: %s\n%s\n", g.getSQLState(), g.getMessage());
            System.exit(1);
        } catch (Exception f) {
            f.printStackTrace();
            System.exit(1);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x01d4 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0204 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void doFileImport(String filename) {
        CompactLongMap<Map<String, String>> data;
        long osm_id;
        List<String> tokens;
        long osm_id2;
        DatabasePseudoTagProvider databasePseudoTagProvider = this;
        int i = 1;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(filename.endsWith(".gz") ? new GZIPInputStream(new FileInputStream(filename)) : new FileInputStream(filename)));
            try {
                System.out.println("DatabasePseudoTagProvider reading from file: " + filename);
                Map<Map<String, String>, Map<String, String>> mapUnifier = new HashMap<>();
                CompactLongMap<Map<String, String>> data2 = null;
                int importType = 0;
                int lastImportType = -1;
                long dbRows = 0;
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.equals("#####nodetags#####")) {
                        importType = 1;
                    } else if (line.equals("#####waytags#####")) {
                        importType = 2;
                    } else {
                        if (importType == lastImportType) {
                            data = data2;
                        } else {
                            if (lastImportType == -1) {
                                data2 = new CompactLongMap<>();
                                dbRows = 0;
                            }
                            if (lastImportType == i) {
                                databasePseudoTagProvider.dbNodeData = new FrozenLongMap<>(data2);
                                System.out.println("read from file: node rows =" + databasePseudoTagProvider.dbNodeData.size() + " unique rows=" + mapUnifier.size());
                                data2 = new CompactLongMap<>();
                                dbRows = 0;
                            } else if (lastImportType == 2) {
                                databasePseudoTagProvider.dbWayData = new FrozenLongMap<>(data2);
                                System.out.println("read from file: way rows =" + databasePseudoTagProvider.dbWayData.size() + " unique rows=" + mapUnifier.size());
                            }
                            lastImportType = importType;
                            data = data2;
                        }
                        if (importType == 1) {
                            try {
                                List<String> tokens2 = databasePseudoTagProvider.tokenize(line);
                                try {
                                    osm_id = Long.parseLong(tokens2.get(0));
                                } catch (NumberFormatException e) {
                                }
                                if (osm_id == -1) {
                                    databasePseudoTagProvider = this;
                                    data2 = data;
                                    i = 1;
                                } else {
                                    Map<String, String> row = new HashMap<>(2);
                                    addTag(row, tokens2.get(1), "estimated_crossing_class");
                                    Map<String, String> knownRow = mapUnifier.get(row);
                                    if (knownRow != null) {
                                        row = knownRow;
                                    } else {
                                        mapUnifier.put(row, row);
                                    }
                                    data.fastPut(osm_id, row);
                                    dbRows++;
                                    if (importType != 2 || importType == 0) {
                                        databasePseudoTagProvider = this;
                                        tokens = databasePseudoTagProvider.tokenize(line);
                                        try {
                                            osm_id2 = Long.parseLong(tokens.get(0));
                                        } catch (NumberFormatException e2) {
                                        }
                                        if (osm_id2 != -1) {
                                            data2 = data;
                                            i = 1;
                                        } else {
                                            Map<String, String> row2 = new HashMap<>(5);
                                            addTag(row2, tokens.get(1), "estimated_noise_class");
                                            addTag(row2, tokens.get(2), "estimated_river_class");
                                            addTag(row2, tokens.get(3), "estimated_forest_class");
                                            addTag(row2, tokens.get(4), "estimated_town_class");
                                            addTag(row2, tokens.get(5), "estimated_traffic_class");
                                            Map<String, String> knownRow2 = mapUnifier.get(row2);
                                            if (knownRow2 != null) {
                                                row2 = knownRow2;
                                            } else {
                                                mapUnifier.put(row2, row2);
                                            }
                                            data.fastPut(osm_id2, row2);
                                            dbRows++;
                                        }
                                    } else {
                                        databasePseudoTagProvider = this;
                                    }
                                    data2 = data;
                                    i = 1;
                                }
                            } catch (Throwable th) {
                                e = th;
                                NumberFormatException numberFormatException = e;
                                try {
                                    br.close();
                                    throw numberFormatException;
                                } catch (Throwable th2) {
                                    numberFormatException.addSuppressed(th2);
                                    throw numberFormatException;
                                }
                            }
                        } else if (importType != 2) {
                            databasePseudoTagProvider = this;
                            tokens = databasePseudoTagProvider.tokenize(line);
                            osm_id2 = Long.parseLong(tokens.get(0));
                            if (osm_id2 != -1) {
                            }
                        }
                    }
                }
                if (lastImportType == i) {
                    databasePseudoTagProvider.dbNodeData = new FrozenLongMap<>(data2);
                    System.out.println("read from file: node rows =" + databasePseudoTagProvider.dbNodeData.size() + " unique rows=" + mapUnifier.size());
                } else if (lastImportType == 2) {
                    databasePseudoTagProvider.dbWayData = new FrozenLongMap<>(data2);
                    System.out.println("read from file: way rows =" + databasePseudoTagProvider.dbWayData.size() + " unique rows=" + mapUnifier.size());
                } else if (data2 != null) {
                    databasePseudoTagProvider.dbWayData = new FrozenLongMap<>(data2);
                    System.out.println("read from file: way rows =" + databasePseudoTagProvider.dbWayData.size() + " unique rows=" + mapUnifier.size());
                }
                br.close();
            } catch (Throwable th3) {
                e = th3;
            }
        } catch (Exception f) {
            f.printStackTrace();
            System.exit(1);
        }
    }

    private List<String> tokenize(String s) {
        List<String> l = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ';') {
                l.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        l.add(sb.toString());
        return l;
    }

    private static void addTag(Map<String, String> row, String s, String name) {
        if (!s.isEmpty()) {
            row.put(name, s);
        }
    }

    private static void addDBTag(Map<String, String> row, ResultSet rs, String name) throws SQLException {
        String v = null;
        try {
            v = rs.getString(name);
        } catch (Exception e) {
        }
        if (v != null) {
            row.put("estimated_" + name, v);
        }
    }

    public void addWayTags(long osm_id, Map<String, String> map) {
        if (this.dbWayData == null || map == null || !map.containsKey("highway")) {
            return;
        }
        this.cntOsmWays++;
        if (this.cntOsmWays % 1000000 == 0) {
            String out = "Osm Ways processed=" + this.cntOsmWays + " way modifs=" + this.cntWayModified;
            for (String key : this.pseudoTagsFound.keySet()) {
                out = out + " " + key + "=" + String.valueOf(this.pseudoTagsFound.get(key));
            }
            System.out.println(out);
        }
        Map<String, String> dbTags = this.dbWayData.get(osm_id);
        if (dbTags == null) {
            return;
        }
        this.cntWayModified++;
        for (String key2 : dbTags.keySet()) {
            map.put(key2, dbTags.get(key2));
            Long cnt = this.pseudoTagsFound.get(key2);
            if (cnt == null) {
                cnt = 0L;
            }
            this.pseudoTagsFound.put(key2, Long.valueOf(cnt.longValue() + 1));
        }
    }

    public void addNodeTags(NodeData n) {
        Map<String, String> dbTags;
        if (this.dbNodeData == null || (dbTags = this.dbNodeData.get(n.nid)) == null) {
            return;
        }
        if (n.tags == null) {
            n.tags = new HashMap();
        }
        this.cntOsmNodes++;
        if (this.cntOsmNodes % 1000000 == 0) {
            String out = "Osm Nodes processed=" + this.cntOsmNodes + " node modifs=" + this.cntNodesModified;
            for (String key : this.pseudoTagsFound.keySet()) {
                out = out + " " + key + "=" + String.valueOf(this.pseudoTagsFound.get(key));
            }
            System.out.println(out);
        }
        this.cntNodesModified++;
        for (String key2 : dbTags.keySet()) {
            n.tags.put(key2, dbTags.get(key2));
            Long cnt = this.pseudoTagsFound.get(key2);
            if (cnt == null) {
                cnt = 0L;
            }
            this.pseudoTagsFound.put(key2, Long.valueOf(cnt.longValue() + 1));
        }
    }
}

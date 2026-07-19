package btools.server;

import btools.router.OsmNodeNamed;
import java.io.File;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ServiceContext {
    public String customProfileDir;
    public List<OsmNodeNamed> nogoList;
    public String profileDir;
    public Map<String, String> profileMap = null;
    public File segmentDir;
    public String sharedProfileDir;
}

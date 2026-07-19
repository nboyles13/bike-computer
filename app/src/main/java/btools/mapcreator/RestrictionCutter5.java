package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class RestrictionCutter5 extends MapCreatorBase {
    private WayCutter5 wayCutter5;

    public void init(File outTileDir, WayCutter5 wayCutter5) throws Exception {
        outTileDir.mkdir();
        this.outTileDir = outTileDir;
        this.wayCutter5 = wayCutter5;
    }

    public void finish() throws Exception {
        closeTileOutStreams();
    }

    public void nextRestriction(RestrictionData data) throws Exception {
        int tileIndex = this.wayCutter5.getTileIndexForNid(data.viaNid);
        if (tileIndex != -1) {
            data.writeTo(getOutStreamForTile(tileIndex));
        }
    }

    @Override // btools.mapcreator.MapCreatorBase
    protected String getNameForTile(int tileIndex) {
        String name = this.wayCutter5.getNameForTile(tileIndex);
        return name.substring(0, name.length() - 3) + "rt5";
    }
}

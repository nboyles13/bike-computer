package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class RestrictionCutter extends MapCreatorBase {
    private WayCutter wayCutter;

    public void init(File outTileDir, WayCutter wayCutter) throws Exception {
        outTileDir.mkdir();
        this.outTileDir = outTileDir;
        this.wayCutter = wayCutter;
    }

    public void finish() throws Exception {
        closeTileOutStreams();
    }

    public void nextRestriction(RestrictionData data) throws Exception {
        int tileIndex = this.wayCutter.getTileIndexForNid(data.viaNid);
        if (tileIndex != -1) {
            data.writeTo(getOutStreamForTile(tileIndex));
        }
    }

    @Override // btools.mapcreator.MapCreatorBase
    protected String getNameForTile(int tileIndex) {
        String name = this.wayCutter.getNameForTile(tileIndex);
        return name.substring(0, name.length() - 3) + "rtl";
    }
}

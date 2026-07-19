package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public interface WayListener {
    void nextWay(WayData wayData) throws Exception;

    void wayFileEnd(File file) throws Exception;

    boolean wayFileStart(File file) throws Exception;
}

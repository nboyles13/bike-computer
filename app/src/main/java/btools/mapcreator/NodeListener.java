package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public interface NodeListener {
    void nextNode(NodeData nodeData) throws Exception;

    void nodeFileEnd(File file) throws Exception;

    void nodeFileStart(File file) throws Exception;
}

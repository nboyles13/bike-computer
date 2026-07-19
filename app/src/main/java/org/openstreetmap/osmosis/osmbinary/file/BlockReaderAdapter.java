package org.openstreetmap.osmosis.osmbinary.file;

/* JADX INFO: loaded from: classes4.dex */
public interface BlockReaderAdapter {
    void complete();

    void handleBlock(FileBlock fileBlock);

    boolean skipBlock(FileBlockPosition fileBlockPosition);
}

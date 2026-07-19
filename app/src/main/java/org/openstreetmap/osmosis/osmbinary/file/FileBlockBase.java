package org.openstreetmap.osmosis.osmbinary.file;

import com.google.protobuf.ByteString;

/* JADX INFO: loaded from: classes4.dex */
public class FileBlockBase {
    static final int MAX_BODY_SIZE = 33554432;
    static final int MAX_HEADER_SIZE = 65536;
    protected final ByteString indexdata;
    protected final String type;

    protected FileBlockBase(String type, ByteString indexdata) {
        this.type = type;
        this.indexdata = indexdata;
    }

    public String getType() {
        return this.type;
    }

    public ByteString getIndexData() {
        return this.indexdata;
    }
}

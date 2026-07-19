package org.openstreetmap.osmosis.osmbinary.file;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes4.dex */
public class FileBlockReference extends FileBlockPosition {
    protected InputStream input;

    protected FileBlockReference(String type, ByteString indexdata) {
        super(type, indexdata);
    }

    public FileBlock read() throws IOException {
        return read(this.input);
    }

    static FileBlockPosition newInstance(FileBlockBase base, InputStream input, long offset, int length) {
        FileBlockReference out = new FileBlockReference(base.type, base.indexdata);
        out.datasize = length;
        out.data_offset = offset;
        out.input = input;
        return out;
    }
}

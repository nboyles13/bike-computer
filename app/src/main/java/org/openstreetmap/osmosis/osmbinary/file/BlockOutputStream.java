package org.openstreetmap.osmosis.osmbinary.file;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class BlockOutputStream {
    OutputStream outwrite;
    List<FileBlockPosition> writtenblocks = new ArrayList();
    CompressFlags compression = CompressFlags.DEFLATE;

    public BlockOutputStream(OutputStream output) {
        this.outwrite = new DataOutputStream(output);
    }

    public void setCompress(CompressFlags flag) {
        this.compression = flag;
    }

    public void setCompress(String s) {
        if (s.equals("none")) {
            this.compression = CompressFlags.NONE;
        } else {
            if (s.equals("deflate")) {
                this.compression = CompressFlags.DEFLATE;
                return;
            }
            throw new Error("Unknown compression type: " + s);
        }
    }

    public void write(FileBlock block) throws IOException {
        write(block, this.compression);
    }

    public void write(FileBlock block, CompressFlags compression) throws IOException {
        FileBlockPosition ref = block.writeTo(this.outwrite, compression);
        this.writtenblocks.add(ref);
    }

    public void flush() throws IOException {
        this.outwrite.flush();
    }

    public void close() throws IOException {
        this.outwrite.flush();
        this.outwrite.close();
    }
}

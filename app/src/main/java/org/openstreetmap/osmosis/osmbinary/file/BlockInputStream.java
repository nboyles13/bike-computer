package org.openstreetmap.osmosis.osmbinary.file;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes4.dex */
public class BlockInputStream {
    BlockReaderAdapter adaptor;
    InputStream input;

    public BlockInputStream(InputStream input, BlockReaderAdapter adaptor) {
        this.input = input;
        this.adaptor = adaptor;
    }

    public void process() throws IOException {
        while (true) {
            try {
                FileBlock.process(this.input, this.adaptor);
            } catch (EOFException e) {
                this.adaptor.complete();
                return;
            }
        }
    }

    public void close() throws IOException {
        this.input.close();
    }
}

package org.openstreetmap.osmosis.osmbinary.file;

import com.google.protobuf.ByteString;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.openstreetmap.osmosis.osmbinary.Fileformat;

/* JADX INFO: loaded from: classes4.dex */
public class FileBlockHead extends FileBlockReference {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    protected FileBlockHead(String type, ByteString indexdata) {
        super(type, indexdata);
    }

    static FileBlockHead readHead(InputStream input) throws IOException {
        DataInputStream datinput = new DataInputStream(input);
        int headersize = datinput.readInt();
        if (headersize > 65536) {
            throw new FileFormatException("Unexpectedly long header 65536 bytes. Possibly corrupt file.");
        }
        byte[] buf = new byte[headersize];
        datinput.readFully(buf);
        Fileformat.BlobHeader header = Fileformat.BlobHeader.parseFrom(buf);
        FileBlockHead fileblock = new FileBlockHead(header.getType(), header.getIndexdata());
        fileblock.datasize = header.getDatasize();
        if (header.getDatasize() > 33554432) {
            throw new FileFormatException("Unexpectedly long body 33554432 bytes. Possibly corrupt file.");
        }
        fileblock.input = input;
        if (input instanceof FileInputStream) {
            fileblock.data_offset = ((FileInputStream) input).getChannel().position();
        }
        return fileblock;
    }

    void skipContents(InputStream input) throws IOException {
        if (input.skip(getDatasize()) != getDatasize()) {
            throw new AssertionError("SHORT READ");
        }
    }

    FileBlock readContents(InputStream input) throws IOException {
        DataInputStream datinput = new DataInputStream(input);
        byte[] buf = new byte[getDatasize()];
        datinput.readFully(buf);
        return parseData(buf);
    }
}

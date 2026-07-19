package org.openstreetmap.osmosis.osmbinary.file;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.openstreetmap.osmosis.osmbinary.Fileformat;

/* JADX INFO: loaded from: classes4.dex */
public class FileBlockPosition extends FileBlockBase {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    long data_offset;
    protected int datasize;

    protected FileBlockPosition(String type, ByteString indexdata) {
        super(type, indexdata);
    }

    FileBlock parseData(byte[] buf) throws InvalidProtocolBufferException {
        FileBlock out = FileBlock.newInstance(this.type, null, this.indexdata);
        Fileformat.Blob blob = Fileformat.Blob.parseFrom(buf);
        if (blob.hasRaw()) {
            out.data = blob.getRaw();
        } else if (blob.hasZlibData()) {
            byte[] buf2 = new byte[blob.getRawSize()];
            Inflater decompresser = new Inflater();
            decompresser.setInput(blob.getZlibData().toByteArray());
            try {
                decompresser.inflate(buf2);
                if (!decompresser.finished()) {
                    throw new AssertionError();
                }
                decompresser.end();
                out.data = ByteString.copyFrom(buf2);
            } catch (DataFormatException e) {
                e.printStackTrace();
                throw new Error(e);
            }
        }
        return out;
    }

    public int getDatasize() {
        return this.datasize;
    }

    static FileBlockPosition newInstance(FileBlockBase base, long offset, int length) {
        FileBlockPosition out = new FileBlockPosition(base.type, base.indexdata);
        out.datasize = length;
        out.data_offset = offset;
        return out;
    }

    public FileBlock read(InputStream input) throws IOException {
        if (input instanceof FileInputStream) {
            ((FileInputStream) input).getChannel().position(this.data_offset);
            byte[] buf = new byte[getDatasize()];
            new DataInputStream(input).readFully(buf);
            return parseData(buf);
        }
        throw new Error("Random access binary reads require seekability");
    }

    public ByteString serialize() {
        throw new Error("TODO");
    }

    static FileBlockPosition parseFrom(ByteString b) {
        throw new Error("TODO");
    }
}

package org.openstreetmap.osmosis.osmbinary.file;

import com.google.protobuf.ByteString;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.Deflater;
import org.openstreetmap.osmosis.osmbinary.Fileformat;

/* JADX INFO: loaded from: classes4.dex */
public class FileBlock extends FileBlockBase {
    static int warncount = 0;
    ByteString data;

    private FileBlock(String type, ByteString blob, ByteString indexdata) {
        super(type, indexdata);
        this.data = blob;
    }

    public static FileBlock newInstance(String type, ByteString blob, ByteString indexdata) {
        if (blob != null && blob.size() > 16777216) {
            System.err.println("Warning: Fileblock has body size too large and may be considered corrupt");
            if (blob != null && blob.size() > 32505856) {
                throw new Error("This file has too many entities in a block. Parsers will reject it.");
            }
        }
        if (indexdata != null && indexdata.size() > 32768) {
            System.err.println("Warning: Fileblock has indexdata too large and may be considered corrupt");
            if (indexdata != null && indexdata.size() > 65024) {
                throw new Error("This file header is too large. Parsers will reject it.");
            }
        }
        return new FileBlock(type, blob, indexdata);
    }

    protected void deflateInto(Fileformat.Blob.Builder blobbuilder) {
        int size = this.data.size();
        Deflater deflater = new Deflater();
        deflater.setInput(this.data.toByteArray());
        deflater.finish();
        byte[] out = new byte[size];
        deflater.deflate(out);
        if (!deflater.finished()) {
            warncount++;
            if (warncount > 10 && warncount % 100 == 0) {
                System.out.println("Compressed buffers are too short, causing extra copy");
            }
            out = Arrays.copyOf(out, (size / 64) + size + 16);
            deflater.deflate(out, deflater.getTotalOut(), out.length - deflater.getTotalOut());
            if (!deflater.finished()) {
                throw new Error("Internal error in compressor");
            }
        }
        ByteString compressed = ByteString.copyFrom(out, 0, deflater.getTotalOut());
        blobbuilder.setZlibData(compressed);
        deflater.end();
    }

    public FileBlockPosition writeTo(OutputStream outwrite, CompressFlags flags) throws IOException {
        Fileformat.BlobHeader.Builder builder = Fileformat.BlobHeader.newBuilder();
        if (this.indexdata != null) {
            builder.setIndexdata(this.indexdata);
        }
        builder.setType(this.type);
        Fileformat.Blob.Builder blobbuilder = Fileformat.Blob.newBuilder();
        if (flags == CompressFlags.NONE) {
            blobbuilder.setRaw(this.data);
            blobbuilder.setRawSize(this.data.size());
        } else {
            blobbuilder.setRawSize(this.data.size());
            if (flags == CompressFlags.DEFLATE) {
                deflateInto(blobbuilder);
            } else {
                throw new Error("Compression flag not understood");
            }
        }
        Fileformat.Blob blob = blobbuilder.build();
        builder.setDatasize(blob.getSerializedSize());
        Fileformat.BlobHeader message = builder.build();
        int size = message.getSerializedSize();
        new DataOutputStream(outwrite).writeInt(size);
        message.writeTo(outwrite);
        long offset = -1;
        if (outwrite instanceof FileOutputStream) {
            offset = ((FileOutputStream) outwrite).getChannel().position();
        }
        blob.writeTo(outwrite);
        return FileBlockPosition.newInstance(this, offset, size);
    }

    static void process(InputStream input, BlockReaderAdapter callback) throws IOException {
        FileBlockHead fileblock = FileBlockHead.readHead(input);
        if (callback.skipBlock(fileblock)) {
            fileblock.skipContents(input);
        } else {
            callback.handleBlock(fileblock.readContents(input));
        }
    }

    public ByteString getData() {
        return this.data;
    }
}

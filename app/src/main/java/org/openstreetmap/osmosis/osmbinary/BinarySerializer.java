package org.openstreetmap.osmosis.osmbinary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openstreetmap.osmosis.osmbinary.Osmformat;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;
import org.openstreetmap.osmosis.osmbinary.file.FileBlock;

/* JADX INFO: loaded from: classes4.dex */
public class BinarySerializer {
    protected BlockOutputStream output;
    protected final int MIN_DENSE = 10;
    protected int batch_limit = 4000;
    protected int granularity = 100;
    protected int date_granularity = 1000;
    protected boolean omit_metadata = false;
    protected int batch_size = 0;
    protected int total_entities = 0;
    private StringTable stringtable = new StringTable();
    protected List<PrimGroupWriterInterface> groups = new ArrayList();
    long debug_bytes = 0;

    protected interface PrimGroupWriterInterface {
        void addStringsToStringtable();

        Osmformat.PrimitiveGroup serialize();
    }

    public void configGranularity(int granularity) {
        this.granularity = granularity;
    }

    public void configOmit(boolean omit_metadata) {
        this.omit_metadata = omit_metadata;
    }

    public void configBatchLimit(int batch_limit) {
        this.batch_limit = batch_limit;
    }

    public BinarySerializer(BlockOutputStream output) {
        this.output = output;
    }

    public StringTable getStringTable() {
        return this.stringtable;
    }

    public void flush() throws IOException {
        processBatch();
        this.output.flush();
    }

    public void close() throws IOException {
        flush();
        this.output.close();
    }

    public void processBatch() {
        if (this.groups.size() == 0) {
            return;
        }
        Osmformat.PrimitiveBlock.Builder primblock = Osmformat.PrimitiveBlock.newBuilder();
        this.stringtable.clear();
        for (PrimGroupWriterInterface i : this.groups) {
            i.addStringsToStringtable();
        }
        this.stringtable.finish();
        for (PrimGroupWriterInterface i2 : this.groups) {
            Osmformat.PrimitiveGroup group = i2.serialize();
            if (group != null) {
                primblock.addPrimitivegroup(group);
            }
        }
        primblock.setStringtable(this.stringtable.serialize());
        primblock.setGranularity(this.granularity);
        primblock.setDateGranularity(this.date_granularity);
        Osmformat.PrimitiveBlock message = primblock.build();
        this.debug_bytes += (long) message.getSerializedSize();
        try {
            try {
                this.output.write(FileBlock.newInstance("OSMData", message.toByteString(), null));
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(e);
            }
        } finally {
            this.batch_size = 0;
            this.groups.clear();
        }
    }

    public long mapRawDegrees(double degrees) {
        return (long) (degrees / 1.0E-9d);
    }

    public int mapDegrees(double degrees) {
        return (int) ((degrees / 1.0E-7d) / ((double) (this.granularity / 100)));
    }
}

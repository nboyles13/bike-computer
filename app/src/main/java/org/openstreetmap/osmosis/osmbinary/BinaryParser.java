package org.openstreetmap.osmosis.osmbinary;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Date;
import java.util.List;
import org.openstreetmap.osmosis.osmbinary.Osmformat;
import org.openstreetmap.osmosis.osmbinary.file.BlockReaderAdapter;
import org.openstreetmap.osmosis.osmbinary.file.FileBlock;
import org.openstreetmap.osmosis.osmbinary.file.FileBlockPosition;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BinaryParser implements BlockReaderAdapter {
    public static final Date NODATE = new Date(-1);
    protected int date_granularity;
    protected int granularity;
    private long lat_offset;
    private long lon_offset;
    private String[] strings;

    protected abstract void parse(Osmformat.HeaderBlock headerBlock);

    protected abstract void parseDense(Osmformat.DenseNodes denseNodes);

    protected abstract void parseNodes(List<Osmformat.Node> list);

    protected abstract void parseRelations(List<Osmformat.Relation> list);

    protected abstract void parseWays(List<Osmformat.Way> list);

    protected Date getDate(Osmformat.Info info) {
        if (info.hasTimestamp()) {
            return new Date(((long) this.date_granularity) * info.getTimestamp());
        }
        return NODATE;
    }

    protected String getStringById(int id) {
        return this.strings[id];
    }

    @Override // org.openstreetmap.osmosis.osmbinary.file.BlockReaderAdapter
    public void handleBlock(FileBlock message) {
        try {
            if (message.getType().equals("OSMHeader")) {
                Osmformat.HeaderBlock headerblock = Osmformat.HeaderBlock.parseFrom(message.getData());
                parse(headerblock);
            } else if (message.getType().equals("OSMData")) {
                Osmformat.PrimitiveBlock primblock = Osmformat.PrimitiveBlock.parseFrom(message.getData());
                parse(primblock);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            throw new Error("ParseError");
        }
    }

    @Override // org.openstreetmap.osmosis.osmbinary.file.BlockReaderAdapter
    public boolean skipBlock(FileBlockPosition block) {
        if (block.getType().equals("OSMData") || block.getType().equals("OSMHeader")) {
            return false;
        }
        System.out.println("Skipped block of type: " + block.getType());
        return true;
    }

    public double parseLat(long degree) {
        return ((((long) this.granularity) * degree) + this.lat_offset) * 1.0E-9d;
    }

    public double parseLon(long degree) {
        return ((((long) this.granularity) * degree) + this.lon_offset) * 1.0E-9d;
    }

    public void parse(Osmformat.PrimitiveBlock block) {
        Osmformat.StringTable stablemessage = block.getStringtable();
        this.strings = new String[stablemessage.getSCount()];
        for (int i = 0; i < this.strings.length; i++) {
            this.strings[i] = stablemessage.getS(i).toStringUtf8();
        }
        int i2 = block.getGranularity();
        this.granularity = i2;
        this.lat_offset = block.getLatOffset();
        this.lon_offset = block.getLonOffset();
        this.date_granularity = block.getDateGranularity();
        for (Osmformat.PrimitiveGroup groupmessage : block.getPrimitivegroupList()) {
            parseNodes(groupmessage.getNodesList());
            parseWays(groupmessage.getWaysList());
            parseRelations(groupmessage.getRelationsList());
            if (groupmessage.hasDense()) {
                parseDense(groupmessage.getDense());
            }
        }
    }
}

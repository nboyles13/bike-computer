package btools.mapcreator;

import java.util.Date;
import org.openstreetmap.osmosis.osmbinary.Osmformat;

/* JADX INFO: loaded from: classes.dex */
public class BPbfFieldDecoder {
    private static final double COORDINATE_SCALING_FACTOR = 1.0E-9d;
    private int coordGranularity;
    private long coordLatitudeOffset;
    private long coordLongitudeOffset;
    private int dateGranularity;
    private String[] strings;

    public BPbfFieldDecoder(Osmformat.PrimitiveBlock primitiveBlock) {
        this.coordGranularity = primitiveBlock.getGranularity();
        this.coordLatitudeOffset = primitiveBlock.getLatOffset();
        this.coordLongitudeOffset = primitiveBlock.getLonOffset();
        this.dateGranularity = primitiveBlock.getDateGranularity();
        Osmformat.StringTable stringTable = primitiveBlock.getStringtable();
        this.strings = new String[stringTable.getSCount()];
        for (int i = 0; i < this.strings.length; i++) {
            this.strings[i] = stringTable.getS(i).toStringUtf8();
        }
    }

    public double decodeLatitude(long rawLatitude) {
        return (this.coordLatitudeOffset + (((long) this.coordGranularity) * rawLatitude)) * COORDINATE_SCALING_FACTOR;
    }

    public double decodeLongitude(long rawLongitude) {
        return (this.coordLongitudeOffset + (((long) this.coordGranularity) * rawLongitude)) * COORDINATE_SCALING_FACTOR;
    }

    public Date decodeTimestamp(long rawTimestamp) {
        return new Date(((long) this.dateGranularity) * rawTimestamp);
    }

    public String decodeString(int rawString) {
        return this.strings[rawString];
    }
}

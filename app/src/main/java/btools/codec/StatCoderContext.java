package btools.codec;

import btools.util.BitCoderContext;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public final class StatCoderContext extends BitCoderContext {
    private static final int[] noisy_bits = new int[1024];
    private static Map<String, long[]> statsPerName;
    private long lastbitpos;

    static {
        for (int i = 0; i < 1024; i++) {
            int noisybits = 0;
            for (int p = i; p > 2; p >>= 1) {
                noisybits++;
            }
            noisy_bits[i] = noisybits;
        }
    }

    public StatCoderContext(byte[] ab) {
        super(ab);
        this.lastbitpos = 0L;
    }

    public void assignBits(String name) {
        long bitpos = getWritingBitPosition();
        if (statsPerName == null) {
            statsPerName = new TreeMap();
        }
        long[] stats = statsPerName.get(name);
        if (stats == null) {
            stats = new long[2];
            statsPerName.put(name, stats);
        }
        stats[0] = stats[0] + (bitpos - this.lastbitpos);
        stats[1] = stats[1] + 1;
        this.lastbitpos = bitpos;
    }

    public static String getBitReport() {
        if (statsPerName == null) {
            return "<empty bit report>";
        }
        StringBuilder sb = new StringBuilder();
        for (String name : statsPerName.keySet()) {
            long[] stats = statsPerName.get(name);
            sb.append(name + " count=" + stats[1] + " bits=" + stats[0] + "\n");
        }
        statsPerName = null;
        return sb.toString();
    }

    public void encodeNoisyNumber(int value, int noisybits) {
        if (value < 0) {
            throw new IllegalArgumentException("encodeVarBits expects positive value");
        }
        if (noisybits > 0) {
            int mask = (-1) >>> (32 - noisybits);
            encodeBounded(mask, value & mask);
            value >>= noisybits;
        }
        encodeVarBits(value);
    }

    public int decodeNoisyNumber(int noisybits) {
        int value = decodeBits(noisybits);
        return (decodeVarBits() << noisybits) | value;
    }

    public void encodeNoisyDiff(int value, int noisybits) {
        if (noisybits > 0) {
            int value2 = value + (1 << (noisybits - 1));
            int mask = (-1) >>> (32 - noisybits);
            encodeBounded(mask, value2 & mask);
            value = value2 >> noisybits;
        }
        encodeVarBits(value < 0 ? -value : value);
        if (value != 0) {
            encodeBit(value < 0);
        }
    }

    public int decodeNoisyDiff(int noisybits) {
        int value = 0;
        if (noisybits > 0) {
            value = decodeBits(noisybits) - (1 << (noisybits - 1));
        }
        int val2 = decodeVarBits() << noisybits;
        if (val2 != 0 && decodeBit()) {
            val2 = -val2;
        }
        return value + val2;
    }

    public void encodePredictedValue(int value, int predictor) {
        int noisybits = 0;
        for (int p = predictor < 0 ? -predictor : predictor; p > 2; p >>= 1) {
            noisybits++;
        }
        encodeNoisyDiff(value - predictor, noisybits);
    }

    public int decodePredictedValue(int predictor) {
        int p = predictor < 0 ? -predictor : predictor;
        int noisybits = 0;
        while (p > 1023) {
            noisybits++;
            p >>= 1;
        }
        return decodeNoisyDiff(noisy_bits[p] + noisybits) + predictor;
    }

    public void encodeSortedArray(int[] values, int offset, int subsize, int nextbit, int mask) {
        int nextbit2;
        if (subsize != 1) {
            nextbit2 = nextbit;
        } else {
            int nextbit3 = nextbit;
            while (nextbit3 != 0) {
                encodeBit((values[offset] & nextbit3) != 0);
                nextbit3 >>= 1;
            }
            nextbit2 = nextbit3;
        }
        if (nextbit2 == 0) {
            return;
        }
        int data = mask & values[offset];
        int mask2 = mask | nextbit2;
        int end = subsize + offset;
        int i = offset;
        while (i < end && (values[i] & mask2) == data) {
            i++;
        }
        int size1 = i - offset;
        int size2 = subsize - size1;
        encodeBounded(subsize, size1);
        if (size1 > 0) {
            encodeSortedArray(values, offset, size1, nextbit2 >> 1, mask2);
        }
        if (size2 > 0) {
            encodeSortedArray(values, i, size2, nextbit2 >> 1, mask2);
        }
    }

    public void decodeSortedArray(int[] values, int offset, int offset2, int nextbitpos, int value) {
        if (offset2 == 1) {
            if (nextbitpos >= 0) {
                value |= decodeBitsReverse(nextbitpos + 1);
            }
            values[offset] = value;
        } else {
            if (nextbitpos >= 0) {
                int size1 = decodeBounded(offset2);
                int size2 = offset2 - size1;
                if (size1 > 0) {
                    decodeSortedArray(values, offset, size1, nextbitpos - 1, value);
                }
                if (size2 > 0) {
                    decodeSortedArray(values, offset + size1, size2, nextbitpos - 1, value | (1 << nextbitpos));
                    return;
                }
                return;
            }
            while (true) {
                int subsize = offset2 - 1;
                if (offset2 > 0) {
                    values[offset] = value;
                    offset++;
                    offset2 = subsize;
                } else {
                    return;
                }
            }
        }
    }
}

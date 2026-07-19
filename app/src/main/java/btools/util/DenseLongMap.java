package btools.util;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DenseLongMap {
    private int[] bitplaneCount;
    private List<byte[]> blocklist;
    private int blocksize;
    private int blocksizeBits;
    private long blocksizeBitsMask;
    private long getCount;
    private int maxvalue;
    private long putCount;

    public DenseLongMap() {
        this(512);
    }

    public DenseLongMap(int blocksize) {
        this.blocklist = new ArrayList(4096);
        this.maxvalue = 254;
        this.bitplaneCount = new int[8];
        this.putCount = 0L;
        this.getCount = 0L;
        int bits = 4;
        while (bits < 28 && (1 << bits) != blocksize) {
            bits++;
        }
        if (bits == 28) {
            throw new RuntimeException("not a valid blocksize: " + blocksize + " ( expected 1 << bits with bits in (4..27) )");
        }
        this.blocksizeBits = bits + 3;
        this.blocksizeBitsMask = (1 << this.blocksizeBits) - 1;
        this.blocksize = blocksize;
    }

    public void put(long key, int value) {
        this.putCount++;
        if (value < 0 || value > this.maxvalue) {
            throw new IllegalArgumentException("value out of range (0.." + this.maxvalue + "): " + value);
        }
        int blockn = (int) (key >> this.blocksizeBits);
        int offset = (int) (key & this.blocksizeBitsMask);
        byte[] block = blockn < this.blocklist.size() ? this.blocklist.get(blockn) : null;
        int valuebits = 1;
        if (block == null) {
            block = new byte[sizeForBits(1)];
            int[] iArr = this.bitplaneCount;
            iArr[0] = iArr[0] + 1;
            while (this.blocklist.size() < blockn + 1) {
                this.blocklist.add(null);
            }
            this.blocklist.set(blockn, block);
        } else {
            while (sizeForBits(valuebits) < block.length) {
                valuebits++;
            }
        }
        int headersize = 1 << valuebits;
        byte v = (byte) (value + 1);
        int idx = 1;
        while (idx < headersize) {
            if (block[idx] == 0) {
                block[idx] = v;
            }
            if (block[idx] == v) {
                break;
            } else {
                idx++;
            }
        }
        if (idx == headersize) {
            block = expandBlock(block, valuebits);
            block[idx] = v;
            this.blocklist.set(blockn, block);
            valuebits++;
            headersize = 1 << valuebits;
        }
        int bitmask = 1 << (offset & 7);
        int invmask = bitmask ^ 255;
        int probebit = 1;
        int blockidx = (offset >> 3) + headersize;
        for (int i = 0; i < valuebits; i++) {
            if ((idx & probebit) != 0) {
                block[blockidx] = (byte) (block[blockidx] | bitmask);
            } else {
                block[blockidx] = (byte) (block[blockidx] & invmask);
            }
            probebit <<= 1;
            blockidx += this.blocksize;
        }
    }

    private int sizeForBits(int bits) {
        return (1 << bits) + (this.blocksize * bits);
    }

    private byte[] expandBlock(byte[] block, int valuebits) {
        int[] iArr = this.bitplaneCount;
        iArr[valuebits] = iArr[valuebits] + 1;
        byte[] newblock = new byte[sizeForBits(valuebits + 1)];
        int headersize = 1 << valuebits;
        System.arraycopy(block, 0, newblock, 0, headersize);
        System.arraycopy(block, headersize, newblock, headersize * 2, block.length - headersize);
        return newblock;
    }

    public int getInt(long key) {
        long j = this.getCount;
        this.getCount = 1 + j;
        if (j == 0) {
            System.out.println("**** DenseLongMap stats ****");
            System.out.println("putCount=" + this.putCount);
            for (int i = 0; i < 8; i++) {
                System.out.println(i + "-bitplanes=" + this.bitplaneCount[i]);
            }
            System.out.println("****************************");
        }
        if (key < 0) {
            return -1;
        }
        int blockn = (int) (key >> this.blocksizeBits);
        int offset = (int) (this.blocksizeBitsMask & key);
        byte[] block = blockn < this.blocklist.size() ? this.blocklist.get(blockn) : null;
        if (block == null) {
            return -1;
        }
        int valuebits = 1;
        while (sizeForBits(valuebits) < block.length) {
            valuebits++;
        }
        int headersize = 1 << valuebits;
        int bitmask = 1 << (offset & 7);
        int probebit = 1;
        int blockidx = (offset >> 3) + headersize;
        int idx = 0;
        for (int i2 = 0; i2 < valuebits; i2++) {
            if ((block[blockidx] & bitmask) != 0) {
                idx |= probebit;
            }
            probebit <<= 1;
            blockidx += this.blocksize;
        }
        int i3 = block[idx];
        return ((i3 + 256) & 255) - 1;
    }
}

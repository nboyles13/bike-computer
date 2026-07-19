package btools.codec;

import btools.util.ByteDataReader;
import btools.util.IByteArrayUnifier;
import java.util.HashMap;
import java.util.Map;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public final class MicroCache2 extends MicroCache {
    private int cellsize;
    private int latBase;
    private int lonBase;

    public MicroCache2(int size, byte[] databuffer, int lonIdx, int latIdx, int divisor) {
        super(databuffer);
        this.faid = new int[size];
        this.fapos = new int[size];
        this.size = 0;
        this.cellsize = DurationKt.NANOS_IN_MILLIS / divisor;
        this.lonBase = this.cellsize * lonIdx;
        this.latBase = this.cellsize * latIdx;
    }

    public byte[] readUnified(int len, IByteArrayUnifier u) {
        byte[] b = u.unify(this.ab, this.aboffset, len);
        this.aboffset += len;
        return b;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Removed duplicated region for block: B:100:0x0342  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x034d  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0392  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0302  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0323  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public MicroCache2(StatCoderContext bc, DataBuffers dataBuffers, int lonIdx, int latIdx, int divisor, TagValueValidator wayValidator, WaypointMatcher waypointMatcher) {
        LinkedListContainer reverseLinks;
        int finalsize;
        int selev;
        int[] validBits;
        TagValueCoder nodeTagCoder;
        NoisyDiffCoder nodeIdxDiff;
        int links;
        TagValueCoder nodeTagCoder2;
        boolean isReverse;
        int dlat_remaining;
        int sizeoffset;
        int sizeoffset2;
        int ilat;
        boolean isReverse2;
        int li;
        int ilon;
        NoisyDiffCoder nodeIdxDiff2;
        boolean isReverse3;
        int sizeoffset3;
        int startPointer;
        String str;
        int finaldatasize;
        WaypointMatcher matcher;
        int transcount;
        int dlon_remaining;
        int i;
        TagValueWrapper wayTags;
        super(null);
        StatCoderContext statCoderContext = bc;
        TagValueValidator tagValueValidator = wayValidator;
        this.cellsize = DurationKt.NANOS_IN_MILLIS / divisor;
        this.lonBase = this.cellsize * lonIdx;
        this.latBase = this.cellsize * latIdx;
        TagValueCoder wayTagCoder = new TagValueCoder(statCoderContext, dataBuffers, tagValueValidator);
        TagValueCoder nodeTagCoder3 = new TagValueCoder(statCoderContext, dataBuffers, null);
        NoisyDiffCoder nodeIdxDiff3 = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder nodeEleDiff = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder extLonDiff = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder extLatDiff = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder transEleDiff = new NoisyDiffCoder(statCoderContext);
        this.size = statCoderContext.decodeNoisyNumber(5);
        this.faid = this.size > dataBuffers.ibuf2.length ? new int[this.size] : dataBuffers.ibuf2;
        this.fapos = this.size > dataBuffers.ibuf3.length ? new int[this.size] : dataBuffers.ibuf3;
        int[] alon = this.size > dataBuffers.alon.length ? new int[this.size] : dataBuffers.alon;
        int[] alat = this.size > dataBuffers.alat.length ? new int[this.size] : dataBuffers.alat;
        if (debug) {
            System.out.println("*** decoding cache of size=" + this.size + " for lonIdx=" + lonIdx + " latIdx=" + latIdx);
        }
        bc.decodeSortedArray(this.faid, 0, this.size, 29, 0);
        for (int n = 0; n < this.size; n++) {
            long id64 = expandId(this.faid[n]);
            alon[n] = (int) (id64 >> 32);
            alat[n] = (int) ((-1) & id64);
        }
        int netdatasize = statCoderContext.decodeNoisyNumber(10);
        this.ab = netdatasize > dataBuffers.bbuf1.length ? new byte[netdatasize] : dataBuffers.bbuf1;
        this.aboffset = 0;
        int[] validBits2 = new int[(this.size + 31) >> 5];
        int finaldatasize2 = 0;
        LinkedListContainer reverseLinks2 = new LinkedListContainer(this.size, dataBuffers.ibuf1);
        int selev2 = 0;
        int n2 = 0;
        while (true) {
            int netdatasize2 = netdatasize;
            if (n2 >= this.size) {
                break;
            }
            int ilon2 = alon[n2];
            int ilat2 = alat[n2];
            int finaldatasize3 = finaldatasize2;
            int featureId = bc.decodeVarBits();
            if (featureId == 13) {
                this.fapos[n2] = this.aboffset;
                int i2 = n2 >> 5;
                validBits2[i2] = validBits2[i2] | (1 << n2);
                nodeTagCoder = nodeTagCoder3;
                nodeIdxDiff = nodeIdxDiff3;
            } else {
                short trExceptions = 0;
                while (featureId != 0) {
                    int bitsize = statCoderContext.decodeNoisyNumber(5);
                    if (featureId == 2) {
                        trExceptions = (short) statCoderContext.decodeBounded(1023);
                    } else if (featureId == 1) {
                        writeBoolean(true);
                        writeShort(trExceptions);
                        trExceptions = 0;
                        writeBoolean(bc.decodeBit());
                        writeInt(ilon2 + statCoderContext.decodeNoisyDiff(10));
                        writeInt(ilat2 + statCoderContext.decodeNoisyDiff(10));
                        writeInt(ilon2 + statCoderContext.decodeNoisyDiff(10));
                        writeInt(ilat2 + statCoderContext.decodeNoisyDiff(10));
                    } else {
                        for (int i3 = 0; i3 < bitsize; i3++) {
                            bc.decodeBit();
                        }
                    }
                    featureId = bc.decodeVarBits();
                }
                writeBoolean(false);
                int selev3 = selev2 + nodeEleDiff.decodeSignedValue();
                writeShort((short) selev3);
                TagValueWrapper nodeTags = nodeTagCoder3.decodeTagValueSet();
                writeVarBytes(nodeTags == null ? null : nodeTags.data);
                int links2 = statCoderContext.decodeNoisyNumber(1);
                String str2 = "/";
                if (debug) {
                    System.out.println("***   decoding node " + ilon2 + "/" + ilat2 + " with links=" + links2);
                }
                int sizeoffset4 = 0;
                while (sizeoffset4 < links2) {
                    int nodeIdx = nodeIdxDiff3.decodeSignedValue() + n2;
                    if (nodeIdx != n2) {
                        int dlon_remaining2 = alon[nodeIdx] - ilon2;
                        links = links2;
                        nodeTagCoder2 = nodeTagCoder3;
                        isReverse = false;
                        dlat_remaining = alat[nodeIdx] - ilat2;
                        sizeoffset = 0;
                        sizeoffset2 = dlon_remaining2;
                    } else {
                        boolean isReverse4 = bc.decodeBit();
                        int dlon_remaining3 = extLonDiff.decodeSignedValue();
                        links = links2;
                        nodeTagCoder2 = nodeTagCoder3;
                        isReverse = isReverse4;
                        dlat_remaining = extLatDiff.decodeSignedValue();
                        sizeoffset = 0;
                        sizeoffset2 = dlon_remaining3;
                    }
                    if (!debug) {
                        ilat = ilat2;
                        isReverse2 = isReverse;
                        li = sizeoffset4;
                        ilon = ilon2;
                        nodeIdxDiff2 = nodeIdxDiff3;
                    } else {
                        nodeIdxDiff2 = nodeIdxDiff3;
                        li = sizeoffset4;
                        int li2 = ilon2 + sizeoffset2;
                        ilat = ilat2;
                        ilon = ilon2;
                        isReverse2 = isReverse;
                        System.out.println("***     decoding link to " + li2 + str2 + (ilat2 + dlat_remaining) + " extern=" + (nodeIdx == n2));
                    }
                    TagValueWrapper wayTags2 = wayTagCoder.decodeTagValueSet();
                    boolean linkValid = wayTags2 != null || tagValueValidator == null;
                    if (!linkValid) {
                        isReverse3 = isReverse2;
                        sizeoffset3 = sizeoffset;
                        startPointer = finaldatasize3;
                    } else {
                        int startPointer2 = this.aboffset;
                        sizeoffset3 = writeSizePlaceHolder();
                        writeVarLengthSigned(sizeoffset2);
                        writeVarLengthSigned(dlat_remaining);
                        int i4 = n2 >> 5;
                        validBits2[i4] = validBits2[i4] | (1 << n2);
                        if (nodeIdx != n2) {
                            reverseLinks2.addDataElement(nodeIdx, n2);
                            finaldatasize3 += (this.aboffset + 1) - startPointer2;
                            int i5 = nodeIdx >> 5;
                            validBits2[i5] = validBits2[i5] | (1 << nodeIdx);
                        }
                        isReverse3 = isReverse2;
                        writeModeAndDesc(isReverse3, wayTags2 == null ? null : wayTags2.data);
                        startPointer = finaldatasize3;
                    }
                    if (isReverse3) {
                        str = str2;
                        finaldatasize = startPointer;
                    } else {
                        if (wayTags2 != null) {
                            str = str2;
                            if (wayTags2.accessType >= 2) {
                                matcher = waypointMatcher;
                            }
                            int ilontarget = ilon + sizeoffset2;
                            int ilattarget = ilat + dlat_remaining;
                            if (matcher != null) {
                                boolean useAsStartWay = tagValueValidator.checkStartWay(wayTags2.data);
                                if (!matcher.start(ilon, ilat, ilontarget, ilattarget, useAsStartWay)) {
                                    matcher = null;
                                }
                            }
                            transcount = bc.decodeVarBits();
                            if (debug) {
                                finaldatasize = startPointer;
                                dlon_remaining = sizeoffset2;
                            } else {
                                finaldatasize = startPointer;
                                dlon_remaining = sizeoffset2;
                                System.out.println("***       decoding geometry with count=" + transcount);
                            }
                            int count = transcount + 1;
                            i = 0;
                            while (i < transcount) {
                                int transcount2 = transcount;
                                int dlon = bc.decodePredictedValue(dlon_remaining / count);
                                int dlat = bc.decodePredictedValue(dlat_remaining / count);
                                dlon_remaining -= dlon;
                                dlat_remaining -= dlat;
                                count--;
                                int elediff = transEleDiff.decodeSignedValue();
                                if (wayTags2 != null) {
                                    writeVarLengthSigned(dlon);
                                    writeVarLengthSigned(dlat);
                                    writeVarLengthSigned(elediff);
                                }
                                if (matcher == null) {
                                    wayTags = wayTags2;
                                } else {
                                    wayTags = wayTags2;
                                    int elediff2 = ilattarget - dlat_remaining;
                                    matcher.transferNode(ilontarget - dlon_remaining, elediff2);
                                }
                                i++;
                                transcount = transcount2;
                                wayTags2 = wayTags;
                            }
                            if (matcher != null) {
                                matcher.end();
                            }
                        } else {
                            str = str2;
                        }
                        matcher = null;
                        int ilontarget2 = ilon + sizeoffset2;
                        int ilattarget2 = ilat + dlat_remaining;
                        if (matcher != null) {
                        }
                        transcount = bc.decodeVarBits();
                        if (debug) {
                        }
                        int count2 = transcount + 1;
                        i = 0;
                        while (i < transcount) {
                        }
                        if (matcher != null) {
                        }
                    }
                    if (linkValid) {
                        injectSize(sizeoffset3);
                    }
                    sizeoffset4 = li + 1;
                    tagValueValidator = wayValidator;
                    finaldatasize3 = finaldatasize;
                    links2 = links;
                    nodeTagCoder3 = nodeTagCoder2;
                    nodeIdxDiff3 = nodeIdxDiff2;
                    ilat2 = ilat;
                    ilon2 = ilon;
                    str2 = str;
                }
                nodeTagCoder = nodeTagCoder3;
                nodeIdxDiff = nodeIdxDiff3;
                this.fapos[n2] = this.aboffset;
                selev2 = selev3;
            }
            finaldatasize2 = finaldatasize3;
            n2++;
            statCoderContext = bc;
            tagValueValidator = wayValidator;
            netdatasize = netdatasize2;
            nodeTagCoder3 = nodeTagCoder;
            nodeIdxDiff3 = nodeIdxDiff;
        }
        int finalsize2 = 0;
        int startpos = 0;
        int finaldatasize4 = finaldatasize2;
        for (int i6 = 0; i6 < this.size; i6++) {
            int endpos = this.fapos[i6];
            if ((validBits2[i6 >> 5] & (1 << i6)) != 0) {
                finaldatasize4 += endpos - startpos;
                finalsize2++;
            }
            startpos = endpos;
        }
        byte[] abOld = this.ab;
        int[] faidOld = this.faid;
        int[] faposOld = this.fapos;
        int sizeOld = this.size;
        this.ab = new byte[finaldatasize4];
        this.faid = new int[finalsize2];
        this.fapos = new int[finalsize2];
        this.aboffset = 0;
        this.size = 0;
        int startpos2 = 0;
        int n3 = 0;
        while (n3 < sizeOld) {
            int endpos2 = faposOld[n3];
            if ((validBits2[n3 >> 5] & (1 << n3)) == 0) {
                reverseLinks = reverseLinks2;
                finalsize = finalsize2;
                selev = selev2;
                validBits = validBits2;
            } else {
                int len = endpos2 - startpos2;
                byte[] bArr = this.ab;
                finalsize = finalsize2;
                int finalsize3 = this.aboffset;
                System.arraycopy(abOld, startpos2, bArr, finalsize3, len);
                if (!debug) {
                    selev = selev2;
                    validBits = validBits2;
                } else {
                    selev = selev2;
                    validBits = validBits2;
                    System.out.println("*** copied " + len + " bytes from " + this.aboffset + " for node " + n3);
                }
                this.aboffset += len;
                int cnt = reverseLinks2.initList(n3);
                if (debug) {
                    System.out.println("*** appending " + cnt + " reverse links for node " + n3);
                }
                int ri = 0;
                while (ri < cnt) {
                    int nodeIdx2 = reverseLinks2.getDataElement();
                    int sizeoffset5 = writeSizePlaceHolder();
                    writeVarLengthSigned(alon[nodeIdx2] - alon[n3]);
                    writeVarLengthSigned(alat[nodeIdx2] - alat[n3]);
                    writeModeAndDesc(true, null);
                    injectSize(sizeoffset5);
                    ri++;
                    cnt = cnt;
                    reverseLinks2 = reverseLinks2;
                }
                reverseLinks = reverseLinks2;
                this.faid[this.size] = faidOld[n3];
                this.fapos[this.size] = this.aboffset;
                this.size++;
            }
            startpos2 = endpos2;
            n3++;
            finalsize2 = finalsize;
            selev2 = selev;
            validBits2 = validBits;
            reverseLinks2 = reverseLinks;
        }
        init(this.size);
    }

    @Override // btools.codec.MicroCache
    public long expandId(int id32) {
        int dlon = 0;
        int dlat = 0;
        for (int bm = 1; bm < 32768; bm <<= 1) {
            if ((id32 & 1) != 0) {
                dlon |= bm;
            }
            if ((id32 & 2) != 0) {
                dlat |= bm;
            }
            id32 >>= 2;
        }
        int bm2 = this.lonBase;
        int lon32 = bm2 + dlon;
        int lat32 = this.latBase + dlat;
        return (((long) lon32) << 32) | ((long) lat32);
    }

    @Override // btools.codec.MicroCache
    public int shrinkId(long id64) {
        int lon32 = (int) (id64 >> 32);
        int lat32 = (int) ((-1) & id64);
        int dlon = lon32 - this.lonBase;
        int dlat = lat32 - this.latBase;
        int id32 = 0;
        for (int bm = 16384; bm > 0; bm >>= 1) {
            id32 <<= 2;
            if ((dlon & bm) != 0) {
                id32 |= 1;
            }
            if ((dlat & bm) != 0) {
                id32 |= 2;
            }
        }
        return id32;
    }

    @Override // btools.codec.MicroCache
    public boolean isInternal(int ilon, int ilat) {
        return ilon >= this.lonBase && ilon < this.lonBase + this.cellsize && ilat >= this.latBase && ilat < this.latBase + this.cellsize;
    }

    @Override // btools.codec.MicroCache
    public int encodeMicroCache(byte[] buffer) {
        NoisyDiffCoder transEleDiff;
        int netdatasize;
        IntegerFifo3Pass transCounts;
        TagValueCoder wayTagCoder;
        NoisyDiffCoder extLatDiff;
        Map<Long, Integer> idMap;
        TagValueCoder nodeTagCoder;
        NoisyDiffCoder nodeIdxDiff;
        NoisyDiffCoder nodeEleDiff;
        IntegerFifo3Pass restrictionBits;
        int ilat;
        NoisyDiffCoder transEleDiff2;
        IntegerFifo3Pass transCounts2;
        TagValueCoder wayTagCoder2;
        NoisyDiffCoder extLonDiff;
        IntegerFifo3Pass linkCounts;
        TagValueCoder nodeTagCoder2;
        byte[] description;
        int n;
        NoisyDiffCoder extLonDiff2;
        NoisyDiffCoder extLatDiff2;
        NoisyDiffCoder nodeIdxDiff2;
        NoisyDiffCoder transEleDiff3;
        IntegerFifo3Pass transCounts3;
        int transcount;
        NoisyDiffCoder nodeEleDiff2;
        IntegerFifo3Pass restrictionBits2;
        MicroCache2 microCache2 = this;
        Map<Long, Integer> idMap2 = new HashMap<>();
        for (int n2 = 0; n2 < microCache2.size; n2++) {
            idMap2.put(Long.valueOf(microCache2.expandId(microCache2.faid[n2])), Integer.valueOf(n2));
        }
        IntegerFifo3Pass linkCounts2 = new IntegerFifo3Pass(256);
        IntegerFifo3Pass transCounts4 = new IntegerFifo3Pass(256);
        IntegerFifo3Pass transCounts5 = new IntegerFifo3Pass(16);
        TagValueCoder wayTagCoder3 = new TagValueCoder();
        TagValueCoder nodeTagCoder3 = new TagValueCoder();
        NoisyDiffCoder nodeIdxDiff3 = new NoisyDiffCoder();
        NoisyDiffCoder nodeEleDiff3 = new NoisyDiffCoder();
        NoisyDiffCoder extLonDiff3 = new NoisyDiffCoder();
        NoisyDiffCoder extLonDiff4 = new NoisyDiffCoder();
        NoisyDiffCoder transEleDiff4 = new NoisyDiffCoder();
        int netdatasize2 = 0;
        int pass = 1;
        while (true) {
            boolean dostats = pass == 3;
            boolean dodebug = debug && pass == 3;
            if (pass < 3) {
                int[] iArr = microCache2.fapos;
                int netdatasize3 = microCache2.size;
                netdatasize2 = iArr[netdatasize3 - 1];
            }
            StatCoderContext bc = new StatCoderContext(buffer);
            linkCounts2.init();
            transCounts4.init();
            transCounts5.init();
            wayTagCoder3.encodeDictionary(bc);
            if (dostats) {
                bc.assignBits("wayTagDictionary");
            }
            nodeTagCoder3.encodeDictionary(bc);
            if (dostats) {
                bc.assignBits("nodeTagDictionary");
            }
            nodeIdxDiff3.encodeDictionary(bc);
            nodeEleDiff3.encodeDictionary(bc);
            extLonDiff3.encodeDictionary(bc);
            extLonDiff4.encodeDictionary(bc);
            transEleDiff4.encodeDictionary(bc);
            if (dostats) {
                bc.assignBits("noisebits");
            }
            int pass2 = pass;
            bc.encodeNoisyNumber(microCache2.size, 5);
            if (dostats) {
                bc.assignBits("nodecount");
            }
            bc.encodeSortedArray(microCache2.faid, 0, microCache2.size, 536870912, 0);
            if (dostats) {
                bc.assignBits("node-positions");
            }
            bc.encodeNoisyNumber(netdatasize2, 10);
            if (dostats) {
                bc.assignBits("netdatasize");
            }
            if (dodebug) {
                netdatasize = netdatasize2;
                transEleDiff = transEleDiff4;
                System.out.println("*** encoding cache of size=" + microCache2.size);
            } else {
                transEleDiff = transEleDiff4;
                netdatasize = netdatasize2;
            }
            int lastSelev = 0;
            int n3 = 0;
            while (n3 < microCache2.size) {
                microCache2.aboffset = microCache2.startPos(n3);
                microCache2.aboffsetEnd = microCache2.fapos[n3];
                if (!dodebug) {
                    transCounts = transCounts4;
                    wayTagCoder = wayTagCoder3;
                    extLatDiff = extLonDiff4;
                } else {
                    transCounts = transCounts4;
                    wayTagCoder = wayTagCoder3;
                    extLatDiff = extLonDiff4;
                    System.out.println("*** encoding node " + n3 + " from " + microCache2.aboffset + " to " + microCache2.aboffsetEnd);
                }
                long id64 = microCache2.expandId(microCache2.faid[n3]);
                NoisyDiffCoder extLonDiff5 = extLonDiff3;
                int ilon = (int) (id64 >> 32);
                IntegerFifo3Pass restrictionBits3 = transCounts5;
                int ilat2 = (int) (id64 & (-1));
                if (microCache2.aboffset == microCache2.aboffsetEnd) {
                    bc.encodeVarBits(13);
                    idMap = idMap2;
                    linkCounts = linkCounts2;
                    nodeTagCoder = nodeTagCoder3;
                    nodeIdxDiff = nodeIdxDiff3;
                    nodeEleDiff = nodeEleDiff3;
                    restrictionBits = restrictionBits3;
                    ilat = n3;
                    transEleDiff2 = transEleDiff;
                    transCounts2 = transCounts;
                    wayTagCoder2 = wayTagCoder;
                    extLonDiff = extLonDiff5;
                } else {
                    while (readBoolean()) {
                        short exceptions = readShort();
                        if (exceptions != 0) {
                            bc.encodeVarBits(2);
                            bc.encodeNoisyNumber(10, 5);
                            bc.encodeBounded(1023, exceptions & 1023);
                        }
                        bc.encodeVarBits(1);
                        bc.encodeNoisyNumber(restrictionBits3.getNext(), 5);
                        long b0 = bc.getWritingBitPosition();
                        bc.encodeBit(readBoolean());
                        bc.encodeNoisyDiff(readInt() - ilon, 10);
                        bc.encodeNoisyDiff(readInt() - ilat2, 10);
                        bc.encodeNoisyDiff(readInt() - ilon, 10);
                        bc.encodeNoisyDiff(readInt() - ilat2, 10);
                        restrictionBits3.add((int) (((long) bc.getWritingBitPosition()) - b0));
                        n3 = n3;
                        extLonDiff5 = extLonDiff5;
                    }
                    NoisyDiffCoder extLonDiff6 = extLonDiff5;
                    int n4 = n3;
                    bc.encodeVarBits(0);
                    if (dostats) {
                        bc.assignBits("extradata");
                    }
                    int ilatlink = readShort();
                    nodeEleDiff3.encodeSignedValue(ilatlink - lastSelev);
                    if (dostats) {
                        bc.assignBits("nodeele");
                    }
                    int lastSelev2 = ilatlink;
                    nodeTagCoder3.encodeTagValueSet(readVarBytes());
                    if (dostats) {
                        bc.assignBits("nodeTagIdx");
                    }
                    int nlinks = linkCounts2.getNext();
                    if (dodebug) {
                        System.out.println("*** nlinks=" + nlinks);
                    }
                    bc.encodeNoisyNumber(nlinks, 1);
                    if (dostats) {
                        bc.assignBits("link-counts");
                    }
                    int nlinks2 = 0;
                    while (hasMoreData()) {
                        int startPointer = microCache2.aboffset;
                        int endPointer = getEndPointer();
                        int ilonlink = readVarLengthSigned() + ilon;
                        int selev = ilatlink;
                        int ilatlink2 = ilat2 + readVarLengthSigned();
                        int sizecode = readVarLengthUnsigned();
                        boolean isReverse = (sizecode & 1) != 0;
                        int lastSelev3 = lastSelev2;
                        int lastSelev4 = sizecode >> 1;
                        if (lastSelev4 <= 0) {
                            nodeTagCoder2 = nodeTagCoder3;
                            description = null;
                        } else {
                            nodeTagCoder2 = nodeTagCoder3;
                            description = new byte[lastSelev4];
                            microCache2.readFully(description);
                        }
                        byte[] description2 = description;
                        IntegerFifo3Pass linkCounts3 = linkCounts2;
                        int ilat3 = ilat2;
                        long link64 = ((long) ilatlink2) | (((long) ilonlink) << 32);
                        Integer idx = idMap2.get(Long.valueOf(link64));
                        boolean isInternal = idx != null;
                        Map<Long, Integer> idMap3 = idMap2;
                        if (!isReverse || !isInternal) {
                            NoisyDiffCoder nodeEleDiff4 = nodeEleDiff3;
                            IntegerFifo3Pass restrictionBits4 = restrictionBits3;
                            if (dodebug) {
                                System.out.println("*** encoding link reverse=" + isReverse + " internal=" + isInternal);
                            }
                            nlinks2++;
                            if (isInternal) {
                                int nodeIdx = idx.intValue();
                                if (dodebug) {
                                    System.out.println("*** target nodeIdx=" + nodeIdx);
                                }
                                n = n4;
                                if (nodeIdx == n) {
                                    throw new RuntimeException("ups: self ref?");
                                }
                                nodeIdxDiff3.encodeSignedValue(nodeIdx - n);
                                if (dostats) {
                                    bc.assignBits("nodeIdx");
                                }
                                extLatDiff2 = extLatDiff;
                                extLonDiff2 = extLonDiff6;
                            } else {
                                n = n4;
                                nodeIdxDiff3.encodeSignedValue(0);
                                bc.encodeBit(isReverse);
                                extLonDiff2 = extLonDiff6;
                                extLonDiff2.encodeSignedValue(ilonlink - ilon);
                                extLatDiff2 = extLatDiff;
                                extLatDiff2.encodeSignedValue(ilatlink2 - ilat3);
                                if (dostats) {
                                    bc.assignBits("externalNode");
                                }
                            }
                            extLatDiff = extLatDiff2;
                            TagValueCoder wayTagCoder4 = wayTagCoder;
                            wayTagCoder4.encodeTagValueSet(description2);
                            if (dostats) {
                                bc.assignBits("wayDescIdx");
                            }
                            if (isReverse) {
                                nodeIdxDiff2 = nodeIdxDiff3;
                                transEleDiff3 = transEleDiff;
                                transCounts3 = transCounts;
                            } else {
                                byte[] geometry = microCache2.readDataUntil(endPointer);
                                int count = transCounts.getNext();
                                if (dodebug) {
                                    System.out.println("*** encoding geometry with count=" + count);
                                }
                                int count2 = count + 1;
                                bc.encodeVarBits(count);
                                if (dostats) {
                                    bc.assignBits("transcount");
                                }
                                if (geometry == null) {
                                    nodeIdxDiff2 = nodeIdxDiff3;
                                    transEleDiff3 = transEleDiff;
                                    transcount = 0;
                                } else {
                                    int dlon_remaining = ilonlink - ilon;
                                    int dlat_remaining = ilatlink2 - ilat3;
                                    int transcount2 = 0;
                                    ByteDataReader r = new ByteDataReader(geometry);
                                    while (r.hasMoreData()) {
                                        transcount2++;
                                        byte[] geometry2 = geometry;
                                        int dlon = r.readVarLengthSigned();
                                        int ilatlink3 = ilatlink2;
                                        int dlat = r.readVarLengthSigned();
                                        NoisyDiffCoder nodeIdxDiff4 = nodeIdxDiff3;
                                        bc.encodePredictedValue(dlon, dlon_remaining / count2);
                                        bc.encodePredictedValue(dlat, dlat_remaining / count2);
                                        dlon_remaining -= dlon;
                                        dlat_remaining -= dlat;
                                        if (count2 > 1) {
                                            count2--;
                                        }
                                        if (dostats) {
                                            bc.assignBits("transpos");
                                        }
                                        int varLengthSigned = r.readVarLengthSigned();
                                        ByteDataReader r2 = r;
                                        NoisyDiffCoder transEleDiff5 = transEleDiff;
                                        transEleDiff5.encodeSignedValue(varLengthSigned);
                                        if (dostats) {
                                            bc.assignBits("transele");
                                        }
                                        transEleDiff = transEleDiff5;
                                        geometry = geometry2;
                                        ilatlink2 = ilatlink3;
                                        nodeIdxDiff3 = nodeIdxDiff4;
                                        r = r2;
                                    }
                                    nodeIdxDiff2 = nodeIdxDiff3;
                                    transEleDiff3 = transEleDiff;
                                    transcount = transcount2;
                                }
                                transCounts3 = transCounts;
                                transCounts3.add(transcount);
                            }
                            transEleDiff = transEleDiff3;
                            n4 = n;
                            transCounts = transCounts3;
                            wayTagCoder = wayTagCoder4;
                            extLonDiff6 = extLonDiff2;
                            ilatlink = selev;
                            lastSelev2 = lastSelev3;
                            nodeEleDiff3 = nodeEleDiff4;
                            nodeTagCoder3 = nodeTagCoder2;
                            linkCounts2 = linkCounts3;
                            ilat2 = ilat3;
                            idMap2 = idMap3;
                            restrictionBits3 = restrictionBits4;
                            nodeIdxDiff3 = nodeIdxDiff2;
                            microCache2 = this;
                        } else {
                            if (!dodebug) {
                                nodeEleDiff2 = nodeEleDiff3;
                                restrictionBits2 = restrictionBits3;
                            } else {
                                nodeEleDiff2 = nodeEleDiff3;
                                restrictionBits2 = restrictionBits3;
                                System.out.println("*** NOT encoding link reverse=" + isReverse + " internal=" + isInternal);
                            }
                            netdatasize -= microCache2.aboffset - startPointer;
                            ilatlink = selev;
                            lastSelev2 = lastSelev3;
                            nodeEleDiff3 = nodeEleDiff2;
                            nodeTagCoder3 = nodeTagCoder2;
                            linkCounts2 = linkCounts3;
                            ilat2 = ilat3;
                            idMap2 = idMap3;
                            restrictionBits3 = restrictionBits2;
                        }
                    }
                    idMap = idMap2;
                    nodeTagCoder = nodeTagCoder3;
                    nodeIdxDiff = nodeIdxDiff3;
                    nodeEleDiff = nodeEleDiff3;
                    restrictionBits = restrictionBits3;
                    ilat = n4;
                    transEleDiff2 = transEleDiff;
                    transCounts2 = transCounts;
                    wayTagCoder2 = wayTagCoder;
                    extLonDiff = extLonDiff6;
                    linkCounts = linkCounts2;
                    linkCounts.add(nlinks2);
                    lastSelev = lastSelev2;
                }
                n3 = ilat + 1;
                transEleDiff = transEleDiff2;
                linkCounts2 = linkCounts;
                transCounts4 = transCounts2;
                wayTagCoder3 = wayTagCoder2;
                extLonDiff3 = extLonDiff;
                extLonDiff4 = extLatDiff;
                nodeEleDiff3 = nodeEleDiff;
                nodeTagCoder3 = nodeTagCoder;
                idMap2 = idMap;
                transCounts5 = restrictionBits;
                nodeIdxDiff3 = nodeIdxDiff;
                microCache2 = this;
            }
            Map<Long, Integer> idMap4 = idMap2;
            IntegerFifo3Pass linkCounts4 = linkCounts2;
            IntegerFifo3Pass restrictionBits5 = transCounts5;
            TagValueCoder nodeTagCoder4 = nodeTagCoder3;
            NoisyDiffCoder nodeIdxDiff5 = nodeIdxDiff3;
            NoisyDiffCoder nodeEleDiff5 = nodeEleDiff3;
            NoisyDiffCoder extLatDiff3 = extLonDiff4;
            NoisyDiffCoder transEleDiff6 = transEleDiff;
            IntegerFifo3Pass restrictionBits6 = transCounts4;
            TagValueCoder wayTagCoder5 = wayTagCoder3;
            NoisyDiffCoder extLatDiff4 = extLonDiff3;
            if (pass2 != 3) {
                pass = pass2 + 1;
                transEleDiff4 = transEleDiff6;
                linkCounts2 = linkCounts4;
                transCounts4 = restrictionBits6;
                wayTagCoder3 = wayTagCoder5;
                extLonDiff3 = extLatDiff4;
                netdatasize2 = netdatasize;
                extLonDiff4 = extLatDiff3;
                nodeEleDiff3 = nodeEleDiff5;
                nodeTagCoder3 = nodeTagCoder4;
                idMap2 = idMap4;
                transCounts5 = restrictionBits5;
                nodeIdxDiff3 = nodeIdxDiff5;
                microCache2 = this;
            } else {
                return bc.closeAndGetEncodedLength();
            }
        }
    }
}

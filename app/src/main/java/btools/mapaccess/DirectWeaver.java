package btools.mapaccess;

import btools.codec.DataBuffers;
import btools.codec.NoisyDiffCoder;
import btools.codec.StatCoderContext;
import btools.codec.TagValueCoder;
import btools.codec.TagValueValidator;
import btools.codec.TagValueWrapper;
import btools.codec.WaypointMatcher;
import btools.util.ByteDataWriter;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public final class DirectWeaver extends ByteDataWriter {
    private static final long[] id32_00 = new long[1024];
    private static final long[] id32_10 = new long[1024];
    private static final long[] id32_20 = new long[1024];
    private long id64Base;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0159  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0167  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01ad  */
    /* JADX WARN: Type inference failed for: r0v0, types: [btools.mapaccess.DirectWeaver, btools.util.ByteDataWriter] */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v10, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [btools.mapaccess.DirectWeaver] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r0v9 */
    /* JADX WARN: Type inference failed for: r10v10, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r10v12, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v11, types: [btools.mapaccess.OsmLink] */
    /* JADX WARN: Type inference failed for: r11v12 */
    /* JADX WARN: Type inference failed for: r11v13 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r11v9 */
    /* JADX WARN: Type inference failed for: r13v1, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r13v2, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r13v3, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r13v6 */
    /* JADX WARN: Type inference failed for: r1v17, types: [btools.mapaccess.OsmNode[]] */
    /* JADX WARN: Type inference failed for: r5v21, types: [btools.mapaccess.OsmNode] */
    /* JADX WARN: Type inference failed for: r5v24, types: [btools.mapaccess.OsmNode] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DirectWeaver(StatCoderContext statCoderContext, DataBuffers dataBuffers, int i, int i2, int i3, TagValueValidator tagValueValidator, WaypointMatcher waypointMatcher, OsmNodesMap osmNodesMap) {
        int i4;
        int iDecodeSignedValue;
        boolean z;
        int iDecodeSignedValue2;
        int i5;
        int i6;
        ?? r0;
        int i7;
        int i8;
        TagValueWrapper tagValueWrapper;
        int i9;
        boolean z2;
        WaypointMatcher waypointMatcher2;
        int i10;
        int iDecodeVarBits;
        int i11;
        boolean z3;
        short s;
        short sDecodeBounded;
        TagValueValidator tagValueValidator2 = tagValueValidator;
        ?? byteDataWriter = new ByteDataWriter(null);
        int i12 = DurationKt.NANOS_IN_MILLIS / i3;
        byteDataWriter.id64Base = (((long) (i * i12)) << 32) | ((long) (i2 * i12));
        TagValueCoder tagValueCoder = new TagValueCoder(statCoderContext, dataBuffers, tagValueValidator2);
        TagValueCoder tagValueCoder2 = new TagValueCoder(statCoderContext, dataBuffers, null);
        NoisyDiffCoder noisyDiffCoder = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder noisyDiffCoder2 = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder noisyDiffCoder3 = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder noisyDiffCoder4 = new NoisyDiffCoder(statCoderContext);
        NoisyDiffCoder noisyDiffCoder5 = new NoisyDiffCoder(statCoderContext);
        int iDecodeNoisyNumber = statCoderContext.decodeNoisyNumber(5);
        int[] iArr = iDecodeNoisyNumber > dataBuffers.ibuf2.length ? new int[iDecodeNoisyNumber] : dataBuffers.ibuf2;
        statCoderContext.decodeSortedArray(iArr, 0, iDecodeNoisyNumber, 29, 0);
        ?? r1 = new OsmNode[iDecodeNoisyNumber];
        for (int i13 = 0; i13 < iDecodeNoisyNumber; i13++) {
            long jExpandId = byteDataWriter.expandId(iArr[i13]);
            int i14 = (int) (jExpandId >> 32);
            int i15 = (int) ((-1) & jExpandId);
            OsmNode osmNode = osmNodesMap.get(i14, i15);
            if (osmNode == null) {
                osmNode = new OsmNode(i14, i15);
            } else {
                osmNode.visitID = 1;
                osmNodesMap.remove(osmNode);
            }
            r1[i13] = osmNode;
        }
        int iDecodeNoisyNumber2 = statCoderContext.decodeNoisyNumber(10);
        byteDataWriter.ab = dataBuffers.bbuf1;
        byteDataWriter.aboffset = 0;
        int i16 = 0;
        int i17 = 0;
        ?? r02 = byteDataWriter;
        while (i17 < iDecodeNoisyNumber) {
            ?? r13 = r1[i17];
            int i18 = r13.ilon;
            int i19 = r13.ilat;
            short s2 = 0;
            ?? r03 = r02;
            while (true) {
                int iDecodeVarBits2 = statCoderContext.decodeVarBits();
                if (iDecodeVarBits2 == 0) {
                    break;
                }
                short s3 = s2;
                int i20 = iDecodeNoisyNumber2;
                int i21 = i18;
                int i22 = iDecodeNoisyNumber;
                ?? r04 = r13;
                int i23 = i17;
                int iDecodeNoisyNumber3 = statCoderContext.decodeNoisyNumber(5);
                if (iDecodeVarBits2 == 2) {
                    sDecodeBounded = (short) statCoderContext.decodeBounded(1023);
                    s = 1;
                } else {
                    s = 1;
                    if (iDecodeVarBits2 == 1) {
                        TurnRestriction turnRestriction = new TurnRestriction();
                        turnRestriction.exceptions = s3;
                        sDecodeBounded = 0;
                        turnRestriction.isPositive = statCoderContext.decodeBit();
                        turnRestriction.fromLon = i21 + statCoderContext.decodeNoisyDiff(10);
                        turnRestriction.fromLat = statCoderContext.decodeNoisyDiff(10) + i19;
                        turnRestriction.toLon = i21 + statCoderContext.decodeNoisyDiff(10);
                        turnRestriction.toLat = statCoderContext.decodeNoisyDiff(10) + i19;
                        r04.addTurnRestriction(turnRestriction);
                    } else {
                        sDecodeBounded = s3;
                        for (int i24 = 0; i24 < iDecodeNoisyNumber3; i24++) {
                            statCoderContext.decodeBit();
                        }
                    }
                }
                r13 = r04;
                s2 = sDecodeBounded;
                i18 = i21;
                iDecodeNoisyNumber = i22;
                i17 = i23;
                iDecodeNoisyNumber2 = i20;
                r03 = this;
                tagValueValidator2 = tagValueValidator;
            }
            int iDecodeSignedValue3 = noisyDiffCoder2.decodeSignedValue() + i16;
            r13.selev = (short) iDecodeSignedValue3;
            TagValueWrapper tagValueWrapperDecodeTagValueSet = tagValueCoder2.decodeTagValueSet();
            r13.nodeDescription = tagValueWrapperDecodeTagValueSet == null ? null : tagValueWrapperDecodeTagValueSet.data;
            int i25 = iDecodeNoisyNumber2;
            int iDecodeNoisyNumber4 = statCoderContext.decodeNoisyNumber(1);
            int i26 = 0;
            ?? r05 = r03;
            while (i26 < iDecodeNoisyNumber4) {
                int i27 = iDecodeNoisyNumber4;
                int iDecodeSignedValue4 = i17 + noisyDiffCoder.decodeSignedValue();
                if (iDecodeSignedValue4 != i17) {
                    i4 = iDecodeSignedValue3;
                    int i28 = r1[iDecodeSignedValue4].ilon - i18;
                    iDecodeSignedValue2 = r1[iDecodeSignedValue4].ilat - i19;
                    z = false;
                    iDecodeSignedValue = i28;
                } else {
                    i4 = iDecodeSignedValue3;
                    boolean zDecodeBit = statCoderContext.decodeBit();
                    iDecodeSignedValue = noisyDiffCoder3.decodeSignedValue();
                    z = zDecodeBit;
                    iDecodeSignedValue2 = noisyDiffCoder4.decodeSignedValue();
                }
                short s4 = s2;
                TagValueWrapper tagValueWrapperDecodeTagValueSet2 = tagValueCoder.decodeTagValueSet();
                int i29 = i18 + iDecodeSignedValue;
                int i30 = i19 + iDecodeSignedValue2;
                TagValueWrapper tagValueWrapper2 = tagValueWrapperDecodeTagValueSet;
                r05.aboffset = 0;
                if (z) {
                    i5 = i18;
                    i6 = iDecodeNoisyNumber;
                } else {
                    if (tagValueWrapperDecodeTagValueSet2 != null) {
                        i6 = iDecodeNoisyNumber;
                        if (tagValueWrapperDecodeTagValueSet2.accessType >= 2) {
                            waypointMatcher2 = waypointMatcher;
                        }
                        int i31 = i18 + iDecodeSignedValue;
                        int i32 = i19 + iDecodeSignedValue2;
                        if (waypointMatcher2 != null) {
                            i10 = iDecodeSignedValue;
                        } else {
                            if (tagValueWrapperDecodeTagValueSet2 != null) {
                                i10 = iDecodeSignedValue;
                                if (!tagValueValidator2.checkStartWay(tagValueWrapperDecodeTagValueSet2.data)) {
                                    z3 = false;
                                }
                                if (!waypointMatcher2.start(i18, i19, i31, i32, z3)) {
                                    waypointMatcher2 = null;
                                }
                            } else {
                                i10 = iDecodeSignedValue;
                            }
                            z3 = true;
                            if (!waypointMatcher2.start(i18, i19, i31, i32, z3)) {
                            }
                        }
                        iDecodeVarBits = statCoderContext.decodeVarBits();
                        int i33 = iDecodeVarBits + 1;
                        i11 = 0;
                        while (i11 < iDecodeVarBits) {
                            int i34 = iDecodeVarBits;
                            int iDecodePredictedValue = statCoderContext.decodePredictedValue(i10 / i33);
                            int i35 = i18;
                            int iDecodePredictedValue2 = statCoderContext.decodePredictedValue(iDecodeSignedValue2 / i33);
                            i10 -= iDecodePredictedValue;
                            iDecodeSignedValue2 -= iDecodePredictedValue2;
                            i33--;
                            int iDecodeSignedValue5 = noisyDiffCoder5.decodeSignedValue();
                            if (tagValueWrapperDecodeTagValueSet2 != null) {
                                r05.writeVarLengthSigned(iDecodePredictedValue);
                                r05.writeVarLengthSigned(iDecodePredictedValue2);
                                r05.writeVarLengthSigned(iDecodeSignedValue5);
                            }
                            if (waypointMatcher2 != null) {
                                waypointMatcher2.transferNode(i31 - i10, i32 - iDecodeSignedValue2);
                            }
                            i11++;
                            iDecodeVarBits = i34;
                            i18 = i35;
                        }
                        i5 = i18;
                        if (waypointMatcher2 != null) {
                            waypointMatcher2.end();
                        }
                    } else {
                        i6 = iDecodeNoisyNumber;
                    }
                    waypointMatcher2 = null;
                    int i312 = i18 + iDecodeSignedValue;
                    int i322 = i19 + iDecodeSignedValue2;
                    if (waypointMatcher2 != null) {
                    }
                    iDecodeVarBits = statCoderContext.decodeVarBits();
                    int i332 = iDecodeVarBits + 1;
                    i11 = 0;
                    while (i11 < iDecodeVarBits) {
                    }
                    i5 = i18;
                    if (waypointMatcher2 != null) {
                    }
                }
                if (tagValueWrapperDecodeTagValueSet2 != null) {
                    byte[] bArr = null;
                    if (r05.aboffset <= 0) {
                        z2 = false;
                    } else {
                        bArr = new byte[r05.aboffset];
                        z2 = false;
                        System.arraycopy(r05.ab, 0, bArr, 0, r05.aboffset);
                    }
                    if (iDecodeSignedValue4 != i17) {
                        ?? r10 = r1[iDecodeSignedValue4];
                        ?? osmLink = r13.isLinkUnused() ? r13 : r10.isLinkUnused() ? r10 : null;
                        osmLink = osmLink == null ? new OsmLink() : osmLink;
                        osmLink.descriptionBitmap = tagValueWrapperDecodeTagValueSet2.data;
                        osmLink.geometry = bArr;
                        r13.addLink(osmLink, z, r10);
                        r0 = r13;
                        i7 = i17;
                        i8 = i5;
                        tagValueWrapper = tagValueWrapper2;
                        i9 = i6;
                    } else {
                        tagValueWrapper = tagValueWrapper2;
                        i8 = i5;
                        i9 = i6;
                        r0 = r13;
                        i7 = i17;
                        r13.addLink(i29, i30, tagValueWrapperDecodeTagValueSet2.data, bArr, osmNodesMap, z);
                        r0.visitID = 1;
                    }
                } else {
                    r0 = r13;
                    i7 = i17;
                    i8 = i5;
                    tagValueWrapper = tagValueWrapper2;
                    i9 = i6;
                }
                i26++;
                tagValueValidator2 = tagValueValidator;
                r13 = r0;
                tagValueWrapperDecodeTagValueSet = tagValueWrapper;
                i18 = i8;
                iDecodeNoisyNumber = i9;
                i17 = i7;
                iDecodeNoisyNumber4 = i27;
                iDecodeSignedValue3 = i4;
                s2 = s4;
                r05 = this;
            }
            i17++;
            r02 = this;
            tagValueValidator2 = tagValueValidator;
            iDecodeNoisyNumber2 = i25;
            i16 = iDecodeSignedValue3;
        }
        osmNodesMap.cleanupAndCount(r1);
    }

    static {
        for (int i = 0; i < 1024; i++) {
            id32_00[i] = _expandId(i);
            id32_10[i] = _expandId(i << 10);
            id32_20[i] = _expandId(i << 20);
        }
    }

    private static long _expandId(int id32) {
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
        return (((long) dlon) << 32) | ((long) dlat);
    }

    public long expandId(int id32) {
        return this.id64Base + id32_00[id32 & 1023] + id32_10[(id32 >> 10) & 1023] + id32_20[(id32 >> 20) & 1023];
    }
}

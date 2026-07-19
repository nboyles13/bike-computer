package btools.mapcreator;

import btools.util.LongList;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.openstreetmap.osmosis.osmbinary.Fileformat;
import org.openstreetmap.osmosis.osmbinary.Osmformat;

/* JADX INFO: loaded from: classes.dex */
public class BPbfBlobDecoder {
    private String blobType;
    private LongList fromWid;
    private OsmParser parser;
    private byte[] rawBlob;
    private LongList toWid;
    private LongList viaNid;

    public BPbfBlobDecoder(String blobType, byte[] rawBlob, OsmParser parser) {
        this.blobType = blobType;
        this.rawBlob = rawBlob;
        this.parser = parser;
    }

    public void process() throws Exception {
        if ("OSMHeader".equals(this.blobType)) {
            processOsmHeader(readBlobContent());
        } else {
            if ("OSMData".equals(this.blobType)) {
                processOsmPrimitives(readBlobContent());
                return;
            }
            System.out.println("Skipping unrecognised blob type " + this.blobType);
        }
    }

    private byte[] readBlobContent() throws IOException {
        Fileformat.Blob blob = Fileformat.Blob.parseFrom(this.rawBlob);
        if (blob.hasRaw()) {
            return blob.getRaw().toByteArray();
        }
        if (blob.hasZlibData()) {
            Inflater inflater = new Inflater();
            inflater.setInput(blob.getZlibData().toByteArray());
            byte[] blobData = new byte[blob.getRawSize()];
            try {
                inflater.inflate(blobData);
                if (!inflater.finished()) {
                    throw new RuntimeException("PBF blob contains incomplete compressed data.");
                }
                return blobData;
            } catch (DataFormatException e) {
                throw new RuntimeException("Unable to decompress PBF blob.", e);
            }
        }
        throw new RuntimeException("PBF blob uses unsupported compression, only raw or zlib may be used.");
    }

    private void processOsmHeader(byte[] data) throws InvalidProtocolBufferException {
        Osmformat.HeaderBlock header = Osmformat.HeaderBlock.parseFrom(data);
        List<String> supportedFeatures = Arrays.asList("OsmSchema-V0.6", "DenseNodes");
        List<String> activeFeatures = new ArrayList<>();
        List<String> unsupportedFeatures = new ArrayList<>();
        for (String feature : header.getRequiredFeaturesList()) {
            if (supportedFeatures.contains(feature)) {
                activeFeatures.add(feature);
            } else {
                unsupportedFeatures.add(feature);
            }
        }
        if (unsupportedFeatures.size() > 0) {
            throw new RuntimeException("PBF file contains unsupported features " + String.valueOf(unsupportedFeatures));
        }
    }

    private Map<String, String> buildTags(List<Integer> keys, List<Integer> values, BPbfFieldDecoder fieldDecoder) {
        Iterator<Integer> keyIterator = keys.iterator();
        Iterator<Integer> valueIterator = values.iterator();
        if (keyIterator.hasNext()) {
            Map<String, String> tags = new HashMap<>();
            while (keyIterator.hasNext()) {
                String key = fieldDecoder.decodeString(keyIterator.next().intValue());
                String value = fieldDecoder.decodeString(valueIterator.next().intValue());
                tags.put(key, value);
            }
            return tags;
        }
        return null;
    }

    private void processNodes(List<Osmformat.Node> nodes, BPbfFieldDecoder fieldDecoder) {
        for (Osmformat.Node node : nodes) {
            Map<String, String> tags = buildTags(node.getKeysList(), node.getValsList(), fieldDecoder);
            this.parser.addNode(node.getId(), tags, fieldDecoder.decodeLatitude(node.getLat()), fieldDecoder.decodeLatitude(node.getLon()));
        }
    }

    private void processNodes(Osmformat.DenseNodes nodes, BPbfFieldDecoder fieldDecoder) {
        int keyIndex;
        BPbfFieldDecoder bPbfFieldDecoder = fieldDecoder;
        List<Long> idList = nodes.getIdList();
        List<Long> latList = nodes.getLatList();
        List<Long> lonList = nodes.getLonList();
        Iterator<Integer> keysValuesIterator = nodes.getKeysValsList().iterator();
        long nodeId = 0;
        long latitude = 0;
        long longitude = 0;
        int i = 0;
        while (i < idList.size()) {
            nodeId += idList.get(i).longValue();
            latitude += latList.get(i).longValue();
            longitude += lonList.get(i).longValue();
            Map<String, String> tags = null;
            while (keysValuesIterator.hasNext() && (keyIndex = keysValuesIterator.next().intValue()) != 0) {
                int valueIndex = keysValuesIterator.next().intValue();
                if (tags == null) {
                    tags = new HashMap<>();
                }
                tags.put(bPbfFieldDecoder.decodeString(keyIndex), bPbfFieldDecoder.decodeString(valueIndex));
                idList = idList;
            }
            this.parser.addNode(nodeId, tags, latitude / 1.0E7d, longitude / 1.0E7d);
            i++;
            bPbfFieldDecoder = fieldDecoder;
            idList = idList;
        }
    }

    private void processWays(List<Osmformat.Way> ways, BPbfFieldDecoder fieldDecoder) {
        for (Osmformat.Way way : ways) {
            Map<String, String> tags = buildTags(way.getKeysList(), way.getValsList(), fieldDecoder);
            long nodeId = 0;
            LongList wayNodes = new LongList(16);
            Iterator<Long> it = way.getRefsList().iterator();
            while (it.hasNext()) {
                long nodeIdOffset = it.next().longValue();
                nodeId += nodeIdOffset;
                wayNodes.add(nodeId);
            }
            this.parser.addWay(way.getId(), tags, wayNodes);
        }
    }

    private LongList addLong(LongList ll, long l) {
        if (ll == null) {
            ll = new LongList(1);
        }
        ll.add(l);
        return ll;
    }

    private LongList buildRelationMembers(List<Long> memberIds, List<Integer> memberRoles, List<Osmformat.Relation.MemberType> memberTypes, BPbfFieldDecoder fieldDecoder) {
        LongList wayIds = new LongList(16);
        this.viaNid = null;
        this.toWid = null;
        this.fromWid = null;
        Iterator<Long> memberIdIterator = memberIds.iterator();
        Iterator<Integer> memberRoleIterator = memberRoles.iterator();
        Iterator<Osmformat.Relation.MemberType> memberTypeIterator = memberTypes.iterator();
        long refId = 0;
        while (memberIdIterator.hasNext()) {
            Osmformat.Relation.MemberType memberType = memberTypeIterator.next();
            refId += memberIdIterator.next().longValue();
            String role = fieldDecoder.decodeString(memberRoleIterator.next().intValue());
            if (memberType == Osmformat.Relation.MemberType.WAY) {
                wayIds.add(refId);
                if ("from".equals(role)) {
                    this.fromWid = addLong(this.fromWid, refId);
                }
                if ("to".equals(role)) {
                    this.toWid = addLong(this.toWid, refId);
                }
            }
            if (memberType == Osmformat.Relation.MemberType.NODE && "via".equals(role)) {
                this.viaNid = addLong(this.viaNid, refId);
            }
        }
        return wayIds;
    }

    private void processRelations(List<Osmformat.Relation> relations, BPbfFieldDecoder fieldDecoder) {
        for (Osmformat.Relation relation : relations) {
            Map<String, String> tags = buildTags(relation.getKeysList(), relation.getValsList(), fieldDecoder);
            LongList wayIds = buildRelationMembers(relation.getMemidsList(), relation.getRolesSidList(), relation.getTypesList(), fieldDecoder);
            this.parser.addRelation(relation.getId(), tags, wayIds, this.fromWid, this.toWid, this.viaNid);
        }
    }

    private void processOsmPrimitives(byte[] data) throws InvalidProtocolBufferException {
        Osmformat.PrimitiveBlock block = Osmformat.PrimitiveBlock.parseFrom(data);
        BPbfFieldDecoder fieldDecoder = new BPbfFieldDecoder(block);
        for (Osmformat.PrimitiveGroup primitiveGroup : block.getPrimitivegroupList()) {
            processNodes(primitiveGroup.getDense(), fieldDecoder);
            processNodes(primitiveGroup.getNodesList(), fieldDecoder);
            processWays(primitiveGroup.getWaysList(), fieldDecoder);
            processRelations(primitiveGroup.getRelationsList(), fieldDecoder);
        }
    }
}

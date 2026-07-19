package org.openstreetmap.osmosis.osmbinary;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public final class Osmformat {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor internal_static_OSMPBF_HeaderBlock_descriptor = getDescriptor().getMessageTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_HeaderBlock_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_HeaderBlock_descriptor, new String[]{"Bbox", "RequiredFeatures", "OptionalFeatures", "Writingprogram", "Source", "OsmosisReplicationTimestamp", "OsmosisReplicationSequenceNumber", "OsmosisReplicationBaseUrl"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_HeaderBBox_descriptor = getDescriptor().getMessageTypes().get(1);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_HeaderBBox_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_HeaderBBox_descriptor, new String[]{"Left", "Right", "Top", "Bottom"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_PrimitiveBlock_descriptor = getDescriptor().getMessageTypes().get(2);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_PrimitiveBlock_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_PrimitiveBlock_descriptor, new String[]{"Stringtable", "Primitivegroup", "Granularity", "LatOffset", "LonOffset", "DateGranularity"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_PrimitiveGroup_descriptor = getDescriptor().getMessageTypes().get(3);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_PrimitiveGroup_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_PrimitiveGroup_descriptor, new String[]{"Nodes", "Dense", "Ways", "Relations", "Changesets"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_StringTable_descriptor = getDescriptor().getMessageTypes().get(4);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_StringTable_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_StringTable_descriptor, new String[]{"S"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_Info_descriptor = getDescriptor().getMessageTypes().get(5);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_Info_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_Info_descriptor, new String[]{"Version", "Timestamp", "Changeset", "Uid", "UserSid", "Visible"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_DenseInfo_descriptor = getDescriptor().getMessageTypes().get(6);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_DenseInfo_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_DenseInfo_descriptor, new String[]{"Version", "Timestamp", "Changeset", "Uid", "UserSid", "Visible"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_ChangeSet_descriptor = getDescriptor().getMessageTypes().get(7);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_ChangeSet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_ChangeSet_descriptor, new String[]{"Id"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_Node_descriptor = getDescriptor().getMessageTypes().get(8);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_Node_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_Node_descriptor, new String[]{"Id", "Keys", "Vals", "Info", "Lat", "Lon"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_DenseNodes_descriptor = getDescriptor().getMessageTypes().get(9);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_DenseNodes_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_DenseNodes_descriptor, new String[]{"Id", "Denseinfo", "Lat", "Lon", "KeysVals"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_Way_descriptor = getDescriptor().getMessageTypes().get(10);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_Way_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_Way_descriptor, new String[]{"Id", "Keys", "Vals", "Info", "Refs", "Lat", "Lon"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_Relation_descriptor = getDescriptor().getMessageTypes().get(11);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_Relation_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_Relation_descriptor, new String[]{"Id", "Keys", "Vals", "Info", "RolesSid", "Memids", "Types"});

    public interface ChangeSetOrBuilder extends MessageOrBuilder {
        long getId();

        boolean hasId();
    }

    public interface DenseInfoOrBuilder extends MessageOrBuilder {
        long getChangeset(int i);

        int getChangesetCount();

        List<Long> getChangesetList();

        long getTimestamp(int i);

        int getTimestampCount();

        List<Long> getTimestampList();

        int getUid(int i);

        int getUidCount();

        List<Integer> getUidList();

        int getUserSid(int i);

        int getUserSidCount();

        List<Integer> getUserSidList();

        int getVersion(int i);

        int getVersionCount();

        List<Integer> getVersionList();

        boolean getVisible(int i);

        int getVisibleCount();

        List<Boolean> getVisibleList();
    }

    public interface DenseNodesOrBuilder extends MessageOrBuilder {
        DenseInfo getDenseinfo();

        DenseInfoOrBuilder getDenseinfoOrBuilder();

        long getId(int i);

        int getIdCount();

        List<Long> getIdList();

        int getKeysVals(int i);

        int getKeysValsCount();

        List<Integer> getKeysValsList();

        long getLat(int i);

        int getLatCount();

        List<Long> getLatList();

        long getLon(int i);

        int getLonCount();

        List<Long> getLonList();

        boolean hasDenseinfo();
    }

    public interface HeaderBBoxOrBuilder extends MessageOrBuilder {
        long getBottom();

        long getLeft();

        long getRight();

        long getTop();

        boolean hasBottom();

        boolean hasLeft();

        boolean hasRight();

        boolean hasTop();
    }

    public interface HeaderBlockOrBuilder extends MessageOrBuilder {
        HeaderBBox getBbox();

        HeaderBBoxOrBuilder getBboxOrBuilder();

        String getOptionalFeatures(int i);

        ByteString getOptionalFeaturesBytes(int i);

        int getOptionalFeaturesCount();

        List<String> getOptionalFeaturesList();

        String getOsmosisReplicationBaseUrl();

        ByteString getOsmosisReplicationBaseUrlBytes();

        long getOsmosisReplicationSequenceNumber();

        long getOsmosisReplicationTimestamp();

        String getRequiredFeatures(int i);

        ByteString getRequiredFeaturesBytes(int i);

        int getRequiredFeaturesCount();

        List<String> getRequiredFeaturesList();

        String getSource();

        ByteString getSourceBytes();

        String getWritingprogram();

        ByteString getWritingprogramBytes();

        boolean hasBbox();

        boolean hasOsmosisReplicationBaseUrl();

        boolean hasOsmosisReplicationSequenceNumber();

        boolean hasOsmosisReplicationTimestamp();

        boolean hasSource();

        boolean hasWritingprogram();
    }

    public interface InfoOrBuilder extends MessageOrBuilder {
        long getChangeset();

        long getTimestamp();

        int getUid();

        int getUserSid();

        int getVersion();

        boolean getVisible();

        boolean hasChangeset();

        boolean hasTimestamp();

        boolean hasUid();

        boolean hasUserSid();

        boolean hasVersion();

        boolean hasVisible();
    }

    public interface NodeOrBuilder extends MessageOrBuilder {
        long getId();

        Info getInfo();

        InfoOrBuilder getInfoOrBuilder();

        int getKeys(int i);

        int getKeysCount();

        List<Integer> getKeysList();

        long getLat();

        long getLon();

        int getVals(int i);

        int getValsCount();

        List<Integer> getValsList();

        boolean hasId();

        boolean hasInfo();

        boolean hasLat();

        boolean hasLon();
    }

    public interface PrimitiveBlockOrBuilder extends MessageOrBuilder {
        int getDateGranularity();

        int getGranularity();

        long getLatOffset();

        long getLonOffset();

        PrimitiveGroup getPrimitivegroup(int i);

        int getPrimitivegroupCount();

        List<PrimitiveGroup> getPrimitivegroupList();

        PrimitiveGroupOrBuilder getPrimitivegroupOrBuilder(int i);

        List<? extends PrimitiveGroupOrBuilder> getPrimitivegroupOrBuilderList();

        StringTable getStringtable();

        StringTableOrBuilder getStringtableOrBuilder();

        boolean hasDateGranularity();

        boolean hasGranularity();

        boolean hasLatOffset();

        boolean hasLonOffset();

        boolean hasStringtable();
    }

    public interface PrimitiveGroupOrBuilder extends MessageOrBuilder {
        ChangeSet getChangesets(int i);

        int getChangesetsCount();

        List<ChangeSet> getChangesetsList();

        ChangeSetOrBuilder getChangesetsOrBuilder(int i);

        List<? extends ChangeSetOrBuilder> getChangesetsOrBuilderList();

        DenseNodes getDense();

        DenseNodesOrBuilder getDenseOrBuilder();

        Node getNodes(int i);

        int getNodesCount();

        List<Node> getNodesList();

        NodeOrBuilder getNodesOrBuilder(int i);

        List<? extends NodeOrBuilder> getNodesOrBuilderList();

        Relation getRelations(int i);

        int getRelationsCount();

        List<Relation> getRelationsList();

        RelationOrBuilder getRelationsOrBuilder(int i);

        List<? extends RelationOrBuilder> getRelationsOrBuilderList();

        Way getWays(int i);

        int getWaysCount();

        List<Way> getWaysList();

        WayOrBuilder getWaysOrBuilder(int i);

        List<? extends WayOrBuilder> getWaysOrBuilderList();

        boolean hasDense();
    }

    public interface RelationOrBuilder extends MessageOrBuilder {
        long getId();

        Info getInfo();

        InfoOrBuilder getInfoOrBuilder();

        int getKeys(int i);

        int getKeysCount();

        List<Integer> getKeysList();

        long getMemids(int i);

        int getMemidsCount();

        List<Long> getMemidsList();

        int getRolesSid(int i);

        int getRolesSidCount();

        List<Integer> getRolesSidList();

        Relation.MemberType getTypes(int i);

        int getTypesCount();

        List<Relation.MemberType> getTypesList();

        int getVals(int i);

        int getValsCount();

        List<Integer> getValsList();

        boolean hasId();

        boolean hasInfo();
    }

    public interface StringTableOrBuilder extends MessageOrBuilder {
        ByteString getS(int i);

        int getSCount();

        List<ByteString> getSList();
    }

    public interface WayOrBuilder extends MessageOrBuilder {
        long getId();

        Info getInfo();

        InfoOrBuilder getInfoOrBuilder();

        int getKeys(int i);

        int getKeysCount();

        List<Integer> getKeysList();

        long getLat(int i);

        int getLatCount();

        List<Long> getLatList();

        long getLon(int i);

        int getLonCount();

        List<Long> getLonList();

        long getRefs(int i);

        int getRefsCount();

        List<Long> getRefsList();

        int getVals(int i);

        int getValsCount();

        List<Integer> getValsList();

        boolean hasId();

        boolean hasInfo();
    }

    private Osmformat() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        registerAllExtensions((ExtensionRegistryLite) registry);
    }

    public static final class HeaderBlock extends GeneratedMessageV3 implements HeaderBlockOrBuilder {
        public static final int BBOX_FIELD_NUMBER = 1;
        public static final int OPTIONAL_FEATURES_FIELD_NUMBER = 5;
        public static final int OSMOSIS_REPLICATION_BASE_URL_FIELD_NUMBER = 34;
        public static final int OSMOSIS_REPLICATION_SEQUENCE_NUMBER_FIELD_NUMBER = 33;
        public static final int OSMOSIS_REPLICATION_TIMESTAMP_FIELD_NUMBER = 32;
        public static final int REQUIRED_FEATURES_FIELD_NUMBER = 4;
        public static final int SOURCE_FIELD_NUMBER = 17;
        public static final int WRITINGPROGRAM_FIELD_NUMBER = 16;
        private static final long serialVersionUID = 0;
        private HeaderBBox bbox_;
        private int bitField0_;
        private byte memoizedIsInitialized;
        private LazyStringList optionalFeatures_;
        private volatile Object osmosisReplicationBaseUrl_;
        private long osmosisReplicationSequenceNumber_;
        private long osmosisReplicationTimestamp_;
        private LazyStringList requiredFeatures_;
        private volatile Object source_;
        private volatile Object writingprogram_;
        private static final HeaderBlock DEFAULT_INSTANCE = new HeaderBlock();

        @Deprecated
        public static final Parser<HeaderBlock> PARSER = new AbstractParser<HeaderBlock>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlock.1
            @Override // com.google.protobuf.Parser
            public HeaderBlock parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new HeaderBlock(input, extensionRegistry);
            }
        };

        private HeaderBlock(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private HeaderBlock() {
            this.memoizedIsInitialized = (byte) -1;
            this.requiredFeatures_ = LazyStringArrayList.EMPTY;
            this.optionalFeatures_ = LazyStringArrayList.EMPTY;
            this.writingprogram_ = "";
            this.source_ = "";
            this.osmosisReplicationBaseUrl_ = "";
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new HeaderBlock();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private HeaderBlock(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 10:
                                    HeaderBBox.Builder subBuilder = (this.bitField0_ & 1) != 0 ? this.bbox_.toBuilder() : null;
                                    this.bbox_ = (HeaderBBox) input.readMessage(HeaderBBox.PARSER, extensionRegistry);
                                    if (subBuilder != null) {
                                        subBuilder.mergeFrom(this.bbox_);
                                        this.bbox_ = subBuilder.buildPartial();
                                    }
                                    this.bitField0_ |= 1;
                                    break;
                                case 34:
                                    ByteString bs = input.readBytes();
                                    if ((mutable_bitField0_ & 2) == 0) {
                                        this.requiredFeatures_ = new LazyStringArrayList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    this.requiredFeatures_.add(bs);
                                    break;
                                case 42:
                                    ByteString bs2 = input.readBytes();
                                    if ((mutable_bitField0_ & 4) == 0) {
                                        this.optionalFeatures_ = new LazyStringArrayList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    this.optionalFeatures_.add(bs2);
                                    break;
                                case 130:
                                    ByteString bs3 = input.readBytes();
                                    this.bitField0_ |= 2;
                                    this.writingprogram_ = bs3;
                                    break;
                                case 138:
                                    ByteString bs4 = input.readBytes();
                                    this.bitField0_ |= 4;
                                    this.source_ = bs4;
                                    break;
                                case 256:
                                    this.bitField0_ |= 8;
                                    this.osmosisReplicationTimestamp_ = input.readInt64();
                                    break;
                                case 264:
                                    this.bitField0_ |= 16;
                                    this.osmosisReplicationSequenceNumber_ = input.readInt64();
                                    break;
                                case 274:
                                    ByteString bs5 = input.readBytes();
                                    this.bitField0_ |= 32;
                                    this.osmosisReplicationBaseUrl_ = bs5;
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw e.setUnfinishedMessage(this);
                        }
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.requiredFeatures_ = this.requiredFeatures_.getUnmodifiableView();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.optionalFeatures_ = this.optionalFeatures_.getUnmodifiableView();
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_HeaderBlock_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_HeaderBlock_fieldAccessorTable.ensureFieldAccessorsInitialized(HeaderBlock.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasBbox() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public HeaderBBox getBbox() {
            return this.bbox_ == null ? HeaderBBox.getDefaultInstance() : this.bbox_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public HeaderBBoxOrBuilder getBboxOrBuilder() {
            return this.bbox_ == null ? HeaderBBox.getDefaultInstance() : this.bbox_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ProtocolStringList getRequiredFeaturesList() {
            return this.requiredFeatures_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public int getRequiredFeaturesCount() {
            return this.requiredFeatures_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public String getRequiredFeatures(int index) {
            return (String) this.requiredFeatures_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ByteString getRequiredFeaturesBytes(int index) {
            return this.requiredFeatures_.getByteString(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ProtocolStringList getOptionalFeaturesList() {
            return this.optionalFeatures_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public int getOptionalFeaturesCount() {
            return this.optionalFeatures_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public String getOptionalFeatures(int index) {
            return (String) this.optionalFeatures_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ByteString getOptionalFeaturesBytes(int index) {
            return this.optionalFeatures_.getByteString(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasWritingprogram() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public String getWritingprogram() {
            Object ref = this.writingprogram_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.writingprogram_ = s;
            }
            return s;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ByteString getWritingprogramBytes() {
            Object ref = this.writingprogram_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.writingprogram_ = b;
                return b;
            }
            return (ByteString) ref;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasSource() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public String getSource() {
            Object ref = this.source_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.source_ = s;
            }
            return s;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ByteString getSourceBytes() {
            Object ref = this.source_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.source_ = b;
                return b;
            }
            return (ByteString) ref;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasOsmosisReplicationTimestamp() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public long getOsmosisReplicationTimestamp() {
            return this.osmosisReplicationTimestamp_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasOsmosisReplicationSequenceNumber() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public long getOsmosisReplicationSequenceNumber() {
            return this.osmosisReplicationSequenceNumber_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public boolean hasOsmosisReplicationBaseUrl() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public String getOsmosisReplicationBaseUrl() {
            Object ref = this.osmosisReplicationBaseUrl_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.osmosisReplicationBaseUrl_ = s;
            }
            return s;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
        public ByteString getOsmosisReplicationBaseUrlBytes() {
            Object ref = this.osmosisReplicationBaseUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.osmosisReplicationBaseUrl_ = b;
                return b;
            }
            return (ByteString) ref;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (hasBbox() && !getBbox().isInitialized()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeMessage(1, getBbox());
            }
            for (int i = 0; i < this.requiredFeatures_.size(); i++) {
                GeneratedMessageV3.writeString(output, 4, this.requiredFeatures_.getRaw(i));
            }
            for (int i2 = 0; i2 < this.optionalFeatures_.size(); i2++) {
                GeneratedMessageV3.writeString(output, 5, this.optionalFeatures_.getRaw(i2));
            }
            int i3 = this.bitField0_;
            if ((i3 & 2) != 0) {
                GeneratedMessageV3.writeString(output, 16, this.writingprogram_);
            }
            if ((this.bitField0_ & 4) != 0) {
                GeneratedMessageV3.writeString(output, 17, this.source_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeInt64(32, this.osmosisReplicationTimestamp_);
            }
            if ((this.bitField0_ & 16) != 0) {
                output.writeInt64(33, this.osmosisReplicationSequenceNumber_);
            }
            if ((this.bitField0_ & 32) != 0) {
                GeneratedMessageV3.writeString(output, 34, this.osmosisReplicationBaseUrl_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeMessageSize(1, getBbox()) : 0;
            int dataSize = 0;
            for (int i = 0; i < this.requiredFeatures_.size(); i++) {
                dataSize += computeStringSizeNoTag(this.requiredFeatures_.getRaw(i));
            }
            int size3 = size2 + dataSize + (getRequiredFeaturesList().size() * 1);
            int dataSize2 = 0;
            for (int i2 = 0; i2 < this.optionalFeatures_.size(); i2++) {
                dataSize2 += computeStringSizeNoTag(this.optionalFeatures_.getRaw(i2));
            }
            int size4 = size3 + dataSize2 + (getOptionalFeaturesList().size() * 1);
            int dataSize3 = this.bitField0_;
            if ((dataSize3 & 2) != 0) {
                size4 += GeneratedMessageV3.computeStringSize(16, this.writingprogram_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size4 += GeneratedMessageV3.computeStringSize(17, this.source_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size4 += CodedOutputStream.computeInt64Size(32, this.osmosisReplicationTimestamp_);
            }
            if ((this.bitField0_ & 16) != 0) {
                size4 += CodedOutputStream.computeInt64Size(33, this.osmosisReplicationSequenceNumber_);
            }
            if ((this.bitField0_ & 32) != 0) {
                size4 += GeneratedMessageV3.computeStringSize(34, this.osmosisReplicationBaseUrl_);
            }
            int size5 = size4 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size5;
            return size5;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof HeaderBlock)) {
                return super.equals(obj);
            }
            HeaderBlock other = (HeaderBlock) obj;
            if (hasBbox() != other.hasBbox()) {
                return false;
            }
            if ((hasBbox() && !getBbox().equals(other.getBbox())) || !getRequiredFeaturesList().equals(other.getRequiredFeaturesList()) || !getOptionalFeaturesList().equals(other.getOptionalFeaturesList()) || hasWritingprogram() != other.hasWritingprogram()) {
                return false;
            }
            if ((hasWritingprogram() && !getWritingprogram().equals(other.getWritingprogram())) || hasSource() != other.hasSource()) {
                return false;
            }
            if ((hasSource() && !getSource().equals(other.getSource())) || hasOsmosisReplicationTimestamp() != other.hasOsmosisReplicationTimestamp()) {
                return false;
            }
            if ((hasOsmosisReplicationTimestamp() && getOsmosisReplicationTimestamp() != other.getOsmosisReplicationTimestamp()) || hasOsmosisReplicationSequenceNumber() != other.hasOsmosisReplicationSequenceNumber()) {
                return false;
            }
            if ((!hasOsmosisReplicationSequenceNumber() || getOsmosisReplicationSequenceNumber() == other.getOsmosisReplicationSequenceNumber()) && hasOsmosisReplicationBaseUrl() == other.hasOsmosisReplicationBaseUrl()) {
                return (!hasOsmosisReplicationBaseUrl() || getOsmosisReplicationBaseUrl().equals(other.getOsmosisReplicationBaseUrl())) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasBbox()) {
                hash = (((hash * 37) + 1) * 53) + getBbox().hashCode();
            }
            if (getRequiredFeaturesCount() > 0) {
                hash = (((hash * 37) + 4) * 53) + getRequiredFeaturesList().hashCode();
            }
            if (getOptionalFeaturesCount() > 0) {
                hash = (((hash * 37) + 5) * 53) + getOptionalFeaturesList().hashCode();
            }
            if (hasWritingprogram()) {
                hash = (((hash * 37) + 16) * 53) + getWritingprogram().hashCode();
            }
            if (hasSource()) {
                hash = (((hash * 37) + 17) * 53) + getSource().hashCode();
            }
            if (hasOsmosisReplicationTimestamp()) {
                hash = (((hash * 37) + 32) * 53) + Internal.hashLong(getOsmosisReplicationTimestamp());
            }
            if (hasOsmosisReplicationSequenceNumber()) {
                hash = (((hash * 37) + 33) * 53) + Internal.hashLong(getOsmosisReplicationSequenceNumber());
            }
            if (hasOsmosisReplicationBaseUrl()) {
                hash = (((hash * 37) + 34) * 53) + getOsmosisReplicationBaseUrl().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static HeaderBlock parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBlock parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBlock parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBlock parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBlock parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBlock parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBlock parseFrom(InputStream input) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static HeaderBlock parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static HeaderBlock parseDelimitedFrom(InputStream input) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static HeaderBlock parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static HeaderBlock parseFrom(CodedInputStream input) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static HeaderBlock parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBlock) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(HeaderBlock prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements HeaderBlockOrBuilder {
            private SingleFieldBuilderV3<HeaderBBox, HeaderBBox.Builder, HeaderBBoxOrBuilder> bboxBuilder_;
            private HeaderBBox bbox_;
            private int bitField0_;
            private LazyStringList optionalFeatures_;
            private Object osmosisReplicationBaseUrl_;
            private long osmosisReplicationSequenceNumber_;
            private long osmosisReplicationTimestamp_;
            private LazyStringList requiredFeatures_;
            private Object source_;
            private Object writingprogram_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_HeaderBlock_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_HeaderBlock_fieldAccessorTable.ensureFieldAccessorsInitialized(HeaderBlock.class, Builder.class);
            }

            private Builder() {
                this.requiredFeatures_ = LazyStringArrayList.EMPTY;
                this.optionalFeatures_ = LazyStringArrayList.EMPTY;
                this.writingprogram_ = "";
                this.source_ = "";
                this.osmosisReplicationBaseUrl_ = "";
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.requiredFeatures_ = LazyStringArrayList.EMPTY;
                this.optionalFeatures_ = LazyStringArrayList.EMPTY;
                this.writingprogram_ = "";
                this.source_ = "";
                this.osmosisReplicationBaseUrl_ = "";
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (HeaderBlock.alwaysUseFieldBuilders) {
                    getBboxFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                if (this.bboxBuilder_ == null) {
                    this.bbox_ = null;
                } else {
                    this.bboxBuilder_.clear();
                }
                this.bitField0_ &= -2;
                this.requiredFeatures_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -3;
                this.optionalFeatures_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -5;
                this.writingprogram_ = "";
                this.bitField0_ &= -9;
                this.source_ = "";
                this.bitField0_ &= -17;
                this.osmosisReplicationTimestamp_ = 0L;
                this.bitField0_ &= -33;
                this.osmosisReplicationSequenceNumber_ = 0L;
                this.bitField0_ &= -65;
                this.osmosisReplicationBaseUrl_ = "";
                this.bitField0_ &= -129;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_HeaderBlock_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public HeaderBlock getDefaultInstanceForType() {
                return HeaderBlock.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public HeaderBlock build() {
                HeaderBlock result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public HeaderBlock buildPartial() {
                HeaderBlock result = new HeaderBlock(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    if (this.bboxBuilder_ == null) {
                        result.bbox_ = this.bbox_;
                    } else {
                        result.bbox_ = (HeaderBBox) this.bboxBuilder_.build();
                    }
                    to_bitField0_ = 0 | 1;
                }
                if ((this.bitField0_ & 2) != 0) {
                    this.requiredFeatures_ = this.requiredFeatures_.getUnmodifiableView();
                    this.bitField0_ &= -3;
                }
                result.requiredFeatures_ = this.requiredFeatures_;
                if ((this.bitField0_ & 4) != 0) {
                    this.optionalFeatures_ = this.optionalFeatures_.getUnmodifiableView();
                    this.bitField0_ &= -5;
                }
                result.optionalFeatures_ = this.optionalFeatures_;
                if ((from_bitField0_ & 8) != 0) {
                    to_bitField0_ |= 2;
                }
                result.writingprogram_ = this.writingprogram_;
                if ((from_bitField0_ & 16) != 0) {
                    to_bitField0_ |= 4;
                }
                result.source_ = this.source_;
                if ((from_bitField0_ & 32) != 0) {
                    result.osmosisReplicationTimestamp_ = this.osmosisReplicationTimestamp_;
                    to_bitField0_ |= 8;
                }
                if ((from_bitField0_ & 64) != 0) {
                    result.osmosisReplicationSequenceNumber_ = this.osmosisReplicationSequenceNumber_;
                    to_bitField0_ |= 16;
                }
                if ((from_bitField0_ & 128) != 0) {
                    to_bitField0_ |= 32;
                }
                result.osmosisReplicationBaseUrl_ = this.osmosisReplicationBaseUrl_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof HeaderBlock) {
                    return mergeFrom((HeaderBlock) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(HeaderBlock other) {
                if (other == HeaderBlock.getDefaultInstance()) {
                    return this;
                }
                if (other.hasBbox()) {
                    mergeBbox(other.getBbox());
                }
                if (!other.requiredFeatures_.isEmpty()) {
                    if (this.requiredFeatures_.isEmpty()) {
                        this.requiredFeatures_ = other.requiredFeatures_;
                        this.bitField0_ &= -3;
                    } else {
                        ensureRequiredFeaturesIsMutable();
                        this.requiredFeatures_.addAll(other.requiredFeatures_);
                    }
                    onChanged();
                }
                if (!other.optionalFeatures_.isEmpty()) {
                    if (this.optionalFeatures_.isEmpty()) {
                        this.optionalFeatures_ = other.optionalFeatures_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureOptionalFeaturesIsMutable();
                        this.optionalFeatures_.addAll(other.optionalFeatures_);
                    }
                    onChanged();
                }
                if (other.hasWritingprogram()) {
                    this.bitField0_ |= 8;
                    this.writingprogram_ = other.writingprogram_;
                    onChanged();
                }
                if (other.hasSource()) {
                    this.bitField0_ |= 16;
                    this.source_ = other.source_;
                    onChanged();
                }
                if (other.hasOsmosisReplicationTimestamp()) {
                    setOsmosisReplicationTimestamp(other.getOsmosisReplicationTimestamp());
                }
                if (other.hasOsmosisReplicationSequenceNumber()) {
                    setOsmosisReplicationSequenceNumber(other.getOsmosisReplicationSequenceNumber());
                }
                if (other.hasOsmosisReplicationBaseUrl()) {
                    this.bitField0_ |= 128;
                    this.osmosisReplicationBaseUrl_ = other.osmosisReplicationBaseUrl_;
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                if (hasBbox() && !getBbox().isInitialized()) {
                    return false;
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                HeaderBlock parsedMessage = null;
                try {
                    try {
                        parsedMessage = HeaderBlock.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasBbox() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public HeaderBBox getBbox() {
                if (this.bboxBuilder_ == null) {
                    return this.bbox_ == null ? HeaderBBox.getDefaultInstance() : this.bbox_;
                }
                return (HeaderBBox) this.bboxBuilder_.getMessage();
            }

            public Builder setBbox(HeaderBBox value) {
                if (this.bboxBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bbox_ = value;
                    onChanged();
                } else {
                    this.bboxBuilder_.setMessage(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder setBbox(HeaderBBox.Builder builderForValue) {
                if (this.bboxBuilder_ == null) {
                    this.bbox_ = builderForValue.build();
                    onChanged();
                } else {
                    this.bboxBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder mergeBbox(HeaderBBox value) {
                if (this.bboxBuilder_ == null) {
                    if ((this.bitField0_ & 1) != 0 && this.bbox_ != null && this.bbox_ != HeaderBBox.getDefaultInstance()) {
                        this.bbox_ = HeaderBBox.newBuilder(this.bbox_).mergeFrom(value).buildPartial();
                    } else {
                        this.bbox_ = value;
                    }
                    onChanged();
                } else {
                    this.bboxBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder clearBbox() {
                if (this.bboxBuilder_ == null) {
                    this.bbox_ = null;
                    onChanged();
                } else {
                    this.bboxBuilder_.clear();
                }
                this.bitField0_ &= -2;
                return this;
            }

            public HeaderBBox.Builder getBboxBuilder() {
                this.bitField0_ |= 1;
                onChanged();
                return (HeaderBBox.Builder) getBboxFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public HeaderBBoxOrBuilder getBboxOrBuilder() {
                if (this.bboxBuilder_ != null) {
                    return (HeaderBBoxOrBuilder) this.bboxBuilder_.getMessageOrBuilder();
                }
                return this.bbox_ == null ? HeaderBBox.getDefaultInstance() : this.bbox_;
            }

            private SingleFieldBuilderV3<HeaderBBox, HeaderBBox.Builder, HeaderBBoxOrBuilder> getBboxFieldBuilder() {
                if (this.bboxBuilder_ == null) {
                    this.bboxBuilder_ = new SingleFieldBuilderV3<>(getBbox(), getParentForChildren(), isClean());
                    this.bbox_ = null;
                }
                return this.bboxBuilder_;
            }

            private void ensureRequiredFeaturesIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.requiredFeatures_ = new LazyStringArrayList(this.requiredFeatures_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ProtocolStringList getRequiredFeaturesList() {
                return this.requiredFeatures_.getUnmodifiableView();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public int getRequiredFeaturesCount() {
                return this.requiredFeatures_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public String getRequiredFeatures(int index) {
                return (String) this.requiredFeatures_.get(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ByteString getRequiredFeaturesBytes(int index) {
                return this.requiredFeatures_.getByteString(index);
            }

            public Builder setRequiredFeatures(int index, String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureRequiredFeaturesIsMutable();
                this.requiredFeatures_.set(index, value);
                onChanged();
                return this;
            }

            public Builder addRequiredFeatures(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureRequiredFeaturesIsMutable();
                this.requiredFeatures_.add(value);
                onChanged();
                return this;
            }

            public Builder addAllRequiredFeatures(Iterable<String> values) {
                ensureRequiredFeaturesIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.requiredFeatures_);
                onChanged();
                return this;
            }

            public Builder clearRequiredFeatures() {
                this.requiredFeatures_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -3;
                onChanged();
                return this;
            }

            public Builder addRequiredFeaturesBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureRequiredFeaturesIsMutable();
                this.requiredFeatures_.add(value);
                onChanged();
                return this;
            }

            private void ensureOptionalFeaturesIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.optionalFeatures_ = new LazyStringArrayList(this.optionalFeatures_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ProtocolStringList getOptionalFeaturesList() {
                return this.optionalFeatures_.getUnmodifiableView();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public int getOptionalFeaturesCount() {
                return this.optionalFeatures_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public String getOptionalFeatures(int index) {
                return (String) this.optionalFeatures_.get(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ByteString getOptionalFeaturesBytes(int index) {
                return this.optionalFeatures_.getByteString(index);
            }

            public Builder setOptionalFeatures(int index, String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureOptionalFeaturesIsMutable();
                this.optionalFeatures_.set(index, value);
                onChanged();
                return this;
            }

            public Builder addOptionalFeatures(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureOptionalFeaturesIsMutable();
                this.optionalFeatures_.add(value);
                onChanged();
                return this;
            }

            public Builder addAllOptionalFeatures(Iterable<String> values) {
                ensureOptionalFeaturesIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.optionalFeatures_);
                onChanged();
                return this;
            }

            public Builder clearOptionalFeatures() {
                this.optionalFeatures_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            public Builder addOptionalFeaturesBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureOptionalFeaturesIsMutable();
                this.optionalFeatures_.add(value);
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasWritingprogram() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public String getWritingprogram() {
                Object ref = this.writingprogram_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString) ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.writingprogram_ = s;
                    }
                    return s;
                }
                return (String) ref;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ByteString getWritingprogramBytes() {
                Object ref = this.writingprogram_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.writingprogram_ = b;
                    return b;
                }
                return (ByteString) ref;
            }

            public Builder setWritingprogram(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.writingprogram_ = value;
                onChanged();
                return this;
            }

            public Builder clearWritingprogram() {
                this.bitField0_ &= -9;
                this.writingprogram_ = HeaderBlock.getDefaultInstance().getWritingprogram();
                onChanged();
                return this;
            }

            public Builder setWritingprogramBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.writingprogram_ = value;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasSource() {
                return (this.bitField0_ & 16) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public String getSource() {
                Object ref = this.source_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString) ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.source_ = s;
                    }
                    return s;
                }
                return (String) ref;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ByteString getSourceBytes() {
                Object ref = this.source_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.source_ = b;
                    return b;
                }
                return (ByteString) ref;
            }

            public Builder setSource(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 16;
                this.source_ = value;
                onChanged();
                return this;
            }

            public Builder clearSource() {
                this.bitField0_ &= -17;
                this.source_ = HeaderBlock.getDefaultInstance().getSource();
                onChanged();
                return this;
            }

            public Builder setSourceBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 16;
                this.source_ = value;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasOsmosisReplicationTimestamp() {
                return (this.bitField0_ & 32) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public long getOsmosisReplicationTimestamp() {
                return this.osmosisReplicationTimestamp_;
            }

            public Builder setOsmosisReplicationTimestamp(long value) {
                this.bitField0_ |= 32;
                this.osmosisReplicationTimestamp_ = value;
                onChanged();
                return this;
            }

            public Builder clearOsmosisReplicationTimestamp() {
                this.bitField0_ &= -33;
                this.osmosisReplicationTimestamp_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasOsmosisReplicationSequenceNumber() {
                return (this.bitField0_ & 64) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public long getOsmosisReplicationSequenceNumber() {
                return this.osmosisReplicationSequenceNumber_;
            }

            public Builder setOsmosisReplicationSequenceNumber(long value) {
                this.bitField0_ |= 64;
                this.osmosisReplicationSequenceNumber_ = value;
                onChanged();
                return this;
            }

            public Builder clearOsmosisReplicationSequenceNumber() {
                this.bitField0_ &= -65;
                this.osmosisReplicationSequenceNumber_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public boolean hasOsmosisReplicationBaseUrl() {
                return (this.bitField0_ & 128) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public String getOsmosisReplicationBaseUrl() {
                Object ref = this.osmosisReplicationBaseUrl_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString) ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.osmosisReplicationBaseUrl_ = s;
                    }
                    return s;
                }
                return (String) ref;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBlockOrBuilder
            public ByteString getOsmosisReplicationBaseUrlBytes() {
                Object ref = this.osmosisReplicationBaseUrl_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.osmosisReplicationBaseUrl_ = b;
                    return b;
                }
                return (ByteString) ref;
            }

            public Builder setOsmosisReplicationBaseUrl(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 128;
                this.osmosisReplicationBaseUrl_ = value;
                onChanged();
                return this;
            }

            public Builder clearOsmosisReplicationBaseUrl() {
                this.bitField0_ &= -129;
                this.osmosisReplicationBaseUrl_ = HeaderBlock.getDefaultInstance().getOsmosisReplicationBaseUrl();
                onChanged();
                return this;
            }

            public Builder setOsmosisReplicationBaseUrlBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 128;
                this.osmosisReplicationBaseUrl_ = value;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static HeaderBlock getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<HeaderBlock> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<HeaderBlock> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public HeaderBlock getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class HeaderBBox extends GeneratedMessageV3 implements HeaderBBoxOrBuilder {
        public static final int BOTTOM_FIELD_NUMBER = 4;
        public static final int LEFT_FIELD_NUMBER = 1;
        public static final int RIGHT_FIELD_NUMBER = 2;
        public static final int TOP_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long bottom_;
        private long left_;
        private byte memoizedIsInitialized;
        private long right_;
        private long top_;
        private static final HeaderBBox DEFAULT_INSTANCE = new HeaderBBox();

        @Deprecated
        public static final Parser<HeaderBBox> PARSER = new AbstractParser<HeaderBBox>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBox.1
            @Override // com.google.protobuf.Parser
            public HeaderBBox parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new HeaderBBox(input, extensionRegistry);
            }
        };

        private HeaderBBox(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private HeaderBBox() {
            this.memoizedIsInitialized = (byte) -1;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new HeaderBBox();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private HeaderBBox(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    this.bitField0_ |= 1;
                                    this.left_ = input.readSInt64();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.right_ = input.readSInt64();
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.top_ = input.readSInt64();
                                    break;
                                case 32:
                                    this.bitField0_ |= 8;
                                    this.bottom_ = input.readSInt64();
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (IOException e) {
                            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                        }
                    } catch (InvalidProtocolBufferException e2) {
                        throw e2.setUnfinishedMessage(this);
                    }
                } finally {
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_HeaderBBox_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_HeaderBBox_fieldAccessorTable.ensureFieldAccessorsInitialized(HeaderBBox.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public boolean hasLeft() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public long getLeft() {
            return this.left_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public boolean hasRight() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public long getRight() {
            return this.right_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public boolean hasTop() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public long getTop() {
            return this.top_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public boolean hasBottom() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
        public long getBottom() {
            return this.bottom_;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasLeft()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasRight()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasTop()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasBottom()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeSInt64(1, this.left_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeSInt64(2, this.right_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeSInt64(3, this.top_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeSInt64(4, this.bottom_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeSInt64Size(1, this.left_) : 0;
            if ((this.bitField0_ & 2) != 0) {
                size2 += CodedOutputStream.computeSInt64Size(2, this.right_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size2 += CodedOutputStream.computeSInt64Size(3, this.top_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size2 += CodedOutputStream.computeSInt64Size(4, this.bottom_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size3;
            return size3;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof HeaderBBox)) {
                return super.equals(obj);
            }
            HeaderBBox other = (HeaderBBox) obj;
            if (hasLeft() != other.hasLeft()) {
                return false;
            }
            if ((hasLeft() && getLeft() != other.getLeft()) || hasRight() != other.hasRight()) {
                return false;
            }
            if ((hasRight() && getRight() != other.getRight()) || hasTop() != other.hasTop()) {
                return false;
            }
            if ((!hasTop() || getTop() == other.getTop()) && hasBottom() == other.hasBottom()) {
                return (!hasBottom() || getBottom() == other.getBottom()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasLeft()) {
                hash = (((hash * 37) + 1) * 53) + Internal.hashLong(getLeft());
            }
            if (hasRight()) {
                hash = (((hash * 37) + 2) * 53) + Internal.hashLong(getRight());
            }
            if (hasTop()) {
                hash = (((hash * 37) + 3) * 53) + Internal.hashLong(getTop());
            }
            if (hasBottom()) {
                hash = (((hash * 37) + 4) * 53) + Internal.hashLong(getBottom());
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static HeaderBBox parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBBox parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBBox parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBBox parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBBox parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static HeaderBBox parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static HeaderBBox parseFrom(InputStream input) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static HeaderBBox parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static HeaderBBox parseDelimitedFrom(InputStream input) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static HeaderBBox parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static HeaderBBox parseFrom(CodedInputStream input) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static HeaderBBox parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (HeaderBBox) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(HeaderBBox prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements HeaderBBoxOrBuilder {
            private int bitField0_;
            private long bottom_;
            private long left_;
            private long right_;
            private long top_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_HeaderBBox_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_HeaderBBox_fieldAccessorTable.ensureFieldAccessorsInitialized(HeaderBBox.class, Builder.class);
            }

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = HeaderBBox.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.left_ = 0L;
                this.bitField0_ &= -2;
                this.right_ = 0L;
                this.bitField0_ &= -3;
                this.top_ = 0L;
                this.bitField0_ &= -5;
                this.bottom_ = 0L;
                this.bitField0_ &= -9;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_HeaderBBox_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public HeaderBBox getDefaultInstanceForType() {
                return HeaderBBox.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public HeaderBBox build() {
                HeaderBBox result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public HeaderBBox buildPartial() {
                HeaderBBox result = new HeaderBBox(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.left_ = this.left_;
                    to_bitField0_ = 0 | 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.right_ = this.right_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.top_ = this.top_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.bottom_ = this.bottom_;
                    to_bitField0_ |= 8;
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof HeaderBBox) {
                    return mergeFrom((HeaderBBox) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(HeaderBBox other) {
                if (other == HeaderBBox.getDefaultInstance()) {
                    return this;
                }
                if (other.hasLeft()) {
                    setLeft(other.getLeft());
                }
                if (other.hasRight()) {
                    setRight(other.getRight());
                }
                if (other.hasTop()) {
                    setTop(other.getTop());
                }
                if (other.hasBottom()) {
                    setBottom(other.getBottom());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return hasLeft() && hasRight() && hasTop() && hasBottom();
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                HeaderBBox parsedMessage = null;
                try {
                    try {
                        parsedMessage = HeaderBBox.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public boolean hasLeft() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public long getLeft() {
                return this.left_;
            }

            public Builder setLeft(long value) {
                this.bitField0_ |= 1;
                this.left_ = value;
                onChanged();
                return this;
            }

            public Builder clearLeft() {
                this.bitField0_ &= -2;
                this.left_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public boolean hasRight() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public long getRight() {
                return this.right_;
            }

            public Builder setRight(long value) {
                this.bitField0_ |= 2;
                this.right_ = value;
                onChanged();
                return this;
            }

            public Builder clearRight() {
                this.bitField0_ &= -3;
                this.right_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public boolean hasTop() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public long getTop() {
                return this.top_;
            }

            public Builder setTop(long value) {
                this.bitField0_ |= 4;
                this.top_ = value;
                onChanged();
                return this;
            }

            public Builder clearTop() {
                this.bitField0_ &= -5;
                this.top_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public boolean hasBottom() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.HeaderBBoxOrBuilder
            public long getBottom() {
                return this.bottom_;
            }

            public Builder setBottom(long value) {
                this.bitField0_ |= 8;
                this.bottom_ = value;
                onChanged();
                return this;
            }

            public Builder clearBottom() {
                this.bitField0_ &= -9;
                this.bottom_ = 0L;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static HeaderBBox getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<HeaderBBox> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<HeaderBBox> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public HeaderBBox getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class PrimitiveBlock extends GeneratedMessageV3 implements PrimitiveBlockOrBuilder {
        public static final int DATE_GRANULARITY_FIELD_NUMBER = 18;
        public static final int GRANULARITY_FIELD_NUMBER = 17;
        public static final int LAT_OFFSET_FIELD_NUMBER = 19;
        public static final int LON_OFFSET_FIELD_NUMBER = 20;
        public static final int PRIMITIVEGROUP_FIELD_NUMBER = 2;
        public static final int STRINGTABLE_FIELD_NUMBER = 1;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private int dateGranularity_;
        private int granularity_;
        private long latOffset_;
        private long lonOffset_;
        private byte memoizedIsInitialized;
        private List<PrimitiveGroup> primitivegroup_;
        private StringTable stringtable_;
        private static final PrimitiveBlock DEFAULT_INSTANCE = new PrimitiveBlock();

        @Deprecated
        public static final Parser<PrimitiveBlock> PARSER = new AbstractParser<PrimitiveBlock>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlock.1
            @Override // com.google.protobuf.Parser
            public PrimitiveBlock parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new PrimitiveBlock(input, extensionRegistry);
            }
        };

        private PrimitiveBlock(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private PrimitiveBlock() {
            this.memoizedIsInitialized = (byte) -1;
            this.primitivegroup_ = Collections.emptyList();
            this.granularity_ = 100;
            this.dateGranularity_ = 1000;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new PrimitiveBlock();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private PrimitiveBlock(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            case 10:
                                StringTable.Builder subBuilder = (this.bitField0_ & 1) != 0 ? this.stringtable_.toBuilder() : null;
                                this.stringtable_ = (StringTable) input.readMessage(StringTable.PARSER, extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.stringtable_);
                                    this.stringtable_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                                break;
                            case 18:
                                if ((mutable_bitField0_ & 2) == 0) {
                                    this.primitivegroup_ = new ArrayList();
                                    mutable_bitField0_ |= 2;
                                }
                                this.primitivegroup_.add((PrimitiveGroup) input.readMessage(PrimitiveGroup.PARSER, extensionRegistry));
                                break;
                            case 136:
                                this.bitField0_ |= 2;
                                this.granularity_ = input.readInt32();
                                break;
                            case 144:
                                this.bitField0_ |= 16;
                                this.dateGranularity_ = input.readInt32();
                                break;
                            case 152:
                                this.bitField0_ |= 4;
                                this.latOffset_ = input.readInt64();
                                break;
                            case 160:
                                this.bitField0_ |= 8;
                                this.lonOffset_ = input.readInt64();
                                break;
                            default:
                                if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.primitivegroup_ = Collections.unmodifiableList(this.primitivegroup_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_PrimitiveBlock_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_PrimitiveBlock_fieldAccessorTable.ensureFieldAccessorsInitialized(PrimitiveBlock.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public boolean hasStringtable() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public StringTable getStringtable() {
            return this.stringtable_ == null ? StringTable.getDefaultInstance() : this.stringtable_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public StringTableOrBuilder getStringtableOrBuilder() {
            return this.stringtable_ == null ? StringTable.getDefaultInstance() : this.stringtable_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public List<PrimitiveGroup> getPrimitivegroupList() {
            return this.primitivegroup_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public List<? extends PrimitiveGroupOrBuilder> getPrimitivegroupOrBuilderList() {
            return this.primitivegroup_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public int getPrimitivegroupCount() {
            return this.primitivegroup_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public PrimitiveGroup getPrimitivegroup(int index) {
            return this.primitivegroup_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public PrimitiveGroupOrBuilder getPrimitivegroupOrBuilder(int index) {
            return this.primitivegroup_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public boolean hasGranularity() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public int getGranularity() {
            return this.granularity_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public boolean hasLatOffset() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public long getLatOffset() {
            return this.latOffset_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public boolean hasLonOffset() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public long getLonOffset() {
            return this.lonOffset_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public boolean hasDateGranularity() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
        public int getDateGranularity() {
            return this.dateGranularity_;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasStringtable()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            for (int i = 0; i < getPrimitivegroupCount(); i++) {
                if (!getPrimitivegroup(i).isInitialized()) {
                    this.memoizedIsInitialized = (byte) 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeMessage(1, getStringtable());
            }
            for (int i = 0; i < this.primitivegroup_.size(); i++) {
                output.writeMessage(2, this.primitivegroup_.get(i));
            }
            int i2 = this.bitField0_;
            if ((i2 & 2) != 0) {
                output.writeInt32(17, this.granularity_);
            }
            if ((this.bitField0_ & 16) != 0) {
                output.writeInt32(18, this.dateGranularity_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt64(19, this.latOffset_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeInt64(20, this.lonOffset_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeMessageSize(1, getStringtable()) : 0;
            for (int i = 0; i < this.primitivegroup_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(2, this.primitivegroup_.get(i));
            }
            int i2 = this.bitField0_;
            if ((i2 & 2) != 0) {
                size2 += CodedOutputStream.computeInt32Size(17, this.granularity_);
            }
            if ((this.bitField0_ & 16) != 0) {
                size2 += CodedOutputStream.computeInt32Size(18, this.dateGranularity_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size2 += CodedOutputStream.computeInt64Size(19, this.latOffset_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size2 += CodedOutputStream.computeInt64Size(20, this.lonOffset_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size3;
            return size3;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PrimitiveBlock)) {
                return super.equals(obj);
            }
            PrimitiveBlock other = (PrimitiveBlock) obj;
            if (hasStringtable() != other.hasStringtable()) {
                return false;
            }
            if ((hasStringtable() && !getStringtable().equals(other.getStringtable())) || !getPrimitivegroupList().equals(other.getPrimitivegroupList()) || hasGranularity() != other.hasGranularity()) {
                return false;
            }
            if ((hasGranularity() && getGranularity() != other.getGranularity()) || hasLatOffset() != other.hasLatOffset()) {
                return false;
            }
            if ((hasLatOffset() && getLatOffset() != other.getLatOffset()) || hasLonOffset() != other.hasLonOffset()) {
                return false;
            }
            if ((!hasLonOffset() || getLonOffset() == other.getLonOffset()) && hasDateGranularity() == other.hasDateGranularity()) {
                return (!hasDateGranularity() || getDateGranularity() == other.getDateGranularity()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasStringtable()) {
                hash = (((hash * 37) + 1) * 53) + getStringtable().hashCode();
            }
            if (getPrimitivegroupCount() > 0) {
                hash = (((hash * 37) + 2) * 53) + getPrimitivegroupList().hashCode();
            }
            if (hasGranularity()) {
                hash = (((hash * 37) + 17) * 53) + getGranularity();
            }
            if (hasLatOffset()) {
                hash = (((hash * 37) + 19) * 53) + Internal.hashLong(getLatOffset());
            }
            if (hasLonOffset()) {
                hash = (((hash * 37) + 20) * 53) + Internal.hashLong(getLonOffset());
            }
            if (hasDateGranularity()) {
                hash = (((hash * 37) + 18) * 53) + getDateGranularity();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static PrimitiveBlock parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveBlock parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveBlock parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveBlock parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveBlock parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveBlock parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveBlock parseFrom(InputStream input) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PrimitiveBlock parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static PrimitiveBlock parseDelimitedFrom(InputStream input) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static PrimitiveBlock parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static PrimitiveBlock parseFrom(CodedInputStream input) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PrimitiveBlock parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveBlock) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PrimitiveBlock prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements PrimitiveBlockOrBuilder {
            private int bitField0_;
            private int dateGranularity_;
            private int granularity_;
            private long latOffset_;
            private long lonOffset_;
            private RepeatedFieldBuilderV3<PrimitiveGroup, PrimitiveGroup.Builder, PrimitiveGroupOrBuilder> primitivegroupBuilder_;
            private List<PrimitiveGroup> primitivegroup_;
            private SingleFieldBuilderV3<StringTable, StringTable.Builder, StringTableOrBuilder> stringtableBuilder_;
            private StringTable stringtable_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_PrimitiveBlock_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_PrimitiveBlock_fieldAccessorTable.ensureFieldAccessorsInitialized(PrimitiveBlock.class, Builder.class);
            }

            private Builder() {
                this.primitivegroup_ = Collections.emptyList();
                this.granularity_ = 100;
                this.dateGranularity_ = 1000;
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.primitivegroup_ = Collections.emptyList();
                this.granularity_ = 100;
                this.dateGranularity_ = 1000;
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (PrimitiveBlock.alwaysUseFieldBuilders) {
                    getStringtableFieldBuilder();
                    getPrimitivegroupFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                if (this.stringtableBuilder_ == null) {
                    this.stringtable_ = null;
                } else {
                    this.stringtableBuilder_.clear();
                }
                this.bitField0_ &= -2;
                if (this.primitivegroupBuilder_ == null) {
                    this.primitivegroup_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                } else {
                    this.primitivegroupBuilder_.clear();
                }
                this.granularity_ = 100;
                this.bitField0_ &= -5;
                this.latOffset_ = 0L;
                this.bitField0_ &= -9;
                this.lonOffset_ = 0L;
                this.bitField0_ &= -17;
                this.dateGranularity_ = 1000;
                this.bitField0_ &= -33;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_PrimitiveBlock_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public PrimitiveBlock getDefaultInstanceForType() {
                return PrimitiveBlock.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public PrimitiveBlock build() {
                PrimitiveBlock result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public PrimitiveBlock buildPartial() {
                PrimitiveBlock result = new PrimitiveBlock(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    if (this.stringtableBuilder_ == null) {
                        result.stringtable_ = this.stringtable_;
                    } else {
                        result.stringtable_ = (StringTable) this.stringtableBuilder_.build();
                    }
                    to_bitField0_ = 0 | 1;
                }
                if (this.primitivegroupBuilder_ != null) {
                    result.primitivegroup_ = this.primitivegroupBuilder_.build();
                } else {
                    if ((this.bitField0_ & 2) != 0) {
                        this.primitivegroup_ = Collections.unmodifiableList(this.primitivegroup_);
                        this.bitField0_ &= -3;
                    }
                    result.primitivegroup_ = this.primitivegroup_;
                }
                if ((from_bitField0_ & 4) != 0) {
                    to_bitField0_ |= 2;
                }
                result.granularity_ = this.granularity_;
                if ((from_bitField0_ & 8) != 0) {
                    result.latOffset_ = this.latOffset_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 16) != 0) {
                    result.lonOffset_ = this.lonOffset_;
                    to_bitField0_ |= 8;
                }
                if ((from_bitField0_ & 32) != 0) {
                    to_bitField0_ |= 16;
                }
                result.dateGranularity_ = this.dateGranularity_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof PrimitiveBlock) {
                    return mergeFrom((PrimitiveBlock) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(PrimitiveBlock other) {
                if (other == PrimitiveBlock.getDefaultInstance()) {
                    return this;
                }
                if (other.hasStringtable()) {
                    mergeStringtable(other.getStringtable());
                }
                if (this.primitivegroupBuilder_ == null) {
                    if (!other.primitivegroup_.isEmpty()) {
                        if (this.primitivegroup_.isEmpty()) {
                            this.primitivegroup_ = other.primitivegroup_;
                            this.bitField0_ &= -3;
                        } else {
                            ensurePrimitivegroupIsMutable();
                            this.primitivegroup_.addAll(other.primitivegroup_);
                        }
                        onChanged();
                    }
                } else if (!other.primitivegroup_.isEmpty()) {
                    if (!this.primitivegroupBuilder_.isEmpty()) {
                        this.primitivegroupBuilder_.addAllMessages(other.primitivegroup_);
                    } else {
                        this.primitivegroupBuilder_.dispose();
                        RepeatedFieldBuilderV3<PrimitiveGroup, PrimitiveGroup.Builder, PrimitiveGroupOrBuilder> primitivegroupFieldBuilder = null;
                        this.primitivegroupBuilder_ = null;
                        this.primitivegroup_ = other.primitivegroup_;
                        this.bitField0_ &= -3;
                        if (PrimitiveBlock.alwaysUseFieldBuilders) {
                            primitivegroupFieldBuilder = getPrimitivegroupFieldBuilder();
                        }
                        this.primitivegroupBuilder_ = primitivegroupFieldBuilder;
                    }
                }
                if (other.hasGranularity()) {
                    setGranularity(other.getGranularity());
                }
                if (other.hasLatOffset()) {
                    setLatOffset(other.getLatOffset());
                }
                if (other.hasLonOffset()) {
                    setLonOffset(other.getLonOffset());
                }
                if (other.hasDateGranularity()) {
                    setDateGranularity(other.getDateGranularity());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                if (!hasStringtable()) {
                    return false;
                }
                for (int i = 0; i < getPrimitivegroupCount(); i++) {
                    if (!getPrimitivegroup(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                PrimitiveBlock parsedMessage = null;
                try {
                    try {
                        parsedMessage = PrimitiveBlock.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public boolean hasStringtable() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public StringTable getStringtable() {
                if (this.stringtableBuilder_ == null) {
                    return this.stringtable_ == null ? StringTable.getDefaultInstance() : this.stringtable_;
                }
                return (StringTable) this.stringtableBuilder_.getMessage();
            }

            public Builder setStringtable(StringTable value) {
                if (this.stringtableBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.stringtable_ = value;
                    onChanged();
                } else {
                    this.stringtableBuilder_.setMessage(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder setStringtable(StringTable.Builder builderForValue) {
                if (this.stringtableBuilder_ == null) {
                    this.stringtable_ = builderForValue.build();
                    onChanged();
                } else {
                    this.stringtableBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder mergeStringtable(StringTable value) {
                if (this.stringtableBuilder_ == null) {
                    if ((this.bitField0_ & 1) != 0 && this.stringtable_ != null && this.stringtable_ != StringTable.getDefaultInstance()) {
                        this.stringtable_ = StringTable.newBuilder(this.stringtable_).mergeFrom(value).buildPartial();
                    } else {
                        this.stringtable_ = value;
                    }
                    onChanged();
                } else {
                    this.stringtableBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 1;
                return this;
            }

            public Builder clearStringtable() {
                if (this.stringtableBuilder_ == null) {
                    this.stringtable_ = null;
                    onChanged();
                } else {
                    this.stringtableBuilder_.clear();
                }
                this.bitField0_ &= -2;
                return this;
            }

            public StringTable.Builder getStringtableBuilder() {
                this.bitField0_ |= 1;
                onChanged();
                return (StringTable.Builder) getStringtableFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public StringTableOrBuilder getStringtableOrBuilder() {
                if (this.stringtableBuilder_ != null) {
                    return (StringTableOrBuilder) this.stringtableBuilder_.getMessageOrBuilder();
                }
                return this.stringtable_ == null ? StringTable.getDefaultInstance() : this.stringtable_;
            }

            private SingleFieldBuilderV3<StringTable, StringTable.Builder, StringTableOrBuilder> getStringtableFieldBuilder() {
                if (this.stringtableBuilder_ == null) {
                    this.stringtableBuilder_ = new SingleFieldBuilderV3<>(getStringtable(), getParentForChildren(), isClean());
                    this.stringtable_ = null;
                }
                return this.stringtableBuilder_;
            }

            private void ensurePrimitivegroupIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.primitivegroup_ = new ArrayList(this.primitivegroup_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public List<PrimitiveGroup> getPrimitivegroupList() {
                if (this.primitivegroupBuilder_ == null) {
                    return Collections.unmodifiableList(this.primitivegroup_);
                }
                return this.primitivegroupBuilder_.getMessageList();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public int getPrimitivegroupCount() {
                if (this.primitivegroupBuilder_ == null) {
                    return this.primitivegroup_.size();
                }
                return this.primitivegroupBuilder_.getCount();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public PrimitiveGroup getPrimitivegroup(int index) {
                if (this.primitivegroupBuilder_ == null) {
                    return this.primitivegroup_.get(index);
                }
                return (PrimitiveGroup) this.primitivegroupBuilder_.getMessage(index);
            }

            public Builder setPrimitivegroup(int index, PrimitiveGroup value) {
                if (this.primitivegroupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.set(index, value);
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setPrimitivegroup(int index, PrimitiveGroup.Builder builderForValue) {
                if (this.primitivegroupBuilder_ == null) {
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addPrimitivegroup(PrimitiveGroup value) {
                if (this.primitivegroupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.add(value);
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addPrimitivegroup(int index, PrimitiveGroup value) {
                if (this.primitivegroupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.add(index, value);
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addPrimitivegroup(PrimitiveGroup.Builder builderForValue) {
                if (this.primitivegroupBuilder_ == null) {
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addPrimitivegroup(int index, PrimitiveGroup.Builder builderForValue) {
                if (this.primitivegroupBuilder_ == null) {
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllPrimitivegroup(Iterable<? extends PrimitiveGroup> values) {
                if (this.primitivegroupBuilder_ == null) {
                    ensurePrimitivegroupIsMutable();
                    AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.primitivegroup_);
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearPrimitivegroup() {
                if (this.primitivegroupBuilder_ == null) {
                    this.primitivegroup_ = Collections.emptyList();
                    this.bitField0_ &= -3;
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.clear();
                }
                return this;
            }

            public Builder removePrimitivegroup(int index) {
                if (this.primitivegroupBuilder_ == null) {
                    ensurePrimitivegroupIsMutable();
                    this.primitivegroup_.remove(index);
                    onChanged();
                } else {
                    this.primitivegroupBuilder_.remove(index);
                }
                return this;
            }

            public PrimitiveGroup.Builder getPrimitivegroupBuilder(int index) {
                return (PrimitiveGroup.Builder) getPrimitivegroupFieldBuilder().getBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public PrimitiveGroupOrBuilder getPrimitivegroupOrBuilder(int index) {
                if (this.primitivegroupBuilder_ == null) {
                    return this.primitivegroup_.get(index);
                }
                return (PrimitiveGroupOrBuilder) this.primitivegroupBuilder_.getMessageOrBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public List<? extends PrimitiveGroupOrBuilder> getPrimitivegroupOrBuilderList() {
                if (this.primitivegroupBuilder_ != null) {
                    return this.primitivegroupBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.primitivegroup_);
            }

            public PrimitiveGroup.Builder addPrimitivegroupBuilder() {
                return (PrimitiveGroup.Builder) getPrimitivegroupFieldBuilder().addBuilder(PrimitiveGroup.getDefaultInstance());
            }

            public PrimitiveGroup.Builder addPrimitivegroupBuilder(int index) {
                return (PrimitiveGroup.Builder) getPrimitivegroupFieldBuilder().addBuilder(index, PrimitiveGroup.getDefaultInstance());
            }

            public List<PrimitiveGroup.Builder> getPrimitivegroupBuilderList() {
                return getPrimitivegroupFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<PrimitiveGroup, PrimitiveGroup.Builder, PrimitiveGroupOrBuilder> getPrimitivegroupFieldBuilder() {
                if (this.primitivegroupBuilder_ == null) {
                    this.primitivegroupBuilder_ = new RepeatedFieldBuilderV3<>(this.primitivegroup_, (this.bitField0_ & 2) != 0, getParentForChildren(), isClean());
                    this.primitivegroup_ = null;
                }
                return this.primitivegroupBuilder_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public boolean hasGranularity() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public int getGranularity() {
                return this.granularity_;
            }

            public Builder setGranularity(int value) {
                this.bitField0_ |= 4;
                this.granularity_ = value;
                onChanged();
                return this;
            }

            public Builder clearGranularity() {
                this.bitField0_ &= -5;
                this.granularity_ = 100;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public boolean hasLatOffset() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public long getLatOffset() {
                return this.latOffset_;
            }

            public Builder setLatOffset(long value) {
                this.bitField0_ |= 8;
                this.latOffset_ = value;
                onChanged();
                return this;
            }

            public Builder clearLatOffset() {
                this.bitField0_ &= -9;
                this.latOffset_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public boolean hasLonOffset() {
                return (this.bitField0_ & 16) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public long getLonOffset() {
                return this.lonOffset_;
            }

            public Builder setLonOffset(long value) {
                this.bitField0_ |= 16;
                this.lonOffset_ = value;
                onChanged();
                return this;
            }

            public Builder clearLonOffset() {
                this.bitField0_ &= -17;
                this.lonOffset_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public boolean hasDateGranularity() {
                return (this.bitField0_ & 32) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveBlockOrBuilder
            public int getDateGranularity() {
                return this.dateGranularity_;
            }

            public Builder setDateGranularity(int value) {
                this.bitField0_ |= 32;
                this.dateGranularity_ = value;
                onChanged();
                return this;
            }

            public Builder clearDateGranularity() {
                this.bitField0_ &= -33;
                this.dateGranularity_ = 1000;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static PrimitiveBlock getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PrimitiveBlock> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<PrimitiveBlock> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public PrimitiveBlock getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class PrimitiveGroup extends GeneratedMessageV3 implements PrimitiveGroupOrBuilder {
        public static final int CHANGESETS_FIELD_NUMBER = 5;
        public static final int DENSE_FIELD_NUMBER = 2;
        public static final int NODES_FIELD_NUMBER = 1;
        public static final int RELATIONS_FIELD_NUMBER = 4;
        public static final int WAYS_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private List<ChangeSet> changesets_;
        private DenseNodes dense_;
        private byte memoizedIsInitialized;
        private List<Node> nodes_;
        private List<Relation> relations_;
        private List<Way> ways_;
        private static final PrimitiveGroup DEFAULT_INSTANCE = new PrimitiveGroup();

        @Deprecated
        public static final Parser<PrimitiveGroup> PARSER = new AbstractParser<PrimitiveGroup>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroup.1
            @Override // com.google.protobuf.Parser
            public PrimitiveGroup parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new PrimitiveGroup(input, extensionRegistry);
            }
        };

        private PrimitiveGroup(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private PrimitiveGroup() {
            this.memoizedIsInitialized = (byte) -1;
            this.nodes_ = Collections.emptyList();
            this.ways_ = Collections.emptyList();
            this.relations_ = Collections.emptyList();
            this.changesets_ = Collections.emptyList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new PrimitiveGroup();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private PrimitiveGroup(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            case 10:
                                if ((mutable_bitField0_ & 1) == 0) {
                                    this.nodes_ = new ArrayList();
                                    mutable_bitField0_ |= 1;
                                }
                                this.nodes_.add((Node) input.readMessage(Node.PARSER, extensionRegistry));
                                break;
                            case 18:
                                DenseNodes.Builder subBuilder = (this.bitField0_ & 1) != 0 ? this.dense_.toBuilder() : null;
                                this.dense_ = (DenseNodes) input.readMessage(DenseNodes.PARSER, extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.dense_);
                                    this.dense_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 1;
                                break;
                            case 26:
                                if ((mutable_bitField0_ & 4) == 0) {
                                    this.ways_ = new ArrayList();
                                    mutable_bitField0_ |= 4;
                                }
                                this.ways_.add((Way) input.readMessage(Way.PARSER, extensionRegistry));
                                break;
                            case 34:
                                if ((mutable_bitField0_ & 8) == 0) {
                                    this.relations_ = new ArrayList();
                                    mutable_bitField0_ |= 8;
                                }
                                this.relations_.add((Relation) input.readMessage(Relation.PARSER, extensionRegistry));
                                break;
                            case 42:
                                if ((mutable_bitField0_ & 16) == 0) {
                                    this.changesets_ = new ArrayList();
                                    mutable_bitField0_ |= 16;
                                }
                                this.changesets_.add((ChangeSet) input.readMessage(ChangeSet.PARSER, extensionRegistry));
                                break;
                            default:
                                if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 1) != 0) {
                        this.nodes_ = Collections.unmodifiableList(this.nodes_);
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.ways_ = Collections.unmodifiableList(this.ways_);
                    }
                    if ((mutable_bitField0_ & 8) != 0) {
                        this.relations_ = Collections.unmodifiableList(this.relations_);
                    }
                    if ((mutable_bitField0_ & 16) != 0) {
                        this.changesets_ = Collections.unmodifiableList(this.changesets_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_PrimitiveGroup_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_PrimitiveGroup_fieldAccessorTable.ensureFieldAccessorsInitialized(PrimitiveGroup.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<Node> getNodesList() {
            return this.nodes_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<? extends NodeOrBuilder> getNodesOrBuilderList() {
            return this.nodes_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public int getNodesCount() {
            return this.nodes_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public Node getNodes(int index) {
            return this.nodes_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public NodeOrBuilder getNodesOrBuilder(int index) {
            return this.nodes_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public boolean hasDense() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public DenseNodes getDense() {
            return this.dense_ == null ? DenseNodes.getDefaultInstance() : this.dense_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public DenseNodesOrBuilder getDenseOrBuilder() {
            return this.dense_ == null ? DenseNodes.getDefaultInstance() : this.dense_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<Way> getWaysList() {
            return this.ways_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<? extends WayOrBuilder> getWaysOrBuilderList() {
            return this.ways_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public int getWaysCount() {
            return this.ways_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public Way getWays(int index) {
            return this.ways_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public WayOrBuilder getWaysOrBuilder(int index) {
            return this.ways_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<Relation> getRelationsList() {
            return this.relations_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<? extends RelationOrBuilder> getRelationsOrBuilderList() {
            return this.relations_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public int getRelationsCount() {
            return this.relations_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public Relation getRelations(int index) {
            return this.relations_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public RelationOrBuilder getRelationsOrBuilder(int index) {
            return this.relations_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<ChangeSet> getChangesetsList() {
            return this.changesets_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public List<? extends ChangeSetOrBuilder> getChangesetsOrBuilderList() {
            return this.changesets_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public int getChangesetsCount() {
            return this.changesets_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public ChangeSet getChangesets(int index) {
            return this.changesets_.get(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
        public ChangeSetOrBuilder getChangesetsOrBuilder(int index) {
            return this.changesets_.get(index);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            for (int i = 0; i < getNodesCount(); i++) {
                if (!getNodes(i).isInitialized()) {
                    this.memoizedIsInitialized = (byte) 0;
                    return false;
                }
            }
            for (int i2 = 0; i2 < getWaysCount(); i2++) {
                if (!getWays(i2).isInitialized()) {
                    this.memoizedIsInitialized = (byte) 0;
                    return false;
                }
            }
            for (int i3 = 0; i3 < getRelationsCount(); i3++) {
                if (!getRelations(i3).isInitialized()) {
                    this.memoizedIsInitialized = (byte) 0;
                    return false;
                }
            }
            for (int i4 = 0; i4 < getChangesetsCount(); i4++) {
                if (!getChangesets(i4).isInitialized()) {
                    this.memoizedIsInitialized = (byte) 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            for (int i = 0; i < this.nodes_.size(); i++) {
                output.writeMessage(1, this.nodes_.get(i));
            }
            int i2 = this.bitField0_;
            if ((i2 & 1) != 0) {
                output.writeMessage(2, getDense());
            }
            for (int i3 = 0; i3 < this.ways_.size(); i3++) {
                output.writeMessage(3, this.ways_.get(i3));
            }
            for (int i4 = 0; i4 < this.relations_.size(); i4++) {
                output.writeMessage(4, this.relations_.get(i4));
            }
            for (int i5 = 0; i5 < this.changesets_.size(); i5++) {
                output.writeMessage(5, this.changesets_.get(i5));
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = 0;
            for (int i = 0; i < this.nodes_.size(); i++) {
                size2 += CodedOutputStream.computeMessageSize(1, this.nodes_.get(i));
            }
            int i2 = this.bitField0_;
            if ((i2 & 1) != 0) {
                size2 += CodedOutputStream.computeMessageSize(2, getDense());
            }
            for (int i3 = 0; i3 < this.ways_.size(); i3++) {
                size2 += CodedOutputStream.computeMessageSize(3, this.ways_.get(i3));
            }
            for (int i4 = 0; i4 < this.relations_.size(); i4++) {
                size2 += CodedOutputStream.computeMessageSize(4, this.relations_.get(i4));
            }
            for (int i5 = 0; i5 < this.changesets_.size(); i5++) {
                size2 += CodedOutputStream.computeMessageSize(5, this.changesets_.get(i5));
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size3;
            return size3;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PrimitiveGroup)) {
                return super.equals(obj);
            }
            PrimitiveGroup other = (PrimitiveGroup) obj;
            if (getNodesList().equals(other.getNodesList()) && hasDense() == other.hasDense()) {
                return (!hasDense() || getDense().equals(other.getDense())) && getWaysList().equals(other.getWaysList()) && getRelationsList().equals(other.getRelationsList()) && getChangesetsList().equals(other.getChangesetsList()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (getNodesCount() > 0) {
                hash = (((hash * 37) + 1) * 53) + getNodesList().hashCode();
            }
            if (hasDense()) {
                hash = (((hash * 37) + 2) * 53) + getDense().hashCode();
            }
            if (getWaysCount() > 0) {
                hash = (((hash * 37) + 3) * 53) + getWaysList().hashCode();
            }
            if (getRelationsCount() > 0) {
                hash = (((hash * 37) + 4) * 53) + getRelationsList().hashCode();
            }
            if (getChangesetsCount() > 0) {
                hash = (((hash * 37) + 5) * 53) + getChangesetsList().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static PrimitiveGroup parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveGroup parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveGroup parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveGroup parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveGroup parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PrimitiveGroup parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PrimitiveGroup parseFrom(InputStream input) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PrimitiveGroup parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static PrimitiveGroup parseDelimitedFrom(InputStream input) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static PrimitiveGroup parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static PrimitiveGroup parseFrom(CodedInputStream input) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PrimitiveGroup parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (PrimitiveGroup) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PrimitiveGroup prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements PrimitiveGroupOrBuilder {
            private int bitField0_;
            private RepeatedFieldBuilderV3<ChangeSet, ChangeSet.Builder, ChangeSetOrBuilder> changesetsBuilder_;
            private List<ChangeSet> changesets_;
            private SingleFieldBuilderV3<DenseNodes, DenseNodes.Builder, DenseNodesOrBuilder> denseBuilder_;
            private DenseNodes dense_;
            private RepeatedFieldBuilderV3<Node, Node.Builder, NodeOrBuilder> nodesBuilder_;
            private List<Node> nodes_;
            private RepeatedFieldBuilderV3<Relation, Relation.Builder, RelationOrBuilder> relationsBuilder_;
            private List<Relation> relations_;
            private RepeatedFieldBuilderV3<Way, Way.Builder, WayOrBuilder> waysBuilder_;
            private List<Way> ways_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_PrimitiveGroup_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_PrimitiveGroup_fieldAccessorTable.ensureFieldAccessorsInitialized(PrimitiveGroup.class, Builder.class);
            }

            private Builder() {
                this.nodes_ = Collections.emptyList();
                this.ways_ = Collections.emptyList();
                this.relations_ = Collections.emptyList();
                this.changesets_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.nodes_ = Collections.emptyList();
                this.ways_ = Collections.emptyList();
                this.relations_ = Collections.emptyList();
                this.changesets_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (PrimitiveGroup.alwaysUseFieldBuilders) {
                    getNodesFieldBuilder();
                    getDenseFieldBuilder();
                    getWaysFieldBuilder();
                    getRelationsFieldBuilder();
                    getChangesetsFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                if (this.nodesBuilder_ == null) {
                    this.nodes_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                } else {
                    this.nodesBuilder_.clear();
                }
                if (this.denseBuilder_ == null) {
                    this.dense_ = null;
                } else {
                    this.denseBuilder_.clear();
                }
                this.bitField0_ &= -3;
                if (this.waysBuilder_ == null) {
                    this.ways_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                } else {
                    this.waysBuilder_.clear();
                }
                if (this.relationsBuilder_ == null) {
                    this.relations_ = Collections.emptyList();
                    this.bitField0_ &= -9;
                } else {
                    this.relationsBuilder_.clear();
                }
                if (this.changesetsBuilder_ == null) {
                    this.changesets_ = Collections.emptyList();
                    this.bitField0_ &= -17;
                } else {
                    this.changesetsBuilder_.clear();
                }
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_PrimitiveGroup_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public PrimitiveGroup getDefaultInstanceForType() {
                return PrimitiveGroup.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public PrimitiveGroup build() {
                PrimitiveGroup result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public PrimitiveGroup buildPartial() {
                PrimitiveGroup result = new PrimitiveGroup(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if (this.nodesBuilder_ != null) {
                    result.nodes_ = this.nodesBuilder_.build();
                } else {
                    if ((this.bitField0_ & 1) != 0) {
                        this.nodes_ = Collections.unmodifiableList(this.nodes_);
                        this.bitField0_ &= -2;
                    }
                    result.nodes_ = this.nodes_;
                }
                if ((from_bitField0_ & 2) != 0) {
                    if (this.denseBuilder_ == null) {
                        result.dense_ = this.dense_;
                    } else {
                        result.dense_ = (DenseNodes) this.denseBuilder_.build();
                    }
                    to_bitField0_ = 0 | 1;
                }
                if (this.waysBuilder_ != null) {
                    result.ways_ = this.waysBuilder_.build();
                } else {
                    if ((this.bitField0_ & 4) != 0) {
                        this.ways_ = Collections.unmodifiableList(this.ways_);
                        this.bitField0_ &= -5;
                    }
                    result.ways_ = this.ways_;
                }
                if (this.relationsBuilder_ != null) {
                    result.relations_ = this.relationsBuilder_.build();
                } else {
                    if ((this.bitField0_ & 8) != 0) {
                        this.relations_ = Collections.unmodifiableList(this.relations_);
                        this.bitField0_ &= -9;
                    }
                    result.relations_ = this.relations_;
                }
                if (this.changesetsBuilder_ != null) {
                    result.changesets_ = this.changesetsBuilder_.build();
                } else {
                    if ((this.bitField0_ & 16) != 0) {
                        this.changesets_ = Collections.unmodifiableList(this.changesets_);
                        this.bitField0_ &= -17;
                    }
                    result.changesets_ = this.changesets_;
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof PrimitiveGroup) {
                    return mergeFrom((PrimitiveGroup) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(PrimitiveGroup other) {
                if (other == PrimitiveGroup.getDefaultInstance()) {
                    return this;
                }
                if (this.nodesBuilder_ == null) {
                    if (!other.nodes_.isEmpty()) {
                        if (this.nodes_.isEmpty()) {
                            this.nodes_ = other.nodes_;
                            this.bitField0_ &= -2;
                        } else {
                            ensureNodesIsMutable();
                            this.nodes_.addAll(other.nodes_);
                        }
                        onChanged();
                    }
                } else if (!other.nodes_.isEmpty()) {
                    if (!this.nodesBuilder_.isEmpty()) {
                        this.nodesBuilder_.addAllMessages(other.nodes_);
                    } else {
                        this.nodesBuilder_.dispose();
                        this.nodesBuilder_ = null;
                        this.nodes_ = other.nodes_;
                        this.bitField0_ &= -2;
                        this.nodesBuilder_ = PrimitiveGroup.alwaysUseFieldBuilders ? getNodesFieldBuilder() : null;
                    }
                }
                if (other.hasDense()) {
                    mergeDense(other.getDense());
                }
                if (this.waysBuilder_ == null) {
                    if (!other.ways_.isEmpty()) {
                        if (this.ways_.isEmpty()) {
                            this.ways_ = other.ways_;
                            this.bitField0_ &= -5;
                        } else {
                            ensureWaysIsMutable();
                            this.ways_.addAll(other.ways_);
                        }
                        onChanged();
                    }
                } else if (!other.ways_.isEmpty()) {
                    if (!this.waysBuilder_.isEmpty()) {
                        this.waysBuilder_.addAllMessages(other.ways_);
                    } else {
                        this.waysBuilder_.dispose();
                        this.waysBuilder_ = null;
                        this.ways_ = other.ways_;
                        this.bitField0_ &= -5;
                        this.waysBuilder_ = PrimitiveGroup.alwaysUseFieldBuilders ? getWaysFieldBuilder() : null;
                    }
                }
                if (this.relationsBuilder_ == null) {
                    if (!other.relations_.isEmpty()) {
                        if (this.relations_.isEmpty()) {
                            this.relations_ = other.relations_;
                            this.bitField0_ &= -9;
                        } else {
                            ensureRelationsIsMutable();
                            this.relations_.addAll(other.relations_);
                        }
                        onChanged();
                    }
                } else if (!other.relations_.isEmpty()) {
                    if (!this.relationsBuilder_.isEmpty()) {
                        this.relationsBuilder_.addAllMessages(other.relations_);
                    } else {
                        this.relationsBuilder_.dispose();
                        this.relationsBuilder_ = null;
                        this.relations_ = other.relations_;
                        this.bitField0_ &= -9;
                        this.relationsBuilder_ = PrimitiveGroup.alwaysUseFieldBuilders ? getRelationsFieldBuilder() : null;
                    }
                }
                if (this.changesetsBuilder_ == null) {
                    if (!other.changesets_.isEmpty()) {
                        if (this.changesets_.isEmpty()) {
                            this.changesets_ = other.changesets_;
                            this.bitField0_ &= -17;
                        } else {
                            ensureChangesetsIsMutable();
                            this.changesets_.addAll(other.changesets_);
                        }
                        onChanged();
                    }
                } else if (!other.changesets_.isEmpty()) {
                    if (!this.changesetsBuilder_.isEmpty()) {
                        this.changesetsBuilder_.addAllMessages(other.changesets_);
                    } else {
                        this.changesetsBuilder_.dispose();
                        this.changesetsBuilder_ = null;
                        this.changesets_ = other.changesets_;
                        this.bitField0_ &= -17;
                        this.changesetsBuilder_ = PrimitiveGroup.alwaysUseFieldBuilders ? getChangesetsFieldBuilder() : null;
                    }
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                for (int i = 0; i < getNodesCount(); i++) {
                    if (!getNodes(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i2 = 0; i2 < getWaysCount(); i2++) {
                    if (!getWays(i2).isInitialized()) {
                        return false;
                    }
                }
                for (int i3 = 0; i3 < getRelationsCount(); i3++) {
                    if (!getRelations(i3).isInitialized()) {
                        return false;
                    }
                }
                for (int i4 = 0; i4 < getChangesetsCount(); i4++) {
                    if (!getChangesets(i4).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                PrimitiveGroup parsedMessage = null;
                try {
                    try {
                        parsedMessage = PrimitiveGroup.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            private void ensureNodesIsMutable() {
                if ((this.bitField0_ & 1) == 0) {
                    this.nodes_ = new ArrayList(this.nodes_);
                    this.bitField0_ |= 1;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<Node> getNodesList() {
                if (this.nodesBuilder_ == null) {
                    return Collections.unmodifiableList(this.nodes_);
                }
                return this.nodesBuilder_.getMessageList();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public int getNodesCount() {
                if (this.nodesBuilder_ == null) {
                    return this.nodes_.size();
                }
                return this.nodesBuilder_.getCount();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public Node getNodes(int index) {
                if (this.nodesBuilder_ == null) {
                    return this.nodes_.get(index);
                }
                return (Node) this.nodesBuilder_.getMessage(index);
            }

            public Builder setNodes(int index, Node value) {
                if (this.nodesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNodesIsMutable();
                    this.nodes_.set(index, value);
                    onChanged();
                } else {
                    this.nodesBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setNodes(int index, Node.Builder builderForValue) {
                if (this.nodesBuilder_ == null) {
                    ensureNodesIsMutable();
                    this.nodes_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.nodesBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addNodes(Node value) {
                if (this.nodesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNodesIsMutable();
                    this.nodes_.add(value);
                    onChanged();
                } else {
                    this.nodesBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addNodes(int index, Node value) {
                if (this.nodesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureNodesIsMutable();
                    this.nodes_.add(index, value);
                    onChanged();
                } else {
                    this.nodesBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addNodes(Node.Builder builderForValue) {
                if (this.nodesBuilder_ == null) {
                    ensureNodesIsMutable();
                    this.nodes_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.nodesBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addNodes(int index, Node.Builder builderForValue) {
                if (this.nodesBuilder_ == null) {
                    ensureNodesIsMutable();
                    this.nodes_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.nodesBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllNodes(Iterable<? extends Node> values) {
                if (this.nodesBuilder_ == null) {
                    ensureNodesIsMutable();
                    AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.nodes_);
                    onChanged();
                } else {
                    this.nodesBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearNodes() {
                if (this.nodesBuilder_ == null) {
                    this.nodes_ = Collections.emptyList();
                    this.bitField0_ &= -2;
                    onChanged();
                } else {
                    this.nodesBuilder_.clear();
                }
                return this;
            }

            public Builder removeNodes(int index) {
                if (this.nodesBuilder_ == null) {
                    ensureNodesIsMutable();
                    this.nodes_.remove(index);
                    onChanged();
                } else {
                    this.nodesBuilder_.remove(index);
                }
                return this;
            }

            public Node.Builder getNodesBuilder(int index) {
                return (Node.Builder) getNodesFieldBuilder().getBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public NodeOrBuilder getNodesOrBuilder(int index) {
                if (this.nodesBuilder_ == null) {
                    return this.nodes_.get(index);
                }
                return (NodeOrBuilder) this.nodesBuilder_.getMessageOrBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<? extends NodeOrBuilder> getNodesOrBuilderList() {
                if (this.nodesBuilder_ != null) {
                    return this.nodesBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.nodes_);
            }

            public Node.Builder addNodesBuilder() {
                return (Node.Builder) getNodesFieldBuilder().addBuilder(Node.getDefaultInstance());
            }

            public Node.Builder addNodesBuilder(int index) {
                return (Node.Builder) getNodesFieldBuilder().addBuilder(index, Node.getDefaultInstance());
            }

            public List<Node.Builder> getNodesBuilderList() {
                return getNodesFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<Node, Node.Builder, NodeOrBuilder> getNodesFieldBuilder() {
                if (this.nodesBuilder_ == null) {
                    this.nodesBuilder_ = new RepeatedFieldBuilderV3<>(this.nodes_, (this.bitField0_ & 1) != 0, getParentForChildren(), isClean());
                    this.nodes_ = null;
                }
                return this.nodesBuilder_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public boolean hasDense() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public DenseNodes getDense() {
                if (this.denseBuilder_ == null) {
                    return this.dense_ == null ? DenseNodes.getDefaultInstance() : this.dense_;
                }
                return (DenseNodes) this.denseBuilder_.getMessage();
            }

            public Builder setDense(DenseNodes value) {
                if (this.denseBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.dense_ = value;
                    onChanged();
                } else {
                    this.denseBuilder_.setMessage(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder setDense(DenseNodes.Builder builderForValue) {
                if (this.denseBuilder_ == null) {
                    this.dense_ = builderForValue.build();
                    onChanged();
                } else {
                    this.denseBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder mergeDense(DenseNodes value) {
                if (this.denseBuilder_ == null) {
                    if ((this.bitField0_ & 2) != 0 && this.dense_ != null && this.dense_ != DenseNodes.getDefaultInstance()) {
                        this.dense_ = DenseNodes.newBuilder(this.dense_).mergeFrom(value).buildPartial();
                    } else {
                        this.dense_ = value;
                    }
                    onChanged();
                } else {
                    this.denseBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder clearDense() {
                if (this.denseBuilder_ == null) {
                    this.dense_ = null;
                    onChanged();
                } else {
                    this.denseBuilder_.clear();
                }
                this.bitField0_ &= -3;
                return this;
            }

            public DenseNodes.Builder getDenseBuilder() {
                this.bitField0_ |= 2;
                onChanged();
                return (DenseNodes.Builder) getDenseFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public DenseNodesOrBuilder getDenseOrBuilder() {
                if (this.denseBuilder_ != null) {
                    return (DenseNodesOrBuilder) this.denseBuilder_.getMessageOrBuilder();
                }
                return this.dense_ == null ? DenseNodes.getDefaultInstance() : this.dense_;
            }

            private SingleFieldBuilderV3<DenseNodes, DenseNodes.Builder, DenseNodesOrBuilder> getDenseFieldBuilder() {
                if (this.denseBuilder_ == null) {
                    this.denseBuilder_ = new SingleFieldBuilderV3<>(getDense(), getParentForChildren(), isClean());
                    this.dense_ = null;
                }
                return this.denseBuilder_;
            }

            private void ensureWaysIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.ways_ = new ArrayList(this.ways_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<Way> getWaysList() {
                if (this.waysBuilder_ == null) {
                    return Collections.unmodifiableList(this.ways_);
                }
                return this.waysBuilder_.getMessageList();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public int getWaysCount() {
                if (this.waysBuilder_ == null) {
                    return this.ways_.size();
                }
                return this.waysBuilder_.getCount();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public Way getWays(int index) {
                if (this.waysBuilder_ == null) {
                    return this.ways_.get(index);
                }
                return (Way) this.waysBuilder_.getMessage(index);
            }

            public Builder setWays(int index, Way value) {
                if (this.waysBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWaysIsMutable();
                    this.ways_.set(index, value);
                    onChanged();
                } else {
                    this.waysBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setWays(int index, Way.Builder builderForValue) {
                if (this.waysBuilder_ == null) {
                    ensureWaysIsMutable();
                    this.ways_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.waysBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addWays(Way value) {
                if (this.waysBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWaysIsMutable();
                    this.ways_.add(value);
                    onChanged();
                } else {
                    this.waysBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addWays(int index, Way value) {
                if (this.waysBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureWaysIsMutable();
                    this.ways_.add(index, value);
                    onChanged();
                } else {
                    this.waysBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addWays(Way.Builder builderForValue) {
                if (this.waysBuilder_ == null) {
                    ensureWaysIsMutable();
                    this.ways_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.waysBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addWays(int index, Way.Builder builderForValue) {
                if (this.waysBuilder_ == null) {
                    ensureWaysIsMutable();
                    this.ways_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.waysBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllWays(Iterable<? extends Way> values) {
                if (this.waysBuilder_ == null) {
                    ensureWaysIsMutable();
                    AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.ways_);
                    onChanged();
                } else {
                    this.waysBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearWays() {
                if (this.waysBuilder_ == null) {
                    this.ways_ = Collections.emptyList();
                    this.bitField0_ &= -5;
                    onChanged();
                } else {
                    this.waysBuilder_.clear();
                }
                return this;
            }

            public Builder removeWays(int index) {
                if (this.waysBuilder_ == null) {
                    ensureWaysIsMutable();
                    this.ways_.remove(index);
                    onChanged();
                } else {
                    this.waysBuilder_.remove(index);
                }
                return this;
            }

            public Way.Builder getWaysBuilder(int index) {
                return (Way.Builder) getWaysFieldBuilder().getBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public WayOrBuilder getWaysOrBuilder(int index) {
                if (this.waysBuilder_ == null) {
                    return this.ways_.get(index);
                }
                return (WayOrBuilder) this.waysBuilder_.getMessageOrBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<? extends WayOrBuilder> getWaysOrBuilderList() {
                if (this.waysBuilder_ != null) {
                    return this.waysBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.ways_);
            }

            public Way.Builder addWaysBuilder() {
                return (Way.Builder) getWaysFieldBuilder().addBuilder(Way.getDefaultInstance());
            }

            public Way.Builder addWaysBuilder(int index) {
                return (Way.Builder) getWaysFieldBuilder().addBuilder(index, Way.getDefaultInstance());
            }

            public List<Way.Builder> getWaysBuilderList() {
                return getWaysFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<Way, Way.Builder, WayOrBuilder> getWaysFieldBuilder() {
                if (this.waysBuilder_ == null) {
                    this.waysBuilder_ = new RepeatedFieldBuilderV3<>(this.ways_, (this.bitField0_ & 4) != 0, getParentForChildren(), isClean());
                    this.ways_ = null;
                }
                return this.waysBuilder_;
            }

            private void ensureRelationsIsMutable() {
                if ((this.bitField0_ & 8) == 0) {
                    this.relations_ = new ArrayList(this.relations_);
                    this.bitField0_ |= 8;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<Relation> getRelationsList() {
                if (this.relationsBuilder_ == null) {
                    return Collections.unmodifiableList(this.relations_);
                }
                return this.relationsBuilder_.getMessageList();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public int getRelationsCount() {
                if (this.relationsBuilder_ == null) {
                    return this.relations_.size();
                }
                return this.relationsBuilder_.getCount();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public Relation getRelations(int index) {
                if (this.relationsBuilder_ == null) {
                    return this.relations_.get(index);
                }
                return (Relation) this.relationsBuilder_.getMessage(index);
            }

            public Builder setRelations(int index, Relation value) {
                if (this.relationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureRelationsIsMutable();
                    this.relations_.set(index, value);
                    onChanged();
                } else {
                    this.relationsBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setRelations(int index, Relation.Builder builderForValue) {
                if (this.relationsBuilder_ == null) {
                    ensureRelationsIsMutable();
                    this.relations_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.relationsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addRelations(Relation value) {
                if (this.relationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureRelationsIsMutable();
                    this.relations_.add(value);
                    onChanged();
                } else {
                    this.relationsBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addRelations(int index, Relation value) {
                if (this.relationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureRelationsIsMutable();
                    this.relations_.add(index, value);
                    onChanged();
                } else {
                    this.relationsBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addRelations(Relation.Builder builderForValue) {
                if (this.relationsBuilder_ == null) {
                    ensureRelationsIsMutable();
                    this.relations_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.relationsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addRelations(int index, Relation.Builder builderForValue) {
                if (this.relationsBuilder_ == null) {
                    ensureRelationsIsMutable();
                    this.relations_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.relationsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllRelations(Iterable<? extends Relation> values) {
                if (this.relationsBuilder_ == null) {
                    ensureRelationsIsMutable();
                    AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.relations_);
                    onChanged();
                } else {
                    this.relationsBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearRelations() {
                if (this.relationsBuilder_ == null) {
                    this.relations_ = Collections.emptyList();
                    this.bitField0_ &= -9;
                    onChanged();
                } else {
                    this.relationsBuilder_.clear();
                }
                return this;
            }

            public Builder removeRelations(int index) {
                if (this.relationsBuilder_ == null) {
                    ensureRelationsIsMutable();
                    this.relations_.remove(index);
                    onChanged();
                } else {
                    this.relationsBuilder_.remove(index);
                }
                return this;
            }

            public Relation.Builder getRelationsBuilder(int index) {
                return (Relation.Builder) getRelationsFieldBuilder().getBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public RelationOrBuilder getRelationsOrBuilder(int index) {
                if (this.relationsBuilder_ == null) {
                    return this.relations_.get(index);
                }
                return (RelationOrBuilder) this.relationsBuilder_.getMessageOrBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<? extends RelationOrBuilder> getRelationsOrBuilderList() {
                if (this.relationsBuilder_ != null) {
                    return this.relationsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.relations_);
            }

            public Relation.Builder addRelationsBuilder() {
                return (Relation.Builder) getRelationsFieldBuilder().addBuilder(Relation.getDefaultInstance());
            }

            public Relation.Builder addRelationsBuilder(int index) {
                return (Relation.Builder) getRelationsFieldBuilder().addBuilder(index, Relation.getDefaultInstance());
            }

            public List<Relation.Builder> getRelationsBuilderList() {
                return getRelationsFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<Relation, Relation.Builder, RelationOrBuilder> getRelationsFieldBuilder() {
                if (this.relationsBuilder_ == null) {
                    this.relationsBuilder_ = new RepeatedFieldBuilderV3<>(this.relations_, (this.bitField0_ & 8) != 0, getParentForChildren(), isClean());
                    this.relations_ = null;
                }
                return this.relationsBuilder_;
            }

            private void ensureChangesetsIsMutable() {
                if ((this.bitField0_ & 16) == 0) {
                    this.changesets_ = new ArrayList(this.changesets_);
                    this.bitField0_ |= 16;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<ChangeSet> getChangesetsList() {
                if (this.changesetsBuilder_ == null) {
                    return Collections.unmodifiableList(this.changesets_);
                }
                return this.changesetsBuilder_.getMessageList();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public int getChangesetsCount() {
                if (this.changesetsBuilder_ == null) {
                    return this.changesets_.size();
                }
                return this.changesetsBuilder_.getCount();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public ChangeSet getChangesets(int index) {
                if (this.changesetsBuilder_ == null) {
                    return this.changesets_.get(index);
                }
                return (ChangeSet) this.changesetsBuilder_.getMessage(index);
            }

            public Builder setChangesets(int index, ChangeSet value) {
                if (this.changesetsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureChangesetsIsMutable();
                    this.changesets_.set(index, value);
                    onChanged();
                } else {
                    this.changesetsBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setChangesets(int index, ChangeSet.Builder builderForValue) {
                if (this.changesetsBuilder_ == null) {
                    ensureChangesetsIsMutable();
                    this.changesets_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    this.changesetsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addChangesets(ChangeSet value) {
                if (this.changesetsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureChangesetsIsMutable();
                    this.changesets_.add(value);
                    onChanged();
                } else {
                    this.changesetsBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addChangesets(int index, ChangeSet value) {
                if (this.changesetsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureChangesetsIsMutable();
                    this.changesets_.add(index, value);
                    onChanged();
                } else {
                    this.changesetsBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addChangesets(ChangeSet.Builder builderForValue) {
                if (this.changesetsBuilder_ == null) {
                    ensureChangesetsIsMutable();
                    this.changesets_.add(builderForValue.build());
                    onChanged();
                } else {
                    this.changesetsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addChangesets(int index, ChangeSet.Builder builderForValue) {
                if (this.changesetsBuilder_ == null) {
                    ensureChangesetsIsMutable();
                    this.changesets_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    this.changesetsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllChangesets(Iterable<? extends ChangeSet> values) {
                if (this.changesetsBuilder_ == null) {
                    ensureChangesetsIsMutable();
                    AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.changesets_);
                    onChanged();
                } else {
                    this.changesetsBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearChangesets() {
                if (this.changesetsBuilder_ == null) {
                    this.changesets_ = Collections.emptyList();
                    this.bitField0_ &= -17;
                    onChanged();
                } else {
                    this.changesetsBuilder_.clear();
                }
                return this;
            }

            public Builder removeChangesets(int index) {
                if (this.changesetsBuilder_ == null) {
                    ensureChangesetsIsMutable();
                    this.changesets_.remove(index);
                    onChanged();
                } else {
                    this.changesetsBuilder_.remove(index);
                }
                return this;
            }

            public ChangeSet.Builder getChangesetsBuilder(int index) {
                return (ChangeSet.Builder) getChangesetsFieldBuilder().getBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public ChangeSetOrBuilder getChangesetsOrBuilder(int index) {
                if (this.changesetsBuilder_ == null) {
                    return this.changesets_.get(index);
                }
                return (ChangeSetOrBuilder) this.changesetsBuilder_.getMessageOrBuilder(index);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.PrimitiveGroupOrBuilder
            public List<? extends ChangeSetOrBuilder> getChangesetsOrBuilderList() {
                if (this.changesetsBuilder_ != null) {
                    return this.changesetsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.changesets_);
            }

            public ChangeSet.Builder addChangesetsBuilder() {
                return (ChangeSet.Builder) getChangesetsFieldBuilder().addBuilder(ChangeSet.getDefaultInstance());
            }

            public ChangeSet.Builder addChangesetsBuilder(int index) {
                return (ChangeSet.Builder) getChangesetsFieldBuilder().addBuilder(index, ChangeSet.getDefaultInstance());
            }

            public List<ChangeSet.Builder> getChangesetsBuilderList() {
                return getChangesetsFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<ChangeSet, ChangeSet.Builder, ChangeSetOrBuilder> getChangesetsFieldBuilder() {
                if (this.changesetsBuilder_ == null) {
                    this.changesetsBuilder_ = new RepeatedFieldBuilderV3<>(this.changesets_, (this.bitField0_ & 16) != 0, getParentForChildren(), isClean());
                    this.changesets_ = null;
                }
                return this.changesetsBuilder_;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static PrimitiveGroup getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PrimitiveGroup> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<PrimitiveGroup> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public PrimitiveGroup getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class StringTable extends GeneratedMessageV3 implements StringTableOrBuilder {
        private static final StringTable DEFAULT_INSTANCE = new StringTable();

        @Deprecated
        public static final Parser<StringTable> PARSER = new AbstractParser<StringTable>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.StringTable.1
            @Override // com.google.protobuf.Parser
            public StringTable parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new StringTable(input, extensionRegistry);
            }
        };
        public static final int S_FIELD_NUMBER = 1;
        private static final long serialVersionUID = 0;
        private byte memoizedIsInitialized;
        private List<ByteString> s_;

        private StringTable(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private StringTable() {
            this.memoizedIsInitialized = (byte) -1;
            this.s_ = Collections.emptyList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new StringTable();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private StringTable(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            case 10:
                                if ((mutable_bitField0_ & 1) == 0) {
                                    this.s_ = new ArrayList();
                                    mutable_bitField0_ |= 1;
                                }
                                this.s_.add(input.readBytes());
                                break;
                            default:
                                if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 1) != 0) {
                        this.s_ = Collections.unmodifiableList(this.s_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_StringTable_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_StringTable_fieldAccessorTable.ensureFieldAccessorsInitialized(StringTable.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
        public List<ByteString> getSList() {
            return this.s_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
        public int getSCount() {
            return this.s_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
        public ByteString getS(int index) {
            return this.s_.get(index);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            for (int i = 0; i < this.s_.size(); i++) {
                output.writeBytes(1, this.s_.get(i));
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int dataSize = 0;
            for (int i = 0; i < this.s_.size(); i++) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.s_.get(i));
            }
            int size2 = 0 + dataSize + (getSList().size() * 1) + this.unknownFields.getSerializedSize();
            this.memoizedSize = size2;
            return size2;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof StringTable)) {
                return super.equals(obj);
            }
            StringTable other = (StringTable) obj;
            return getSList().equals(other.getSList()) && this.unknownFields.equals(other.unknownFields);
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (getSCount() > 0) {
                hash = (((hash * 37) + 1) * 53) + getSList().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static StringTable parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static StringTable parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static StringTable parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static StringTable parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static StringTable parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static StringTable parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static StringTable parseFrom(InputStream input) throws IOException {
            return (StringTable) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static StringTable parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (StringTable) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static StringTable parseDelimitedFrom(InputStream input) throws IOException {
            return (StringTable) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static StringTable parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (StringTable) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static StringTable parseFrom(CodedInputStream input) throws IOException {
            return (StringTable) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static StringTable parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (StringTable) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(StringTable prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StringTableOrBuilder {
            private int bitField0_;
            private List<ByteString> s_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_StringTable_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_StringTable_fieldAccessorTable.ensureFieldAccessorsInitialized(StringTable.class, Builder.class);
            }

            private Builder() {
                this.s_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.s_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = StringTable.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.s_ = Collections.emptyList();
                this.bitField0_ &= -2;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_StringTable_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public StringTable getDefaultInstanceForType() {
                return StringTable.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public StringTable build() {
                StringTable result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public StringTable buildPartial() {
                StringTable result = new StringTable(this);
                int i = this.bitField0_;
                if ((this.bitField0_ & 1) != 0) {
                    this.s_ = Collections.unmodifiableList(this.s_);
                    this.bitField0_ &= -2;
                }
                result.s_ = this.s_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof StringTable) {
                    return mergeFrom((StringTable) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(StringTable other) {
                if (other == StringTable.getDefaultInstance()) {
                    return this;
                }
                if (!other.s_.isEmpty()) {
                    if (this.s_.isEmpty()) {
                        this.s_ = other.s_;
                        this.bitField0_ &= -2;
                    } else {
                        ensureSIsMutable();
                        this.s_.addAll(other.s_);
                    }
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                StringTable parsedMessage = null;
                try {
                    try {
                        parsedMessage = StringTable.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            private void ensureSIsMutable() {
                if ((this.bitField0_ & 1) == 0) {
                    this.s_ = new ArrayList(this.s_);
                    this.bitField0_ |= 1;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
            public List<ByteString> getSList() {
                return (this.bitField0_ & 1) != 0 ? Collections.unmodifiableList(this.s_) : this.s_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
            public int getSCount() {
                return this.s_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.StringTableOrBuilder
            public ByteString getS(int index) {
                return this.s_.get(index);
            }

            public Builder setS(int index, ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureSIsMutable();
                this.s_.set(index, value);
                onChanged();
                return this;
            }

            public Builder addS(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureSIsMutable();
                this.s_.add(value);
                onChanged();
                return this;
            }

            public Builder addAllS(Iterable<? extends ByteString> values) {
                ensureSIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.s_);
                onChanged();
                return this;
            }

            public Builder clearS() {
                this.s_ = Collections.emptyList();
                this.bitField0_ &= -2;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static StringTable getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<StringTable> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<StringTable> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public StringTable getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class Info extends GeneratedMessageV3 implements InfoOrBuilder {
        public static final int CHANGESET_FIELD_NUMBER = 3;
        private static final Info DEFAULT_INSTANCE = new Info();

        @Deprecated
        public static final Parser<Info> PARSER = new AbstractParser<Info>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Info.1
            @Override // com.google.protobuf.Parser
            public Info parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Info(input, extensionRegistry);
            }
        };
        public static final int TIMESTAMP_FIELD_NUMBER = 2;
        public static final int UID_FIELD_NUMBER = 4;
        public static final int USER_SID_FIELD_NUMBER = 5;
        public static final int VERSION_FIELD_NUMBER = 1;
        public static final int VISIBLE_FIELD_NUMBER = 6;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long changeset_;
        private byte memoizedIsInitialized;
        private long timestamp_;
        private int uid_;
        private int userSid_;
        private int version_;
        private boolean visible_;

        private Info(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private Info() {
            this.memoizedIsInitialized = (byte) -1;
            this.version_ = -1;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Info();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Info(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            case 8:
                                this.bitField0_ |= 1;
                                this.version_ = input.readInt32();
                                break;
                            case 16:
                                this.bitField0_ |= 2;
                                this.timestamp_ = input.readInt64();
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.changeset_ = input.readInt64();
                                break;
                            case 32:
                                this.bitField0_ |= 8;
                                this.uid_ = input.readInt32();
                                break;
                            case 40:
                                this.bitField0_ |= 16;
                                this.userSid_ = input.readUInt32();
                                break;
                            case 48:
                                this.bitField0_ |= 32;
                                this.visible_ = input.readBool();
                                break;
                            default:
                                if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_Info_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_Info_fieldAccessorTable.ensureFieldAccessorsInitialized(Info.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasVersion() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public int getVersion() {
            return this.version_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasTimestamp() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public long getTimestamp() {
            return this.timestamp_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasChangeset() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public long getChangeset() {
            return this.changeset_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasUid() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public int getUid() {
            return this.uid_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasUserSid() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public int getUserSid() {
            return this.userSid_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean hasVisible() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
        public boolean getVisible() {
            return this.visible_;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt32(1, this.version_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeInt64(2, this.timestamp_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt64(3, this.changeset_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeInt32(4, this.uid_);
            }
            if ((this.bitField0_ & 16) != 0) {
                output.writeUInt32(5, this.userSid_);
            }
            if ((this.bitField0_ & 32) != 0) {
                output.writeBool(6, this.visible_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeInt32Size(1, this.version_) : 0;
            if ((this.bitField0_ & 2) != 0) {
                size2 += CodedOutputStream.computeInt64Size(2, this.timestamp_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size2 += CodedOutputStream.computeInt64Size(3, this.changeset_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size2 += CodedOutputStream.computeInt32Size(4, this.uid_);
            }
            if ((this.bitField0_ & 16) != 0) {
                size2 += CodedOutputStream.computeUInt32Size(5, this.userSid_);
            }
            if ((this.bitField0_ & 32) != 0) {
                size2 += CodedOutputStream.computeBoolSize(6, this.visible_);
            }
            int size3 = size2 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size3;
            return size3;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Info)) {
                return super.equals(obj);
            }
            Info other = (Info) obj;
            if (hasVersion() != other.hasVersion()) {
                return false;
            }
            if ((hasVersion() && getVersion() != other.getVersion()) || hasTimestamp() != other.hasTimestamp()) {
                return false;
            }
            if ((hasTimestamp() && getTimestamp() != other.getTimestamp()) || hasChangeset() != other.hasChangeset()) {
                return false;
            }
            if ((hasChangeset() && getChangeset() != other.getChangeset()) || hasUid() != other.hasUid()) {
                return false;
            }
            if ((hasUid() && getUid() != other.getUid()) || hasUserSid() != other.hasUserSid()) {
                return false;
            }
            if ((!hasUserSid() || getUserSid() == other.getUserSid()) && hasVisible() == other.hasVisible()) {
                return (!hasVisible() || getVisible() == other.getVisible()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasVersion()) {
                hash = (((hash * 37) + 1) * 53) + getVersion();
            }
            if (hasTimestamp()) {
                hash = (((hash * 37) + 2) * 53) + Internal.hashLong(getTimestamp());
            }
            if (hasChangeset()) {
                hash = (((hash * 37) + 3) * 53) + Internal.hashLong(getChangeset());
            }
            if (hasUid()) {
                hash = (((hash * 37) + 4) * 53) + getUid();
            }
            if (hasUserSid()) {
                hash = (((hash * 37) + 5) * 53) + getUserSid();
            }
            if (hasVisible()) {
                hash = (((hash * 37) + 6) * 53) + Internal.hashBoolean(getVisible());
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static Info parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Info parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Info parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Info parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Info parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Info parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Info parseFrom(InputStream input) throws IOException {
            return (Info) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Info parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Info) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Info parseDelimitedFrom(InputStream input) throws IOException {
            return (Info) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Info parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Info) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Info parseFrom(CodedInputStream input) throws IOException {
            return (Info) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Info parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Info) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Info prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements InfoOrBuilder {
            private int bitField0_;
            private long changeset_;
            private long timestamp_;
            private int uid_;
            private int userSid_;
            private int version_;
            private boolean visible_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_Info_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_Info_fieldAccessorTable.ensureFieldAccessorsInitialized(Info.class, Builder.class);
            }

            private Builder() {
                this.version_ = -1;
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.version_ = -1;
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = Info.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.version_ = -1;
                this.bitField0_ &= -2;
                this.timestamp_ = 0L;
                this.bitField0_ &= -3;
                this.changeset_ = 0L;
                this.bitField0_ &= -5;
                this.uid_ = 0;
                this.bitField0_ &= -9;
                this.userSid_ = 0;
                this.bitField0_ &= -17;
                this.visible_ = false;
                this.bitField0_ &= -33;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_Info_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public Info getDefaultInstanceForType() {
                return Info.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Info build() {
                Info result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Info buildPartial() {
                Info result = new Info(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ = 0 | 1;
                }
                result.version_ = this.version_;
                if ((from_bitField0_ & 2) != 0) {
                    result.timestamp_ = this.timestamp_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.changeset_ = this.changeset_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.uid_ = this.uid_;
                    to_bitField0_ |= 8;
                }
                if ((from_bitField0_ & 16) != 0) {
                    result.userSid_ = this.userSid_;
                    to_bitField0_ |= 16;
                }
                if ((from_bitField0_ & 32) != 0) {
                    result.visible_ = this.visible_;
                    to_bitField0_ |= 32;
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof Info) {
                    return mergeFrom((Info) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Info other) {
                if (other == Info.getDefaultInstance()) {
                    return this;
                }
                if (other.hasVersion()) {
                    setVersion(other.getVersion());
                }
                if (other.hasTimestamp()) {
                    setTimestamp(other.getTimestamp());
                }
                if (other.hasChangeset()) {
                    setChangeset(other.getChangeset());
                }
                if (other.hasUid()) {
                    setUid(other.getUid());
                }
                if (other.hasUserSid()) {
                    setUserSid(other.getUserSid());
                }
                if (other.hasVisible()) {
                    setVisible(other.getVisible());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Info parsedMessage = null;
                try {
                    try {
                        parsedMessage = Info.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasVersion() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public int getVersion() {
                return this.version_;
            }

            public Builder setVersion(int value) {
                this.bitField0_ |= 1;
                this.version_ = value;
                onChanged();
                return this;
            }

            public Builder clearVersion() {
                this.bitField0_ &= -2;
                this.version_ = -1;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasTimestamp() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public long getTimestamp() {
                return this.timestamp_;
            }

            public Builder setTimestamp(long value) {
                this.bitField0_ |= 2;
                this.timestamp_ = value;
                onChanged();
                return this;
            }

            public Builder clearTimestamp() {
                this.bitField0_ &= -3;
                this.timestamp_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasChangeset() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public long getChangeset() {
                return this.changeset_;
            }

            public Builder setChangeset(long value) {
                this.bitField0_ |= 4;
                this.changeset_ = value;
                onChanged();
                return this;
            }

            public Builder clearChangeset() {
                this.bitField0_ &= -5;
                this.changeset_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasUid() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public int getUid() {
                return this.uid_;
            }

            public Builder setUid(int value) {
                this.bitField0_ |= 8;
                this.uid_ = value;
                onChanged();
                return this;
            }

            public Builder clearUid() {
                this.bitField0_ &= -9;
                this.uid_ = 0;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasUserSid() {
                return (this.bitField0_ & 16) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public int getUserSid() {
                return this.userSid_;
            }

            public Builder setUserSid(int value) {
                this.bitField0_ |= 16;
                this.userSid_ = value;
                onChanged();
                return this;
            }

            public Builder clearUserSid() {
                this.bitField0_ &= -17;
                this.userSid_ = 0;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean hasVisible() {
                return (this.bitField0_ & 32) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.InfoOrBuilder
            public boolean getVisible() {
                return this.visible_;
            }

            public Builder setVisible(boolean value) {
                this.bitField0_ |= 32;
                this.visible_ = value;
                onChanged();
                return this;
            }

            public Builder clearVisible() {
                this.bitField0_ &= -33;
                this.visible_ = false;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static Info getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Info> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<Info> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public Info getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class DenseInfo extends GeneratedMessageV3 implements DenseInfoOrBuilder {
        public static final int CHANGESET_FIELD_NUMBER = 3;
        private static final DenseInfo DEFAULT_INSTANCE = new DenseInfo();

        @Deprecated
        public static final Parser<DenseInfo> PARSER = new AbstractParser<DenseInfo>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfo.1
            @Override // com.google.protobuf.Parser
            public DenseInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new DenseInfo(input, extensionRegistry);
            }
        };
        public static final int TIMESTAMP_FIELD_NUMBER = 2;
        public static final int UID_FIELD_NUMBER = 4;
        public static final int USER_SID_FIELD_NUMBER = 5;
        public static final int VERSION_FIELD_NUMBER = 1;
        public static final int VISIBLE_FIELD_NUMBER = 6;
        private static final long serialVersionUID = 0;
        private int changesetMemoizedSerializedSize;
        private Internal.LongList changeset_;
        private byte memoizedIsInitialized;
        private int timestampMemoizedSerializedSize;
        private Internal.LongList timestamp_;
        private int uidMemoizedSerializedSize;
        private Internal.IntList uid_;
        private int userSidMemoizedSerializedSize;
        private Internal.IntList userSid_;
        private int versionMemoizedSerializedSize;
        private Internal.IntList version_;
        private int visibleMemoizedSerializedSize;
        private Internal.BooleanList visible_;

        private DenseInfo(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.versionMemoizedSerializedSize = -1;
            this.timestampMemoizedSerializedSize = -1;
            this.changesetMemoizedSerializedSize = -1;
            this.uidMemoizedSerializedSize = -1;
            this.userSidMemoizedSerializedSize = -1;
            this.visibleMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
        }

        private DenseInfo() {
            this.versionMemoizedSerializedSize = -1;
            this.timestampMemoizedSerializedSize = -1;
            this.changesetMemoizedSerializedSize = -1;
            this.uidMemoizedSerializedSize = -1;
            this.userSidMemoizedSerializedSize = -1;
            this.visibleMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
            this.version_ = emptyIntList();
            this.timestamp_ = emptyLongList();
            this.changeset_ = emptyLongList();
            this.uid_ = emptyIntList();
            this.userSid_ = emptyIntList();
            this.visible_ = emptyBooleanList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new DenseInfo();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private DenseInfo(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    int length = mutable_bitField0_ & 1;
                                    if (length == 0) {
                                        this.version_ = newIntList();
                                        mutable_bitField0_ |= 1;
                                    }
                                    this.version_.addInt(input.readInt32());
                                    break;
                                case 10:
                                    int length2 = input.readRawVarint32();
                                    int limit = input.pushLimit(length2);
                                    if ((mutable_bitField0_ & 1) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.version_ = newIntList();
                                        mutable_bitField0_ |= 1;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.version_.addInt(input.readInt32());
                                    }
                                    input.popLimit(limit);
                                    break;
                                case 16:
                                    int length3 = mutable_bitField0_ & 2;
                                    if (length3 == 0) {
                                        this.timestamp_ = newLongList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    this.timestamp_.addLong(input.readSInt64());
                                    break;
                                case 18:
                                    int length4 = input.readRawVarint32();
                                    int limit2 = input.pushLimit(length4);
                                    if ((mutable_bitField0_ & 2) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.timestamp_ = newLongList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.timestamp_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit2);
                                    break;
                                case 24:
                                    int length5 = mutable_bitField0_ & 4;
                                    if (length5 == 0) {
                                        this.changeset_ = newLongList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    this.changeset_.addLong(input.readSInt64());
                                    break;
                                case 26:
                                    int length6 = input.readRawVarint32();
                                    int limit3 = input.pushLimit(length6);
                                    if ((mutable_bitField0_ & 4) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.changeset_ = newLongList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.changeset_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit3);
                                    break;
                                case 32:
                                    int length7 = mutable_bitField0_ & 8;
                                    if (length7 == 0) {
                                        this.uid_ = newIntList();
                                        mutable_bitField0_ |= 8;
                                    }
                                    this.uid_.addInt(input.readSInt32());
                                    break;
                                case 34:
                                    int length8 = input.readRawVarint32();
                                    int limit4 = input.pushLimit(length8);
                                    if ((mutable_bitField0_ & 8) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.uid_ = newIntList();
                                        mutable_bitField0_ |= 8;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.uid_.addInt(input.readSInt32());
                                    }
                                    input.popLimit(limit4);
                                    break;
                                case 40:
                                    int length9 = mutable_bitField0_ & 16;
                                    if (length9 == 0) {
                                        this.userSid_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    this.userSid_.addInt(input.readSInt32());
                                    break;
                                case 42:
                                    int length10 = input.readRawVarint32();
                                    int limit5 = input.pushLimit(length10);
                                    if ((mutable_bitField0_ & 16) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.userSid_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.userSid_.addInt(input.readSInt32());
                                    }
                                    input.popLimit(limit5);
                                    break;
                                case 48:
                                    int length11 = mutable_bitField0_ & 32;
                                    if (length11 == 0) {
                                        this.visible_ = newBooleanList();
                                        mutable_bitField0_ |= 32;
                                    }
                                    this.visible_.addBoolean(input.readBool());
                                    break;
                                case AccessibilityNodeInfoCompat.MAX_NUMBER_OF_PREFETCHED_NODES /* 50 */:
                                    int length12 = input.readRawVarint32();
                                    int limit6 = input.pushLimit(length12);
                                    if ((mutable_bitField0_ & 32) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.visible_ = newBooleanList();
                                        mutable_bitField0_ |= 32;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.visible_.addBoolean(input.readBool());
                                    }
                                    input.popLimit(limit6);
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (IOException e) {
                            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                        }
                    } catch (InvalidProtocolBufferException e2) {
                        throw e2.setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 1) != 0) {
                        this.version_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.timestamp_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.changeset_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 8) != 0) {
                        this.uid_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 16) != 0) {
                        this.userSid_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 32) != 0) {
                        this.visible_.makeImmutable();
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_DenseInfo_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_DenseInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(DenseInfo.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Integer> getVersionList() {
            return this.version_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getVersionCount() {
            return this.version_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getVersion(int index) {
            return this.version_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Long> getTimestampList() {
            return this.timestamp_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getTimestampCount() {
            return this.timestamp_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public long getTimestamp(int index) {
            return this.timestamp_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Long> getChangesetList() {
            return this.changeset_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getChangesetCount() {
            return this.changeset_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public long getChangeset(int index) {
            return this.changeset_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Integer> getUidList() {
            return this.uid_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getUidCount() {
            return this.uid_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getUid(int index) {
            return this.uid_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Integer> getUserSidList() {
            return this.userSid_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getUserSidCount() {
            return this.userSid_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getUserSid(int index) {
            return this.userSid_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public List<Boolean> getVisibleList() {
            return this.visible_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public int getVisibleCount() {
            return this.visible_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
        public boolean getVisible(int index) {
            return this.visible_.getBoolean(index);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if (getVersionList().size() > 0) {
                output.writeUInt32NoTag(10);
                output.writeUInt32NoTag(this.versionMemoizedSerializedSize);
            }
            for (int i = 0; i < this.version_.size(); i++) {
                output.writeInt32NoTag(this.version_.getInt(i));
            }
            if (getTimestampList().size() > 0) {
                output.writeUInt32NoTag(18);
                output.writeUInt32NoTag(this.timestampMemoizedSerializedSize);
            }
            for (int i2 = 0; i2 < this.timestamp_.size(); i2++) {
                output.writeSInt64NoTag(this.timestamp_.getLong(i2));
            }
            if (getChangesetList().size() > 0) {
                output.writeUInt32NoTag(26);
                output.writeUInt32NoTag(this.changesetMemoizedSerializedSize);
            }
            for (int i3 = 0; i3 < this.changeset_.size(); i3++) {
                output.writeSInt64NoTag(this.changeset_.getLong(i3));
            }
            if (getUidList().size() > 0) {
                output.writeUInt32NoTag(34);
                output.writeUInt32NoTag(this.uidMemoizedSerializedSize);
            }
            for (int i4 = 0; i4 < this.uid_.size(); i4++) {
                output.writeSInt32NoTag(this.uid_.getInt(i4));
            }
            if (getUserSidList().size() > 0) {
                output.writeUInt32NoTag(42);
                output.writeUInt32NoTag(this.userSidMemoizedSerializedSize);
            }
            for (int i5 = 0; i5 < this.userSid_.size(); i5++) {
                output.writeSInt32NoTag(this.userSid_.getInt(i5));
            }
            if (getVisibleList().size() > 0) {
                output.writeUInt32NoTag(50);
                output.writeUInt32NoTag(this.visibleMemoizedSerializedSize);
            }
            for (int i6 = 0; i6 < this.visible_.size(); i6++) {
                output.writeBoolNoTag(this.visible_.getBoolean(i6));
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int dataSize = 0;
            for (int i = 0; i < this.version_.size(); i++) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(this.version_.getInt(i));
            }
            int size2 = 0 + dataSize;
            if (!getVersionList().isEmpty()) {
                size2 = size2 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
            }
            this.versionMemoizedSerializedSize = dataSize;
            int dataSize2 = 0;
            for (int i2 = 0; i2 < this.timestamp_.size(); i2++) {
                dataSize2 += CodedOutputStream.computeSInt64SizeNoTag(this.timestamp_.getLong(i2));
            }
            int size3 = size2 + dataSize2;
            if (!getTimestampList().isEmpty()) {
                size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize2);
            }
            this.timestampMemoizedSerializedSize = dataSize2;
            int dataSize3 = 0;
            for (int i3 = 0; i3 < this.changeset_.size(); i3++) {
                dataSize3 += CodedOutputStream.computeSInt64SizeNoTag(this.changeset_.getLong(i3));
            }
            int size4 = size3 + dataSize3;
            if (!getChangesetList().isEmpty()) {
                size4 = size4 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize3);
            }
            this.changesetMemoizedSerializedSize = dataSize3;
            int dataSize4 = 0;
            for (int i4 = 0; i4 < this.uid_.size(); i4++) {
                dataSize4 += CodedOutputStream.computeSInt32SizeNoTag(this.uid_.getInt(i4));
            }
            int size5 = size4 + dataSize4;
            if (!getUidList().isEmpty()) {
                size5 = size5 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize4);
            }
            this.uidMemoizedSerializedSize = dataSize4;
            int dataSize5 = 0;
            for (int i5 = 0; i5 < this.userSid_.size(); i5++) {
                dataSize5 += CodedOutputStream.computeSInt32SizeNoTag(this.userSid_.getInt(i5));
            }
            int size6 = size5 + dataSize5;
            if (!getUserSidList().isEmpty()) {
                size6 = size6 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize5);
            }
            this.userSidMemoizedSerializedSize = dataSize5;
            int dataSize6 = getVisibleList().size() * 1;
            int size7 = size6 + dataSize6;
            if (!getVisibleList().isEmpty()) {
                size7 = size7 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize6);
            }
            this.visibleMemoizedSerializedSize = dataSize6;
            int size8 = size7 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size8;
            return size8;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof DenseInfo)) {
                return super.equals(obj);
            }
            DenseInfo other = (DenseInfo) obj;
            return getVersionList().equals(other.getVersionList()) && getTimestampList().equals(other.getTimestampList()) && getChangesetList().equals(other.getChangesetList()) && getUidList().equals(other.getUidList()) && getUserSidList().equals(other.getUserSidList()) && getVisibleList().equals(other.getVisibleList()) && this.unknownFields.equals(other.unknownFields);
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (getVersionCount() > 0) {
                hash = (((hash * 37) + 1) * 53) + getVersionList().hashCode();
            }
            if (getTimestampCount() > 0) {
                hash = (((hash * 37) + 2) * 53) + getTimestampList().hashCode();
            }
            if (getChangesetCount() > 0) {
                hash = (((hash * 37) + 3) * 53) + getChangesetList().hashCode();
            }
            if (getUidCount() > 0) {
                hash = (((hash * 37) + 4) * 53) + getUidList().hashCode();
            }
            if (getUserSidCount() > 0) {
                hash = (((hash * 37) + 5) * 53) + getUserSidList().hashCode();
            }
            if (getVisibleCount() > 0) {
                hash = (((hash * 37) + 6) * 53) + getVisibleList().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static DenseInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseInfo parseFrom(InputStream input) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static DenseInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static DenseInfo parseDelimitedFrom(InputStream input) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static DenseInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static DenseInfo parseFrom(CodedInputStream input) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static DenseInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseInfo) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(DenseInfo prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DenseInfoOrBuilder {
            private int bitField0_;
            private Internal.LongList changeset_;
            private Internal.LongList timestamp_;
            private Internal.IntList uid_;
            private Internal.IntList userSid_;
            private Internal.IntList version_;
            private Internal.BooleanList visible_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_DenseInfo_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_DenseInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(DenseInfo.class, Builder.class);
            }

            private Builder() {
                this.version_ = DenseInfo.emptyIntList();
                this.timestamp_ = DenseInfo.emptyLongList();
                this.changeset_ = DenseInfo.emptyLongList();
                this.uid_ = DenseInfo.emptyIntList();
                this.userSid_ = DenseInfo.emptyIntList();
                this.visible_ = DenseInfo.emptyBooleanList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.version_ = DenseInfo.emptyIntList();
                this.timestamp_ = DenseInfo.emptyLongList();
                this.changeset_ = DenseInfo.emptyLongList();
                this.uid_ = DenseInfo.emptyIntList();
                this.userSid_ = DenseInfo.emptyIntList();
                this.visible_ = DenseInfo.emptyBooleanList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = DenseInfo.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.version_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -2;
                this.timestamp_ = DenseInfo.emptyLongList();
                this.bitField0_ &= -3;
                this.changeset_ = DenseInfo.emptyLongList();
                this.bitField0_ &= -5;
                this.uid_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -9;
                this.userSid_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -17;
                this.visible_ = DenseInfo.emptyBooleanList();
                this.bitField0_ &= -33;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_DenseInfo_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public DenseInfo getDefaultInstanceForType() {
                return DenseInfo.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public DenseInfo build() {
                DenseInfo result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public DenseInfo buildPartial() {
                DenseInfo result = new DenseInfo(this);
                int i = this.bitField0_;
                if ((this.bitField0_ & 1) != 0) {
                    this.version_.makeImmutable();
                    this.bitField0_ &= -2;
                }
                result.version_ = this.version_;
                if ((this.bitField0_ & 2) != 0) {
                    this.timestamp_.makeImmutable();
                    this.bitField0_ &= -3;
                }
                result.timestamp_ = this.timestamp_;
                if ((this.bitField0_ & 4) != 0) {
                    this.changeset_.makeImmutable();
                    this.bitField0_ &= -5;
                }
                result.changeset_ = this.changeset_;
                if ((this.bitField0_ & 8) != 0) {
                    this.uid_.makeImmutable();
                    this.bitField0_ &= -9;
                }
                result.uid_ = this.uid_;
                if ((this.bitField0_ & 16) != 0) {
                    this.userSid_.makeImmutable();
                    this.bitField0_ &= -17;
                }
                result.userSid_ = this.userSid_;
                if ((this.bitField0_ & 32) != 0) {
                    this.visible_.makeImmutable();
                    this.bitField0_ &= -33;
                }
                result.visible_ = this.visible_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof DenseInfo) {
                    return mergeFrom((DenseInfo) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(DenseInfo other) {
                if (other == DenseInfo.getDefaultInstance()) {
                    return this;
                }
                if (!other.version_.isEmpty()) {
                    if (this.version_.isEmpty()) {
                        this.version_ = other.version_;
                        this.bitField0_ &= -2;
                    } else {
                        ensureVersionIsMutable();
                        this.version_.addAll(other.version_);
                    }
                    onChanged();
                }
                if (!other.timestamp_.isEmpty()) {
                    if (this.timestamp_.isEmpty()) {
                        this.timestamp_ = other.timestamp_;
                        this.bitField0_ &= -3;
                    } else {
                        ensureTimestampIsMutable();
                        this.timestamp_.addAll(other.timestamp_);
                    }
                    onChanged();
                }
                if (!other.changeset_.isEmpty()) {
                    if (this.changeset_.isEmpty()) {
                        this.changeset_ = other.changeset_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureChangesetIsMutable();
                        this.changeset_.addAll(other.changeset_);
                    }
                    onChanged();
                }
                if (!other.uid_.isEmpty()) {
                    if (this.uid_.isEmpty()) {
                        this.uid_ = other.uid_;
                        this.bitField0_ &= -9;
                    } else {
                        ensureUidIsMutable();
                        this.uid_.addAll(other.uid_);
                    }
                    onChanged();
                }
                if (!other.userSid_.isEmpty()) {
                    if (this.userSid_.isEmpty()) {
                        this.userSid_ = other.userSid_;
                        this.bitField0_ &= -17;
                    } else {
                        ensureUserSidIsMutable();
                        this.userSid_.addAll(other.userSid_);
                    }
                    onChanged();
                }
                if (!other.visible_.isEmpty()) {
                    if (this.visible_.isEmpty()) {
                        this.visible_ = other.visible_;
                        this.bitField0_ &= -33;
                    } else {
                        ensureVisibleIsMutable();
                        this.visible_.addAll(other.visible_);
                    }
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                DenseInfo parsedMessage = null;
                try {
                    try {
                        parsedMessage = DenseInfo.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            private void ensureVersionIsMutable() {
                if ((this.bitField0_ & 1) == 0) {
                    this.version_ = DenseInfo.mutableCopy(this.version_);
                    this.bitField0_ |= 1;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Integer> getVersionList() {
                return (this.bitField0_ & 1) != 0 ? Collections.unmodifiableList(this.version_) : this.version_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getVersionCount() {
                return this.version_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getVersion(int index) {
                return this.version_.getInt(index);
            }

            public Builder setVersion(int index, int value) {
                ensureVersionIsMutable();
                this.version_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addVersion(int value) {
                ensureVersionIsMutable();
                this.version_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllVersion(Iterable<? extends Integer> values) {
                ensureVersionIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.version_);
                onChanged();
                return this;
            }

            public Builder clearVersion() {
                this.version_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -2;
                onChanged();
                return this;
            }

            private void ensureTimestampIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.timestamp_ = DenseInfo.mutableCopy(this.timestamp_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Long> getTimestampList() {
                return (this.bitField0_ & 2) != 0 ? Collections.unmodifiableList(this.timestamp_) : this.timestamp_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getTimestampCount() {
                return this.timestamp_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public long getTimestamp(int index) {
                return this.timestamp_.getLong(index);
            }

            public Builder setTimestamp(int index, long value) {
                ensureTimestampIsMutable();
                this.timestamp_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addTimestamp(long value) {
                ensureTimestampIsMutable();
                this.timestamp_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllTimestamp(Iterable<? extends Long> values) {
                ensureTimestampIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.timestamp_);
                onChanged();
                return this;
            }

            public Builder clearTimestamp() {
                this.timestamp_ = DenseInfo.emptyLongList();
                this.bitField0_ &= -3;
                onChanged();
                return this;
            }

            private void ensureChangesetIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.changeset_ = DenseInfo.mutableCopy(this.changeset_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Long> getChangesetList() {
                return (this.bitField0_ & 4) != 0 ? Collections.unmodifiableList(this.changeset_) : this.changeset_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getChangesetCount() {
                return this.changeset_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public long getChangeset(int index) {
                return this.changeset_.getLong(index);
            }

            public Builder setChangeset(int index, long value) {
                ensureChangesetIsMutable();
                this.changeset_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addChangeset(long value) {
                ensureChangesetIsMutable();
                this.changeset_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllChangeset(Iterable<? extends Long> values) {
                ensureChangesetIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.changeset_);
                onChanged();
                return this;
            }

            public Builder clearChangeset() {
                this.changeset_ = DenseInfo.emptyLongList();
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            private void ensureUidIsMutable() {
                if ((this.bitField0_ & 8) == 0) {
                    this.uid_ = DenseInfo.mutableCopy(this.uid_);
                    this.bitField0_ |= 8;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Integer> getUidList() {
                return (this.bitField0_ & 8) != 0 ? Collections.unmodifiableList(this.uid_) : this.uid_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getUidCount() {
                return this.uid_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getUid(int index) {
                return this.uid_.getInt(index);
            }

            public Builder setUid(int index, int value) {
                ensureUidIsMutable();
                this.uid_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addUid(int value) {
                ensureUidIsMutable();
                this.uid_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllUid(Iterable<? extends Integer> values) {
                ensureUidIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.uid_);
                onChanged();
                return this;
            }

            public Builder clearUid() {
                this.uid_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -9;
                onChanged();
                return this;
            }

            private void ensureUserSidIsMutable() {
                if ((this.bitField0_ & 16) == 0) {
                    this.userSid_ = DenseInfo.mutableCopy(this.userSid_);
                    this.bitField0_ |= 16;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Integer> getUserSidList() {
                return (this.bitField0_ & 16) != 0 ? Collections.unmodifiableList(this.userSid_) : this.userSid_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getUserSidCount() {
                return this.userSid_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getUserSid(int index) {
                return this.userSid_.getInt(index);
            }

            public Builder setUserSid(int index, int value) {
                ensureUserSidIsMutable();
                this.userSid_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addUserSid(int value) {
                ensureUserSidIsMutable();
                this.userSid_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllUserSid(Iterable<? extends Integer> values) {
                ensureUserSidIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.userSid_);
                onChanged();
                return this;
            }

            public Builder clearUserSid() {
                this.userSid_ = DenseInfo.emptyIntList();
                this.bitField0_ &= -17;
                onChanged();
                return this;
            }

            private void ensureVisibleIsMutable() {
                if ((this.bitField0_ & 32) == 0) {
                    this.visible_ = DenseInfo.mutableCopy(this.visible_);
                    this.bitField0_ |= 32;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public List<Boolean> getVisibleList() {
                return (this.bitField0_ & 32) != 0 ? Collections.unmodifiableList(this.visible_) : this.visible_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public int getVisibleCount() {
                return this.visible_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseInfoOrBuilder
            public boolean getVisible(int index) {
                return this.visible_.getBoolean(index);
            }

            public Builder setVisible(int index, boolean value) {
                ensureVisibleIsMutable();
                this.visible_.setBoolean(index, value);
                onChanged();
                return this;
            }

            public Builder addVisible(boolean value) {
                ensureVisibleIsMutable();
                this.visible_.addBoolean(value);
                onChanged();
                return this;
            }

            public Builder addAllVisible(Iterable<? extends Boolean> values) {
                ensureVisibleIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.visible_);
                onChanged();
                return this;
            }

            public Builder clearVisible() {
                this.visible_ = DenseInfo.emptyBooleanList();
                this.bitField0_ &= -33;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static DenseInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<DenseInfo> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<DenseInfo> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public DenseInfo getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class ChangeSet extends GeneratedMessageV3 implements ChangeSetOrBuilder {
        public static final int ID_FIELD_NUMBER = 1;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long id_;
        private byte memoizedIsInitialized;
        private static final ChangeSet DEFAULT_INSTANCE = new ChangeSet();

        @Deprecated
        public static final Parser<ChangeSet> PARSER = new AbstractParser<ChangeSet>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.ChangeSet.1
            @Override // com.google.protobuf.Parser
            public ChangeSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ChangeSet(input, extensionRegistry);
            }
        };

        private ChangeSet(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private ChangeSet() {
            this.memoizedIsInitialized = (byte) -1;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new ChangeSet();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private ChangeSet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    this.bitField0_ |= 1;
                                    this.id_ = input.readInt64();
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw e.setUnfinishedMessage(this);
                        }
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_ChangeSet_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_ChangeSet_fieldAccessorTable.ensureFieldAccessorsInitialized(ChangeSet.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.ChangeSetOrBuilder
        public boolean hasId() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.ChangeSetOrBuilder
        public long getId() {
            return this.id_;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasId()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt64(1, this.id_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = ((this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeInt64Size(1, this.id_) : 0) + this.unknownFields.getSerializedSize();
            this.memoizedSize = size2;
            return size2;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ChangeSet)) {
                return super.equals(obj);
            }
            ChangeSet other = (ChangeSet) obj;
            if (hasId() != other.hasId()) {
                return false;
            }
            return (!hasId() || getId() == other.getId()) && this.unknownFields.equals(other.unknownFields);
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasId()) {
                hash = (((hash * 37) + 1) * 53) + Internal.hashLong(getId());
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static ChangeSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ChangeSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ChangeSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ChangeSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ChangeSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ChangeSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ChangeSet parseFrom(InputStream input) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static ChangeSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static ChangeSet parseDelimitedFrom(InputStream input) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static ChangeSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static ChangeSet parseFrom(CodedInputStream input) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static ChangeSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ChangeSet) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ChangeSet prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ChangeSetOrBuilder {
            private int bitField0_;
            private long id_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_ChangeSet_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_ChangeSet_fieldAccessorTable.ensureFieldAccessorsInitialized(ChangeSet.class, Builder.class);
            }

            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = ChangeSet.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= -2;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_ChangeSet_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public ChangeSet getDefaultInstanceForType() {
                return ChangeSet.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public ChangeSet build() {
                ChangeSet result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public ChangeSet buildPartial() {
                ChangeSet result = new ChangeSet(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.id_ = this.id_;
                    to_bitField0_ = 0 | 1;
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof ChangeSet) {
                    return mergeFrom((ChangeSet) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ChangeSet other) {
                if (other == ChangeSet.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    setId(other.getId());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                if (!hasId()) {
                    return false;
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                ChangeSet parsedMessage = null;
                try {
                    try {
                        parsedMessage = ChangeSet.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.ChangeSetOrBuilder
            public boolean hasId() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.ChangeSetOrBuilder
            public long getId() {
                return this.id_;
            }

            public Builder setId(long value) {
                this.bitField0_ |= 1;
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.bitField0_ &= -2;
                this.id_ = 0L;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static ChangeSet getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ChangeSet> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<ChangeSet> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public ChangeSet getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class Node extends GeneratedMessageV3 implements NodeOrBuilder {
        public static final int ID_FIELD_NUMBER = 1;
        public static final int INFO_FIELD_NUMBER = 4;
        public static final int KEYS_FIELD_NUMBER = 2;
        public static final int LAT_FIELD_NUMBER = 8;
        public static final int LON_FIELD_NUMBER = 9;
        public static final int VALS_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long id_;
        private Info info_;
        private int keysMemoizedSerializedSize;
        private Internal.IntList keys_;
        private long lat_;
        private long lon_;
        private byte memoizedIsInitialized;
        private int valsMemoizedSerializedSize;
        private Internal.IntList vals_;
        private static final Node DEFAULT_INSTANCE = new Node();

        @Deprecated
        public static final Parser<Node> PARSER = new AbstractParser<Node>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Node.1
            @Override // com.google.protobuf.Parser
            public Node parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Node(input, extensionRegistry);
            }
        };

        private Node(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
        }

        private Node() {
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
            this.keys_ = emptyIntList();
            this.vals_ = emptyIntList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Node();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Node(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    this.bitField0_ |= 1;
                                    this.id_ = input.readSInt64();
                                    break;
                                case 16:
                                    int length = mutable_bitField0_ & 2;
                                    if (length == 0) {
                                        this.keys_ = newIntList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    this.keys_.addInt(input.readUInt32());
                                    break;
                                case 18:
                                    int length2 = input.readRawVarint32();
                                    int limit = input.pushLimit(length2);
                                    if ((mutable_bitField0_ & 2) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.keys_ = newIntList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.keys_.addInt(input.readUInt32());
                                    }
                                    input.popLimit(limit);
                                    break;
                                case 24:
                                    int length3 = mutable_bitField0_ & 4;
                                    if (length3 == 0) {
                                        this.vals_ = newIntList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    this.vals_.addInt(input.readUInt32());
                                    break;
                                case 26:
                                    int length4 = input.readRawVarint32();
                                    int limit2 = input.pushLimit(length4);
                                    if ((mutable_bitField0_ & 4) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.vals_ = newIntList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.vals_.addInt(input.readUInt32());
                                    }
                                    input.popLimit(limit2);
                                    break;
                                case 34:
                                    Info.Builder subBuilder = (this.bitField0_ & 2) != 0 ? this.info_.toBuilder() : null;
                                    this.info_ = (Info) input.readMessage(Info.PARSER, extensionRegistry);
                                    if (subBuilder != null) {
                                        subBuilder.mergeFrom(this.info_);
                                        this.info_ = subBuilder.buildPartial();
                                    }
                                    this.bitField0_ |= 2;
                                    break;
                                case 64:
                                    this.bitField0_ |= 4;
                                    this.lat_ = input.readSInt64();
                                    break;
                                case 72:
                                    this.bitField0_ |= 8;
                                    this.lon_ = input.readSInt64();
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            throw e.setUnfinishedMessage(this);
                        }
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.keys_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.vals_.makeImmutable();
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_Node_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_Node_fieldAccessorTable.ensureFieldAccessorsInitialized(Node.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public boolean hasId() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public long getId() {
            return this.id_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public List<Integer> getKeysList() {
            return this.keys_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public int getKeysCount() {
            return this.keys_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public int getKeys(int index) {
            return this.keys_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public List<Integer> getValsList() {
            return this.vals_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public int getValsCount() {
            return this.vals_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public int getVals(int index) {
            return this.vals_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public boolean hasInfo() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public Info getInfo() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public InfoOrBuilder getInfoOrBuilder() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public boolean hasLat() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public long getLat() {
            return this.lat_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public boolean hasLon() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
        public long getLon() {
            return this.lon_;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasId()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasLat()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasLon()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                output.writeSInt64(1, this.id_);
            }
            if (getKeysList().size() > 0) {
                output.writeUInt32NoTag(18);
                output.writeUInt32NoTag(this.keysMemoizedSerializedSize);
            }
            for (int i = 0; i < this.keys_.size(); i++) {
                output.writeUInt32NoTag(this.keys_.getInt(i));
            }
            if (getValsList().size() > 0) {
                output.writeUInt32NoTag(26);
                output.writeUInt32NoTag(this.valsMemoizedSerializedSize);
            }
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                output.writeUInt32NoTag(this.vals_.getInt(i2));
            }
            int i3 = this.bitField0_;
            if ((i3 & 2) != 0) {
                output.writeMessage(4, getInfo());
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeSInt64(8, this.lat_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeSInt64(9, this.lon_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeSInt64Size(1, this.id_) : 0;
            int dataSize = 0;
            for (int i = 0; i < this.keys_.size(); i++) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(this.keys_.getInt(i));
            }
            int size3 = size2 + dataSize;
            if (!getKeysList().isEmpty()) {
                size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
            }
            this.keysMemoizedSerializedSize = dataSize;
            int dataSize2 = 0;
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                dataSize2 += CodedOutputStream.computeUInt32SizeNoTag(this.vals_.getInt(i2));
            }
            int size4 = size3 + dataSize2;
            if (!getValsList().isEmpty()) {
                size4 = size4 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize2);
            }
            this.valsMemoizedSerializedSize = dataSize2;
            int dataSize3 = this.bitField0_;
            if ((dataSize3 & 2) != 0) {
                size4 += CodedOutputStream.computeMessageSize(4, getInfo());
            }
            if ((this.bitField0_ & 4) != 0) {
                size4 += CodedOutputStream.computeSInt64Size(8, this.lat_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size4 += CodedOutputStream.computeSInt64Size(9, this.lon_);
            }
            int size5 = size4 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size5;
            return size5;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Node)) {
                return super.equals(obj);
            }
            Node other = (Node) obj;
            if (hasId() != other.hasId()) {
                return false;
            }
            if ((hasId() && getId() != other.getId()) || !getKeysList().equals(other.getKeysList()) || !getValsList().equals(other.getValsList()) || hasInfo() != other.hasInfo()) {
                return false;
            }
            if ((hasInfo() && !getInfo().equals(other.getInfo())) || hasLat() != other.hasLat()) {
                return false;
            }
            if ((!hasLat() || getLat() == other.getLat()) && hasLon() == other.hasLon()) {
                return (!hasLon() || getLon() == other.getLon()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasId()) {
                hash = (((hash * 37) + 1) * 53) + Internal.hashLong(getId());
            }
            if (getKeysCount() > 0) {
                hash = (((hash * 37) + 2) * 53) + getKeysList().hashCode();
            }
            if (getValsCount() > 0) {
                hash = (((hash * 37) + 3) * 53) + getValsList().hashCode();
            }
            if (hasInfo()) {
                hash = (((hash * 37) + 4) * 53) + getInfo().hashCode();
            }
            if (hasLat()) {
                hash = (((hash * 37) + 8) * 53) + Internal.hashLong(getLat());
            }
            if (hasLon()) {
                hash = (((hash * 37) + 9) * 53) + Internal.hashLong(getLon());
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static Node parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Node parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Node parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Node parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Node parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Node parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Node parseFrom(InputStream input) throws IOException {
            return (Node) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Node parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Node) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Node parseDelimitedFrom(InputStream input) throws IOException {
            return (Node) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Node parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Node) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Node parseFrom(CodedInputStream input) throws IOException {
            return (Node) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Node parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Node) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Node prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements NodeOrBuilder {
            private int bitField0_;
            private long id_;
            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> infoBuilder_;
            private Info info_;
            private Internal.IntList keys_;
            private long lat_;
            private long lon_;
            private Internal.IntList vals_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_Node_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_Node_fieldAccessorTable.ensureFieldAccessorsInitialized(Node.class, Builder.class);
            }

            private Builder() {
                this.keys_ = Node.emptyIntList();
                this.vals_ = Node.emptyIntList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.keys_ = Node.emptyIntList();
                this.vals_ = Node.emptyIntList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (Node.alwaysUseFieldBuilders) {
                    getInfoFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= -2;
                this.keys_ = Node.emptyIntList();
                this.bitField0_ &= -3;
                this.vals_ = Node.emptyIntList();
                this.bitField0_ &= -5;
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                this.lat_ = 0L;
                this.bitField0_ &= -17;
                this.lon_ = 0L;
                this.bitField0_ &= -33;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_Node_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public Node getDefaultInstanceForType() {
                return Node.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Node build() {
                Node result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Node buildPartial() {
                Node result = new Node(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.id_ = this.id_;
                    to_bitField0_ = 0 | 1;
                }
                if ((this.bitField0_ & 2) != 0) {
                    this.keys_.makeImmutable();
                    this.bitField0_ &= -3;
                }
                result.keys_ = this.keys_;
                if ((this.bitField0_ & 4) != 0) {
                    this.vals_.makeImmutable();
                    this.bitField0_ &= -5;
                }
                result.vals_ = this.vals_;
                if ((from_bitField0_ & 8) != 0) {
                    if (this.infoBuilder_ == null) {
                        result.info_ = this.info_;
                    } else {
                        result.info_ = (Info) this.infoBuilder_.build();
                    }
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 16) != 0) {
                    result.lat_ = this.lat_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 32) != 0) {
                    result.lon_ = this.lon_;
                    to_bitField0_ |= 8;
                }
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof Node) {
                    return mergeFrom((Node) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Node other) {
                if (other == Node.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    setId(other.getId());
                }
                if (!other.keys_.isEmpty()) {
                    if (this.keys_.isEmpty()) {
                        this.keys_ = other.keys_;
                        this.bitField0_ &= -3;
                    } else {
                        ensureKeysIsMutable();
                        this.keys_.addAll(other.keys_);
                    }
                    onChanged();
                }
                if (!other.vals_.isEmpty()) {
                    if (this.vals_.isEmpty()) {
                        this.vals_ = other.vals_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureValsIsMutable();
                        this.vals_.addAll(other.vals_);
                    }
                    onChanged();
                }
                if (other.hasInfo()) {
                    mergeInfo(other.getInfo());
                }
                if (other.hasLat()) {
                    setLat(other.getLat());
                }
                if (other.hasLon()) {
                    setLon(other.getLon());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return hasId() && hasLat() && hasLon();
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Node parsedMessage = null;
                try {
                    try {
                        parsedMessage = Node.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public boolean hasId() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public long getId() {
                return this.id_;
            }

            public Builder setId(long value) {
                this.bitField0_ |= 1;
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.bitField0_ &= -2;
                this.id_ = 0L;
                onChanged();
                return this;
            }

            private void ensureKeysIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.keys_ = Node.mutableCopy(this.keys_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public List<Integer> getKeysList() {
                return (this.bitField0_ & 2) != 0 ? Collections.unmodifiableList(this.keys_) : this.keys_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public int getKeysCount() {
                return this.keys_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public int getKeys(int index) {
                return this.keys_.getInt(index);
            }

            public Builder setKeys(int index, int value) {
                ensureKeysIsMutable();
                this.keys_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addKeys(int value) {
                ensureKeysIsMutable();
                this.keys_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllKeys(Iterable<? extends Integer> values) {
                ensureKeysIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.keys_);
                onChanged();
                return this;
            }

            public Builder clearKeys() {
                this.keys_ = Node.emptyIntList();
                this.bitField0_ &= -3;
                onChanged();
                return this;
            }

            private void ensureValsIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.vals_ = Node.mutableCopy(this.vals_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public List<Integer> getValsList() {
                return (this.bitField0_ & 4) != 0 ? Collections.unmodifiableList(this.vals_) : this.vals_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public int getValsCount() {
                return this.vals_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public int getVals(int index) {
                return this.vals_.getInt(index);
            }

            public Builder setVals(int index, int value) {
                ensureValsIsMutable();
                this.vals_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addVals(int value) {
                ensureValsIsMutable();
                this.vals_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllVals(Iterable<? extends Integer> values) {
                ensureValsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.vals_);
                onChanged();
                return this;
            }

            public Builder clearVals() {
                this.vals_ = Node.emptyIntList();
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public boolean hasInfo() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public Info getInfo() {
                if (this.infoBuilder_ == null) {
                    return this.info_ == null ? Info.getDefaultInstance() : this.info_;
                }
                return (Info) this.infoBuilder_.getMessage();
            }

            public Builder setInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.info_ = value;
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setInfo(Info.Builder builderForValue) {
                if (this.infoBuilder_ == null) {
                    this.info_ = builderForValue.build();
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if ((this.bitField0_ & 8) != 0 && this.info_ != null && this.info_ != Info.getDefaultInstance()) {
                        this.info_ = Info.newBuilder(this.info_).mergeFrom(value).buildPartial();
                    } else {
                        this.info_ = value;
                    }
                    onChanged();
                } else {
                    this.infoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearInfo() {
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                    onChanged();
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public Info.Builder getInfoBuilder() {
                this.bitField0_ |= 8;
                onChanged();
                return (Info.Builder) getInfoFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public InfoOrBuilder getInfoOrBuilder() {
                if (this.infoBuilder_ != null) {
                    return (InfoOrBuilder) this.infoBuilder_.getMessageOrBuilder();
                }
                return this.info_ == null ? Info.getDefaultInstance() : this.info_;
            }

            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> getInfoFieldBuilder() {
                if (this.infoBuilder_ == null) {
                    this.infoBuilder_ = new SingleFieldBuilderV3<>(getInfo(), getParentForChildren(), isClean());
                    this.info_ = null;
                }
                return this.infoBuilder_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public boolean hasLat() {
                return (this.bitField0_ & 16) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public long getLat() {
                return this.lat_;
            }

            public Builder setLat(long value) {
                this.bitField0_ |= 16;
                this.lat_ = value;
                onChanged();
                return this;
            }

            public Builder clearLat() {
                this.bitField0_ &= -17;
                this.lat_ = 0L;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public boolean hasLon() {
                return (this.bitField0_ & 32) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.NodeOrBuilder
            public long getLon() {
                return this.lon_;
            }

            public Builder setLon(long value) {
                this.bitField0_ |= 32;
                this.lon_ = value;
                onChanged();
                return this;
            }

            public Builder clearLon() {
                this.bitField0_ &= -33;
                this.lon_ = 0L;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static Node getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Node> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<Node> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public Node getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class DenseNodes extends GeneratedMessageV3 implements DenseNodesOrBuilder {
        public static final int DENSEINFO_FIELD_NUMBER = 5;
        public static final int ID_FIELD_NUMBER = 1;
        public static final int KEYS_VALS_FIELD_NUMBER = 10;
        public static final int LAT_FIELD_NUMBER = 8;
        public static final int LON_FIELD_NUMBER = 9;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private DenseInfo denseinfo_;
        private int idMemoizedSerializedSize;
        private Internal.LongList id_;
        private int keysValsMemoizedSerializedSize;
        private Internal.IntList keysVals_;
        private int latMemoizedSerializedSize;
        private Internal.LongList lat_;
        private int lonMemoizedSerializedSize;
        private Internal.LongList lon_;
        private byte memoizedIsInitialized;
        private static final DenseNodes DEFAULT_INSTANCE = new DenseNodes();

        @Deprecated
        public static final Parser<DenseNodes> PARSER = new AbstractParser<DenseNodes>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodes.1
            @Override // com.google.protobuf.Parser
            public DenseNodes parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new DenseNodes(input, extensionRegistry);
            }
        };

        private DenseNodes(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.idMemoizedSerializedSize = -1;
            this.latMemoizedSerializedSize = -1;
            this.lonMemoizedSerializedSize = -1;
            this.keysValsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
        }

        private DenseNodes() {
            this.idMemoizedSerializedSize = -1;
            this.latMemoizedSerializedSize = -1;
            this.lonMemoizedSerializedSize = -1;
            this.keysValsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
            this.id_ = emptyLongList();
            this.lat_ = emptyLongList();
            this.lon_ = emptyLongList();
            this.keysVals_ = emptyIntList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new DenseNodes();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private DenseNodes(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    int length = mutable_bitField0_ & 1;
                                    if (length == 0) {
                                        this.id_ = newLongList();
                                        mutable_bitField0_ |= 1;
                                    }
                                    this.id_.addLong(input.readSInt64());
                                    break;
                                case 10:
                                    int length2 = input.readRawVarint32();
                                    int limit = input.pushLimit(length2);
                                    if ((mutable_bitField0_ & 1) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.id_ = newLongList();
                                        mutable_bitField0_ |= 1;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.id_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit);
                                    break;
                                case 42:
                                    DenseInfo.Builder subBuilder = (this.bitField0_ & 1) != 0 ? this.denseinfo_.toBuilder() : null;
                                    this.denseinfo_ = (DenseInfo) input.readMessage(DenseInfo.PARSER, extensionRegistry);
                                    if (subBuilder != null) {
                                        subBuilder.mergeFrom(this.denseinfo_);
                                        this.denseinfo_ = subBuilder.buildPartial();
                                    }
                                    this.bitField0_ |= 1;
                                    break;
                                case 64:
                                    int length3 = mutable_bitField0_ & 4;
                                    if (length3 == 0) {
                                        this.lat_ = newLongList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    this.lat_.addLong(input.readSInt64());
                                    break;
                                case 66:
                                    int length4 = input.readRawVarint32();
                                    int limit2 = input.pushLimit(length4);
                                    if ((mutable_bitField0_ & 4) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.lat_ = newLongList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.lat_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit2);
                                    break;
                                case 72:
                                    int length5 = mutable_bitField0_ & 8;
                                    if (length5 == 0) {
                                        this.lon_ = newLongList();
                                        mutable_bitField0_ |= 8;
                                    }
                                    this.lon_.addLong(input.readSInt64());
                                    break;
                                case 74:
                                    int length6 = input.readRawVarint32();
                                    int limit3 = input.pushLimit(length6);
                                    if ((mutable_bitField0_ & 8) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.lon_ = newLongList();
                                        mutable_bitField0_ |= 8;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.lon_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit3);
                                    break;
                                case 80:
                                    int length7 = mutable_bitField0_ & 16;
                                    if (length7 == 0) {
                                        this.keysVals_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    this.keysVals_.addInt(input.readInt32());
                                    break;
                                case 82:
                                    int length8 = input.readRawVarint32();
                                    int limit4 = input.pushLimit(length8);
                                    if ((mutable_bitField0_ & 16) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.keysVals_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.keysVals_.addInt(input.readInt32());
                                    }
                                    input.popLimit(limit4);
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (IOException e) {
                            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                        }
                    } catch (InvalidProtocolBufferException e2) {
                        throw e2.setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 1) != 0) {
                        this.id_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.lat_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 8) != 0) {
                        this.lon_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 16) != 0) {
                        this.keysVals_.makeImmutable();
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_DenseNodes_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_DenseNodes_fieldAccessorTable.ensureFieldAccessorsInitialized(DenseNodes.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public List<Long> getIdList() {
            return this.id_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public int getIdCount() {
            return this.id_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public long getId(int index) {
            return this.id_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public boolean hasDenseinfo() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public DenseInfo getDenseinfo() {
            return this.denseinfo_ == null ? DenseInfo.getDefaultInstance() : this.denseinfo_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public DenseInfoOrBuilder getDenseinfoOrBuilder() {
            return this.denseinfo_ == null ? DenseInfo.getDefaultInstance() : this.denseinfo_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public List<Long> getLatList() {
            return this.lat_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public int getLatCount() {
            return this.lat_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public long getLat(int index) {
            return this.lat_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public List<Long> getLonList() {
            return this.lon_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public int getLonCount() {
            return this.lon_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public long getLon(int index) {
            return this.lon_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public List<Integer> getKeysValsList() {
            return this.keysVals_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public int getKeysValsCount() {
            return this.keysVals_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
        public int getKeysVals(int index) {
            return this.keysVals_.getInt(index);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if (getIdList().size() > 0) {
                output.writeUInt32NoTag(10);
                output.writeUInt32NoTag(this.idMemoizedSerializedSize);
            }
            for (int i = 0; i < this.id_.size(); i++) {
                output.writeSInt64NoTag(this.id_.getLong(i));
            }
            int i2 = this.bitField0_;
            if ((i2 & 1) != 0) {
                output.writeMessage(5, getDenseinfo());
            }
            if (getLatList().size() > 0) {
                output.writeUInt32NoTag(66);
                output.writeUInt32NoTag(this.latMemoizedSerializedSize);
            }
            for (int i3 = 0; i3 < this.lat_.size(); i3++) {
                output.writeSInt64NoTag(this.lat_.getLong(i3));
            }
            if (getLonList().size() > 0) {
                output.writeUInt32NoTag(74);
                output.writeUInt32NoTag(this.lonMemoizedSerializedSize);
            }
            for (int i4 = 0; i4 < this.lon_.size(); i4++) {
                output.writeSInt64NoTag(this.lon_.getLong(i4));
            }
            if (getKeysValsList().size() > 0) {
                output.writeUInt32NoTag(82);
                output.writeUInt32NoTag(this.keysValsMemoizedSerializedSize);
            }
            for (int i5 = 0; i5 < this.keysVals_.size(); i5++) {
                output.writeInt32NoTag(this.keysVals_.getInt(i5));
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int dataSize = 0;
            for (int i = 0; i < this.id_.size(); i++) {
                dataSize += CodedOutputStream.computeSInt64SizeNoTag(this.id_.getLong(i));
            }
            int size2 = 0 + dataSize;
            if (!getIdList().isEmpty()) {
                size2 = size2 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
            }
            this.idMemoizedSerializedSize = dataSize;
            int dataSize2 = this.bitField0_;
            if ((dataSize2 & 1) != 0) {
                size2 += CodedOutputStream.computeMessageSize(5, getDenseinfo());
            }
            int dataSize3 = 0;
            for (int i2 = 0; i2 < this.lat_.size(); i2++) {
                dataSize3 += CodedOutputStream.computeSInt64SizeNoTag(this.lat_.getLong(i2));
            }
            int size3 = size2 + dataSize3;
            if (!getLatList().isEmpty()) {
                size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize3);
            }
            this.latMemoizedSerializedSize = dataSize3;
            int dataSize4 = 0;
            for (int i3 = 0; i3 < this.lon_.size(); i3++) {
                dataSize4 += CodedOutputStream.computeSInt64SizeNoTag(this.lon_.getLong(i3));
            }
            int size4 = size3 + dataSize4;
            if (!getLonList().isEmpty()) {
                size4 = size4 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize4);
            }
            this.lonMemoizedSerializedSize = dataSize4;
            int dataSize5 = 0;
            for (int i4 = 0; i4 < this.keysVals_.size(); i4++) {
                dataSize5 += CodedOutputStream.computeInt32SizeNoTag(this.keysVals_.getInt(i4));
            }
            int size5 = size4 + dataSize5;
            if (!getKeysValsList().isEmpty()) {
                size5 = size5 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize5);
            }
            this.keysValsMemoizedSerializedSize = dataSize5;
            int size6 = size5 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size6;
            return size6;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof DenseNodes)) {
                return super.equals(obj);
            }
            DenseNodes other = (DenseNodes) obj;
            if (getIdList().equals(other.getIdList()) && hasDenseinfo() == other.hasDenseinfo()) {
                return (!hasDenseinfo() || getDenseinfo().equals(other.getDenseinfo())) && getLatList().equals(other.getLatList()) && getLonList().equals(other.getLonList()) && getKeysValsList().equals(other.getKeysValsList()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (getIdCount() > 0) {
                hash = (((hash * 37) + 1) * 53) + getIdList().hashCode();
            }
            if (hasDenseinfo()) {
                hash = (((hash * 37) + 5) * 53) + getDenseinfo().hashCode();
            }
            if (getLatCount() > 0) {
                hash = (((hash * 37) + 8) * 53) + getLatList().hashCode();
            }
            if (getLonCount() > 0) {
                hash = (((hash * 37) + 9) * 53) + getLonList().hashCode();
            }
            if (getKeysValsCount() > 0) {
                hash = (((hash * 37) + 10) * 53) + getKeysValsList().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static DenseNodes parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseNodes parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseNodes parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseNodes parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseNodes parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DenseNodes parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DenseNodes parseFrom(InputStream input) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static DenseNodes parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static DenseNodes parseDelimitedFrom(InputStream input) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static DenseNodes parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static DenseNodes parseFrom(CodedInputStream input) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static DenseNodes parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (DenseNodes) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(DenseNodes prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DenseNodesOrBuilder {
            private int bitField0_;
            private SingleFieldBuilderV3<DenseInfo, DenseInfo.Builder, DenseInfoOrBuilder> denseinfoBuilder_;
            private DenseInfo denseinfo_;
            private Internal.LongList id_;
            private Internal.IntList keysVals_;
            private Internal.LongList lat_;
            private Internal.LongList lon_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_DenseNodes_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_DenseNodes_fieldAccessorTable.ensureFieldAccessorsInitialized(DenseNodes.class, Builder.class);
            }

            private Builder() {
                this.id_ = DenseNodes.emptyLongList();
                this.lat_ = DenseNodes.emptyLongList();
                this.lon_ = DenseNodes.emptyLongList();
                this.keysVals_ = DenseNodes.emptyIntList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.id_ = DenseNodes.emptyLongList();
                this.lat_ = DenseNodes.emptyLongList();
                this.lon_ = DenseNodes.emptyLongList();
                this.keysVals_ = DenseNodes.emptyIntList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (DenseNodes.alwaysUseFieldBuilders) {
                    getDenseinfoFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.id_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -2;
                if (this.denseinfoBuilder_ == null) {
                    this.denseinfo_ = null;
                } else {
                    this.denseinfoBuilder_.clear();
                }
                this.bitField0_ &= -3;
                this.lat_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -5;
                this.lon_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -9;
                this.keysVals_ = DenseNodes.emptyIntList();
                this.bitField0_ &= -17;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_DenseNodes_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public DenseNodes getDefaultInstanceForType() {
                return DenseNodes.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public DenseNodes build() {
                DenseNodes result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public DenseNodes buildPartial() {
                DenseNodes result = new DenseNodes(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((this.bitField0_ & 1) != 0) {
                    this.id_.makeImmutable();
                    this.bitField0_ &= -2;
                }
                result.id_ = this.id_;
                if ((from_bitField0_ & 2) != 0) {
                    if (this.denseinfoBuilder_ == null) {
                        result.denseinfo_ = this.denseinfo_;
                    } else {
                        result.denseinfo_ = (DenseInfo) this.denseinfoBuilder_.build();
                    }
                    to_bitField0_ = 0 | 1;
                }
                if ((this.bitField0_ & 4) != 0) {
                    this.lat_.makeImmutable();
                    this.bitField0_ &= -5;
                }
                result.lat_ = this.lat_;
                if ((this.bitField0_ & 8) != 0) {
                    this.lon_.makeImmutable();
                    this.bitField0_ &= -9;
                }
                result.lon_ = this.lon_;
                if ((this.bitField0_ & 16) != 0) {
                    this.keysVals_.makeImmutable();
                    this.bitField0_ &= -17;
                }
                result.keysVals_ = this.keysVals_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof DenseNodes) {
                    return mergeFrom((DenseNodes) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(DenseNodes other) {
                if (other == DenseNodes.getDefaultInstance()) {
                    return this;
                }
                if (!other.id_.isEmpty()) {
                    if (this.id_.isEmpty()) {
                        this.id_ = other.id_;
                        this.bitField0_ &= -2;
                    } else {
                        ensureIdIsMutable();
                        this.id_.addAll(other.id_);
                    }
                    onChanged();
                }
                if (other.hasDenseinfo()) {
                    mergeDenseinfo(other.getDenseinfo());
                }
                if (!other.lat_.isEmpty()) {
                    if (this.lat_.isEmpty()) {
                        this.lat_ = other.lat_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureLatIsMutable();
                        this.lat_.addAll(other.lat_);
                    }
                    onChanged();
                }
                if (!other.lon_.isEmpty()) {
                    if (this.lon_.isEmpty()) {
                        this.lon_ = other.lon_;
                        this.bitField0_ &= -9;
                    } else {
                        ensureLonIsMutable();
                        this.lon_.addAll(other.lon_);
                    }
                    onChanged();
                }
                if (!other.keysVals_.isEmpty()) {
                    if (this.keysVals_.isEmpty()) {
                        this.keysVals_ = other.keysVals_;
                        this.bitField0_ &= -17;
                    } else {
                        ensureKeysValsIsMutable();
                        this.keysVals_.addAll(other.keysVals_);
                    }
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                DenseNodes parsedMessage = null;
                try {
                    try {
                        parsedMessage = DenseNodes.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            private void ensureIdIsMutable() {
                if ((this.bitField0_ & 1) == 0) {
                    this.id_ = DenseNodes.mutableCopy(this.id_);
                    this.bitField0_ |= 1;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public List<Long> getIdList() {
                return (this.bitField0_ & 1) != 0 ? Collections.unmodifiableList(this.id_) : this.id_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public int getIdCount() {
                return this.id_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public long getId(int index) {
                return this.id_.getLong(index);
            }

            public Builder setId(int index, long value) {
                ensureIdIsMutable();
                this.id_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addId(long value) {
                ensureIdIsMutable();
                this.id_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllId(Iterable<? extends Long> values) {
                ensureIdIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.id_);
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.id_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -2;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public boolean hasDenseinfo() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public DenseInfo getDenseinfo() {
                if (this.denseinfoBuilder_ == null) {
                    return this.denseinfo_ == null ? DenseInfo.getDefaultInstance() : this.denseinfo_;
                }
                return (DenseInfo) this.denseinfoBuilder_.getMessage();
            }

            public Builder setDenseinfo(DenseInfo value) {
                if (this.denseinfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.denseinfo_ = value;
                    onChanged();
                } else {
                    this.denseinfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder setDenseinfo(DenseInfo.Builder builderForValue) {
                if (this.denseinfoBuilder_ == null) {
                    this.denseinfo_ = builderForValue.build();
                    onChanged();
                } else {
                    this.denseinfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder mergeDenseinfo(DenseInfo value) {
                if (this.denseinfoBuilder_ == null) {
                    if ((this.bitField0_ & 2) != 0 && this.denseinfo_ != null && this.denseinfo_ != DenseInfo.getDefaultInstance()) {
                        this.denseinfo_ = DenseInfo.newBuilder(this.denseinfo_).mergeFrom(value).buildPartial();
                    } else {
                        this.denseinfo_ = value;
                    }
                    onChanged();
                } else {
                    this.denseinfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder clearDenseinfo() {
                if (this.denseinfoBuilder_ == null) {
                    this.denseinfo_ = null;
                    onChanged();
                } else {
                    this.denseinfoBuilder_.clear();
                }
                this.bitField0_ &= -3;
                return this;
            }

            public DenseInfo.Builder getDenseinfoBuilder() {
                this.bitField0_ |= 2;
                onChanged();
                return (DenseInfo.Builder) getDenseinfoFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public DenseInfoOrBuilder getDenseinfoOrBuilder() {
                if (this.denseinfoBuilder_ != null) {
                    return (DenseInfoOrBuilder) this.denseinfoBuilder_.getMessageOrBuilder();
                }
                return this.denseinfo_ == null ? DenseInfo.getDefaultInstance() : this.denseinfo_;
            }

            private SingleFieldBuilderV3<DenseInfo, DenseInfo.Builder, DenseInfoOrBuilder> getDenseinfoFieldBuilder() {
                if (this.denseinfoBuilder_ == null) {
                    this.denseinfoBuilder_ = new SingleFieldBuilderV3<>(getDenseinfo(), getParentForChildren(), isClean());
                    this.denseinfo_ = null;
                }
                return this.denseinfoBuilder_;
            }

            private void ensureLatIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.lat_ = DenseNodes.mutableCopy(this.lat_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public List<Long> getLatList() {
                return (this.bitField0_ & 4) != 0 ? Collections.unmodifiableList(this.lat_) : this.lat_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public int getLatCount() {
                return this.lat_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public long getLat(int index) {
                return this.lat_.getLong(index);
            }

            public Builder setLat(int index, long value) {
                ensureLatIsMutable();
                this.lat_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addLat(long value) {
                ensureLatIsMutable();
                this.lat_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllLat(Iterable<? extends Long> values) {
                ensureLatIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.lat_);
                onChanged();
                return this;
            }

            public Builder clearLat() {
                this.lat_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            private void ensureLonIsMutable() {
                if ((this.bitField0_ & 8) == 0) {
                    this.lon_ = DenseNodes.mutableCopy(this.lon_);
                    this.bitField0_ |= 8;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public List<Long> getLonList() {
                return (this.bitField0_ & 8) != 0 ? Collections.unmodifiableList(this.lon_) : this.lon_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public int getLonCount() {
                return this.lon_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public long getLon(int index) {
                return this.lon_.getLong(index);
            }

            public Builder setLon(int index, long value) {
                ensureLonIsMutable();
                this.lon_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addLon(long value) {
                ensureLonIsMutable();
                this.lon_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllLon(Iterable<? extends Long> values) {
                ensureLonIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.lon_);
                onChanged();
                return this;
            }

            public Builder clearLon() {
                this.lon_ = DenseNodes.emptyLongList();
                this.bitField0_ &= -9;
                onChanged();
                return this;
            }

            private void ensureKeysValsIsMutable() {
                if ((this.bitField0_ & 16) == 0) {
                    this.keysVals_ = DenseNodes.mutableCopy(this.keysVals_);
                    this.bitField0_ |= 16;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public List<Integer> getKeysValsList() {
                return (this.bitField0_ & 16) != 0 ? Collections.unmodifiableList(this.keysVals_) : this.keysVals_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public int getKeysValsCount() {
                return this.keysVals_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.DenseNodesOrBuilder
            public int getKeysVals(int index) {
                return this.keysVals_.getInt(index);
            }

            public Builder setKeysVals(int index, int value) {
                ensureKeysValsIsMutable();
                this.keysVals_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addKeysVals(int value) {
                ensureKeysValsIsMutable();
                this.keysVals_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllKeysVals(Iterable<? extends Integer> values) {
                ensureKeysValsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.keysVals_);
                onChanged();
                return this;
            }

            public Builder clearKeysVals() {
                this.keysVals_ = DenseNodes.emptyIntList();
                this.bitField0_ &= -17;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static DenseNodes getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<DenseNodes> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<DenseNodes> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public DenseNodes getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class Way extends GeneratedMessageV3 implements WayOrBuilder {
        public static final int ID_FIELD_NUMBER = 1;
        public static final int INFO_FIELD_NUMBER = 4;
        public static final int KEYS_FIELD_NUMBER = 2;
        public static final int LAT_FIELD_NUMBER = 9;
        public static final int LON_FIELD_NUMBER = 10;
        public static final int REFS_FIELD_NUMBER = 8;
        public static final int VALS_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long id_;
        private Info info_;
        private int keysMemoizedSerializedSize;
        private Internal.IntList keys_;
        private int latMemoizedSerializedSize;
        private Internal.LongList lat_;
        private int lonMemoizedSerializedSize;
        private Internal.LongList lon_;
        private byte memoizedIsInitialized;
        private int refsMemoizedSerializedSize;
        private Internal.LongList refs_;
        private int valsMemoizedSerializedSize;
        private Internal.IntList vals_;
        private static final Way DEFAULT_INSTANCE = new Way();

        @Deprecated
        public static final Parser<Way> PARSER = new AbstractParser<Way>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Way.1
            @Override // com.google.protobuf.Parser
            public Way parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Way(input, extensionRegistry);
            }
        };

        private Way(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.refsMemoizedSerializedSize = -1;
            this.latMemoizedSerializedSize = -1;
            this.lonMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
        }

        private Way() {
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.refsMemoizedSerializedSize = -1;
            this.latMemoizedSerializedSize = -1;
            this.lonMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
            this.keys_ = emptyIntList();
            this.vals_ = emptyIntList();
            this.refs_ = emptyLongList();
            this.lat_ = emptyLongList();
            this.lon_ = emptyLongList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Way();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Way(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0:
                                done = true;
                                break;
                            case 8:
                                this.bitField0_ |= 1;
                                this.id_ = input.readInt64();
                                break;
                            case 16:
                                int length = mutable_bitField0_ & 2;
                                if (length == 0) {
                                    this.keys_ = newIntList();
                                    mutable_bitField0_ |= 2;
                                }
                                this.keys_.addInt(input.readUInt32());
                                break;
                            case 18:
                                int length2 = input.readRawVarint32();
                                int limit = input.pushLimit(length2);
                                if ((mutable_bitField0_ & 2) == 0 && input.getBytesUntilLimit() > 0) {
                                    this.keys_ = newIntList();
                                    mutable_bitField0_ |= 2;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.keys_.addInt(input.readUInt32());
                                }
                                input.popLimit(limit);
                                break;
                            case 24:
                                int length3 = mutable_bitField0_ & 4;
                                if (length3 == 0) {
                                    this.vals_ = newIntList();
                                    mutable_bitField0_ |= 4;
                                }
                                this.vals_.addInt(input.readUInt32());
                                break;
                            case 26:
                                int length4 = input.readRawVarint32();
                                int limit2 = input.pushLimit(length4);
                                if ((mutable_bitField0_ & 4) == 0 && input.getBytesUntilLimit() > 0) {
                                    this.vals_ = newIntList();
                                    mutable_bitField0_ |= 4;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.vals_.addInt(input.readUInt32());
                                }
                                input.popLimit(limit2);
                                break;
                            case 34:
                                Info.Builder subBuilder = (this.bitField0_ & 2) != 0 ? this.info_.toBuilder() : null;
                                this.info_ = (Info) input.readMessage(Info.PARSER, extensionRegistry);
                                if (subBuilder != null) {
                                    subBuilder.mergeFrom(this.info_);
                                    this.info_ = subBuilder.buildPartial();
                                }
                                this.bitField0_ |= 2;
                                break;
                            case 64:
                                int length5 = mutable_bitField0_ & 16;
                                if (length5 == 0) {
                                    this.refs_ = newLongList();
                                    mutable_bitField0_ |= 16;
                                }
                                this.refs_.addLong(input.readSInt64());
                                break;
                            case 66:
                                int length6 = input.readRawVarint32();
                                int limit3 = input.pushLimit(length6);
                                if ((mutable_bitField0_ & 16) == 0 && input.getBytesUntilLimit() > 0) {
                                    this.refs_ = newLongList();
                                    mutable_bitField0_ |= 16;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.refs_.addLong(input.readSInt64());
                                }
                                input.popLimit(limit3);
                                break;
                            case 72:
                                int length7 = mutable_bitField0_ & 32;
                                if (length7 == 0) {
                                    this.lat_ = newLongList();
                                    mutable_bitField0_ |= 32;
                                }
                                this.lat_.addLong(input.readSInt64());
                                break;
                            case 74:
                                int length8 = input.readRawVarint32();
                                int limit4 = input.pushLimit(length8);
                                if ((mutable_bitField0_ & 32) == 0 && input.getBytesUntilLimit() > 0) {
                                    this.lat_ = newLongList();
                                    mutable_bitField0_ |= 32;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.lat_.addLong(input.readSInt64());
                                }
                                input.popLimit(limit4);
                                break;
                            case 80:
                                int length9 = mutable_bitField0_ & 64;
                                if (length9 == 0) {
                                    this.lon_ = newLongList();
                                    mutable_bitField0_ |= 64;
                                }
                                this.lon_.addLong(input.readSInt64());
                                break;
                            case 82:
                                int length10 = input.readRawVarint32();
                                int limit5 = input.pushLimit(length10);
                                if ((mutable_bitField0_ & 64) == 0 && input.getBytesUntilLimit() > 0) {
                                    this.lon_ = newLongList();
                                    mutable_bitField0_ |= 64;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.lon_.addLong(input.readSInt64());
                                }
                                input.popLimit(limit5);
                                break;
                            default:
                                if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(this);
                    } catch (IOException e2) {
                        throw new InvalidProtocolBufferException(e2).setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.keys_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.vals_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 16) != 0) {
                        this.refs_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 32) != 0) {
                        this.lat_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 64) != 0) {
                        this.lon_.makeImmutable();
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_Way_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_Way_fieldAccessorTable.ensureFieldAccessorsInitialized(Way.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public boolean hasId() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public long getId() {
            return this.id_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public List<Integer> getKeysList() {
            return this.keys_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getKeysCount() {
            return this.keys_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getKeys(int index) {
            return this.keys_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public List<Integer> getValsList() {
            return this.vals_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getValsCount() {
            return this.vals_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getVals(int index) {
            return this.vals_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public boolean hasInfo() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public Info getInfo() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public InfoOrBuilder getInfoOrBuilder() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public List<Long> getRefsList() {
            return this.refs_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getRefsCount() {
            return this.refs_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public long getRefs(int index) {
            return this.refs_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public List<Long> getLatList() {
            return this.lat_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getLatCount() {
            return this.lat_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public long getLat(int index) {
            return this.lat_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public List<Long> getLonList() {
            return this.lon_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public int getLonCount() {
            return this.lon_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
        public long getLon(int index) {
            return this.lon_.getLong(index);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasId()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt64(1, this.id_);
            }
            if (getKeysList().size() > 0) {
                output.writeUInt32NoTag(18);
                output.writeUInt32NoTag(this.keysMemoizedSerializedSize);
            }
            for (int i = 0; i < this.keys_.size(); i++) {
                output.writeUInt32NoTag(this.keys_.getInt(i));
            }
            if (getValsList().size() > 0) {
                output.writeUInt32NoTag(26);
                output.writeUInt32NoTag(this.valsMemoizedSerializedSize);
            }
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                output.writeUInt32NoTag(this.vals_.getInt(i2));
            }
            int i3 = this.bitField0_;
            if ((i3 & 2) != 0) {
                output.writeMessage(4, getInfo());
            }
            if (getRefsList().size() > 0) {
                output.writeUInt32NoTag(66);
                output.writeUInt32NoTag(this.refsMemoizedSerializedSize);
            }
            for (int i4 = 0; i4 < this.refs_.size(); i4++) {
                output.writeSInt64NoTag(this.refs_.getLong(i4));
            }
            if (getLatList().size() > 0) {
                output.writeUInt32NoTag(74);
                output.writeUInt32NoTag(this.latMemoizedSerializedSize);
            }
            for (int i5 = 0; i5 < this.lat_.size(); i5++) {
                output.writeSInt64NoTag(this.lat_.getLong(i5));
            }
            if (getLonList().size() > 0) {
                output.writeUInt32NoTag(82);
                output.writeUInt32NoTag(this.lonMemoizedSerializedSize);
            }
            for (int i6 = 0; i6 < this.lon_.size(); i6++) {
                output.writeSInt64NoTag(this.lon_.getLong(i6));
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeInt64Size(1, this.id_) : 0;
            int dataSize = 0;
            for (int i = 0; i < this.keys_.size(); i++) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(this.keys_.getInt(i));
            }
            int size3 = size2 + dataSize;
            if (!getKeysList().isEmpty()) {
                size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
            }
            this.keysMemoizedSerializedSize = dataSize;
            int dataSize2 = 0;
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                dataSize2 += CodedOutputStream.computeUInt32SizeNoTag(this.vals_.getInt(i2));
            }
            int size4 = size3 + dataSize2;
            if (!getValsList().isEmpty()) {
                size4 = size4 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize2);
            }
            this.valsMemoizedSerializedSize = dataSize2;
            int dataSize3 = this.bitField0_;
            if ((dataSize3 & 2) != 0) {
                size4 += CodedOutputStream.computeMessageSize(4, getInfo());
            }
            int dataSize4 = 0;
            for (int i3 = 0; i3 < this.refs_.size(); i3++) {
                dataSize4 += CodedOutputStream.computeSInt64SizeNoTag(this.refs_.getLong(i3));
            }
            int size5 = size4 + dataSize4;
            if (!getRefsList().isEmpty()) {
                size5 = size5 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize4);
            }
            this.refsMemoizedSerializedSize = dataSize4;
            int dataSize5 = 0;
            for (int i4 = 0; i4 < this.lat_.size(); i4++) {
                dataSize5 += CodedOutputStream.computeSInt64SizeNoTag(this.lat_.getLong(i4));
            }
            int size6 = size5 + dataSize5;
            if (!getLatList().isEmpty()) {
                size6 = size6 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize5);
            }
            this.latMemoizedSerializedSize = dataSize5;
            int dataSize6 = 0;
            for (int i5 = 0; i5 < this.lon_.size(); i5++) {
                dataSize6 += CodedOutputStream.computeSInt64SizeNoTag(this.lon_.getLong(i5));
            }
            int size7 = size6 + dataSize6;
            if (!getLonList().isEmpty()) {
                size7 = size7 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize6);
            }
            this.lonMemoizedSerializedSize = dataSize6;
            int size8 = size7 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size8;
            return size8;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Way)) {
                return super.equals(obj);
            }
            Way other = (Way) obj;
            if (hasId() != other.hasId()) {
                return false;
            }
            if ((!hasId() || getId() == other.getId()) && getKeysList().equals(other.getKeysList()) && getValsList().equals(other.getValsList()) && hasInfo() == other.hasInfo()) {
                return (!hasInfo() || getInfo().equals(other.getInfo())) && getRefsList().equals(other.getRefsList()) && getLatList().equals(other.getLatList()) && getLonList().equals(other.getLonList()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasId()) {
                hash = (((hash * 37) + 1) * 53) + Internal.hashLong(getId());
            }
            if (getKeysCount() > 0) {
                hash = (((hash * 37) + 2) * 53) + getKeysList().hashCode();
            }
            if (getValsCount() > 0) {
                hash = (((hash * 37) + 3) * 53) + getValsList().hashCode();
            }
            if (hasInfo()) {
                hash = (((hash * 37) + 4) * 53) + getInfo().hashCode();
            }
            if (getRefsCount() > 0) {
                hash = (((hash * 37) + 8) * 53) + getRefsList().hashCode();
            }
            if (getLatCount() > 0) {
                hash = (((hash * 37) + 9) * 53) + getLatList().hashCode();
            }
            if (getLonCount() > 0) {
                hash = (((hash * 37) + 10) * 53) + getLonList().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static Way parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Way parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Way parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Way parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Way parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Way parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Way parseFrom(InputStream input) throws IOException {
            return (Way) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Way parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Way) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Way parseDelimitedFrom(InputStream input) throws IOException {
            return (Way) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Way parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Way) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Way parseFrom(CodedInputStream input) throws IOException {
            return (Way) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Way parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Way) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Way prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements WayOrBuilder {
            private int bitField0_;
            private long id_;
            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> infoBuilder_;
            private Info info_;
            private Internal.IntList keys_;
            private Internal.LongList lat_;
            private Internal.LongList lon_;
            private Internal.LongList refs_;
            private Internal.IntList vals_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_Way_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_Way_fieldAccessorTable.ensureFieldAccessorsInitialized(Way.class, Builder.class);
            }

            private Builder() {
                this.keys_ = Way.emptyIntList();
                this.vals_ = Way.emptyIntList();
                this.refs_ = Way.emptyLongList();
                this.lat_ = Way.emptyLongList();
                this.lon_ = Way.emptyLongList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.keys_ = Way.emptyIntList();
                this.vals_ = Way.emptyIntList();
                this.refs_ = Way.emptyLongList();
                this.lat_ = Way.emptyLongList();
                this.lon_ = Way.emptyLongList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (Way.alwaysUseFieldBuilders) {
                    getInfoFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= -2;
                this.keys_ = Way.emptyIntList();
                this.bitField0_ &= -3;
                this.vals_ = Way.emptyIntList();
                this.bitField0_ &= -5;
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                this.refs_ = Way.emptyLongList();
                this.bitField0_ &= -17;
                this.lat_ = Way.emptyLongList();
                this.bitField0_ &= -33;
                this.lon_ = Way.emptyLongList();
                this.bitField0_ &= -65;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_Way_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public Way getDefaultInstanceForType() {
                return Way.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Way build() {
                Way result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Way buildPartial() {
                Way result = new Way(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.id_ = this.id_;
                    to_bitField0_ = 0 | 1;
                }
                if ((this.bitField0_ & 2) != 0) {
                    this.keys_.makeImmutable();
                    this.bitField0_ &= -3;
                }
                result.keys_ = this.keys_;
                if ((this.bitField0_ & 4) != 0) {
                    this.vals_.makeImmutable();
                    this.bitField0_ &= -5;
                }
                result.vals_ = this.vals_;
                if ((from_bitField0_ & 8) != 0) {
                    if (this.infoBuilder_ == null) {
                        result.info_ = this.info_;
                    } else {
                        result.info_ = (Info) this.infoBuilder_.build();
                    }
                    to_bitField0_ |= 2;
                }
                if ((this.bitField0_ & 16) != 0) {
                    this.refs_.makeImmutable();
                    this.bitField0_ &= -17;
                }
                result.refs_ = this.refs_;
                if ((this.bitField0_ & 32) != 0) {
                    this.lat_.makeImmutable();
                    this.bitField0_ &= -33;
                }
                result.lat_ = this.lat_;
                if ((this.bitField0_ & 64) != 0) {
                    this.lon_.makeImmutable();
                    this.bitField0_ &= -65;
                }
                result.lon_ = this.lon_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof Way) {
                    return mergeFrom((Way) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Way other) {
                if (other == Way.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    setId(other.getId());
                }
                if (!other.keys_.isEmpty()) {
                    if (this.keys_.isEmpty()) {
                        this.keys_ = other.keys_;
                        this.bitField0_ &= -3;
                    } else {
                        ensureKeysIsMutable();
                        this.keys_.addAll(other.keys_);
                    }
                    onChanged();
                }
                if (!other.vals_.isEmpty()) {
                    if (this.vals_.isEmpty()) {
                        this.vals_ = other.vals_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureValsIsMutable();
                        this.vals_.addAll(other.vals_);
                    }
                    onChanged();
                }
                if (other.hasInfo()) {
                    mergeInfo(other.getInfo());
                }
                if (!other.refs_.isEmpty()) {
                    if (this.refs_.isEmpty()) {
                        this.refs_ = other.refs_;
                        this.bitField0_ &= -17;
                    } else {
                        ensureRefsIsMutable();
                        this.refs_.addAll(other.refs_);
                    }
                    onChanged();
                }
                if (!other.lat_.isEmpty()) {
                    if (this.lat_.isEmpty()) {
                        this.lat_ = other.lat_;
                        this.bitField0_ &= -33;
                    } else {
                        ensureLatIsMutable();
                        this.lat_.addAll(other.lat_);
                    }
                    onChanged();
                }
                if (!other.lon_.isEmpty()) {
                    if (this.lon_.isEmpty()) {
                        this.lon_ = other.lon_;
                        this.bitField0_ &= -65;
                    } else {
                        ensureLonIsMutable();
                        this.lon_.addAll(other.lon_);
                    }
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                if (!hasId()) {
                    return false;
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Way parsedMessage = null;
                try {
                    try {
                        parsedMessage = Way.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public boolean hasId() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public long getId() {
                return this.id_;
            }

            public Builder setId(long value) {
                this.bitField0_ |= 1;
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.bitField0_ &= -2;
                this.id_ = 0L;
                onChanged();
                return this;
            }

            private void ensureKeysIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.keys_ = Way.mutableCopy(this.keys_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public List<Integer> getKeysList() {
                return (this.bitField0_ & 2) != 0 ? Collections.unmodifiableList(this.keys_) : this.keys_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getKeysCount() {
                return this.keys_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getKeys(int index) {
                return this.keys_.getInt(index);
            }

            public Builder setKeys(int index, int value) {
                ensureKeysIsMutable();
                this.keys_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addKeys(int value) {
                ensureKeysIsMutable();
                this.keys_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllKeys(Iterable<? extends Integer> values) {
                ensureKeysIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.keys_);
                onChanged();
                return this;
            }

            public Builder clearKeys() {
                this.keys_ = Way.emptyIntList();
                this.bitField0_ &= -3;
                onChanged();
                return this;
            }

            private void ensureValsIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.vals_ = Way.mutableCopy(this.vals_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public List<Integer> getValsList() {
                return (this.bitField0_ & 4) != 0 ? Collections.unmodifiableList(this.vals_) : this.vals_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getValsCount() {
                return this.vals_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getVals(int index) {
                return this.vals_.getInt(index);
            }

            public Builder setVals(int index, int value) {
                ensureValsIsMutable();
                this.vals_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addVals(int value) {
                ensureValsIsMutable();
                this.vals_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllVals(Iterable<? extends Integer> values) {
                ensureValsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.vals_);
                onChanged();
                return this;
            }

            public Builder clearVals() {
                this.vals_ = Way.emptyIntList();
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public boolean hasInfo() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public Info getInfo() {
                if (this.infoBuilder_ == null) {
                    return this.info_ == null ? Info.getDefaultInstance() : this.info_;
                }
                return (Info) this.infoBuilder_.getMessage();
            }

            public Builder setInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.info_ = value;
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setInfo(Info.Builder builderForValue) {
                if (this.infoBuilder_ == null) {
                    this.info_ = builderForValue.build();
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if ((this.bitField0_ & 8) != 0 && this.info_ != null && this.info_ != Info.getDefaultInstance()) {
                        this.info_ = Info.newBuilder(this.info_).mergeFrom(value).buildPartial();
                    } else {
                        this.info_ = value;
                    }
                    onChanged();
                } else {
                    this.infoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearInfo() {
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                    onChanged();
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public Info.Builder getInfoBuilder() {
                this.bitField0_ |= 8;
                onChanged();
                return (Info.Builder) getInfoFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public InfoOrBuilder getInfoOrBuilder() {
                if (this.infoBuilder_ != null) {
                    return (InfoOrBuilder) this.infoBuilder_.getMessageOrBuilder();
                }
                return this.info_ == null ? Info.getDefaultInstance() : this.info_;
            }

            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> getInfoFieldBuilder() {
                if (this.infoBuilder_ == null) {
                    this.infoBuilder_ = new SingleFieldBuilderV3<>(getInfo(), getParentForChildren(), isClean());
                    this.info_ = null;
                }
                return this.infoBuilder_;
            }

            private void ensureRefsIsMutable() {
                if ((this.bitField0_ & 16) == 0) {
                    this.refs_ = Way.mutableCopy(this.refs_);
                    this.bitField0_ |= 16;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public List<Long> getRefsList() {
                return (this.bitField0_ & 16) != 0 ? Collections.unmodifiableList(this.refs_) : this.refs_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getRefsCount() {
                return this.refs_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public long getRefs(int index) {
                return this.refs_.getLong(index);
            }

            public Builder setRefs(int index, long value) {
                ensureRefsIsMutable();
                this.refs_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addRefs(long value) {
                ensureRefsIsMutable();
                this.refs_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllRefs(Iterable<? extends Long> values) {
                ensureRefsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.refs_);
                onChanged();
                return this;
            }

            public Builder clearRefs() {
                this.refs_ = Way.emptyLongList();
                this.bitField0_ &= -17;
                onChanged();
                return this;
            }

            private void ensureLatIsMutable() {
                if ((this.bitField0_ & 32) == 0) {
                    this.lat_ = Way.mutableCopy(this.lat_);
                    this.bitField0_ |= 32;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public List<Long> getLatList() {
                return (this.bitField0_ & 32) != 0 ? Collections.unmodifiableList(this.lat_) : this.lat_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getLatCount() {
                return this.lat_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public long getLat(int index) {
                return this.lat_.getLong(index);
            }

            public Builder setLat(int index, long value) {
                ensureLatIsMutable();
                this.lat_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addLat(long value) {
                ensureLatIsMutable();
                this.lat_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllLat(Iterable<? extends Long> values) {
                ensureLatIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.lat_);
                onChanged();
                return this;
            }

            public Builder clearLat() {
                this.lat_ = Way.emptyLongList();
                this.bitField0_ &= -33;
                onChanged();
                return this;
            }

            private void ensureLonIsMutable() {
                if ((this.bitField0_ & 64) == 0) {
                    this.lon_ = Way.mutableCopy(this.lon_);
                    this.bitField0_ |= 64;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public List<Long> getLonList() {
                return (this.bitField0_ & 64) != 0 ? Collections.unmodifiableList(this.lon_) : this.lon_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public int getLonCount() {
                return this.lon_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.WayOrBuilder
            public long getLon(int index) {
                return this.lon_.getLong(index);
            }

            public Builder setLon(int index, long value) {
                ensureLonIsMutable();
                this.lon_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addLon(long value) {
                ensureLonIsMutable();
                this.lon_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllLon(Iterable<? extends Long> values) {
                ensureLonIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.lon_);
                onChanged();
                return this;
            }

            public Builder clearLon() {
                this.lon_ = Way.emptyLongList();
                this.bitField0_ &= -65;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static Way getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Way> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<Way> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public Way getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class Relation extends GeneratedMessageV3 implements RelationOrBuilder {
        public static final int ID_FIELD_NUMBER = 1;
        public static final int INFO_FIELD_NUMBER = 4;
        public static final int KEYS_FIELD_NUMBER = 2;
        public static final int MEMIDS_FIELD_NUMBER = 9;
        public static final int ROLES_SID_FIELD_NUMBER = 8;
        public static final int TYPES_FIELD_NUMBER = 10;
        public static final int VALS_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private long id_;
        private Info info_;
        private int keysMemoizedSerializedSize;
        private Internal.IntList keys_;
        private int memidsMemoizedSerializedSize;
        private Internal.LongList memids_;
        private byte memoizedIsInitialized;
        private int rolesSidMemoizedSerializedSize;
        private Internal.IntList rolesSid_;
        private int typesMemoizedSerializedSize;
        private List<Integer> types_;
        private int valsMemoizedSerializedSize;
        private Internal.IntList vals_;
        private static final Internal.ListAdapter.Converter<Integer, MemberType> types_converter_ = new Internal.ListAdapter.Converter<Integer, MemberType>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Relation.1
            @Override // com.google.protobuf.Internal.ListAdapter.Converter
            public MemberType convert(Integer from) {
                MemberType result = MemberType.valueOf(from.intValue());
                return result == null ? MemberType.NODE : result;
            }
        };
        private static final Relation DEFAULT_INSTANCE = new Relation();

        @Deprecated
        public static final Parser<Relation> PARSER = new AbstractParser<Relation>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Relation.2
            @Override // com.google.protobuf.Parser
            public Relation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Relation(input, extensionRegistry);
            }
        };

        private Relation(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.rolesSidMemoizedSerializedSize = -1;
            this.memidsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
        }

        private Relation() {
            this.keysMemoizedSerializedSize = -1;
            this.valsMemoizedSerializedSize = -1;
            this.rolesSidMemoizedSerializedSize = -1;
            this.memidsMemoizedSerializedSize = -1;
            this.memoizedIsInitialized = (byte) -1;
            this.keys_ = emptyIntList();
            this.vals_ = emptyIntList();
            this.rolesSid_ = emptyIntList();
            this.memids_ = emptyLongList();
            this.types_ = Collections.emptyList();
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Relation();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Relation(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            boolean done = false;
            while (!done) {
                try {
                    try {
                        try {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    this.bitField0_ |= 1;
                                    this.id_ = input.readInt64();
                                    break;
                                case 16:
                                    int length = mutable_bitField0_ & 2;
                                    if (length == 0) {
                                        this.keys_ = newIntList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    this.keys_.addInt(input.readUInt32());
                                    break;
                                case 18:
                                    int length2 = input.readRawVarint32();
                                    int limit = input.pushLimit(length2);
                                    if ((mutable_bitField0_ & 2) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.keys_ = newIntList();
                                        mutable_bitField0_ |= 2;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.keys_.addInt(input.readUInt32());
                                    }
                                    input.popLimit(limit);
                                    break;
                                case 24:
                                    int length3 = mutable_bitField0_ & 4;
                                    if (length3 == 0) {
                                        this.vals_ = newIntList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    this.vals_.addInt(input.readUInt32());
                                    break;
                                case 26:
                                    int length4 = input.readRawVarint32();
                                    int limit2 = input.pushLimit(length4);
                                    if ((mutable_bitField0_ & 4) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.vals_ = newIntList();
                                        mutable_bitField0_ |= 4;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.vals_.addInt(input.readUInt32());
                                    }
                                    input.popLimit(limit2);
                                    break;
                                case 34:
                                    Info.Builder subBuilder = (this.bitField0_ & 2) != 0 ? this.info_.toBuilder() : null;
                                    this.info_ = (Info) input.readMessage(Info.PARSER, extensionRegistry);
                                    if (subBuilder != null) {
                                        subBuilder.mergeFrom(this.info_);
                                        this.info_ = subBuilder.buildPartial();
                                    }
                                    this.bitField0_ |= 2;
                                    break;
                                case 64:
                                    int length5 = mutable_bitField0_ & 16;
                                    if (length5 == 0) {
                                        this.rolesSid_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    this.rolesSid_.addInt(input.readInt32());
                                    break;
                                case 66:
                                    int length6 = input.readRawVarint32();
                                    int limit3 = input.pushLimit(length6);
                                    if ((mutable_bitField0_ & 16) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.rolesSid_ = newIntList();
                                        mutable_bitField0_ |= 16;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.rolesSid_.addInt(input.readInt32());
                                    }
                                    input.popLimit(limit3);
                                    break;
                                case 72:
                                    int length7 = mutable_bitField0_ & 32;
                                    if (length7 == 0) {
                                        this.memids_ = newLongList();
                                        mutable_bitField0_ |= 32;
                                    }
                                    this.memids_.addLong(input.readSInt64());
                                    break;
                                case 74:
                                    int length8 = input.readRawVarint32();
                                    int limit4 = input.pushLimit(length8);
                                    if ((mutable_bitField0_ & 32) == 0 && input.getBytesUntilLimit() > 0) {
                                        this.memids_ = newLongList();
                                        mutable_bitField0_ |= 32;
                                    }
                                    while (input.getBytesUntilLimit() > 0) {
                                        this.memids_.addLong(input.readSInt64());
                                    }
                                    input.popLimit(limit4);
                                    break;
                                case 80:
                                    int rawValue = input.readEnum();
                                    MemberType value = MemberType.valueOf(rawValue);
                                    if (value == null) {
                                        unknownFields.mergeVarintField(10, rawValue);
                                    } else {
                                        if ((mutable_bitField0_ & 64) == 0) {
                                            this.types_ = new ArrayList();
                                            mutable_bitField0_ |= 64;
                                        }
                                        this.types_.add(Integer.valueOf(rawValue));
                                    }
                                    break;
                                case 82:
                                    int length9 = input.readRawVarint32();
                                    int oldLimit = input.pushLimit(length9);
                                    while (input.getBytesUntilLimit() > 0) {
                                        int rawValue2 = input.readEnum();
                                        MemberType value2 = MemberType.valueOf(rawValue2);
                                        if (value2 == null) {
                                            unknownFields.mergeVarintField(10, rawValue2);
                                        } else {
                                            if ((mutable_bitField0_ & 64) == 0) {
                                                this.types_ = new ArrayList();
                                                mutable_bitField0_ |= 64;
                                            }
                                            this.types_.add(Integer.valueOf(rawValue2));
                                        }
                                    }
                                    input.popLimit(oldLimit);
                                    break;
                                default:
                                    if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                                    break;
                            }
                        } catch (IOException e) {
                            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                        }
                    } catch (InvalidProtocolBufferException e2) {
                        throw e2.setUnfinishedMessage(this);
                    }
                } finally {
                    if ((mutable_bitField0_ & 2) != 0) {
                        this.keys_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 4) != 0) {
                        this.vals_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 16) != 0) {
                        this.rolesSid_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 32) != 0) {
                        this.memids_.makeImmutable();
                    }
                    if ((mutable_bitField0_ & 64) != 0) {
                        this.types_ = Collections.unmodifiableList(this.types_);
                    }
                    this.unknownFields = unknownFields.build();
                    makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Osmformat.internal_static_OSMPBF_Relation_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Osmformat.internal_static_OSMPBF_Relation_fieldAccessorTable.ensureFieldAccessorsInitialized(Relation.class, Builder.class);
        }

        public enum MemberType implements ProtocolMessageEnum {
            NODE(0),
            WAY(1),
            RELATION(2);

            public static final int NODE_VALUE = 0;
            public static final int RELATION_VALUE = 2;
            public static final int WAY_VALUE = 1;
            private final int value;
            private static final Internal.EnumLiteMap<MemberType> internalValueMap = new Internal.EnumLiteMap<MemberType>() { // from class: org.openstreetmap.osmosis.osmbinary.Osmformat.Relation.MemberType.1
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public MemberType findValueByNumber(int number) {
                    return MemberType.forNumber(number);
                }
            };
            private static final MemberType[] VALUES = values();

            @Override // com.google.protobuf.ProtocolMessageEnum, com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static MemberType valueOf(int value) {
                return forNumber(value);
            }

            public static MemberType forNumber(int value) {
                switch (value) {
                    case 0:
                        return NODE;
                    case 1:
                        return WAY;
                    case 2:
                        return RELATION;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<MemberType> internalGetValueMap() {
                return internalValueMap;
            }

            @Override // com.google.protobuf.ProtocolMessageEnum
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(ordinal());
            }

            @Override // com.google.protobuf.ProtocolMessageEnum
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return Relation.getDescriptor().getEnumTypes().get(0);
            }

            public static MemberType valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            MemberType(int value) {
                this.value = value;
            }
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public boolean hasId() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public long getId() {
            return this.id_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public List<Integer> getKeysList() {
            return this.keys_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getKeysCount() {
            return this.keys_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getKeys(int index) {
            return this.keys_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public List<Integer> getValsList() {
            return this.vals_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getValsCount() {
            return this.vals_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getVals(int index) {
            return this.vals_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public boolean hasInfo() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public Info getInfo() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public InfoOrBuilder getInfoOrBuilder() {
            return this.info_ == null ? Info.getDefaultInstance() : this.info_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public List<Integer> getRolesSidList() {
            return this.rolesSid_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getRolesSidCount() {
            return this.rolesSid_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getRolesSid(int index) {
            return this.rolesSid_.getInt(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public List<Long> getMemidsList() {
            return this.memids_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getMemidsCount() {
            return this.memids_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public long getMemids(int index) {
            return this.memids_.getLong(index);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public List<MemberType> getTypesList() {
            return new Internal.ListAdapter(this.types_, types_converter_);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public int getTypesCount() {
            return this.types_.size();
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
        public MemberType getTypes(int index) {
            return types_converter_.convert(this.types_.get(index));
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            if (!hasId()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            getSerializedSize();
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt64(1, this.id_);
            }
            if (getKeysList().size() > 0) {
                output.writeUInt32NoTag(18);
                output.writeUInt32NoTag(this.keysMemoizedSerializedSize);
            }
            for (int i = 0; i < this.keys_.size(); i++) {
                output.writeUInt32NoTag(this.keys_.getInt(i));
            }
            if (getValsList().size() > 0) {
                output.writeUInt32NoTag(26);
                output.writeUInt32NoTag(this.valsMemoizedSerializedSize);
            }
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                output.writeUInt32NoTag(this.vals_.getInt(i2));
            }
            int i3 = this.bitField0_;
            if ((i3 & 2) != 0) {
                output.writeMessage(4, getInfo());
            }
            if (getRolesSidList().size() > 0) {
                output.writeUInt32NoTag(66);
                output.writeUInt32NoTag(this.rolesSidMemoizedSerializedSize);
            }
            for (int i4 = 0; i4 < this.rolesSid_.size(); i4++) {
                output.writeInt32NoTag(this.rolesSid_.getInt(i4));
            }
            if (getMemidsList().size() > 0) {
                output.writeUInt32NoTag(74);
                output.writeUInt32NoTag(this.memidsMemoizedSerializedSize);
            }
            for (int i5 = 0; i5 < this.memids_.size(); i5++) {
                output.writeSInt64NoTag(this.memids_.getLong(i5));
            }
            if (getTypesList().size() > 0) {
                output.writeUInt32NoTag(82);
                output.writeUInt32NoTag(this.typesMemoizedSerializedSize);
            }
            for (int i6 = 0; i6 < this.types_.size(); i6++) {
                output.writeEnumNoTag(this.types_.get(i6).intValue());
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeInt64Size(1, this.id_) : 0;
            int dataSize = 0;
            for (int i = 0; i < this.keys_.size(); i++) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(this.keys_.getInt(i));
            }
            int size3 = size2 + dataSize;
            if (!getKeysList().isEmpty()) {
                size3 = size3 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize);
            }
            this.keysMemoizedSerializedSize = dataSize;
            int dataSize2 = 0;
            for (int i2 = 0; i2 < this.vals_.size(); i2++) {
                dataSize2 += CodedOutputStream.computeUInt32SizeNoTag(this.vals_.getInt(i2));
            }
            int size4 = size3 + dataSize2;
            if (!getValsList().isEmpty()) {
                size4 = size4 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize2);
            }
            this.valsMemoizedSerializedSize = dataSize2;
            int dataSize3 = this.bitField0_;
            if ((dataSize3 & 2) != 0) {
                size4 += CodedOutputStream.computeMessageSize(4, getInfo());
            }
            int dataSize4 = 0;
            for (int i3 = 0; i3 < this.rolesSid_.size(); i3++) {
                dataSize4 += CodedOutputStream.computeInt32SizeNoTag(this.rolesSid_.getInt(i3));
            }
            int size5 = size4 + dataSize4;
            if (!getRolesSidList().isEmpty()) {
                size5 = size5 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize4);
            }
            this.rolesSidMemoizedSerializedSize = dataSize4;
            int dataSize5 = 0;
            for (int i4 = 0; i4 < this.memids_.size(); i4++) {
                dataSize5 += CodedOutputStream.computeSInt64SizeNoTag(this.memids_.getLong(i4));
            }
            int size6 = size5 + dataSize5;
            if (!getMemidsList().isEmpty()) {
                size6 = size6 + 1 + CodedOutputStream.computeInt32SizeNoTag(dataSize5);
            }
            this.memidsMemoizedSerializedSize = dataSize5;
            int dataSize6 = 0;
            for (int i5 = 0; i5 < this.types_.size(); i5++) {
                dataSize6 += CodedOutputStream.computeEnumSizeNoTag(this.types_.get(i5).intValue());
            }
            int size7 = size6 + dataSize6;
            if (!getTypesList().isEmpty()) {
                size7 = size7 + 1 + CodedOutputStream.computeUInt32SizeNoTag(dataSize6);
            }
            this.typesMemoizedSerializedSize = dataSize6;
            int size8 = size7 + this.unknownFields.getSerializedSize();
            this.memoizedSize = size8;
            return size8;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Relation)) {
                return super.equals(obj);
            }
            Relation other = (Relation) obj;
            if (hasId() != other.hasId()) {
                return false;
            }
            if ((!hasId() || getId() == other.getId()) && getKeysList().equals(other.getKeysList()) && getValsList().equals(other.getValsList()) && hasInfo() == other.hasInfo()) {
                return (!hasInfo() || getInfo().equals(other.getInfo())) && getRolesSidList().equals(other.getRolesSidList()) && getMemidsList().equals(other.getMemidsList()) && this.types_.equals(other.types_) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasId()) {
                hash = (((hash * 37) + 1) * 53) + Internal.hashLong(getId());
            }
            if (getKeysCount() > 0) {
                hash = (((hash * 37) + 2) * 53) + getKeysList().hashCode();
            }
            if (getValsCount() > 0) {
                hash = (((hash * 37) + 3) * 53) + getValsList().hashCode();
            }
            if (hasInfo()) {
                hash = (((hash * 37) + 4) * 53) + getInfo().hashCode();
            }
            if (getRolesSidCount() > 0) {
                hash = (((hash * 37) + 8) * 53) + getRolesSidList().hashCode();
            }
            if (getMemidsCount() > 0) {
                hash = (((hash * 37) + 9) * 53) + getMemidsList().hashCode();
            }
            if (getTypesCount() > 0) {
                hash = (((hash * 37) + 10) * 53) + this.types_.hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static Relation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Relation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Relation parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Relation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Relation parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Relation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Relation parseFrom(InputStream input) throws IOException {
            return (Relation) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Relation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Relation) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Relation parseDelimitedFrom(InputStream input) throws IOException {
            return (Relation) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Relation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Relation) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Relation parseFrom(CodedInputStream input) throws IOException {
            return (Relation) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Relation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Relation) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Relation prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.GeneratedMessageV3
        public Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements RelationOrBuilder {
            private int bitField0_;
            private long id_;
            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> infoBuilder_;
            private Info info_;
            private Internal.IntList keys_;
            private Internal.LongList memids_;
            private Internal.IntList rolesSid_;
            private List<Integer> types_;
            private Internal.IntList vals_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Osmformat.internal_static_OSMPBF_Relation_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Osmformat.internal_static_OSMPBF_Relation_fieldAccessorTable.ensureFieldAccessorsInitialized(Relation.class, Builder.class);
            }

            private Builder() {
                this.keys_ = Relation.emptyIntList();
                this.vals_ = Relation.emptyIntList();
                this.rolesSid_ = Relation.emptyIntList();
                this.memids_ = Relation.emptyLongList();
                this.types_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.keys_ = Relation.emptyIntList();
                this.vals_ = Relation.emptyIntList();
                this.rolesSid_ = Relation.emptyIntList();
                this.memids_ = Relation.emptyLongList();
                this.types_ = Collections.emptyList();
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (Relation.alwaysUseFieldBuilders) {
                    getInfoFieldBuilder();
                }
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= -2;
                this.keys_ = Relation.emptyIntList();
                this.bitField0_ &= -3;
                this.vals_ = Relation.emptyIntList();
                this.bitField0_ &= -5;
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                this.rolesSid_ = Relation.emptyIntList();
                this.bitField0_ &= -17;
                this.memids_ = Relation.emptyLongList();
                this.bitField0_ &= -33;
                this.types_ = Collections.emptyList();
                this.bitField0_ &= -65;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Osmformat.internal_static_OSMPBF_Relation_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public Relation getDefaultInstanceForType() {
                return Relation.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Relation build() {
                Relation result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Relation buildPartial() {
                Relation result = new Relation(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.id_ = this.id_;
                    to_bitField0_ = 0 | 1;
                }
                if ((this.bitField0_ & 2) != 0) {
                    this.keys_.makeImmutable();
                    this.bitField0_ &= -3;
                }
                result.keys_ = this.keys_;
                if ((this.bitField0_ & 4) != 0) {
                    this.vals_.makeImmutable();
                    this.bitField0_ &= -5;
                }
                result.vals_ = this.vals_;
                if ((from_bitField0_ & 8) != 0) {
                    if (this.infoBuilder_ == null) {
                        result.info_ = this.info_;
                    } else {
                        result.info_ = (Info) this.infoBuilder_.build();
                    }
                    to_bitField0_ |= 2;
                }
                if ((this.bitField0_ & 16) != 0) {
                    this.rolesSid_.makeImmutable();
                    this.bitField0_ &= -17;
                }
                result.rolesSid_ = this.rolesSid_;
                if ((this.bitField0_ & 32) != 0) {
                    this.memids_.makeImmutable();
                    this.bitField0_ &= -33;
                }
                result.memids_ = this.memids_;
                if ((this.bitField0_ & 64) != 0) {
                    this.types_ = Collections.unmodifiableList(this.types_);
                    this.bitField0_ &= -65;
                }
                result.types_ = this.types_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder
            /* JADX INFO: renamed from: clone */
            public Builder mo111clone() {
                return (Builder) super.mo111clone();
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(Message other) {
                if (other instanceof Relation) {
                    return mergeFrom((Relation) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Relation other) {
                if (other == Relation.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    setId(other.getId());
                }
                if (!other.keys_.isEmpty()) {
                    if (this.keys_.isEmpty()) {
                        this.keys_ = other.keys_;
                        this.bitField0_ &= -3;
                    } else {
                        ensureKeysIsMutable();
                        this.keys_.addAll(other.keys_);
                    }
                    onChanged();
                }
                if (!other.vals_.isEmpty()) {
                    if (this.vals_.isEmpty()) {
                        this.vals_ = other.vals_;
                        this.bitField0_ &= -5;
                    } else {
                        ensureValsIsMutable();
                        this.vals_.addAll(other.vals_);
                    }
                    onChanged();
                }
                if (other.hasInfo()) {
                    mergeInfo(other.getInfo());
                }
                if (!other.rolesSid_.isEmpty()) {
                    if (this.rolesSid_.isEmpty()) {
                        this.rolesSid_ = other.rolesSid_;
                        this.bitField0_ &= -17;
                    } else {
                        ensureRolesSidIsMutable();
                        this.rolesSid_.addAll(other.rolesSid_);
                    }
                    onChanged();
                }
                if (!other.memids_.isEmpty()) {
                    if (this.memids_.isEmpty()) {
                        this.memids_ = other.memids_;
                        this.bitField0_ &= -33;
                    } else {
                        ensureMemidsIsMutable();
                        this.memids_.addAll(other.memids_);
                    }
                    onChanged();
                }
                if (!other.types_.isEmpty()) {
                    if (this.types_.isEmpty()) {
                        this.types_ = other.types_;
                        this.bitField0_ &= -65;
                    } else {
                        ensureTypesIsMutable();
                        this.types_.addAll(other.types_);
                    }
                    onChanged();
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                if (!hasId()) {
                    return false;
                }
                return true;
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Relation parsedMessage = null;
                try {
                    try {
                        parsedMessage = Relation.PARSER.parsePartialFrom(input, extensionRegistry);
                        return this;
                    } catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public boolean hasId() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public long getId() {
                return this.id_;
            }

            public Builder setId(long value) {
                this.bitField0_ |= 1;
                this.id_ = value;
                onChanged();
                return this;
            }

            public Builder clearId() {
                this.bitField0_ &= -2;
                this.id_ = 0L;
                onChanged();
                return this;
            }

            private void ensureKeysIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.keys_ = Relation.mutableCopy(this.keys_);
                    this.bitField0_ |= 2;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public List<Integer> getKeysList() {
                return (this.bitField0_ & 2) != 0 ? Collections.unmodifiableList(this.keys_) : this.keys_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getKeysCount() {
                return this.keys_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getKeys(int index) {
                return this.keys_.getInt(index);
            }

            public Builder setKeys(int index, int value) {
                ensureKeysIsMutable();
                this.keys_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addKeys(int value) {
                ensureKeysIsMutable();
                this.keys_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllKeys(Iterable<? extends Integer> values) {
                ensureKeysIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.keys_);
                onChanged();
                return this;
            }

            public Builder clearKeys() {
                this.keys_ = Relation.emptyIntList();
                this.bitField0_ &= -3;
                onChanged();
                return this;
            }

            private void ensureValsIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.vals_ = Relation.mutableCopy(this.vals_);
                    this.bitField0_ |= 4;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public List<Integer> getValsList() {
                return (this.bitField0_ & 4) != 0 ? Collections.unmodifiableList(this.vals_) : this.vals_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getValsCount() {
                return this.vals_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getVals(int index) {
                return this.vals_.getInt(index);
            }

            public Builder setVals(int index, int value) {
                ensureValsIsMutable();
                this.vals_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addVals(int value) {
                ensureValsIsMutable();
                this.vals_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllVals(Iterable<? extends Integer> values) {
                ensureValsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.vals_);
                onChanged();
                return this;
            }

            public Builder clearVals() {
                this.vals_ = Relation.emptyIntList();
                this.bitField0_ &= -5;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public boolean hasInfo() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public Info getInfo() {
                if (this.infoBuilder_ == null) {
                    return this.info_ == null ? Info.getDefaultInstance() : this.info_;
                }
                return (Info) this.infoBuilder_.getMessage();
            }

            public Builder setInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.info_ = value;
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setInfo(Info.Builder builderForValue) {
                if (this.infoBuilder_ == null) {
                    this.info_ = builderForValue.build();
                    onChanged();
                } else {
                    this.infoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeInfo(Info value) {
                if (this.infoBuilder_ == null) {
                    if ((this.bitField0_ & 8) != 0 && this.info_ != null && this.info_ != Info.getDefaultInstance()) {
                        this.info_ = Info.newBuilder(this.info_).mergeFrom(value).buildPartial();
                    } else {
                        this.info_ = value;
                    }
                    onChanged();
                } else {
                    this.infoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearInfo() {
                if (this.infoBuilder_ == null) {
                    this.info_ = null;
                    onChanged();
                } else {
                    this.infoBuilder_.clear();
                }
                this.bitField0_ &= -9;
                return this;
            }

            public Info.Builder getInfoBuilder() {
                this.bitField0_ |= 8;
                onChanged();
                return (Info.Builder) getInfoFieldBuilder().getBuilder();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public InfoOrBuilder getInfoOrBuilder() {
                if (this.infoBuilder_ != null) {
                    return (InfoOrBuilder) this.infoBuilder_.getMessageOrBuilder();
                }
                return this.info_ == null ? Info.getDefaultInstance() : this.info_;
            }

            private SingleFieldBuilderV3<Info, Info.Builder, InfoOrBuilder> getInfoFieldBuilder() {
                if (this.infoBuilder_ == null) {
                    this.infoBuilder_ = new SingleFieldBuilderV3<>(getInfo(), getParentForChildren(), isClean());
                    this.info_ = null;
                }
                return this.infoBuilder_;
            }

            private void ensureRolesSidIsMutable() {
                if ((this.bitField0_ & 16) == 0) {
                    this.rolesSid_ = Relation.mutableCopy(this.rolesSid_);
                    this.bitField0_ |= 16;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public List<Integer> getRolesSidList() {
                return (this.bitField0_ & 16) != 0 ? Collections.unmodifiableList(this.rolesSid_) : this.rolesSid_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getRolesSidCount() {
                return this.rolesSid_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getRolesSid(int index) {
                return this.rolesSid_.getInt(index);
            }

            public Builder setRolesSid(int index, int value) {
                ensureRolesSidIsMutable();
                this.rolesSid_.setInt(index, value);
                onChanged();
                return this;
            }

            public Builder addRolesSid(int value) {
                ensureRolesSidIsMutable();
                this.rolesSid_.addInt(value);
                onChanged();
                return this;
            }

            public Builder addAllRolesSid(Iterable<? extends Integer> values) {
                ensureRolesSidIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.rolesSid_);
                onChanged();
                return this;
            }

            public Builder clearRolesSid() {
                this.rolesSid_ = Relation.emptyIntList();
                this.bitField0_ &= -17;
                onChanged();
                return this;
            }

            private void ensureMemidsIsMutable() {
                if ((this.bitField0_ & 32) == 0) {
                    this.memids_ = Relation.mutableCopy(this.memids_);
                    this.bitField0_ |= 32;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public List<Long> getMemidsList() {
                return (this.bitField0_ & 32) != 0 ? Collections.unmodifiableList(this.memids_) : this.memids_;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getMemidsCount() {
                return this.memids_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public long getMemids(int index) {
                return this.memids_.getLong(index);
            }

            public Builder setMemids(int index, long value) {
                ensureMemidsIsMutable();
                this.memids_.setLong(index, value);
                onChanged();
                return this;
            }

            public Builder addMemids(long value) {
                ensureMemidsIsMutable();
                this.memids_.addLong(value);
                onChanged();
                return this;
            }

            public Builder addAllMemids(Iterable<? extends Long> values) {
                ensureMemidsIsMutable();
                AbstractMessageLite.Builder.addAll((Iterable) values, (List) this.memids_);
                onChanged();
                return this;
            }

            public Builder clearMemids() {
                this.memids_ = Relation.emptyLongList();
                this.bitField0_ &= -33;
                onChanged();
                return this;
            }

            private void ensureTypesIsMutable() {
                if ((this.bitField0_ & 64) == 0) {
                    this.types_ = new ArrayList(this.types_);
                    this.bitField0_ |= 64;
                }
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public List<MemberType> getTypesList() {
                return new Internal.ListAdapter(this.types_, Relation.types_converter_);
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public int getTypesCount() {
                return this.types_.size();
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Osmformat.RelationOrBuilder
            public MemberType getTypes(int index) {
                return (MemberType) Relation.types_converter_.convert(this.types_.get(index));
            }

            public Builder setTypes(int index, MemberType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTypesIsMutable();
                this.types_.set(index, Integer.valueOf(value.getNumber()));
                onChanged();
                return this;
            }

            public Builder addTypes(MemberType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTypesIsMutable();
                this.types_.add(Integer.valueOf(value.getNumber()));
                onChanged();
                return this;
            }

            public Builder addAllTypes(Iterable<? extends MemberType> values) {
                ensureTypesIsMutable();
                for (MemberType value : values) {
                    this.types_.add(Integer.valueOf(value.getNumber()));
                }
                onChanged();
                return this;
            }

            public Builder clearTypes() {
                this.types_ = Collections.emptyList();
                this.bitField0_ &= -65;
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.setUnknownFields(unknownFields);
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.Message.Builder
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder) super.mergeUnknownFields(unknownFields);
            }
        }

        public static Relation getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Relation> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<Relation> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public Relation getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = {"\n!src/main/protobuf/osmformat.proto\u0012\u0006OSMPBF\"\u0087\u0002\n\u000bHeaderBlock\u0012 \n\u0004bbox\u0018\u0001 \u0001(\u000b2\u0012.OSMPBF.HeaderBBox\u0012\u0019\n\u0011required_features\u0018\u0004 \u0003(\t\u0012\u0019\n\u0011optional_features\u0018\u0005 \u0003(\t\u0012\u0016\n\u000ewritingprogram\u0018\u0010 \u0001(\t\u0012\u000e\n\u0006source\u0018\u0011 \u0001(\t\u0012%\n\u001dosmosis_replication_timestamp\u0018  \u0001(\u0003\u0012+\n#osmosis_replication_sequence_number\u0018! \u0001(\u0003\u0012$\n\u001cosmosis_replication_base_url\u0018\" \u0001(\t\"F\n\nHeaderBBox\u0012\f\n\u0004left\u0018\u0001 \u0002(\u0012\u0012\r\n\u0005right\u0018\u0002 \u0002(\u0012\u0012\u000b\n\u0003top\u0018\u0003 \u0002(\u0012\u0012\u000e\n\u0006bottom\u0018\u0004 \u0002(\u0012\"Ò\u0001\n\u000ePrimitiveBlock\u0012(\n\u000bstringtable\u0018\u0001 \u0002(\u000b2\u0013.OSMPBF.StringTable\u0012.\n\u000eprimitivegroup\u0018\u0002 \u0003(\u000b2\u0016.OSMPBF.PrimitiveGroup\u0012\u0018\n\u000bgranularity\u0018\u0011 \u0001(\u0005:\u0003100\u0012\u0015\n\nlat_offset\u0018\u0013 \u0001(\u0003:\u00010\u0012\u0015\n\nlon_offset\u0018\u0014 \u0001(\u0003:\u00010\u0012\u001e\n\u0010date_granularity\u0018\u0012 \u0001(\u0005:\u00041000\"·\u0001\n\u000ePrimitiveGroup\u0012\u001b\n\u0005nodes\u0018\u0001 \u0003(\u000b2\f.OSMPBF.Node\u0012!\n\u0005dense\u0018\u0002 \u0001(\u000b2\u0012.OSMPBF.DenseNodes\u0012\u0019\n\u0004ways\u0018\u0003 \u0003(\u000b2\u000b.OSMPBF.Way\u0012#\n\trelations\u0018\u0004 \u0003(\u000b2\u0010.OSMPBF.Relation\u0012%\n\nchangesets\u0018\u0005 \u0003(\u000b2\u0011.OSMPBF.ChangeSet\"\u0018\n\u000bStringTable\u0012\t\n\u0001s\u0018\u0001 \u0003(\f\"q\n\u0004Info\u0012\u0013\n\u0007version\u0018\u0001 \u0001(\u0005:\u0002-1\u0012\u0011\n\ttimestamp\u0018\u0002 \u0001(\u0003\u0012\u0011\n\tchangeset\u0018\u0003 \u0001(\u0003\u0012\u000b\n\u0003uid\u0018\u0004 \u0001(\u0005\u0012\u0010\n\buser_sid\u0018\u0005 \u0001(\r\u0012\u000f\n\u0007visible\u0018\u0006 \u0001(\b\"\u008a\u0001\n\tDenseInfo\u0012\u0013\n\u0007version\u0018\u0001 \u0003(\u0005B\u0002\u0010\u0001\u0012\u0015\n\ttimestamp\u0018\u0002 \u0003(\u0012B\u0002\u0010\u0001\u0012\u0015\n\tchangeset\u0018\u0003 \u0003(\u0012B\u0002\u0010\u0001\u0012\u000f\n\u0003uid\u0018\u0004 \u0003(\u0011B\u0002\u0010\u0001\u0012\u0014\n\buser_sid\u0018\u0005 \u0003(\u0011B\u0002\u0010\u0001\u0012\u0013\n\u0007visible\u0018\u0006 \u0003(\bB\u0002\u0010\u0001\"\u0017\n\tChangeSet\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\"l\n\u0004Node\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0012\u0012\u0010\n\u0004keys\u0018\u0002 \u0003(\rB\u0002\u0010\u0001\u0012\u0010\n\u0004vals\u0018\u0003 \u0003(\rB\u0002\u0010\u0001\u0012\u001a\n\u0004info\u0018\u0004 \u0001(\u000b2\f.OSMPBF.Info\u0012\u000b\n\u0003lat\u0018\b \u0002(\u0012\u0012\u000b\n\u0003lon\u0018\t \u0002(\u0012\"{\n\nDenseNodes\u0012\u000e\n\u0002id\u0018\u0001 \u0003(\u0012B\u0002\u0010\u0001\u0012$\n\tdenseinfo\u0018\u0005 \u0001(\u000b2\u0011.OSMPBF.DenseInfo\u0012\u000f\n\u0003lat\u0018\b \u0003(\u0012B\u0002\u0010\u0001\u0012\u000f\n\u0003lon\u0018\t \u0003(\u0012B\u0002\u0010\u0001\u0012\u0015\n\tkeys_vals\u0018\n \u0003(\u0005B\u0002\u0010\u0001\"\u0085\u0001\n\u0003Way\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\u0012\u0010\n\u0004keys\u0018\u0002 \u0003(\rB\u0002\u0010\u0001\u0012\u0010\n\u0004vals\u0018\u0003 \u0003(\rB\u0002\u0010\u0001\u0012\u001a\n\u0004info\u0018\u0004 \u0001(\u000b2\f.OSMPBF.Info\u0012\u0010\n\u0004refs\u0018\b \u0003(\u0012B\u0002\u0010\u0001\u0012\u000f\n\u0003lat\u0018\t \u0003(\u0012B\u0002\u0010\u0001\u0012\u000f\n\u0003lon\u0018\n \u0003(\u0012B\u0002\u0010\u0001\"à\u0001\n\bRelation\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\u0012\u0010\n\u0004keys\u0018\u0002 \u0003(\rB\u0002\u0010\u0001\u0012\u0010\n\u0004vals\u0018\u0003 \u0003(\rB\u0002\u0010\u0001\u0012\u001a\n\u0004info\u0018\u0004 \u0001(\u000b2\f.OSMPBF.Info\u0012\u0015\n\troles_sid\u0018\b \u0003(\u0005B\u0002\u0010\u0001\u0012\u0012\n\u0006memids\u0018\t \u0003(\u0012B\u0002\u0010\u0001\u0012.\n\u0005types\u0018\n \u0003(\u000e2\u001b.OSMPBF.Relation.MemberTypeB\u0002\u0010\u0001\"-\n\nMemberType\u0012\b\n\u0004NODE\u0010\u0000\u0012\u0007\n\u0003WAY\u0010\u0001\u0012\f\n\bRELATION\u0010\u0002B%\n#org.openstreetmap.osmosis.osmbinary"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    }
}

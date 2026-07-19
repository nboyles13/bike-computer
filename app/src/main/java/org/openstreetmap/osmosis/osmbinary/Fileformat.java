package org.openstreetmap.osmosis.osmbinary;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes4.dex */
public final class Fileformat {
    private static Descriptors.FileDescriptor descriptor;
    private static final Descriptors.Descriptor internal_static_OSMPBF_Blob_descriptor = getDescriptor().getMessageTypes().get(0);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_Blob_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_Blob_descriptor, new String[]{"Raw", "RawSize", "ZlibData", "LzmaData", "OBSOLETEBzip2Data"});
    private static final Descriptors.Descriptor internal_static_OSMPBF_BlobHeader_descriptor = getDescriptor().getMessageTypes().get(1);
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_OSMPBF_BlobHeader_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_OSMPBF_BlobHeader_descriptor, new String[]{"Type", "Indexdata", "Datasize"});

    public interface BlobHeaderOrBuilder extends MessageOrBuilder {
        int getDatasize();

        ByteString getIndexdata();

        String getType();

        ByteString getTypeBytes();

        boolean hasDatasize();

        boolean hasIndexdata();

        boolean hasType();
    }

    public interface BlobOrBuilder extends MessageOrBuilder {
        ByteString getLzmaData();

        @Deprecated
        ByteString getOBSOLETEBzip2Data();

        ByteString getRaw();

        int getRawSize();

        ByteString getZlibData();

        boolean hasLzmaData();

        @Deprecated
        boolean hasOBSOLETEBzip2Data();

        boolean hasRaw();

        boolean hasRawSize();

        boolean hasZlibData();
    }

    private Fileformat() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        registerAllExtensions((ExtensionRegistryLite) registry);
    }

    public static final class Blob extends GeneratedMessageV3 implements BlobOrBuilder {
        public static final int LZMA_DATA_FIELD_NUMBER = 4;
        public static final int OBSOLETE_BZIP2_DATA_FIELD_NUMBER = 5;
        public static final int RAW_FIELD_NUMBER = 1;
        public static final int RAW_SIZE_FIELD_NUMBER = 2;
        public static final int ZLIB_DATA_FIELD_NUMBER = 3;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private ByteString lzmaData_;
        private byte memoizedIsInitialized;
        private ByteString oBSOLETEBzip2Data_;
        private int rawSize_;
        private ByteString raw_;
        private ByteString zlibData_;
        private static final Blob DEFAULT_INSTANCE = new Blob();

        @Deprecated
        public static final Parser<Blob> PARSER = new AbstractParser<Blob>() { // from class: org.openstreetmap.osmosis.osmbinary.Fileformat.Blob.1
            @Override // com.google.protobuf.Parser
            public Blob parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Blob(input, extensionRegistry);
            }
        };

        private Blob(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private Blob() {
            this.memoizedIsInitialized = (byte) -1;
            this.raw_ = ByteString.EMPTY;
            this.zlibData_ = ByteString.EMPTY;
            this.lzmaData_ = ByteString.EMPTY;
            this.oBSOLETEBzip2Data_ = ByteString.EMPTY;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Blob();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Blob(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                case 10:
                                    this.bitField0_ |= 1;
                                    this.raw_ = input.readBytes();
                                    break;
                                case 16:
                                    this.bitField0_ |= 2;
                                    this.rawSize_ = input.readInt32();
                                    break;
                                case 26:
                                    this.bitField0_ |= 4;
                                    this.zlibData_ = input.readBytes();
                                    break;
                                case 34:
                                    this.bitField0_ |= 8;
                                    this.lzmaData_ = input.readBytes();
                                    break;
                                case 42:
                                    this.bitField0_ |= 16;
                                    this.oBSOLETEBzip2Data_ = input.readBytes();
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
            return Fileformat.internal_static_OSMPBF_Blob_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Fileformat.internal_static_OSMPBF_Blob_fieldAccessorTable.ensureFieldAccessorsInitialized(Blob.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public boolean hasRaw() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public ByteString getRaw() {
            return this.raw_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public boolean hasRawSize() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public int getRawSize() {
            return this.rawSize_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public boolean hasZlibData() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public ByteString getZlibData() {
            return this.zlibData_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public boolean hasLzmaData() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        public ByteString getLzmaData() {
            return this.lzmaData_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        @Deprecated
        public boolean hasOBSOLETEBzip2Data() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
        @Deprecated
        public ByteString getOBSOLETEBzip2Data() {
            return this.oBSOLETEBzip2Data_;
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
                output.writeBytes(1, this.raw_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeInt32(2, this.rawSize_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeBytes(3, this.zlibData_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeBytes(4, this.lzmaData_);
            }
            if ((this.bitField0_ & 16) != 0) {
                output.writeBytes(5, this.oBSOLETEBzip2Data_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + CodedOutputStream.computeBytesSize(1, this.raw_) : 0;
            if ((this.bitField0_ & 2) != 0) {
                size2 += CodedOutputStream.computeInt32Size(2, this.rawSize_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size2 += CodedOutputStream.computeBytesSize(3, this.zlibData_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size2 += CodedOutputStream.computeBytesSize(4, this.lzmaData_);
            }
            if ((this.bitField0_ & 16) != 0) {
                size2 += CodedOutputStream.computeBytesSize(5, this.oBSOLETEBzip2Data_);
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
            if (!(obj instanceof Blob)) {
                return super.equals(obj);
            }
            Blob other = (Blob) obj;
            if (hasRaw() != other.hasRaw()) {
                return false;
            }
            if ((hasRaw() && !getRaw().equals(other.getRaw())) || hasRawSize() != other.hasRawSize()) {
                return false;
            }
            if ((hasRawSize() && getRawSize() != other.getRawSize()) || hasZlibData() != other.hasZlibData()) {
                return false;
            }
            if ((hasZlibData() && !getZlibData().equals(other.getZlibData())) || hasLzmaData() != other.hasLzmaData()) {
                return false;
            }
            if ((!hasLzmaData() || getLzmaData().equals(other.getLzmaData())) && hasOBSOLETEBzip2Data() == other.hasOBSOLETEBzip2Data()) {
                return (!hasOBSOLETEBzip2Data() || getOBSOLETEBzip2Data().equals(other.getOBSOLETEBzip2Data())) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasRaw()) {
                hash = (((hash * 37) + 1) * 53) + getRaw().hashCode();
            }
            if (hasRawSize()) {
                hash = (((hash * 37) + 2) * 53) + getRawSize();
            }
            if (hasZlibData()) {
                hash = (((hash * 37) + 3) * 53) + getZlibData().hashCode();
            }
            if (hasLzmaData()) {
                hash = (((hash * 37) + 4) * 53) + getLzmaData().hashCode();
            }
            if (hasOBSOLETEBzip2Data()) {
                hash = (((hash * 37) + 5) * 53) + getOBSOLETEBzip2Data().hashCode();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static Blob parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Blob parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Blob parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Blob parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Blob parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Blob parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Blob parseFrom(InputStream input) throws IOException {
            return (Blob) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Blob parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Blob) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Blob parseDelimitedFrom(InputStream input) throws IOException {
            return (Blob) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Blob parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Blob) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Blob parseFrom(CodedInputStream input) throws IOException {
            return (Blob) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Blob parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (Blob) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Blob prototype) {
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

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements BlobOrBuilder {
            private int bitField0_;
            private ByteString lzmaData_;
            private ByteString oBSOLETEBzip2Data_;
            private int rawSize_;
            private ByteString raw_;
            private ByteString zlibData_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Fileformat.internal_static_OSMPBF_Blob_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Fileformat.internal_static_OSMPBF_Blob_fieldAccessorTable.ensureFieldAccessorsInitialized(Blob.class, Builder.class);
            }

            private Builder() {
                this.raw_ = ByteString.EMPTY;
                this.zlibData_ = ByteString.EMPTY;
                this.lzmaData_ = ByteString.EMPTY;
                this.oBSOLETEBzip2Data_ = ByteString.EMPTY;
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.raw_ = ByteString.EMPTY;
                this.zlibData_ = ByteString.EMPTY;
                this.lzmaData_ = ByteString.EMPTY;
                this.oBSOLETEBzip2Data_ = ByteString.EMPTY;
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = Blob.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.raw_ = ByteString.EMPTY;
                this.bitField0_ &= -2;
                this.rawSize_ = 0;
                this.bitField0_ &= -3;
                this.zlibData_ = ByteString.EMPTY;
                this.bitField0_ &= -5;
                this.lzmaData_ = ByteString.EMPTY;
                this.bitField0_ &= -9;
                this.oBSOLETEBzip2Data_ = ByteString.EMPTY;
                this.bitField0_ &= -17;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Fileformat.internal_static_OSMPBF_Blob_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public Blob getDefaultInstanceForType() {
                return Blob.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Blob build() {
                Blob result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Blob buildPartial() {
                Blob result = new Blob(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ = 0 | 1;
                }
                result.raw_ = this.raw_;
                if ((from_bitField0_ & 2) != 0) {
                    result.rawSize_ = this.rawSize_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    to_bitField0_ |= 4;
                }
                result.zlibData_ = this.zlibData_;
                if ((from_bitField0_ & 8) != 0) {
                    to_bitField0_ |= 8;
                }
                result.lzmaData_ = this.lzmaData_;
                if ((from_bitField0_ & 16) != 0) {
                    to_bitField0_ |= 16;
                }
                result.oBSOLETEBzip2Data_ = this.oBSOLETEBzip2Data_;
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
                if (other instanceof Blob) {
                    return mergeFrom((Blob) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Blob other) {
                if (other == Blob.getDefaultInstance()) {
                    return this;
                }
                if (other.hasRaw()) {
                    setRaw(other.getRaw());
                }
                if (other.hasRawSize()) {
                    setRawSize(other.getRawSize());
                }
                if (other.hasZlibData()) {
                    setZlibData(other.getZlibData());
                }
                if (other.hasLzmaData()) {
                    setLzmaData(other.getLzmaData());
                }
                if (other.hasOBSOLETEBzip2Data()) {
                    setOBSOLETEBzip2Data(other.getOBSOLETEBzip2Data());
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
                Blob parsedMessage = null;
                try {
                    try {
                        parsedMessage = Blob.PARSER.parsePartialFrom(input, extensionRegistry);
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

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public boolean hasRaw() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public ByteString getRaw() {
                return this.raw_;
            }

            public Builder setRaw(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.raw_ = value;
                onChanged();
                return this;
            }

            public Builder clearRaw() {
                this.bitField0_ &= -2;
                this.raw_ = Blob.getDefaultInstance().getRaw();
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public boolean hasRawSize() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public int getRawSize() {
                return this.rawSize_;
            }

            public Builder setRawSize(int value) {
                this.bitField0_ |= 2;
                this.rawSize_ = value;
                onChanged();
                return this;
            }

            public Builder clearRawSize() {
                this.bitField0_ &= -3;
                this.rawSize_ = 0;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public boolean hasZlibData() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public ByteString getZlibData() {
                return this.zlibData_;
            }

            public Builder setZlibData(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                this.zlibData_ = value;
                onChanged();
                return this;
            }

            public Builder clearZlibData() {
                this.bitField0_ &= -5;
                this.zlibData_ = Blob.getDefaultInstance().getZlibData();
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public boolean hasLzmaData() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            public ByteString getLzmaData() {
                return this.lzmaData_;
            }

            public Builder setLzmaData(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.lzmaData_ = value;
                onChanged();
                return this;
            }

            public Builder clearLzmaData() {
                this.bitField0_ &= -9;
                this.lzmaData_ = Blob.getDefaultInstance().getLzmaData();
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            @Deprecated
            public boolean hasOBSOLETEBzip2Data() {
                return (this.bitField0_ & 16) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobOrBuilder
            @Deprecated
            public ByteString getOBSOLETEBzip2Data() {
                return this.oBSOLETEBzip2Data_;
            }

            @Deprecated
            public Builder setOBSOLETEBzip2Data(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 16;
                this.oBSOLETEBzip2Data_ = value;
                onChanged();
                return this;
            }

            @Deprecated
            public Builder clearOBSOLETEBzip2Data() {
                this.bitField0_ &= -17;
                this.oBSOLETEBzip2Data_ = Blob.getDefaultInstance().getOBSOLETEBzip2Data();
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

        public static Blob getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Blob> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<Blob> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public Blob getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static final class BlobHeader extends GeneratedMessageV3 implements BlobHeaderOrBuilder {
        public static final int DATASIZE_FIELD_NUMBER = 3;
        public static final int INDEXDATA_FIELD_NUMBER = 2;
        public static final int TYPE_FIELD_NUMBER = 1;
        private static final long serialVersionUID = 0;
        private int bitField0_;
        private int datasize_;
        private ByteString indexdata_;
        private byte memoizedIsInitialized;
        private volatile Object type_;
        private static final BlobHeader DEFAULT_INSTANCE = new BlobHeader();

        @Deprecated
        public static final Parser<BlobHeader> PARSER = new AbstractParser<BlobHeader>() { // from class: org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeader.1
            @Override // com.google.protobuf.Parser
            public BlobHeader parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new BlobHeader(input, extensionRegistry);
            }
        };

        private BlobHeader(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = (byte) -1;
        }

        private BlobHeader() {
            this.memoizedIsInitialized = (byte) -1;
            this.type_ = "";
            this.indexdata_ = ByteString.EMPTY;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new BlobHeader();
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageOrBuilder
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private BlobHeader(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            case 10:
                                ByteString bs = input.readBytes();
                                this.bitField0_ |= 1;
                                this.type_ = bs;
                                break;
                            case 18:
                                this.bitField0_ |= 2;
                                this.indexdata_ = input.readBytes();
                                break;
                            case 24:
                                this.bitField0_ |= 4;
                                this.datasize_ = input.readInt32();
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
            return Fileformat.internal_static_OSMPBF_BlobHeader_descriptor;
        }

        @Override // com.google.protobuf.GeneratedMessageV3
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return Fileformat.internal_static_OSMPBF_BlobHeader_fieldAccessorTable.ensureFieldAccessorsInitialized(BlobHeader.class, Builder.class);
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public String getType() {
            Object ref = this.type_;
            if (ref instanceof String) {
                return (String) ref;
            }
            ByteString bs = (ByteString) ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.type_ = s;
            }
            return s;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public ByteString getTypeBytes() {
            Object ref = this.type_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                this.type_ = b;
                return b;
            }
            return (ByteString) ref;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public boolean hasIndexdata() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public ByteString getIndexdata() {
            return this.indexdata_;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public boolean hasDatasize() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
        public int getDatasize() {
            return this.datasize_;
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
            if (!hasType()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            if (!hasDatasize()) {
                this.memoizedIsInitialized = (byte) 0;
                return false;
            }
            this.memoizedIsInitialized = (byte) 1;
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessageV3.writeString(output, 1, this.type_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeBytes(2, this.indexdata_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt32(3, this.datasize_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.AbstractMessage, com.google.protobuf.MessageLite
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            int size2 = (this.bitField0_ & 1) != 0 ? 0 + GeneratedMessageV3.computeStringSize(1, this.type_) : 0;
            if ((this.bitField0_ & 2) != 0) {
                size2 += CodedOutputStream.computeBytesSize(2, this.indexdata_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size2 += CodedOutputStream.computeInt32Size(3, this.datasize_);
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
            if (!(obj instanceof BlobHeader)) {
                return super.equals(obj);
            }
            BlobHeader other = (BlobHeader) obj;
            if (hasType() != other.hasType()) {
                return false;
            }
            if ((hasType() && !getType().equals(other.getType())) || hasIndexdata() != other.hasIndexdata()) {
                return false;
            }
            if ((!hasIndexdata() || getIndexdata().equals(other.getIndexdata())) && hasDatasize() == other.hasDatasize()) {
                return (!hasDatasize() || getDatasize() == other.getDatasize()) && this.unknownFields.equals(other.unknownFields);
            }
            return false;
        }

        @Override // com.google.protobuf.AbstractMessage, com.google.protobuf.Message
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = (41 * 19) + getDescriptor().hashCode();
            if (hasType()) {
                hash = (((hash * 37) + 1) * 53) + getType().hashCode();
            }
            if (hasIndexdata()) {
                hash = (((hash * 37) + 2) * 53) + getIndexdata().hashCode();
            }
            if (hasDatasize()) {
                hash = (((hash * 37) + 3) * 53) + getDatasize();
            }
            int hash2 = (hash * 29) + this.unknownFields.hashCode();
            this.memoizedHashCode = hash2;
            return hash2;
        }

        public static BlobHeader parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static BlobHeader parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static BlobHeader parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static BlobHeader parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static BlobHeader parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static BlobHeader parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static BlobHeader parseFrom(InputStream input) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static BlobHeader parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static BlobHeader parseDelimitedFrom(InputStream input) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static BlobHeader parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static BlobHeader parseFrom(CodedInputStream input) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static BlobHeader parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (BlobHeader) GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override // com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(BlobHeader prototype) {
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

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements BlobHeaderOrBuilder {
            private int bitField0_;
            private int datasize_;
            private ByteString indexdata_;
            private Object type_;

            public static final Descriptors.Descriptor getDescriptor() {
                return Fileformat.internal_static_OSMPBF_BlobHeader_descriptor;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return Fileformat.internal_static_OSMPBF_BlobHeader_fieldAccessorTable.ensureFieldAccessorsInitialized(BlobHeader.class, Builder.class);
            }

            private Builder() {
                this.type_ = "";
                this.indexdata_ = ByteString.EMPTY;
                maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.type_ = "";
                this.indexdata_ = ByteString.EMPTY;
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                boolean unused = BlobHeader.alwaysUseFieldBuilders;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder clear() {
                super.clear();
                this.type_ = "";
                this.bitField0_ &= -2;
                this.indexdata_ = ByteString.EMPTY;
                this.bitField0_ &= -3;
                this.datasize_ = 0;
                this.bitField0_ &= -5;
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.Message.Builder, com.google.protobuf.MessageOrBuilder
            public Descriptors.Descriptor getDescriptorForType() {
                return Fileformat.internal_static_OSMPBF_BlobHeader_descriptor;
            }

            @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
            public BlobHeader getDefaultInstanceForType() {
                return BlobHeader.getDefaultInstance();
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public BlobHeader build() {
                BlobHeader result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException((Message) result);
                }
                return result;
            }

            @Override // com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public BlobHeader buildPartial() {
                BlobHeader result = new BlobHeader(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ = 0 | 1;
                }
                result.type_ = this.type_;
                if ((from_bitField0_ & 2) != 0) {
                    to_bitField0_ |= 2;
                }
                result.indexdata_ = this.indexdata_;
                if ((from_bitField0_ & 4) != 0) {
                    result.datasize_ = this.datasize_;
                    to_bitField0_ |= 4;
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
                if (other instanceof BlobHeader) {
                    return mergeFrom((BlobHeader) other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(BlobHeader other) {
                if (other == BlobHeader.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    this.bitField0_ |= 1;
                    this.type_ = other.type_;
                    onChanged();
                }
                if (other.hasIndexdata()) {
                    setIndexdata(other.getIndexdata());
                }
                if (other.hasDatasize()) {
                    setDatasize(other.getDatasize());
                }
                mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @Override // com.google.protobuf.GeneratedMessageV3.Builder, com.google.protobuf.MessageLiteOrBuilder
            public final boolean isInitialized() {
                return hasType() && hasDatasize();
            }

            @Override // com.google.protobuf.AbstractMessage.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder, com.google.protobuf.Message.Builder
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                BlobHeader parsedMessage = null;
                try {
                    try {
                        parsedMessage = BlobHeader.PARSER.parsePartialFrom(input, extensionRegistry);
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

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public boolean hasType() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public String getType() {
                Object ref = this.type_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString) ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.type_ = s;
                    }
                    return s;
                }
                return (String) ref;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public ByteString getTypeBytes() {
                Object ref = this.type_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String) ref);
                    this.type_ = b;
                    return b;
                }
                return (ByteString) ref;
            }

            public Builder setType(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.type_ = value;
                onChanged();
                return this;
            }

            public Builder clearType() {
                this.bitField0_ &= -2;
                this.type_ = BlobHeader.getDefaultInstance().getType();
                onChanged();
                return this;
            }

            public Builder setTypeBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.type_ = value;
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public boolean hasIndexdata() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public ByteString getIndexdata() {
                return this.indexdata_;
            }

            public Builder setIndexdata(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.indexdata_ = value;
                onChanged();
                return this;
            }

            public Builder clearIndexdata() {
                this.bitField0_ &= -3;
                this.indexdata_ = BlobHeader.getDefaultInstance().getIndexdata();
                onChanged();
                return this;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public boolean hasDatasize() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override // org.openstreetmap.osmosis.osmbinary.Fileformat.BlobHeaderOrBuilder
            public int getDatasize() {
                return this.datasize_;
            }

            public Builder setDatasize(int value) {
                this.bitField0_ |= 4;
                this.datasize_ = value;
                onChanged();
                return this;
            }

            public Builder clearDatasize() {
                this.bitField0_ &= -5;
                this.datasize_ = 0;
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

        public static BlobHeader getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<BlobHeader> parser() {
            return PARSER;
        }

        @Override // com.google.protobuf.GeneratedMessageV3, com.google.protobuf.MessageLite, com.google.protobuf.Message
        public Parser<BlobHeader> getParserForType() {
            return PARSER;
        }

        @Override // com.google.protobuf.MessageLiteOrBuilder, com.google.protobuf.MessageOrBuilder
        public BlobHeader getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = {"\n\"src/main/protobuf/fileformat.proto\u0012\u0006OSMPBF\"l\n\u0004Blob\u0012\u000b\n\u0003raw\u0018\u0001 \u0001(\f\u0012\u0010\n\braw_size\u0018\u0002 \u0001(\u0005\u0012\u0011\n\tzlib_data\u0018\u0003 \u0001(\f\u0012\u0011\n\tlzma_data\u0018\u0004 \u0001(\f\u0012\u001f\n\u0013OBSOLETE_bzip2_data\u0018\u0005 \u0001(\fB\u0002\u0018\u0001\"?\n\nBlobHeader\u0012\f\n\u0004type\u0018\u0001 \u0002(\t\u0012\u0011\n\tindexdata\u0018\u0002 \u0001(\f\u0012\u0010\n\bdatasize\u0018\u0003 \u0002(\u0005B%\n#org.openstreetmap.osmosis.osmbinary"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    }
}

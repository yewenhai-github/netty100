package com.netty100.netty.codec.common.entity;

/**
 * @author yewenhai
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public final class TopeRequestBodyProto2 {
  private TopeRequestBodyProto2() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface RequestBodyOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string url = 1;
    /**
     * <code>required string url = 1;</code>
     */
    boolean hasUrl();
    /**
     * <code>required string url = 1;</code>
     */
    String getUrl();
    /**
     * <code>required string url = 1;</code>
     */
    com.google.protobuf.ByteString
        getUrlBytes();

    // required string token = 2;
    /**
     * <code>required string token = 2;</code>
     */
    boolean hasToken();
    /**
     * <code>required string token = 2;</code>
     */
    String getToken();
    /**
     * <code>required string token = 2;</code>
     */
    com.google.protobuf.ByteString
        getTokenBytes();

    // optional bytes data = 3;
    /**
     * <code>optional bytes data = 3;</code>
     */
    boolean hasData();
    /**
     * <code>optional bytes data = 3;</code>
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code RequestBody}
   *
   * <pre>
   *Netty框架请求参数消息协议
   * </pre>
   */
  public static final class RequestBody extends
      com.google.protobuf.GeneratedMessage
      implements RequestBodyOrBuilder {
    // Use RequestBody.newBuilder() to construct.
    private RequestBody(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private RequestBody(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final RequestBody defaultInstance;
    public static RequestBody getDefaultInstance() {
      return defaultInstance;
    }

    public RequestBody getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private RequestBody(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              url_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              token_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              data_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return TopeRequestBodyProto2.internal_static_RequestBody_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return TopeRequestBodyProto2.internal_static_RequestBody_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              RequestBody.class, Builder.class);
    }

    public static com.google.protobuf.Parser<RequestBody> PARSER =
        new com.google.protobuf.AbstractParser<RequestBody>() {
      public RequestBody parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RequestBody(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<RequestBody> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required string url = 1;
    public static final int URL_FIELD_NUMBER = 1;
    private Object url_;
    /**
     * <code>required string url = 1;</code>
     */
    public boolean hasUrl() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string url = 1;</code>
     */
    public String getUrl() {
      Object ref = url_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          url_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string url = 1;</code>
     */
    public com.google.protobuf.ByteString
        getUrlBytes() {
      Object ref = url_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        url_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required string token = 2;
    public static final int TOKEN_FIELD_NUMBER = 2;
    private Object token_;
    /**
     * <code>required string token = 2;</code>
     */
    public boolean hasToken() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string token = 2;</code>
     */
    public String getToken() {
      Object ref = token_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          token_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string token = 2;</code>
     */
    public com.google.protobuf.ByteString
        getTokenBytes() {
      Object ref = token_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        token_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // optional bytes data = 3;
    public static final int DATA_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString data_;
    /**
     * <code>optional bytes data = 3;</code>
     */
    public boolean hasData() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional bytes data = 3;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    private void initFields() {
      url_ = "";
      token_ = "";
      data_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1){
        return isInitialized == 1;
      }

      if (!hasUrl()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasToken()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getUrlBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getTokenBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, data_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1){
        return size;
      }

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getUrlBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getTokenBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, data_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static RequestBody parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RequestBody parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RequestBody parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static RequestBody parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static RequestBody parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static RequestBody parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static RequestBody parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static RequestBody parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static RequestBody parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static RequestBody parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(RequestBody prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code RequestBody}
     *
     * <pre>
     *Netty框架请求参数消息协议
     * </pre>
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements RequestBodyOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return TopeRequestBodyProto2.internal_static_RequestBody_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return TopeRequestBodyProto2.internal_static_RequestBody_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                RequestBody.class, Builder.class);
      }

      // Construct using TopeProto2RequestBody.RequestBody.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        url_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        token_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return TopeRequestBodyProto2.internal_static_RequestBody_descriptor;
      }

      public RequestBody getDefaultInstanceForType() {
        return RequestBody.getDefaultInstance();
      }

      public RequestBody build() {
        RequestBody result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public RequestBody buildPartial() {
        RequestBody result = new RequestBody(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.url_ = url_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.token_ = token_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.data_ = data_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof RequestBody) {
          return mergeFrom((RequestBody)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(RequestBody other) {
        if (other == RequestBody.getDefaultInstance()){
          return this;
        }
        if (other.hasUrl()) {
          bitField0_ |= 0x00000001;
          url_ = other.url_;
          onChanged();
        }
        if (other.hasToken()) {
          bitField0_ |= 0x00000002;
          token_ = other.token_;
          onChanged();
        }
        if (other.hasData()) {
          setData(other.getData());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasUrl()) {
          
          return false;
        }
        if (!hasToken()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        RequestBody parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (RequestBody) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required string url = 1;
      private Object url_ = "";
      /**
       * <code>required string url = 1;</code>
       */
      public boolean hasUrl() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string url = 1;</code>
       */
      public String getUrl() {
        Object ref = url_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          url_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string url = 1;</code>
       */
      public com.google.protobuf.ByteString
          getUrlBytes() {
        Object ref = url_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          url_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string url = 1;</code>
       */
      public Builder setUrl(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        url_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string url = 1;</code>
       */
      public Builder clearUrl() {
        bitField0_ = (bitField0_ & ~0x00000001);
        url_ = getDefaultInstance().getUrl();
        onChanged();
        return this;
      }
      /**
       * <code>required string url = 1;</code>
       */
      public Builder setUrlBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        url_ = value;
        onChanged();
        return this;
      }

      // required string token = 2;
      private Object token_ = "";
      /**
       * <code>required string token = 2;</code>
       */
      public boolean hasToken() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string token = 2;</code>
       */
      public String getToken() {
        Object ref = token_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          token_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string token = 2;</code>
       */
      public com.google.protobuf.ByteString
          getTokenBytes() {
        Object ref = token_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          token_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string token = 2;</code>
       */
      public Builder setToken(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        token_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string token = 2;</code>
       */
      public Builder clearToken() {
        bitField0_ = (bitField0_ & ~0x00000002);
        token_ = getDefaultInstance().getToken();
        onChanged();
        return this;
      }
      /**
       * <code>required string token = 2;</code>
       */
      public Builder setTokenBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        token_ = value;
        onChanged();
        return this;
      }

      // optional bytes data = 3;
      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes data = 3;</code>
       */
      public boolean hasData() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional bytes data = 3;</code>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>optional bytes data = 3;</code>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes data = 3;</code>
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000004);
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:RequestBody)
    }

    static {
      defaultInstance = new RequestBody(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:RequestBody)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_RequestBody_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_RequestBody_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\033TopeProto2RequestBody.proto\"7\n\013Request" +
      "Body\022\013\n\003url\030\001 \002(\t\022\r\n\005token\030\002 \002(\t\022\014\n\004data" +
      "\030\003 \001(\014B\037\n\035com.why.netty.core.entity"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_RequestBody_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_RequestBody_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_RequestBody_descriptor,
              new String[] { "Url", "Token", "Data", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}

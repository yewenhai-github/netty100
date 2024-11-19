package com.netty100.netty.codec.common.entity;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public final class TopeResponseBodyProto2 {
  private TopeResponseBodyProto2() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ResponseBodyOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string url = 1;
    /**
     * <code>required string url = 1;</code>
     *
     * <pre>
     *业务标识
     * </pre>
     */
    boolean hasUrl();
    /**
     * <code>required string url = 1;</code>
     *
     * <pre>
     *业务标识
     * </pre>
     */
    String getUrl();
    /**
     * <code>required string url = 1;</code>
     *
     * <pre>
     *业务标识
     * </pre>
     */
    com.google.protobuf.ByteString
        getUrlBytes();

    // optional bytes data = 2;
    /**
     * <code>optional bytes data = 2;</code>
     *
     * <pre>
     *业务信息
     * </pre>
     */
    boolean hasData();
    /**
     * <code>optional bytes data = 2;</code>
     *
     * <pre>
     *业务信息
     * </pre>
     */
    com.google.protobuf.ByteString getData();

    // optional string code = 3;
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    boolean hasCode();
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    String getCode();
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    com.google.protobuf.ByteString
        getCodeBytes();

    // optional string message = 4;
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    boolean hasMessage();
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    String getMessage();
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    com.google.protobuf.ByteString
        getMessageBytes();
  }
  /**
   * Protobuf type {@code ResponseBody}
   *
   * <pre>
   *Netty框架返回参数消息协议
   * </pre>
   */
  public static final class ResponseBody extends
      com.google.protobuf.GeneratedMessage
      implements ResponseBodyOrBuilder {
    // Use ResponseBody.newBuilder() to construct.
    private ResponseBody(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private ResponseBody(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final ResponseBody defaultInstance;
    public static ResponseBody getDefaultInstance() {
      return defaultInstance;
    }

    public ResponseBody getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private ResponseBody(
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
              data_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              code_ = input.readBytes();
              break;
            }
            case 34: {
              bitField0_ |= 0x00000008;
              message_ = input.readBytes();
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
      return TopeResponseBodyProto2.internal_static_ResponseBody_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return TopeResponseBodyProto2.internal_static_ResponseBody_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ResponseBody.class, Builder.class);
    }

    public static com.google.protobuf.Parser<ResponseBody> PARSER =
        new com.google.protobuf.AbstractParser<ResponseBody>() {
      public ResponseBody parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ResponseBody(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<ResponseBody> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required string url = 1;
    public static final int URL_FIELD_NUMBER = 1;
    private Object url_;
    /**
     * <code>required string url = 1;</code>
     *
     * <pre>
     *业务标识
     * </pre>
     */
    public boolean hasUrl() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string url = 1;</code>
     *
     * <pre>
     *业务标识
     * </pre>
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
     *
     * <pre>
     *业务标识
     * </pre>
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

    // optional bytes data = 2;
    public static final int DATA_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString data_;
    /**
     * <code>optional bytes data = 2;</code>
     *
     * <pre>
     *业务信息
     * </pre>
     */
    public boolean hasData() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional bytes data = 2;</code>
     *
     * <pre>
     *业务信息
     * </pre>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    // optional string code = 3;
    public static final int CODE_FIELD_NUMBER = 3;
    private Object code_;
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    public boolean hasCode() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    public String getCode() {
      Object ref = code_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          code_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string code = 3;</code>
     *
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     */
    public com.google.protobuf.ByteString
        getCodeBytes() {
      Object ref = code_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        code_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // optional string message = 4;
    public static final int MESSAGE_FIELD_NUMBER = 4;
    private Object message_;
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    public String getMessage() {
      Object ref = message_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          message_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string message = 4;</code>
     *
     * <pre>
     *提示消息
     * </pre>
     */
    public com.google.protobuf.ByteString
        getMessageBytes() {
      Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      url_ = "";
      data_ = com.google.protobuf.ByteString.EMPTY;
      code_ = "";
      message_ = "";
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
        output.writeBytes(2, data_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getCodeBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeBytes(4, getMessageBytes());
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
          .computeBytesSize(2, data_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getCodeBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, getMessageBytes());
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

    public static ResponseBody parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ResponseBody parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ResponseBody parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ResponseBody parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ResponseBody parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ResponseBody parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static ResponseBody parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static ResponseBody parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static ResponseBody parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ResponseBody parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(ResponseBody prototype) {
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
     * Protobuf type {@code ResponseBody}
     *
     * <pre>
     *Netty框架返回参数消息协议
     * </pre>
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements ResponseBodyOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return TopeResponseBodyProto2.internal_static_ResponseBody_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return TopeResponseBodyProto2.internal_static_ResponseBody_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ResponseBody.class, Builder.class);
      }

      // Construct using TopeProto2ResponseBody.ResponseBody.newBuilder()
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
        data_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        code_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        message_ = "";
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return TopeResponseBodyProto2.internal_static_ResponseBody_descriptor;
      }

      public ResponseBody getDefaultInstanceForType() {
        return ResponseBody.getDefaultInstance();
      }

      public ResponseBody build() {
        ResponseBody result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public ResponseBody buildPartial() {
        ResponseBody result = new ResponseBody(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.url_ = url_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.data_ = data_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.code_ = code_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.message_ = message_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ResponseBody) {
          return mergeFrom((ResponseBody)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ResponseBody other) {
        if (other == ResponseBody.getDefaultInstance()){
          return this;
        }
        if (other.hasUrl()) {
          bitField0_ |= 0x00000001;
          url_ = other.url_;
          onChanged();
        }
        if (other.hasData()) {
          setData(other.getData());
        }
        if (other.hasCode()) {
          bitField0_ |= 0x00000004;
          code_ = other.code_;
          onChanged();
        }
        if (other.hasMessage()) {
          bitField0_ |= 0x00000008;
          message_ = other.message_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasUrl()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ResponseBody parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ResponseBody) e.getUnfinishedMessage();
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
       *
       * <pre>
       *业务标识
       * </pre>
       */
      public boolean hasUrl() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string url = 1;</code>
       *
       * <pre>
       *业务标识
       * </pre>
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
       *
       * <pre>
       *业务标识
       * </pre>
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
       *
       * <pre>
       *业务标识
       * </pre>
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
       *
       * <pre>
       *业务标识
       * </pre>
       */
      public Builder clearUrl() {
        bitField0_ = (bitField0_ & ~0x00000001);
        url_ = getDefaultInstance().getUrl();
        onChanged();
        return this;
      }
      /**
       * <code>required string url = 1;</code>
       *
       * <pre>
       *业务标识
       * </pre>
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

      // optional bytes data = 2;
      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes data = 2;</code>
       *
       * <pre>
       *业务信息
       * </pre>
       */
      public boolean hasData() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional bytes data = 2;</code>
       *
       * <pre>
       *业务信息
       * </pre>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>optional bytes data = 2;</code>
       *
       * <pre>
       *业务信息
       * </pre>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes data = 2;</code>
       *
       * <pre>
       *业务信息
       * </pre>
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }

      // optional string code = 3;
      private Object code_ = "";
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public boolean hasCode() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public String getCode() {
        Object ref = code_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          code_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public com.google.protobuf.ByteString
          getCodeBytes() {
        Object ref = code_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          code_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public Builder setCode(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        code_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public Builder clearCode() {
        bitField0_ = (bitField0_ & ~0x00000004);
        code_ = getDefaultInstance().getCode();
        onChanged();
        return this;
      }
      /**
       * <code>optional string code = 3;</code>
       *
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       */
      public Builder setCodeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        code_ = value;
        onChanged();
        return this;
      }

      // optional string message = 4;
      private Object message_ = "";
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public boolean hasMessage() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public String getMessage() {
        Object ref = message_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          message_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public com.google.protobuf.ByteString
          getMessageBytes() {
        Object ref = message_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          message_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public Builder setMessage(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        message_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public Builder clearMessage() {
        bitField0_ = (bitField0_ & ~0x00000008);
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }
      /**
       * <code>optional string message = 4;</code>
       *
       * <pre>
       *提示消息
       * </pre>
       */
      public Builder setMessageBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        message_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:ResponseBody)
    }

    static {
      defaultInstance = new ResponseBody(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:ResponseBody)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_ResponseBody_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_ResponseBody_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\034TopeProto2ResponseBody.proto\"H\n\014Respon" +
      "seBody\022\013\n\003url\030\001 \002(\t\022\014\n\004data\030\002 \001(\014\022\014\n\004cod" +
      "e\030\003 \001(\t\022\017\n\007message\030\004 \001(\tB\037\n\035com.why." +
      "netty.core.entity"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_ResponseBody_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_ResponseBody_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_ResponseBody_descriptor,
              new String[] { "Url", "Data", "Code", "Message", });
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

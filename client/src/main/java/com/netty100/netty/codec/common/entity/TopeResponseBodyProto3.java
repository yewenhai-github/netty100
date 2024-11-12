// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: TopeResponseBodyProto3.proto

package com.netty100.netty.codec.common.entity;

public final class TopeResponseBodyProto3 {
  private TopeResponseBodyProto3() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ResponseBodyOrBuilder extends
      // @@protoc_insertion_point(interface_extends:ResponseBody)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return Whether the url field is set.
     */
    boolean hasUrl();
    /**
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return The url.
     */
    String getUrl();
    /**
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return The bytes for url.
     */
    com.google.protobuf.ByteString
        getUrlBytes();

    /**
     * <pre>
     *业务信息
     * </pre>
     *
     * <code>optional bytes data = 2;</code>
     * @return Whether the data field is set.
     */
    boolean hasData();
    /**
     * <pre>
     *业务信息
     * </pre>
     *
     * <code>optional bytes data = 2;</code>
     * @return The data.
     */
    com.google.protobuf.ByteString getData();

    /**
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return Whether the code field is set.
     */
    boolean hasCode();
    /**
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return The code.
     */
    String getCode();
    /**
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return The bytes for code.
     */
    com.google.protobuf.ByteString
        getCodeBytes();

    /**
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return Whether the message field is set.
     */
    boolean hasMessage();
    /**
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return The message.
     */
    String getMessage();
    /**
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return The bytes for message.
     */
    com.google.protobuf.ByteString
        getMessageBytes();
  }
  /**
   * <pre>
   *Netty框架返回参数消息协议
   * </pre>
   *
   * Protobuf type {@code ResponseBody}
   */
  public static final class ResponseBody extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:ResponseBody)
      ResponseBodyOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ResponseBody.newBuilder() to construct.
    private ResponseBody(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ResponseBody() {
      url_ = "";
      data_ = com.google.protobuf.ByteString.EMPTY;
      code_ = "";
      message_ = "";
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new ResponseBody();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ResponseBody(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
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
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              url_ = bs;
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              data_ = input.readBytes();
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              code_ = bs;
              break;
            }
            case 34: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000008;
              message_ = bs;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return TopeResponseBodyProto3.internal_static_ResponseBody_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return TopeResponseBodyProto3.internal_static_ResponseBody_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ResponseBody.class, Builder.class);
    }

    private int bitField0_;
    public static final int URL_FIELD_NUMBER = 1;
    private volatile Object url_;
    /**
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return Whether the url field is set.
     */
    @Override
    public boolean hasUrl() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return The url.
     */
    @Override
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
     * <pre>
     *业务标识
     * </pre>
     *
     * <code>required string url = 1;</code>
     * @return The bytes for url.
     */
    @Override
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

    public static final int DATA_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString data_;
    /**
     * <pre>
     *业务信息
     * </pre>
     *
     * <code>optional bytes data = 2;</code>
     * @return Whether the data field is set.
     */
    @Override
    public boolean hasData() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <pre>
     *业务信息
     * </pre>
     *
     * <code>optional bytes data = 2;</code>
     * @return The data.
     */
    @Override
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    public static final int CODE_FIELD_NUMBER = 3;
    private volatile Object code_;
    /**
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return Whether the code field is set.
     */
    @Override
    public boolean hasCode() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return The code.
     */
    @Override
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
     * <pre>
     *状态码 200成功 500服务器错误
     * </pre>
     *
     * <code>optional string code = 3;</code>
     * @return The bytes for code.
     */
    @Override
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

    public static final int MESSAGE_FIELD_NUMBER = 4;
    private volatile Object message_;
    /**
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return Whether the message field is set.
     */
    @Override
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return The message.
     */
    @Override
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
     * <pre>
     *提示消息
     * </pre>
     *
     * <code>optional string message = 4;</code>
     * @return The bytes for message.
     */
    @Override
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

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasUrl()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, url_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeBytes(2, data_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, code_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, message_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, url_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, data_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, code_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, message_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ResponseBody)) {
        return super.equals(obj);
      }
      ResponseBody other = (ResponseBody) obj;

      if (hasUrl() != other.hasUrl()) return false;
      if (hasUrl()) {
        if (!getUrl()
            .equals(other.getUrl())) return false;
      }
      if (hasData() != other.hasData()) return false;
      if (hasData()) {
        if (!getData()
            .equals(other.getData())) return false;
      }
      if (hasCode() != other.hasCode()) return false;
      if (hasCode()) {
        if (!getCode()
            .equals(other.getCode())) return false;
      }
      if (hasMessage() != other.hasMessage()) return false;
      if (hasMessage()) {
        if (!getMessage()
            .equals(other.getMessage())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasUrl()) {
        hash = (37 * hash) + URL_FIELD_NUMBER;
        hash = (53 * hash) + getUrl().hashCode();
      }
      if (hasData()) {
        hash = (37 * hash) + DATA_FIELD_NUMBER;
        hash = (53 * hash) + getData().hashCode();
      }
      if (hasCode()) {
        hash = (37 * hash) + CODE_FIELD_NUMBER;
        hash = (53 * hash) + getCode().hashCode();
      }
      if (hasMessage()) {
        hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
        hash = (53 * hash) + getMessage().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ResponseBody parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ResponseBody parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
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
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ResponseBody parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ResponseBody parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ResponseBody parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ResponseBody parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ResponseBody parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(ResponseBody prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     *Netty框架返回参数消息协议
     * </pre>
     *
     * Protobuf type {@code ResponseBody}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:ResponseBody)
        ResponseBodyOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return TopeResponseBodyProto3.internal_static_ResponseBody_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return TopeResponseBodyProto3.internal_static_ResponseBody_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ResponseBody.class, Builder.class);
      }

      // Construct using com.why.netty.codec.common.entity.TopeResponseBodyProto3.ResponseBody.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
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

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return TopeResponseBodyProto3.internal_static_ResponseBody_descriptor;
      }

      @Override
      public ResponseBody getDefaultInstanceForType() {
        return ResponseBody.getDefaultInstance();
      }

      @Override
      public ResponseBody build() {
        ResponseBody result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public ResponseBody buildPartial() {
        ResponseBody result = new ResponseBody(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.url_ = url_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.data_ = data_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.code_ = code_;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          to_bitField0_ |= 0x00000008;
        }
        result.message_ = message_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ResponseBody) {
          return mergeFrom((ResponseBody)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ResponseBody other) {
        if (other == ResponseBody.getDefaultInstance()) return this;
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
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        if (!hasUrl()) {
          return false;
        }
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ResponseBody parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ResponseBody) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private Object url_ = "";
      /**
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @return Whether the url field is set.
       */
      public boolean hasUrl() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @return The url.
       */
      public String getUrl() {
        Object ref = url_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            url_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @return The bytes for url.
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
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @param value The url to set.
       * @return This builder for chaining.
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
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearUrl() {
        bitField0_ = (bitField0_ & ~0x00000001);
        url_ = getDefaultInstance().getUrl();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *业务标识
       * </pre>
       *
       * <code>required string url = 1;</code>
       * @param value The bytes for url to set.
       * @return This builder for chaining.
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

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       *业务信息
       * </pre>
       *
       * <code>optional bytes data = 2;</code>
       * @return Whether the data field is set.
       */
      @Override
      public boolean hasData() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <pre>
       *业务信息
       * </pre>
       *
       * <code>optional bytes data = 2;</code>
       * @return The data.
       */
      @Override
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <pre>
       *业务信息
       * </pre>
       *
       * <code>optional bytes data = 2;</code>
       * @param value The data to set.
       * @return This builder for chaining.
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
       * <pre>
       *业务信息
       * </pre>
       *
       * <code>optional bytes data = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }

      private Object code_ = "";
      /**
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @return Whether the code field is set.
       */
      public boolean hasCode() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @return The code.
       */
      public String getCode() {
        Object ref = code_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            code_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @return The bytes for code.
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
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @param value The code to set.
       * @return This builder for chaining.
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
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearCode() {
        bitField0_ = (bitField0_ & ~0x00000004);
        code_ = getDefaultInstance().getCode();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *状态码 200成功 500服务器错误
       * </pre>
       *
       * <code>optional string code = 3;</code>
       * @param value The bytes for code to set.
       * @return This builder for chaining.
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

      private Object message_ = "";
      /**
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @return Whether the message field is set.
       */
      public boolean hasMessage() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @return The message.
       */
      public String getMessage() {
        Object ref = message_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            message_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @return The bytes for message.
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
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @param value The message to set.
       * @return This builder for chaining.
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
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearMessage() {
        bitField0_ = (bitField0_ & ~0x00000008);
        message_ = getDefaultInstance().getMessage();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *提示消息
       * </pre>
       *
       * <code>optional string message = 4;</code>
       * @param value The bytes for message to set.
       * @return This builder for chaining.
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
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:ResponseBody)
    }

    // @@protoc_insertion_point(class_scope:ResponseBody)
    private static final ResponseBody DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ResponseBody();
    }

    public static ResponseBody getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<ResponseBody>
        PARSER = new com.google.protobuf.AbstractParser<ResponseBody>() {
      @Override
      public ResponseBody parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ResponseBody(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ResponseBody> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<ResponseBody> getParserForType() {
      return PARSER;
    }

    @Override
    public ResponseBody getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ResponseBody_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ResponseBody_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\034TopeResponseBodyProto3.proto\"H\n\014Respon" +
      "seBody\022\013\n\003url\030\001 \002(\t\022\014\n\004data\030\002 \001(\014\022\014\n\004cod" +
      "e\030\003 \001(\t\022\017\n\007message\030\004 \001(\tB\'\n%com.why." +
      "netty.codec.common.entity"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_ResponseBody_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ResponseBody_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ResponseBody_descriptor,
        new String[] { "Url", "Data", "Code", "Message", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Data.proto

package com.netty100.cluster.consistency.entity;

public interface LogOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Log)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string group = 1;</code>
   * @return The group.
   */
  String getGroup();
  /**
   * <code>string group = 1;</code>
   * @return The bytes for group.
   */
  com.google.protobuf.ByteString
      getGroupBytes();

  /**
   * <code>string key = 2;</code>
   * @return The key.
   */
  String getKey();
  /**
   * <code>string key = 2;</code>
   * @return The bytes for key.
   */
  com.google.protobuf.ByteString
      getKeyBytes();

  /**
   * <code>bytes data = 3;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>string type = 4;</code>
   * @return The type.
   */
  String getType();
  /**
   * <code>string type = 4;</code>
   * @return The bytes for type.
   */
  com.google.protobuf.ByteString
      getTypeBytes();

  /**
   * <code>string operation = 5;</code>
   * @return The operation.
   */
  String getOperation();
  /**
   * <code>string operation = 5;</code>
   * @return The bytes for operation.
   */
  com.google.protobuf.ByteString
      getOperationBytes();

  /**
   * <code>map&lt;string, string&gt; extendInfo = 6;</code>
   */
  int getExtendInfoCount();
  /**
   * <code>map&lt;string, string&gt; extendInfo = 6;</code>
   */
  boolean containsExtendInfo(
      String key);
  /**
   * Use {@link #getExtendInfoMap()} instead.
   */
  @Deprecated
  java.util.Map<String, String>
  getExtendInfo();
  /**
   * <code>map&lt;string, string&gt; extendInfo = 6;</code>
   */
  java.util.Map<String, String>
  getExtendInfoMap();
  /**
   * <code>map&lt;string, string&gt; extendInfo = 6;</code>
   */
  /* nullable */
String getExtendInfoOrDefault(
      String key,
      /* nullable */
String defaultValue);
  /**
   * <code>map&lt;string, string&gt; extendInfo = 6;</code>
   */
  String getExtendInfoOrThrow(
      String key);
}
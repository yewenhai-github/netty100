// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: consistency.proto

package com.netty100.cluster.consistency.entity;

public interface ResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Response)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bytes data = 1;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>string errMsg = 2;</code>
   * @return The errMsg.
   */
  String getErrMsg();
  /**
   * <code>string errMsg = 2;</code>
   * @return The bytes for errMsg.
   */
  com.google.protobuf.ByteString
      getErrMsgBytes();

  /**
   * <code>bool success = 3;</code>
   * @return The success.
   */
  boolean getSuccess();
}

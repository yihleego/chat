// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: chatserver.proto

package io.leego.chat.core;

public interface MetaOrBuilder extends
    // @@protoc_insertion_point(interface_extends:chatserver.Meta)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 node = 1;</code>
   * @return The node.
   */
  int getNode();

  /**
   * <code>int32 channel = 2;</code>
   * @return The channel.
   */
  int getChannel();

  /**
   * <code>int64 user = 3;</code>
   * @return The user.
   */
  long getUser();

  /**
   * <code>int32 client = 4;</code>
   * @return The client.
   */
  int getClient();
}
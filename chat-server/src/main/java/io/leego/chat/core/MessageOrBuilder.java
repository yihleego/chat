// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: chatserver.proto

package io.leego.chat.core;

public interface MessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:chatserver.Message)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>int64 sender = 2;</code>
   * @return The sender.
   */
  long getSender();

  /**
   * <code>int64 recipient = 3;</code>
   * @return The recipient.
   */
  long getRecipient();
}

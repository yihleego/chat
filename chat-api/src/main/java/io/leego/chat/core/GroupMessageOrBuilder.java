// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: chatapi.proto

package io.leego.chat.core;

public interface GroupMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:chatapi.GroupMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>int64 groupId = 2;</code>
   * @return The groupId.
   */
  long getGroupId();

  /**
   * <code>int64 sender = 3;</code>
   * @return The sender.
   */
  long getSender();

  /**
   * <code>int64 recipient = 4;</code>
   * @return The recipient.
   */
  long getRecipient();

  /**
   * <code>int64 sentTime = 5;</code>
   * @return The sentTime.
   */
  long getSentTime();

  /**
   * <code>int32 type = 6;</code>
   * @return The type.
   */
  int getType();

  /**
   * <code>string content = 7;</code>
   * @return The content.
   */
  java.lang.String getContent();
  /**
   * <code>string content = 7;</code>
   * @return The bytes for content.
   */
  com.google.protobuf.ByteString
      getContentBytes();

  /**
   * <code>bool taken = 8;</code>
   * @return The taken.
   */
  boolean getTaken();

  /**
   * <code>bool seen = 9;</code>
   * @return The seen.
   */
  boolean getSeen();

  /**
   * <code>bool revoked = 10;</code>
   * @return The revoked.
   */
  boolean getRevoked();

  /**
   * <code>repeated .chatapi.Mention mention = 11;</code>
   */
  java.util.List<io.leego.chat.core.Mention> 
      getMentionList();
  /**
   * <code>repeated .chatapi.Mention mention = 11;</code>
   */
  io.leego.chat.core.Mention getMention(int index);
  /**
   * <code>repeated .chatapi.Mention mention = 11;</code>
   */
  int getMentionCount();
  /**
   * <code>repeated .chatapi.Mention mention = 11;</code>
   */
  java.util.List<? extends io.leego.chat.core.MentionOrBuilder> 
      getMentionOrBuilderList();
  /**
   * <code>repeated .chatapi.Mention mention = 11;</code>
   */
  io.leego.chat.core.MentionOrBuilder getMentionOrBuilder(
      int index);

  /**
   * <code>int32 status = 12;</code>
   * @return The status.
   */
  int getStatus();
}

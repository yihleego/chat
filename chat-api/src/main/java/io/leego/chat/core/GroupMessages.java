// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: chatapi.proto

package io.leego.chat.core;

/**
 * Protobuf type {@code chatapi.GroupMessages}
 */
public final class GroupMessages extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:chatapi.GroupMessages)
    GroupMessagesOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GroupMessages.newBuilder() to construct.
  private GroupMessages(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GroupMessages() {
    message_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GroupMessages();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return io.leego.chat.core.ChatFactory.internal_static_chatapi_GroupMessages_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return io.leego.chat.core.ChatFactory.internal_static_chatapi_GroupMessages_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            io.leego.chat.core.GroupMessages.class, io.leego.chat.core.GroupMessages.Builder.class);
  }

  public static final int MESSAGE_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<io.leego.chat.core.GroupMessage> message_;
  /**
   * <code>repeated .chatapi.GroupMessage message = 1;</code>
   */
  @java.lang.Override
  public java.util.List<io.leego.chat.core.GroupMessage> getMessageList() {
    return message_;
  }
  /**
   * <code>repeated .chatapi.GroupMessage message = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends io.leego.chat.core.GroupMessageOrBuilder> 
      getMessageOrBuilderList() {
    return message_;
  }
  /**
   * <code>repeated .chatapi.GroupMessage message = 1;</code>
   */
  @java.lang.Override
  public int getMessageCount() {
    return message_.size();
  }
  /**
   * <code>repeated .chatapi.GroupMessage message = 1;</code>
   */
  @java.lang.Override
  public io.leego.chat.core.GroupMessage getMessage(int index) {
    return message_.get(index);
  }
  /**
   * <code>repeated .chatapi.GroupMessage message = 1;</code>
   */
  @java.lang.Override
  public io.leego.chat.core.GroupMessageOrBuilder getMessageOrBuilder(
      int index) {
    return message_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    for (int i = 0; i < message_.size(); i++) {
      output.writeMessage(1, message_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < message_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, message_.get(i));
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof io.leego.chat.core.GroupMessages)) {
      return super.equals(obj);
    }
    io.leego.chat.core.GroupMessages other = (io.leego.chat.core.GroupMessages) obj;

    if (!getMessageList()
        .equals(other.getMessageList())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (getMessageCount() > 0) {
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessageList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.leego.chat.core.GroupMessages parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.leego.chat.core.GroupMessages parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static io.leego.chat.core.GroupMessages parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static io.leego.chat.core.GroupMessages parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(io.leego.chat.core.GroupMessages prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code chatapi.GroupMessages}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:chatapi.GroupMessages)
      io.leego.chat.core.GroupMessagesOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return io.leego.chat.core.ChatFactory.internal_static_chatapi_GroupMessages_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return io.leego.chat.core.ChatFactory.internal_static_chatapi_GroupMessages_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              io.leego.chat.core.GroupMessages.class, io.leego.chat.core.GroupMessages.Builder.class);
    }

    // Construct using io.leego.chat.core.GroupMessages.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (messageBuilder_ == null) {
        message_ = java.util.Collections.emptyList();
      } else {
        message_ = null;
        messageBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return io.leego.chat.core.ChatFactory.internal_static_chatapi_GroupMessages_descriptor;
    }

    @java.lang.Override
    public io.leego.chat.core.GroupMessages getDefaultInstanceForType() {
      return io.leego.chat.core.GroupMessages.getDefaultInstance();
    }

    @java.lang.Override
    public io.leego.chat.core.GroupMessages build() {
      io.leego.chat.core.GroupMessages result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public io.leego.chat.core.GroupMessages buildPartial() {
      io.leego.chat.core.GroupMessages result = new io.leego.chat.core.GroupMessages(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(io.leego.chat.core.GroupMessages result) {
      if (messageBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          message_ = java.util.Collections.unmodifiableList(message_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.message_ = message_;
      } else {
        result.message_ = messageBuilder_.build();
      }
    }

    private void buildPartial0(io.leego.chat.core.GroupMessages result) {
      int from_bitField0_ = bitField0_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof io.leego.chat.core.GroupMessages) {
        return mergeFrom((io.leego.chat.core.GroupMessages)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.leego.chat.core.GroupMessages other) {
      if (other == io.leego.chat.core.GroupMessages.getDefaultInstance()) return this;
      if (messageBuilder_ == null) {
        if (!other.message_.isEmpty()) {
          if (message_.isEmpty()) {
            message_ = other.message_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureMessageIsMutable();
            message_.addAll(other.message_);
          }
          onChanged();
        }
      } else {
        if (!other.message_.isEmpty()) {
          if (messageBuilder_.isEmpty()) {
            messageBuilder_.dispose();
            messageBuilder_ = null;
            message_ = other.message_;
            bitField0_ = (bitField0_ & ~0x00000001);
            messageBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getMessageFieldBuilder() : null;
          } else {
            messageBuilder_.addAllMessages(other.message_);
          }
        }
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              io.leego.chat.core.GroupMessage m =
                  input.readMessage(
                      io.leego.chat.core.GroupMessage.parser(),
                      extensionRegistry);
              if (messageBuilder_ == null) {
                ensureMessageIsMutable();
                message_.add(m);
              } else {
                messageBuilder_.addMessage(m);
              }
              break;
            } // case 10
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private java.util.List<io.leego.chat.core.GroupMessage> message_ =
      java.util.Collections.emptyList();
    private void ensureMessageIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        message_ = new java.util.ArrayList<io.leego.chat.core.GroupMessage>(message_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        io.leego.chat.core.GroupMessage, io.leego.chat.core.GroupMessage.Builder, io.leego.chat.core.GroupMessageOrBuilder> messageBuilder_;

    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public java.util.List<io.leego.chat.core.GroupMessage> getMessageList() {
      if (messageBuilder_ == null) {
        return java.util.Collections.unmodifiableList(message_);
      } else {
        return messageBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public int getMessageCount() {
      if (messageBuilder_ == null) {
        return message_.size();
      } else {
        return messageBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public io.leego.chat.core.GroupMessage getMessage(int index) {
      if (messageBuilder_ == null) {
        return message_.get(index);
      } else {
        return messageBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder setMessage(
        int index, io.leego.chat.core.GroupMessage value) {
      if (messageBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessageIsMutable();
        message_.set(index, value);
        onChanged();
      } else {
        messageBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder setMessage(
        int index, io.leego.chat.core.GroupMessage.Builder builderForValue) {
      if (messageBuilder_ == null) {
        ensureMessageIsMutable();
        message_.set(index, builderForValue.build());
        onChanged();
      } else {
        messageBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder addMessage(io.leego.chat.core.GroupMessage value) {
      if (messageBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessageIsMutable();
        message_.add(value);
        onChanged();
      } else {
        messageBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder addMessage(
        int index, io.leego.chat.core.GroupMessage value) {
      if (messageBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessageIsMutable();
        message_.add(index, value);
        onChanged();
      } else {
        messageBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder addMessage(
        io.leego.chat.core.GroupMessage.Builder builderForValue) {
      if (messageBuilder_ == null) {
        ensureMessageIsMutable();
        message_.add(builderForValue.build());
        onChanged();
      } else {
        messageBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder addMessage(
        int index, io.leego.chat.core.GroupMessage.Builder builderForValue) {
      if (messageBuilder_ == null) {
        ensureMessageIsMutable();
        message_.add(index, builderForValue.build());
        onChanged();
      } else {
        messageBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder addAllMessage(
        java.lang.Iterable<? extends io.leego.chat.core.GroupMessage> values) {
      if (messageBuilder_ == null) {
        ensureMessageIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, message_);
        onChanged();
      } else {
        messageBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder clearMessage() {
      if (messageBuilder_ == null) {
        message_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        messageBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public Builder removeMessage(int index) {
      if (messageBuilder_ == null) {
        ensureMessageIsMutable();
        message_.remove(index);
        onChanged();
      } else {
        messageBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public io.leego.chat.core.GroupMessage.Builder getMessageBuilder(
        int index) {
      return getMessageFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public io.leego.chat.core.GroupMessageOrBuilder getMessageOrBuilder(
        int index) {
      if (messageBuilder_ == null) {
        return message_.get(index);  } else {
        return messageBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public java.util.List<? extends io.leego.chat.core.GroupMessageOrBuilder> 
         getMessageOrBuilderList() {
      if (messageBuilder_ != null) {
        return messageBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(message_);
      }
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public io.leego.chat.core.GroupMessage.Builder addMessageBuilder() {
      return getMessageFieldBuilder().addBuilder(
          io.leego.chat.core.GroupMessage.getDefaultInstance());
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public io.leego.chat.core.GroupMessage.Builder addMessageBuilder(
        int index) {
      return getMessageFieldBuilder().addBuilder(
          index, io.leego.chat.core.GroupMessage.getDefaultInstance());
    }
    /**
     * <code>repeated .chatapi.GroupMessage message = 1;</code>
     */
    public java.util.List<io.leego.chat.core.GroupMessage.Builder> 
         getMessageBuilderList() {
      return getMessageFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        io.leego.chat.core.GroupMessage, io.leego.chat.core.GroupMessage.Builder, io.leego.chat.core.GroupMessageOrBuilder> 
        getMessageFieldBuilder() {
      if (messageBuilder_ == null) {
        messageBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            io.leego.chat.core.GroupMessage, io.leego.chat.core.GroupMessage.Builder, io.leego.chat.core.GroupMessageOrBuilder>(
                message_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        message_ = null;
      }
      return messageBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:chatapi.GroupMessages)
  }

  // @@protoc_insertion_point(class_scope:chatapi.GroupMessages)
  private static final io.leego.chat.core.GroupMessages DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.leego.chat.core.GroupMessages();
  }

  public static io.leego.chat.core.GroupMessages getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GroupMessages>
      PARSER = new com.google.protobuf.AbstractParser<GroupMessages>() {
    @java.lang.Override
    public GroupMessages parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<GroupMessages> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GroupMessages> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public io.leego.chat.core.GroupMessages getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


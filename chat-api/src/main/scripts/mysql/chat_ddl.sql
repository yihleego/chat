create database if not exists chat;
use chat;


create table chat_contact
(
    id           bigint primary key auto_increment not null,
    sender       bigint                            not null,
    recipient    bigint                            not null,
    alias        varchar(40)                       null,
    status       tinyint unsigned                  not null,
    deleted      bigint default 0                  not null,
    created_time timestamp(3)                      not null,
    updated_time timestamp(3)                      null,
    deleted_time timestamp(3)                      null
);
create unique index uk_contact_sender_recipient on chat_contact (sender, recipient, deleted);

create table chat_contact_request
(
    id           bigint primary key auto_increment not null,
    sender       bigint                            not null,
    recipient    bigint                            not null,
    message      varchar(191)                      null,
    status       tinyint unsigned                  not null,
    created_time timestamp(3)                      not null,
    updated_time timestamp(3)                      null
);
create index idx_contact_request_sender on chat_contact_request (sender);
create index idx_contact_request_recipient on chat_contact_request (recipient);

create table chat_message
(
    id           bigint primary key auto_increment not null,
    sender       bigint                            not null,
    recipient    bigint                            not null,
    type         tinyint unsigned                  not null,
    content      varchar(2000)                     null,
    filename     varchar(191)                      null,
    size         int unsigned                      null,
    event_time   timestamp(3)                      not null,
    sent_time    timestamp(3)                      not null,
    taken_time   timestamp(3)                      null,
    seen_time    timestamp(3)                      null,
    revoked_time timestamp(3)                      null
);
create index idx_message_sender_event_time on chat_message (sender, event_time);
create index idx_message_recipient_event_time on chat_message (recipient, event_time);

create table chat_message_stamp
(
    id          bigint primary key auto_increment not null,
    user_id     bigint                            not null,
    user_type   tinyint unsigned                  not null,
    device_id   bigint                            not null,
    device_type tinyint unsigned                  not null,
    client_type tinyint unsigned                  not null,
    message_id  bigint                            not null,
    last_time   timestamp(3)                      not null
);
create unique index uk_message_stamp_all on chat_message_stamp (user_id, user_type, device_id, device_type, client_type);


create table chat_group
(
    id           bigint primary key auto_increment not null,
    name         varchar(40)                       not null,
    avatar       varchar(191)                      null,
    owner        bigint                            not null,
    size         smallint                          not null,
    status       tinyint unsigned                  not null,
    created_time timestamp(3)                      not null,
    updated_time timestamp(3)                      null
);
create index idx_chat_group_owner on chat_group (owner);

create table chat_group_member
(
    id           bigint primary key auto_increment not null,
    group_id     bigint                            not null,
    user_id      bigint                            not null,
    alias        varchar(40)                       null,
    status       tinyint unsigned                  not null,
    deleted      bigint default 0                  not null,
    created_time timestamp(3)                      not null,
    updated_time timestamp(3)                      null,
    deleted_time timestamp(3)                      null
);
create unique index uk_chat_group_member_group_id_user_id on chat_group_member (group_id, user_id, deleted);
create index idx_chat_group_member_user_id on chat_group_member (user_id);

create table chat_group_message
(
    id           bigint primary key auto_increment not null,
    group_id     bigint                            not null,
    sender       bigint                            not null,
    type         tinyint unsigned                  not null,
    content      varchar(2000)                     null,
    mentions     json                              null,
    status       tinyint unsigned                  not null,
    event_time   timestamp(3)                      not null,
    sent_time    timestamp(3)                      not null,
    taken_time   timestamp(3)                      null,
    seen_time    timestamp(3)                      null,
    revoked_time timestamp(3)                      null
);
create index idx_group_message_sender_event_time on chat_group_message (sender, event_time);
create index idx_group_message_group_id_event_time on chat_group_message (group_id, event_time);

create table chat_group_message_item
(
    id         bigint primary key auto_increment not null,
    message_id bigint                            not null,
    recipient  bigint                            not null,
    event_time timestamp(3)                      not null,
    taken_time timestamp(3)                      null,
    seen_time  timestamp(3)                      null
);
create index idx_group_message_item_message_id on chat_group_message_item (message_id);
create index idx_group_message_item_recipient_event_time on chat_group_message_item (recipient, event_time);

create table chat_group_message_stamp
(
    id          bigint primary key auto_increment not null,
    user_id     bigint                            not null,
    user_type   tinyint unsigned                  not null,
    device_id   bigint                            not null,
    device_type tinyint unsigned                  not null,
    client_type tinyint unsigned                  not null,
    message_id  bigint                            not null,
    last_time   timestamp(3)                      not null
);
create unique index uk_group_message_stamp_all on chat_group_message_stamp (user_id, user_type, device_id, device_type, client_type);


-- For testing
create table test_user
(
    id       bigint primary key auto_increment not null,
    username varchar(40)                       not null,
    password varchar(40)                       not null,
    nickname varchar(40)                       not null,
    avatar   varchar(191)                      null
);
create unique index uk_user_username on test_user (username);
create index idx_user_nickname on test_user (nickname);

create table test_file
(
    id       varchar(40) primary key not null,
    filename varchar(191)            null,
    size     int unsigned            null,
    data     blob                    null
);

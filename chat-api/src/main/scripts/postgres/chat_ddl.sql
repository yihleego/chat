create database chat;
\connect chat;


create table chat_contact
(
    id           bigserial primary key    not null,
    sender       bigint                   not null,
    recipient    bigint                   not null,
    alias        text                     null,
    status       smallint                 not null,
    deleted      bigint default 0         not null,
    created_time timestamp with time zone not null,
    updated_time timestamp with time zone null,
    deleted_time timestamp with time zone null
);
create unique index uk_contact_sender_recipient on chat_contact (sender, recipient, deleted);

create table chat_contact_request
(
    id           bigserial primary key    not null,
    sender       bigint                   not null,
    recipient    bigint                   not null,
    message      text                     null,
    status       smallint                 not null,
    created_time timestamp with time zone not null,
    updated_time timestamp with time zone null
);
create index idx_contact_request_sender on chat_contact_request (sender);
create index idx_contact_request_recipient on chat_contact_request (recipient);

create table chat_message
(
    id           bigserial primary key    not null,
    sender       bigint                   not null,
    recipient    bigint                   not null,
    type         smallint                 not null,
    content      text                     null,
    filename     text                     null,
    size         integer                  null,
    event_time   timestamp with time zone not null,
    sent_time    timestamp with time zone not null,
    taken_time   timestamp with time zone null,
    seen_time    timestamp with time zone null,
    revoked_time timestamp with time zone null
);
create index idx_message_sender_event_time on chat_message (sender, event_time);
create index idx_message_recipient_event_time on chat_message (recipient, event_time);

create table chat_message_stamp
(
    id          bigserial primary key    not null,
    user_id     bigint                   not null,
    user_type   smallint                 not null,
    device_id   bigint                   not null,
    device_type smallint                 not null,
    client_type smallint                 not null,
    message_id  bigint                   not null,
    last_time   timestamp with time zone not null
);
create unique index uk_message_stamp_all on chat_message_stamp (user_id, user_type, device_id, device_type, client_type);


create table chat_group
(
    id           bigserial primary key    not null,
    name         text                     not null,
    avatar       text                     null,
    owner        bigint                   not null,
    size         smallint                 not null,
    status       smallint                 not null,
    created_time timestamp with time zone not null,
    updated_time timestamp with time zone null
);
create index idx_chat_group_owner on chat_group (owner);

create table chat_group_member
(
    id           bigserial primary key    not null,
    group_id     bigint                   not null,
    user_id      bigint                   not null,
    alias        text                     null,
    status       smallint                 not null,
    deleted      bigint default 0         not null,
    created_time timestamp with time zone not null,
    updated_time timestamp with time zone null,
    deleted_time timestamp with time zone null
);
create unique index uk_chat_group_member_group_id_user_id on chat_group_member (group_id, user_id, deleted);
create index idx_chat_group_member_user_id on chat_group_member (user_id);

create table chat_group_message
(
    id           bigserial primary key    not null,
    group_id     bigint                   not null,
    sender       bigint                   not null,
    type         smallint                 not null,
    content      text                     null,
    mentions     jsonb                    null,
    status       smallint                 not null,
    event_time   timestamp with time zone not null,
    sent_time    timestamp with time zone not null,
    taken_time   timestamp with time zone null,
    seen_time    timestamp with time zone null,
    revoked_time timestamp with time zone null
);
create index idx_group_message_sender_event_time on chat_group_message (sender, event_time);
create index idx_group_message_group_id_event_time on chat_group_message (group_id, event_time);

create table chat_group_message_item
(
    id         bigserial primary key    not null,
    message_id bigint                   not null,
    recipient  bigint                   not null,
    event_time timestamp with time zone not null,
    taken_time timestamp with time zone null,
    seen_time  timestamp with time zone null
);
create index idx_group_message_item_message_id on chat_group_message_item (message_id);
create index idx_group_message_item_recipient_event_time on chat_group_message_item (recipient, event_time);

create table chat_group_message_stamp
(
    id          bigserial primary key    not null,
    user_id     bigint                   not null,
    user_type   smallint                 not null,
    device_id   bigint                   not null,
    device_type smallint                 not null,
    client_type smallint                 not null,
    message_id  bigint                   not null,
    last_time   timestamp with time zone not null
);
create unique index uk_group_message_stamp_all on chat_group_message_stamp (user_id, user_type, device_id, device_type, client_type);


-- For testing
create table test_user
(
    id       bigserial primary key not null,
    username text                  not null,
    password text                  not null,
    nickname text                  not null,
    avatar   text                  null
);
create unique index uk_user_username on test_user (username);
create index idx_user_nickname on test_user (nickname);

create table test_file
(
    id       text primary key not null,
    filename text             null,
    size     int              null,
    data     bytea            null
);

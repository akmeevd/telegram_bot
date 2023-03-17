-- liquibase formatted sql

-- changeset akmeev:1
create table notification_task(
    id bigint primary key,
  chat_id bigint,
  message text,
  date_time timestamp
);
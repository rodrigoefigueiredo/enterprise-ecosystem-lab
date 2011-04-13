create sequence users_seq start with 1 increment by 1;
create sequence password_credentials_seq start with 1 increment by 1;
create sequence audit_events_seq start with 1 increment by 1;

create table users (
  id bigint not null primary key,
  public_id varchar(36) not null,
  email varchar(255) not null,
  display_name varchar(255) not null,
  status varchar(32) not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  version integer not null
);

alter table users add constraint uk_users_public_id unique (public_id);
alter table users add constraint uk_users_email unique (email);

create table password_credentials (
  id bigint not null primary key,
  user_id bigint not null,
  password_hash varchar(512) not null,
  hash_algorithm varchar(64) not null,
  created_at timestamp not null,
  active boolean not null,
  version integer not null
);

alter table password_credentials
  add constraint fk_password_credentials_user
  foreign key (user_id) references users (id);

create table audit_events (
  id bigint not null primary key,
  event_type varchar(64) not null,
  subject_type varchar(64) not null,
  subject_id varchar(64) not null,
  occurred_at timestamp not null,
  outcome varchar(32) not null
);

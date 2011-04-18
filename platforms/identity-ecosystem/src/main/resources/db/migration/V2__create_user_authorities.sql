create table user_authorities (
  user_id bigint not null,
  authority varchar(64) not null
);

alter table user_authorities
  add constraint pk_user_authorities primary key (user_id, authority);

alter table user_authorities
  add constraint fk_user_authorities_user
  foreign key (user_id) references users (id);

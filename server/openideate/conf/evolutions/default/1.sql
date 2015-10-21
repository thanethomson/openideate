# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table ideas (
  id                        bigserial not null,
  title                     varchar(255),
  details                   text,
  creator_id                bigint,
  forked_from_id            bigint,
  when_created              timestamp,
  when_updated              timestamp,
  version                   bigint not null,
  constraint pk_ideas primary key (id))
;

create table idea_downvotes (
  id                        bigserial not null,
  when_created              timestamp,
  idea_id                   bigint,
  user_id                   bigint,
  constraint pk_idea_downvotes primary key (id))
;

create table idea_stars (
  id                        bigserial not null,
  when_created              timestamp,
  user_id                   bigint,
  idea_id                   bigint,
  constraint pk_idea_stars primary key (id))
;

create table idea_tags (
  id                        serial not null,
  name                      varchar(50),
  constraint uq_idea_tags_name unique (name),
  constraint pk_idea_tags primary key (id))
;

create table idea_upvotes (
  id                        bigserial not null,
  when_created              timestamp,
  idea_id                   bigint,
  user_id                   bigint,
  constraint pk_idea_upvotes primary key (id))
;

create table linked_accounts (
  id                        bigserial not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_accounts primary key (id))
;

create table users (
  id                        bigserial not null,
  when_created              timestamp,
  last_login                timestamp,
  email                     varchar(300),
  password_hash             varchar(100),
  name                      varchar(255),
  when_email_validated      timestamp,
  active                    boolean,
  picture_url               text,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id))
;


create table ideas_idea_tags (
  ideas_id                       bigint not null,
  idea_tags_id                   integer not null,
  constraint pk_ideas_idea_tags primary key (ideas_id, idea_tags_id))
;
alter table ideas add constraint fk_ideas_creator_1 foreign key (creator_id) references users (id);
create index ix_ideas_creator_1 on ideas (creator_id);
alter table ideas add constraint fk_ideas_forkedFrom_2 foreign key (forked_from_id) references ideas (id);
create index ix_ideas_forkedFrom_2 on ideas (forked_from_id);
alter table idea_downvotes add constraint fk_idea_downvotes_idea_3 foreign key (idea_id) references ideas (id);
create index ix_idea_downvotes_idea_3 on idea_downvotes (idea_id);
alter table idea_downvotes add constraint fk_idea_downvotes_user_4 foreign key (user_id) references users (id);
create index ix_idea_downvotes_user_4 on idea_downvotes (user_id);
alter table idea_stars add constraint fk_idea_stars_user_5 foreign key (user_id) references users (id);
create index ix_idea_stars_user_5 on idea_stars (user_id);
alter table idea_stars add constraint fk_idea_stars_idea_6 foreign key (idea_id) references ideas (id);
create index ix_idea_stars_idea_6 on idea_stars (idea_id);
alter table idea_upvotes add constraint fk_idea_upvotes_idea_7 foreign key (idea_id) references ideas (id);
create index ix_idea_upvotes_idea_7 on idea_upvotes (idea_id);
alter table idea_upvotes add constraint fk_idea_upvotes_user_8 foreign key (user_id) references users (id);
create index ix_idea_upvotes_user_8 on idea_upvotes (user_id);
alter table linked_accounts add constraint fk_linked_accounts_user_9 foreign key (user_id) references users (id);
create index ix_linked_accounts_user_9 on linked_accounts (user_id);



alter table ideas_idea_tags add constraint fk_ideas_idea_tags_ideas_01 foreign key (ideas_id) references ideas (id);

alter table ideas_idea_tags add constraint fk_ideas_idea_tags_idea_tags_02 foreign key (idea_tags_id) references idea_tags (id);

# --- !Downs

drop table if exists ideas cascade;

drop table if exists ideas_idea_tags cascade;

drop table if exists idea_downvotes cascade;

drop table if exists idea_stars cascade;

drop table if exists idea_tags cascade;

drop table if exists idea_upvotes cascade;

drop table if exists linked_accounts cascade;

drop table if exists users cascade;


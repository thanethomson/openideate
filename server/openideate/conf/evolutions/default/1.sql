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

create table idea_tags (
  id                        serial not null,
  name                      varchar(50),
  constraint uq_idea_tags_name unique (name),
  constraint pk_idea_tags primary key (id))
;

create table users (
  id                        bigserial not null,
  when_created              timestamp,
  email                     varchar(300),
  password_hash             varchar(100),
  first_name                varchar(255),
  last_name                 varchar(255),
  when_email_validated      timestamp,
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



alter table ideas_idea_tags add constraint fk_ideas_idea_tags_ideas_01 foreign key (ideas_id) references ideas (id);

alter table ideas_idea_tags add constraint fk_ideas_idea_tags_idea_tags_02 foreign key (idea_tags_id) references idea_tags (id);

# --- !Downs

drop table if exists ideas cascade;

drop table if exists ideas_idea_tags cascade;

drop table if exists idea_tags cascade;

drop table if exists users cascade;


create sequence user_id_seq start with 1 increment by 100;

create table users (
    id bigint DEFAULT nextval('user_id_seq') not null,
    text varchar(1024) not null,
    primary key (id)
)
create sequence student_id_seq start with 1 increment by 100;

create table students (
    id bigint DEFAULT nextval('student_id_seq') not null,
    text varchar(1024) not null,
    primary key (id)
)
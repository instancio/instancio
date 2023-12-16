create table jooq_person
(
    id bigint not null primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    preferred_name varchar(50) not null,
    dob date not null,
    gender varchar(1),
    last_modified datetime not null
);


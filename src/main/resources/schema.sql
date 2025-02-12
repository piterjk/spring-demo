-- auto-generated definition
create table acl_class
(
    id            bigserial
        primary key,
    class         varchar(255) not null
        unique,
    class_id_type varchar(100) default 'java.lang.Long'::character varying
);


-- auto-generated definition
create table acl_entry
(
    id                  bigserial
        primary key,
    acl_object_identity bigint  not null
        constraint fk_acl_entry_object
            references acl_object_identity
            on delete cascade,
    ace_order           integer not null,
    sid                 bigint  not null
        constraint fk_acl_entry_sid
            references acl_sid,
    mask                integer not null,
    granting            boolean not null,
    audit_success       boolean not null,
    audit_failure       boolean not null
);


-- auto-generated definition
create table acl_object_identity
(
    id                 bigserial
        primary key,
    object_id_class    bigint      not null
        constraint fk_acl_object_identity_class
            references acl_class,
    object_id_identity varchar(36) not null,
    parent_object      bigint
        constraint fk_acl_object_identity_parent
            references acl_object_identity,
    owner_sid          bigint
        constraint fk_acl_object_identity_owner
            references acl_sid,
    entries_inheriting boolean     not null
);


-- auto-generated definition
create table acl_sid
(
    id        bigserial
        primary key,
    principal boolean      not null,
    sid       varchar(100) not null
        unique
);

-- auto-generated definition
create table roles
(
    id   bigserial
        primary key,
    name varchar(255) not null
        unique
);

-- auto-generated definition
create table user_roles
(
    user_id bigint not null
        references users
            on delete cascade,
    role_id bigint not null
        references roles
            on delete cascade,
    primary key (user_id, role_id)
);

-- auto-generated definition
create table users
(
    id       bigserial
        primary key,
    username varchar(255) not null
        unique,
    password varchar(255) not null
);


-- auto-generated definition
create table posts
(
    id      bigserial
        primary key,
    title   varchar(100) not null,
    content varchar(255) not null,
    user_id bigint
        constraint users_id_fkey
            references users
);



-- auto-generated definition
create table common_code
(
    code       varchar(255) not null
        constraint common_code_pk
            primary key,
    code_name  varchar(255),
    code_group varchar(255) not null
);

comment on column common_code.code is '코드';

comment on column common_code.code_name is '코드명';

comment on column common_code.code_group is '코드 그룹';
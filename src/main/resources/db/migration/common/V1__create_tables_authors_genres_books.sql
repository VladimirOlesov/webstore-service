CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE authors
(
    author_id   bigserial primary key,
    author_name varchar(255) not null
);

CREATE TABLE genres
(
    genre_id   bigserial primary key,
    genre_name varchar(255) not null
);

CREATE TABLE books
(
    book_id          bigserial primary key,
    title            varchar(255) not null,
    author_id        bigint references authors (author_id),
    genre_id         bigint references genres (genre_id),
    publication_year int,
    price            decimal(8, 2),
    ISBN             varchar(20)  not null,
    page_count       int,
    age_rating       int,
    cover_path       varchar(255),
    deleted          boolean      not null default false
);
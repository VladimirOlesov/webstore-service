CREATE TABLE orders
(
    order_id   bigserial primary key,
    user_uuid  uuid not null,
    order_date timestamp,
    status     varchar(255)
);

CREATE TABLE orders_books
(
    order_id bigint not null references orders (order_id),
    book_id  bigint not null references books (book_id),
    primary key (order_id, book_id)
);

CREATE TABLE favorites
(
    user_uuid uuid not null,
    book_id   bigint not null references books (book_id),
    primary key (user_uuid, book_id)
);
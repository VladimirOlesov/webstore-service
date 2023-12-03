INSERT INTO orders (user_uuid, order_date, status)
VALUES ('3dad110d-4ba0-4f35-aa87-65d24a185301', null, 'IN_CART');

INSERT INTO orders_books (order_id, book_id)
SELECT order_id, 1
FROM orders
WHERE status = 'IN_CART';
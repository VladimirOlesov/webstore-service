INSERT INTO favorites (user_uuid, book_id)
VALUES ('3dad110d-4ba0-4f35-aa87-65d24a185301', 1);

INSERT INTO favorites (user_uuid, book_id)
VALUES ((SELECT user_uuid
         FROM favorites
         WHERE book_id = 1), 2);
--  06 INSERT books
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Test Book 1', 'Author A', 'a9781234567896', 10.91, 'This is a sample book1 description.',
        'http://example.com/cover1.jpg', false);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Test Book 2', 'Author B', 'b9781234567897', 14.94, 'This is a sample book2 description.',
        'http://example.com/cover2.jpg', false);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Test Book 3', 'Author C', 'c9781234567898', 19.98, 'This is a sample book3 description.',
        'http://example.com/cover3.jpg', false);

-- 12 INSERT categories
INSERT INTO categories (`id`, `name`, `description`, `is_deleted`)
VALUES (1, 'Fiction', 'Fiction books', false);
INSERT INTO categories (`id`, `name`, `description`, `is_deleted`)
VALUES (2, 'Novels', 'Novels books', false);

-- 13 INSERT books_categories
INSERT INTO books_categories (`book_id`, `category_id`) VALUES (1, 1);
INSERT INTO books_categories (`book_id`, `category_id`) VALUES (2, 1);
INSERT INTO books_categories (`book_id`, `category_id`) VALUES (3, 2);

-- 14  INSERT INTO shopping_carts
INSERT INTO shopping_carts (user_id) VALUES (1);
INSERT INTO shopping_carts (user_id) VALUES (2);

-- 17 INSERT INTO cart_items
INSERT INTO cart_items (id, shopping_carts_id, book_id, quantity, is_deleted) VALUES (1, 2, 1, 3, false);
INSERT INTO cart_items (id, shopping_carts_id, book_id, quantity, is_deleted) VALUES (2, 2, 2, 2, false);
INSERT INTO cart_items (id, shopping_carts_id, book_id, quantity, is_deleted) VALUES (3, 2, 3, 1, false);
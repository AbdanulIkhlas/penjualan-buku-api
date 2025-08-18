# --- !Ups

-- Insert dummy genres
INSERT INTO genres (name, description) VALUES
('Fiksi', 'Buku fiksi berbagai genre seperti fantasi, drama, dll'),
('Non-Fiksi', 'Buku non-fiksi seperti biografi, sejarah, dll'),
('Teknologi', 'Buku teknologi dan pemrograman'),
('Anak-anak', 'Buku bacaan untuk anak-anak');

-- Insert dummy books
INSERT INTO books (title, author, genre_id, description, price, image_url, stock) VALUES
('Belajar Scala', 'John Doe', 3, 'Buku pemrograman Scala untuk pemula', 150000.00, 'scala.jpg', 10),
('Sejarah Dunia', 'Jane Smith', 2, 'Sejarah dunia dari awal peradaban sampai modern', 120000.00, 'history.jpg', 5),
('Petualangan Si Kancil', 'Budi Santoso', 4, 'Cerita anak-anak tentang si Kancil yang cerdik', 50000.00, 'kancil.jpg', 20),
('Fantasi Negeri Ajaib', 'Alice Wonderland', 1, 'Novel fantasi penuh petualangan', 90000.00, 'fantasy.jpg', 15);

-- Insert dummy users
INSERT INTO users (name, email, city_id, address) VALUES
('Andi', 'andi@example.com', 'YOG', 'Jl. Malioboro No.1, Yogyakarta'),
('Budi', 'budi@example.com', 'JKT', 'Jl. Sudirman No.2, Jakarta'),
('Citra', 'citra@example.com', 'BDG', 'Jl. Asia Afrika No.3, Bandung');

-- Insert dummy carts
INSERT INTO cart (user_id, price, status) VALUES
(1, 0.00, 'active'),
(2, 0.00, 'active');

-- Insert dummy cart_books
INSERT INTO cart_books (cart_id, book_id, qty, unit_price, total_price) VALUES
(1, 1, 2, 150000.00, 300000.00),
(1, 3, 1, 50000.00, 50000.00),
(2, 2, 1, 120000.00, 120000.00);

-- Update cart total price
UPDATE cart SET price = 350000.00 WHERE id = 1;
UPDATE cart SET price = 120000.00 WHERE id = 2;

-- Insert dummy transactions
INSERT INTO transactions (cart_id, cart_price, delivery_service_price, total_price) VALUES
(1, 350000.00, 20000.00, 370000.00),
(2, 120000.00, 15000.00, 135000.00);

# --- !Downs

DELETE FROM transactions;
DELETE FROM cart_books;
DELETE FROM cart;
DELETE FROM books;
DELETE FROM users;
DELETE FROM genres;
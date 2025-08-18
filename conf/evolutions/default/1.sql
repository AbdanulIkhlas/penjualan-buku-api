# --- !Ups

CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_delete_genres BOOLEAN DEFAULT FALSE
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre_id INT NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    stock INT NOT NULL DEFAULT 0,
    is_delete_books BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    city_id VARCHAR(255) NOT NULL,
    address TEXT,
    is_delete_users BOOLEAN DEFAULT FALSE
);

CREATE TYPE cart_status AS ENUM ('active', 'ordered', 'cancelled');

CREATE TABLE cart (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status cart_status NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_delete_cart BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE cart_books (
    id SERIAL PRIMARY KEY,
    cart_id INT NOT NULL,
    book_id INT NOT NULL,
    qty INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    is_delete_cart_books BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE ON UPDATE CASCADE
--     UNIQUE (cart_id, book_id)
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    cart_id INT NOT NULL,
    cart_price DECIMAL(10, 2) NOT NULL,
    delivery_service_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_delete_transactions BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cart_books;
DROP TABLE IF EXISTS cart;
DROP TYPE IF EXISTS cart_status;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;




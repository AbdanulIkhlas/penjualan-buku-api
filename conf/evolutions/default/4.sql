# --- !Ups

ALTER TABLE books
ALTER COLUMN price TYPE DECIMAL(10,2) USING price::DECIMAL(10,2);

ALTER TABLE cart
ALTER COLUMN price TYPE DECIMAL(10,2) USING price::DECIMAL(10,2);

ALTER TABLE cart_books
ALTER COLUMN unit_price TYPE DECIMAL(10,2) USING unit_price::DECIMAL(10,2),
    ALTER COLUMN total_price TYPE DECIMAL(10,2) USING total_price::DECIMAL(10,2);

ALTER TABLE transactions
ALTER COLUMN cart_price TYPE DECIMAL(10,2) USING cart_price::DECIMAL(10,2),
    ALTER COLUMN delivery_service_price TYPE DECIMAL(10,2) USING delivery_service_price::DECIMAL(10,2),
    ALTER COLUMN total_price TYPE DECIMAL(10,2) USING total_price::DECIMAL(10,2);

# --- !Downs

ALTER TABLE books
ALTER COLUMN price TYPE BIGINT USING ROUND(price);

ALTER TABLE cart
ALTER COLUMN price TYPE BIGINT USING ROUND(price);

ALTER TABLE cart_books
ALTER COLUMN unit_price TYPE BIGINT USING ROUND(unit_price),
    ALTER COLUMN total_price TYPE BIGINT USING ROUND(total_price);

ALTER TABLE transactions
ALTER COLUMN cart_price TYPE BIGINT USING ROUND(cart_price),
    ALTER COLUMN delivery_service_price TYPE BIGINT USING ROUND(delivery_service_price),
    ALTER COLUMN total_price TYPE BIGINT USING ROUND(total_price);

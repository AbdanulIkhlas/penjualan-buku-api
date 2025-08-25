# --- !Ups
-- Ubah kolom status menjadi VARCHAR
ALTER TABLE cart
ALTER COLUMN status TYPE VARCHAR(20);

-- Set default value ke 'active'
ALTER TABLE cart
    ALTER COLUMN status SET DEFAULT 'active';

-- Hapus type ENUM karena tidak dipakai lagi
DROP TYPE IF EXISTS cart_status;


# --- !Downs
-- Buat ulang type ENUM cart_status
CREATE TYPE cart_status AS ENUM ('active', 'ordered', 'cancelled');

-- Ubah kolom status kembali ke cart_status
ALTER TABLE cart
ALTER COLUMN status TYPE cart_status
    USING status::cart_status;

-- Set default value ulang
ALTER TABLE cart
    ALTER COLUMN status SET DEFAULT 'active';

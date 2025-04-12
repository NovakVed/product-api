DROP TABLE IF EXISTS products CASCADE;
DROP INDEX IF EXISTS idx_products_code;
DROP INDEX IF EXISTS idx_products_id;

CREATE TABLE products
(
    id            SERIAL PRIMARY KEY                  NOT NULL,
    code          VARCHAR(10)                         NOT NULL UNIQUE,
    name          VARCHAR(255)                        NOT NULL, -- TODO add localization?
    price_eur     NUMERIC(38, 2)                      NOT NULL, -- TODO is check needed if you will do the same via code?
    -- TODO check if 38 is too much?
    is_available  BOOLEAN   DEFAULT FALSE             NOT NULL,
    creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
-- TODO: what is pk in db, is this used everywhere?, pk vs id?
-- TODO: create index for code, check what is index as well!
);

CREATE INDEX idx_products_id ON products (id);
CREATE INDEX idx_products_code ON products (code);

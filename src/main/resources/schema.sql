DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE products
(
    id           SERIAL PRIMARY KEY    NOT NULL,
    code         VARCHAR(10)           NOT NULL UNIQUE,
    name         VARCHAR(255)          NOT NULL, -- TODO add localization?
    price_eur    NUMERIC(38, 2)        NOT NULL, -- TODO is check needed if you will do the same via code?
    price_usd    NUMERIC(38, 2)        NOT NULL, -- TODO maybe get the latest value, cache it and then serve this cache in response?
    is_available BOOLEAN DEFAULT FALSE NOT NULL
-- TODO: add creation time
--  add last modification time?
);
CREATE TABLE IF NOT EXISTS order_entity
(
    id                 SERIAL PRIMARY KEY,
    order_id           UUID         NOT NULL UNIQUE,
    order_state        VARCHAR(100) NOT NULL,
    placed_by_consumer UUID         NOT NULL,
    placed_at_branch   UUID         NOT NULL,
    payment_till       TIMESTAMP,
    paid_when          TIMESTAMP,
    cancelled_why      VARCHAR(100),
    version            INTEGER
);

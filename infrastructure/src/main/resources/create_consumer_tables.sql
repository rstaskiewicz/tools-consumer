CREATE TABLE IF NOT EXISTS consumer_entity
(
    id          SERIAL PRIMARY KEY,
    consumer_id UUID NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS consumer_order_entity
(
    id              SERIAL PRIMARY KEY,
    order_id        UUID      NOT NULL UNIQUE,
    consumer_id     UUID      NOT NULL,
    sales_branch_id UUID      NOT NULL,
    ordered_at      TIMESTAMP NOT NULL,
    payment_till    TIMESTAMP NOT NULL,
    consumer_entity INTEGER   NOT NULL REFERENCES consumer_entity
);

CREATE TABLE IF NOT EXISTS overdue_payment_entity
(
    id              SERIAL PRIMARY KEY,
    order_id        UUID    NOT NULL UNIQUE,
    consumer_id     UUID    NOT NULL,
    sales_branch_id UUID    NOT NULL,
    consumer_entity INTEGER NOT NULL REFERENCES consumer_entity
);

CREATE TABLE IF NOT EXISTS event_descriptor
(
    id           SERIAL PRIMARY KEY,
    event_id     UUID          NOT NULL UNIQUE,
    aggregate_id UUID          NOT NULL,
    body         VARCHAR(1000) NOT NULL,
    type         VARCHAR(100)  NOT NULL,
    occurred_at  TIMESTAMP     NOT NULL
);


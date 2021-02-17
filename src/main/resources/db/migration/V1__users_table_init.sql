CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      TEXT                     NOT NULL UNIQUE,
    password   TEXT                     NOT NULL,
    first_name TEXT                     NOT NULL,
    last_name  TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

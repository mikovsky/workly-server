CREATE TABLE projects
(
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT                     NOT NULL,
    user_id    BIGINT                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

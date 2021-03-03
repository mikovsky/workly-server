CREATE TABLE sections
(
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT                     NOT NULL,
    project_id BIGINT                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id)
);

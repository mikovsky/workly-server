CREATE TABLE project_members
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_project_id ON project_members (project_id);
CREATE INDEX idx_user_id ON project_members (user_id);

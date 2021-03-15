ALTER TABLE tasks
    ADD COLUMN project_id  BIGINT,
    ADD COLUMN section_id  BIGINT,
    ADD COLUMN assignee_id BIGINT;

--liquibase formatted sql

--changeset razinkova:1
CREATE INDEX idx_student_last_name ON student (last_name);

--changeset razinkova:2
CREATE INDEX IF NOT EXISTS idx_student_phone_number ON student (phone_number);
CREATE INDEX IF NOT EXISTS idx_student_student_tiсket ON student (student_tiсket);

--changeset razinkova:3
create index idx_faculty_name on faculty (name);
create index idx_faculty_color on faculty (color);


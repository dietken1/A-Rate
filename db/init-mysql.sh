#!/bin/bash

mysql -u root -p${MYSQL_ROOT_PASSWORD} << EOF
CREATE DATABASE IF NOT EXISTS arate_db;
USE arate_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL COMMENT 'School email',
    nickname VARCHAR(50) NOT NULL COMMENT 'Nickname',
    department VARCHAR(255),
    role ENUM('STUDENT', 'PROFESSOR', 'ADMIN') NOT NULL COMMENT 'User role',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Registration time',
    student_number VARCHAR(255),
    profile_image VARCHAR(255)
);

CREATE TABLE professors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT COMMENT 'User ID for authentication'
);

CREATE TABLE lectures (
    id INT PRIMARY KEY,
    professor_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    course_type ENUM('교선', '교필', '일선', '전기', '전선', '전필') NOT NULL COMMENT 'Course classification',
    is_english_lecture BOOLEAN DEFAULT FALSE COMMENT 'Whether the lecture is in English',
    FOREIGN KEY (professor_id) REFERENCES professors(id)
);

CREATE TABLE lecture_evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    lecture_id INT NOT NULL,
    semester VARCHAR(255),
    content TEXT,
    delivery_score TINYINT,
    expertise_score TINYINT,
    generosity_score TINYINT,
    effectiveness_score TINYINT,
    character_score TINYINT,
    difficulty_score TINYINT,
    assignment_amount ENUM('NONE', 'FEW', 'NORMAL', 'MANY'),
    assignment_difficulty ENUM('EASY', 'NORMAL', 'HARD'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    exam ENUM('midterm', 'final', 'both'),
    team_project BOOLEAN,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

CREATE TABLE enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    lecture_id INT NOT NULL,
    certification_image TEXT,
    grade VARCHAR(255),
    is_certified BOOLEAN DEFAULT FALSE,
    semester VARCHAR(255),
    created_at DATETIME,
    certified_at DATETIME,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

CREATE TABLE shared_materials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uploader_id INT NOT NULL,
    lecture_id INT NOT NULL,
    title VARCHAR(200),
    content TEXT,
    file TEXT,
    tags JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploader_id) REFERENCES users(id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

CREATE TABLE replies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id INT NOT NULL,
    author_id INT NOT NULL,
    content VARCHAR(255),
    FOREIGN KEY (evaluation_id) REFERENCES lecture_evaluations(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);
EOF
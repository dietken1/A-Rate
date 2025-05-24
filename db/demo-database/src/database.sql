-- 테이블 생성

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS lectures;
DROP TABLE IF EXISTS evaluations;

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    role TEXT NOT NULL  -- 예: 'student', 'professor'
);

CREATE TABLE lectures (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    professor_id INTEGER,
    FOREIGN KEY (professor_id) REFERENCES users(id)
);

CREATE TABLE evaluations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    lecture_id INTEGER,
    semester TEXT,
    delivery_score INTEGER,
    expertise_score INTEGER,
    fairness_score INTEGER,
    effectiveness_score INTEGER,
    personality_score INTEGER,
    difficulty_score INTEGER,
    comment TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (lecture_id) REFERENCES lectures(id)
);

-- 사용자 삽입

INSERT INTO users (name, email, role) VALUES
('김지훈', 'jihun@ajou.ac.kr', 'student'),
('박유진', 'yujin@ajou.ac.kr', 'student'),
('이서연', 'seoyeon@ajou.ac.kr', 'student'),
('정민수', 'msjung@ajou.ac.kr', 'professor'),
('오지현', 'jh.oh@ajou.ac.kr', 'professor');

-- 강의 삽입

INSERT INTO lectures (title, professor_id) VALUES
('오픈소스SW입문', 4),
('데이터사이언스개론', 5),
('객체지향프로그래밍', 4);

-- 강의평가 삽입

INSERT INTO evaluations (user_id, lecture_id, semester, delivery_score, expertise_score, fairness_score, effectiveness_score, personality_score, difficulty_score, comment) VALUES
(1, 1, '2023-1', 5, 4, 5, 4, 5, 3, '설명이 친절하고 과제도 많지 않아서 좋았어요.'),
(2, 1, '2023-1', 4, 4, 4, 4, 4, 2, '전반적으로 무난하지만 조별과제가 좀 아쉬워요.'),
(3, 2, '2023-2', 5, 5, 5, 5, 5, 4, '강의 내용이 아주 알차고 유익했습니다.'),
(1, 2, '2023-2', 4, 5, 4, 5, 4, 4, '조금 어려웠지만 얻어가는 게 많았어요.'),
(2, 3, '2024-1', 3, 3, 4, 3, 3, 2, '수업 템포가 빠르지만 교수님은 열정적이에요.'),
(3, 3, '2024-1', 5, 5, 5, 5, 5, 3, '추천합니다! 개념 정리가 정말 잘 되어 있어요.');
